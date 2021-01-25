/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface;

import com.romana.devices.SystemOperations;
import com.romana.devices.WeightInfo;
import com.romana.userinterface.commonwidgets.Buttons;
import com.romana.userinterface.commonwidgets.InteractivePanel;
import com.romana.userinterface.commonwidgets.TablePanel;
import com.romana.utilities.CommonUtils;
import com.romana.utilities.Configuration;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author rafael
 */
public class ReceiptPanel extends InteractivePanel {

    private final Style.TitleLabel titleLabel = new Style.TitleLabel("Resumen del pesaje");
    private final String WEB_TEXT = "Revise la información de este pesaje en:";
    private final Style.StyledJLabel webLabel = new Style.StyledJLabel(WEB_TEXT, 40);
    private final Style.StyledJLabel urlLabel = new Style.StyledJLabel(45);
    private final Style.RoundedPanel urlContainer = new Style.RoundedPanel();
    private final WeightTable weightTable = new WeightTable();
    private final String WEB_DOMAIN = Configuration.getStringConfig("WEB_DOMAIN");
    private final int SIDE_SPACING = 300;
    private final int BOTTOM_SPACING = 30;

    private static final int INACTIVITY_TIMEOUT = 60;

    public ReceiptPanel() {
        super(INACTIVITY_TIMEOUT);
        initComponents();
    }

    // For dev purpouses only!
    private void gen(WeightInfo actualWeightInfo) {
        weightTable.generateFrom(actualWeightInfo);

        String url = actualWeightInfo.getUrl();
        urlLabel.setText(WEB_DOMAIN + "/" + url);
    }

    @Override
    public void actionOnShow() {
        WeightInfo actualWeightInfo = systemActions.getActualWeightInfo();
        weightTable.generateFrom(actualWeightInfo);

        String url = actualWeightInfo.getUrl();
        urlLabel.setText(WEB_DOMAIN + "/" + url);
    }

    @Override
    public void timeoutAction() {
        interfaceActions.switchPanel(PullTicketPanel.class);
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        GridBagConstraints gridBagTitle = new GridBagConstraints();
        gridBagTitle.gridy = 0;
        add(titleLabel, gridBagTitle);

        GridBagConstraints gridBagTable = new GridBagConstraints();
        gridBagTable.gridy = 1;
        gridBagTable.fill = GridBagConstraints.HORIZONTAL;
        gridBagTable.insets = new Insets(25, SIDE_SPACING, 25, SIDE_SPACING);
        add(weightTable, gridBagTable);

        GridBagConstraints gridBagWebLabel = new GridBagConstraints();
        gridBagWebLabel.gridy = 2;
        gridBagWebLabel.insets = new Insets(0, 0, 15, 0);
        add(webLabel, gridBagWebLabel);

        urlLabel.setBold();
        urlContainer.setBorderWidth(2);
        urlContainer.setTextMode(urlLabel, new Insets(10, 10, 10, 10));
        GridBagConstraints gridBagContainer = new GridBagConstraints();
        gridBagContainer.gridy = 3;
        add(urlContainer, gridBagContainer);

        addVerticalStretch(4);

        Buttons.ConfirmButton okButton = new Buttons.ConfirmButton(80, 10) {
            @Override
            public void clickAction(MouseEvent e) {
                timeoutAction();
            }
        };

        GridBagConstraints gridBagButton = new GridBagConstraints();
        gridBagButton.gridy = 5;
        gridBagButton.insets = new Insets(0, 0, BOTTOM_SPACING, 0);
        add(okButton, gridBagButton);

    }

    private final class WeightTable extends TablePanel {

        private final int HEAD_SIZE = 35;
        private final int ELEMENT_SIZE = 30;
        private final Insets HEAD_MARGINS = new Insets(10, 25, 10, 25);
        private final int ROW_SEPARATION = 20;

        public WeightTable() {
            super(3);
            setHeadSize(HEAD_SIZE);
            setElementSize(ELEMENT_SIZE);
            setHeadInsets(HEAD_MARGINS);
            setRowSeparation(ROW_SEPARATION);
            setMaximizedColumn(0);
            setHeadBold();
            setHeads("Tipo", "Fecha y hora", "Valor (Kg)");
        }

        public void generateFrom(WeightInfo info) {
            clearRows();
            switch (info.getType()) {
                case SIMPLE:
                    addWeightRow("PESAJE SIMPLE", info.getWeights().get(0));
                    break;

                case TWO_PHASE:
                    WeightInfo.DatedWeight first = info.getWeights().get(0);
                    WeightInfo.DatedWeight second = info.getWeights().get(1);
                    String difference = String.valueOf(info.getWeightResume());
                    addWeightRow("PRIMER PESAJE", first);
                    addWeightRow("SEGUNDO PESAJE", second);
                    addRow("DIFERENCIA", "", difference);
                    break;

                case AXIS:
                    int counter = 1;
                    for (WeightInfo.DatedWeight datedWeight : info.getWeights()) {
                        addWeightRow("PESAJE EJE " + counter, datedWeight);
                        counter++;
                    }
                    break;
            }
            generateTable();
        }

        public void addWeightRow(String name, WeightInfo.DatedWeight datedWeight) {

            String date = CommonUtils.formatDate(datedWeight.date);
            String value = String.valueOf(datedWeight.weight);
            addRow(name, date, value);
        }

    }

    public static void main(String[] args) throws AWTException {
        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame();
            ReceiptPanel summary = new ReceiptPanel();
            WeightInfo info = new WeightInfo(SystemOperations.WeightType.AXIS);
            info.addWeight(41);
//            info.addWeight(9.12);
//            info.addWeight(19.32);
//            info.addWeight(19.32);
//            info.addWeight(19.32);
//            info.addWeight(19.32);
//            info.addWeight(19.32);
//            info.addWeight(19.32);
//            info.addWeight(19.32);
            info.finishProcess();
            System.out.println(info.getWeightResume());
            info.url = "uFsx12d";
            summary.gen(info);
            mainFrame.add(summary);
            mainFrame.pack();
            mainFrame.setVisible(true);
        });
    }
}
