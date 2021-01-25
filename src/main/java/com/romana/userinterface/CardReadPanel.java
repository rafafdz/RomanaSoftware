/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface;

import com.romana.database.DatabaseException;
import com.romana.devices.CardDatabase;
import com.romana.devices.SerialException;
import com.romana.devices.SystemOperations;
import com.romana.devices.SystemOperations.WeightType;
import com.romana.devices.WeightInfo;
import com.romana.userinterface.commonwidgets.InteractivePanel;
import com.romana.utilities.CommonUtils;
import com.romana.utilities.Configuration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rafa
 */
public class CardReadPanel extends InteractivePanel {

    private static final Logger LOGGER = Logger.getLogger(CardReadPanel.class.getName());
    private final Style.TitleLabel titleLabel = new Style.TitleLabel("Acerque su tarjeta al lector");
    private final Style.StyledJLabel priceLabel = new Style.StyledJLabel(80);
    private final Style.StyledJLabel debugLabel = new Style.StyledJLabel(30);
    private static final String PIC_PATH = "/gui_img/card_512.png";

    private final Style.StyledImage picLabel = new Style.StyledImage(PIC_PATH, 400, 400);
    private static final int INACTIVITY_TIMEOUT = 60;

    public CardReadPanel() {
        super(INACTIVITY_TIMEOUT);
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        priceLabel.setBold();

        GridBagConstraints gridBagTitle = new GridBagConstraints();
        gridBagTitle.gridy = 0;
        add(titleLabel, gridBagTitle);

        Style.StyledJPanel imageContainer = new Style.StyledJPanel(new GridBagLayout());
        imageContainer.add(picLabel, new GridBagConstraints());

        GridBagConstraints gridBagImage = new GridBagConstraints();
        gridBagImage.gridy = 1;
        gridBagImage.anchor = GridBagConstraints.CENTER;
        gridBagImage.weightx = 1;
        gridBagImage.weighty = 1;
        add(picLabel, gridBagImage);

        GridBagConstraints gridBagPricePanel = new GridBagConstraints();
        gridBagPricePanel.gridy = 2;
        gridBagPricePanel.insets = new Insets(5, 5, 5, 5);
        add(new PricePanel(), gridBagPricePanel);

        addVerticalStretch(3);

        GridBagConstraints gridBagDebug = new GridBagConstraints();
        gridBagDebug.gridy = 4;
        gridBagDebug.insets = new Insets(5, 5, 5, 5);
        add(debugLabel, gridBagDebug);
    }

    private final class PricePanel extends Style.RoundedPanel {

        private static final int BORDER_RADIUS = 30;
        private static final int BORDER_WIDTH = 10;
        private final Insets priceMargins = new Insets(10, 20, 10, 20);

        public PricePanel() {
            super(BORDER_RADIUS, BORDER_WIDTH);
            setLayout(new GridBagLayout());
            GridBagConstraints gridBagPrice = new GridBagConstraints();
            gridBagPrice.insets = priceMargins;
            add(priceLabel, gridBagPrice);
        }

    }

    public void setPrice(int price) {
        priceLabel.setText("$" + String.valueOf(price));
    }

    @Override
    public void actionOnShow() {
        WeightInfo actualWeight = systemActions.getActualWeightInfo();
        setPrice(actualWeight.getPriceToPay());
        new ReadCardWorker().execute();
    }

    @Override
    public void actionOnShowDebug() {
        // Do not execute the card reading
        setPrice(10000);
        debugLabel.setText("READER DEVICE: " + Configuration.getStringConfig("CARD_PORT"));
        super.actionOnShowDebug();
    }

    @Override
    public void timeoutAction() {
        // Set text of the error panel and switch to it
        systemActions.returnToMainMenu();
    }

    private class ReadCardWorker extends OperationSwingWorker<String, Object> {

        public ReadCardWorker() {
            super(interfaceActions);
        }

        @Override
        protected String doInBackground() throws SerialException {
           LOGGER.log(Level.FINE, "Started ReadCard worker");

            SystemOperations systemOps = systemActions.getSystemOperations();
            return systemOps.getCardId();
        }

        // Executed in the EDT
        @Override
        protected void doneAndCatch() throws InterruptedException, ExecutionException,
                DatabaseException {

            WeightInfo actualWeight = systemActions.getActualWeightInfo();
            CardDatabase cardDatabase = systemActions.getCardDatabase();
            SystemOperations systemOps = systemActions.getSystemOperations();

            String cardID = get();
            String plate = actualWeight.getPlate();

            if (cardID == null) {
                interfaceActions.switchPanel(NoCardPanel.class);
                return;
            }

            if (!cardDatabase.isRegistered(cardID)) {
                LOGGER.log(Level.INFO, "Card not Registered Detected: {0}", cardID);
                interfaceActions.switchPanel(NotRegisteredPanel.class);
                return;
            }

            if (actualWeight.getType() == WeightType.TWO_PHASE && actualWeight.isSecondPhase()) {

                if (!systemOps.plateMatchesCard(plate, cardID)) {
                    LOGGER.log(Level.INFO, String.format("Invalid card %s for plate %s",
                            cardID, plate));
                    interfaceActions.switchPanel(InvalidCardPanel.class);
                    return;
                }

                LOGGER.log(Level.INFO, "Card verified for plate {0}", plate);

                Date firstDate = actualWeight.getFirstWeightDate();
                Date secondDate = new Date();
                int hourLimit = Configuration.getIntConfig("HOUR_LIMIT_SECOND_PHASE");

                if (CommonUtils.hourDifferenceDecimal(firstDate, secondDate) > hourLimit) {
                    LOGGER.log(Level.INFO, "Hour limit exceeded for plate {0}", plate);
                    interfaceActions.switchPanel(TimeExceededPanel.class);
                    return;
                }
            }

            if (!cardDatabase.hasMoney(cardID, actualWeight.getSalePrice())) {

                int actualBalance = cardDatabase.readCardBalance(cardID);
                int totalPrice = actualWeight.getTotalPrice();
                interfaceActions.setNoMoney(actualBalance, totalPrice);
                interfaceActions.switchPanel(NoMoneyPanel.class);
                return;
            }

            actualWeight.setCardId(cardID);
            LOGGER.log(Level.INFO, "Set cardId to {0}", cardID);
            interfaceActions.switchPanel(MoveTruckPanel.class);
        }

    }
}
