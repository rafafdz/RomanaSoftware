/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface;

import com.romana.devices.WeightInfo;
import com.romana.userinterface.commonwidgets.MessageTwoButton;
import java.text.DecimalFormat;
import javax.swing.JFrame;

/**
 *
 * @author Rafa
 */
public class ConfirmManualWeightPanel extends MessageTwoButton {

    private static final int INACTIVITY_TIMEOUT = 60;
    private static final String PIC_PATH = "/gui_img/cargo_truck_512.png";
    private final Style.StyledImage image = new Style.StyledImage(PIC_PATH, 400, 400);
    private final static String TITLE = "Confirmar Pesaje Manual";
    private static final String MESSAGE = "¿Esta seguro de ingresar el pesaje de \n %s?";
    public final DecimalFormat decimalFormat;

    public ConfirmManualWeightPanel() {
        super(INACTIVITY_TIMEOUT);
        setup();
        decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);
    }

    private void setup() {
        setTitle(TITLE);
        setButtonText("OK");
        finishSetup();
    }

    @Override
    public void actionOnShow() {
        WeightInfo info = systemActions.getActualWeightInfo();
        int preWeight = info.getPreWeight();
        String preWeightString = decimalFormat.format(preWeight).replace(',', '.');
        setText(String.format(MESSAGE, preWeightString));
    }

    @Override
    public void timeoutAction() {
        systemActions.returnToMainMenu();
    }

    @Override
    public void quitAction() {
        timeoutAction();
    }

    @Override
    public void buttonAction() {
        int weight = systemActions.getActualWeightInfo().getPreWeight();
        // Setear el peso en scale device
        systemActions.setManualWeight(weight);
        interfaceActions.switchPanel(WeighingPanel.class);
    }

    public static void main(String[] args) {
        JFrame mainFrame = new JFrame();
        ConfirmManualWeightPanel entry = new ConfirmManualWeightPanel();
//        System.out.println(entry.decimalFormat.format(1234567));
//        entry.setWeightToShow(7);
        entry.actionOnShow();
        mainFrame.add(entry);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}
