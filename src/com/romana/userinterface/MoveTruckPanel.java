/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface;

import com.romana.devices.WeightInfo;
import com.romana.userinterface.commonwidgets.MessageTwoButton;
import javax.swing.JFrame;

/**
 *
 * @author Rafa
 */
public class MoveTruckPanel extends MessageTwoButton {

    private static final int INACTIVITY_TIMEOUT = 80;
    private static final String PIC_PATH = "/gui_img/cargo_truck_512.png";
    private final Style.StyledImage image = new Style.StyledImage(PIC_PATH, 400, 400);
    private final static String TITLE = "Mueva su camión";
    private static final String MESSAGE = "Mueva su camión a la %s y cuando esté "
            + "listo presione 'Pesar'";

    private static final String[] AXIS = {"PRIMER", "SEGUNDO", "TERCER", "CUARTO", "QUINTO",
        "SEXTO", "SÉPTIMO", "OCTAVO", "NOVENO"};

    public MoveTruckPanel() {
        super(INACTIVITY_TIMEOUT);
        setup();
    }

    private void setup() {
        setTitle(TITLE);
        setImage(image);
        setButtonText("Pesar");
        finishSetup();
    }

    @Override
    public void actionOnShow() {
        WeightInfo info = systemActions.getActualWeightInfo();
        switch (info.getType()) {
            case AXIS:
                int nextAxis = info.getNextAxis();
                setAxisText(nextAxis);
                break;

            default:
                setFullWeightText();
                break;
        }
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
        interfaceActions.switchPanel(WeighingPanel.class);
    }

    private void setFullWeightText() {
        setText(String.format(MESSAGE, "romana"));
    }

    public void setAxisText(int axisNumberMinusOne) {
        String text = "posición del " + AXIS[axisNumberMinusOne] + " EJE";
        setText(String.format(MESSAGE, text));

    }

    public static void main(String[] args) {
        JFrame mainFrame = new JFrame();
        MoveTruckPanel entry = new MoveTruckPanel();
        entry.setAxisText(7);

        mainFrame.add(entry);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}
