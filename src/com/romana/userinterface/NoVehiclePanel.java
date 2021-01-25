/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface;

import com.romana.userinterface.commonwidgets.MessageTwoButton;

/**
 *
 * @author rafael
 */
public class NoVehiclePanel extends MessageTwoButton{
    
    private static final String TITLE = "Vehículo no Detectado";
    private static final String MESSAGE = "Se detectó un peso muy bajo, lo que significa que no "
            + "ha movido su vehiculo. Pongalo sobre la romana y presione 'Continuar'";
    private static final int INACTIVITY_TIMEOUT = 120;
    
    
    
    
    public NoVehiclePanel(){
        super(INACTIVITY_TIMEOUT);
        setup();
    }
    
    private void setup(){
        setTitle(TITLE);
        setText(MESSAGE);
        setButtonText("Continuar");
        finishSetup();
    }
    
    @Override
    public void quitAction(){
        systemActions.returnToMainMenu();
    }
    
    @Override
    public void buttonAction(){
        interfaceActions.switchPanel(WeighingPanel.class);
    }
}
