package com.romana.userinterface;

import com.romana.database.DatabaseException;
import com.romana.devices.card.CardDatabase;
import com.romana.devices.scale.ScaleResponse;
import static com.romana.devices.scale.ScaleResponse.ScaleCode.*;
import com.romana.devices.SerialException;
import com.romana.devices.SystemOperations;
import com.romana.devices.SystemOperations.WeightType;
import com.romana.devices.WeightInfo;
import com.romana.devices.scale.ScaleException;
import com.romana.userinterface.commonwidgets.InteractivePanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 * To do: Reuse code between cardRead and this
 *
 * @author rafael
 */
public class WeighingPanel extends InteractivePanel {

    private static final Logger LOGGER = Logger.getLogger(WeighingPanel.class.getName());
    private final Style.TitleLabel titleLabel = new Style.TitleLabel("Pesando el vehículo");
    private final Style.StyledJLabel commentLabel = new Style.StyledJLabel("Espere por favor", 40);
    private Style.StyledImage picLabel;

    public WeighingPanel() {
        initComponents();
    }
    
    @Override
    public void actionOnShow(){
        new WeighWorker().execute();
    }
    
    private void initComponents() {
        setLayout(new GridBagLayout());

        GridBagConstraints gridBagTitle = new GridBagConstraints();
        gridBagTitle.gridy = 0;
        add(titleLabel, gridBagTitle);

        picLabel = new Style.StyledImage("/gui_img/scale_512.png", 400, 400);
        Style.StyledJPanel imageContainer = new Style.StyledJPanel(new GridBagLayout());
        imageContainer.add(picLabel, new GridBagConstraints());

        GridBagConstraints gridBagImage = new GridBagConstraints();
        gridBagImage.gridy = 1;
        gridBagImage.anchor = GridBagConstraints.CENTER;
        gridBagImage.weightx = 1;
        gridBagImage.weighty = 1;
        add(picLabel, gridBagImage);

        GridBagConstraints gridBagComment = new GridBagConstraints();
        gridBagComment.gridy = 2;
        gridBagComment.insets = new Insets(5, 5, 100, 5);
        add(commentLabel, gridBagComment);

    }

    private class WeighWorker extends OperationSwingWorker<ScaleResponse, Object> {

        public WeighWorker() {
            super(interfaceActions);
        }

        @Override
        protected ScaleResponse doInBackground() throws ScaleException, DatabaseException {

            WeightInfo actualWeight = systemActions.getActualWeightInfo();
            SystemOperations systemOps = systemActions.getSystemOperations();

            switch (actualWeight.getType()) {
                case SIMPLE:
                    return systemOps.simpleWeight(actualWeight);

                case TWO_PHASE:
                    if (actualWeight.isSecondPhase()) {
                        return systemOps.finishTwoPhase(actualWeight);
                    } else {
                        return systemOps.startTwoPhase(actualWeight);
                    }

                case AXIS:
                    return systemOps.nextAxis(actualWeight);

            }
            return null; // Should never reach this statement!

        }

        @Override
        protected void doneAndCatch() throws InterruptedException, ExecutionException, DatabaseException {
            
            WeightInfo actualWeight = systemActions.getActualWeightInfo();
            CardDatabase cardDatabase = systemActions.getCardDatabase();
            
            ScaleResponse response = get();

            if (response.code == WEIGHT_TOO_LOW || response.code == NOT_ENOUGH_DIFFERENCE) {
                LOGGER.log(Level.INFO, "Vehicle not moved: {0}", response.code);
                interfaceActions.switchPanel(NoVehiclePanel.class);
                return;
            }

            if (response.code != OPERATION_OK) {
                LOGGER.log(Level.SEVERE, "Received strange response {0}", response.code);
                return;
            }
            
            if (actualWeight.getType() == WeightType.AXIS 
                    && actualWeight.getNumberOfWeights() == actualWeight.getAxisNumber()){
                
                SystemOperations sysOps = systemActions.getSystemOperations();
                sysOps.finishAxis(actualWeight);
            }
            
            if (actualWeight.isFinished()){

                int priceToPay = actualWeight.getSalePrice();
                String cardId = actualWeight.getCardId();
                
                LOGGER.log(Level.INFO, String.format("Success. Discounting %s from %s", 
                        priceToPay, cardId));
                
                cardDatabase.discountMoney(cardId, priceToPay);
                interfaceActions.switchPanel(ReceiptPanel.class);
                return;
            }

            // At this point are OPERATION_OK and not finished
            if (actualWeight.getType() == WeightType.AXIS) {
                interfaceActions.switchPanel(MoveTruckPanel.class);

            } else if (actualWeight.getType() == WeightType.TWO_PHASE){
                interfaceActions.switchPanel(FirstPhaseFinishedPanel.class);
            }
        }
    }

    public static void main(String[] args) {
        JFrame mainFrame = new JFrame();
        WeighingPanel panel = new WeighingPanel();
        //panel.setPrice(5000);
        mainFrame.add(panel);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

}
