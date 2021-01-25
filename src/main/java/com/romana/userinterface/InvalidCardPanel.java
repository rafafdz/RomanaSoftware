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
public class InvalidCardPanel extends MessageTwoButton {
    
    private static final String TITLE = "Tarjeta inválida";
    private static final String MESSAGE = "La tarjeta ingresada no se la misma que se utilizó "
            + "para hacer el primer pesaje de este vehículo. Use la tarjeta ingresada antes para "
            + "continauar con la segunda fase del pesaje.";
    private static final int INACTIVITY_TIMEOUT = 20;
    
    
    public InvalidCardPanel(){
        super(INACTIVITY_TIMEOUT);
        setup();
    }
    
    private void setup(){
        setTitle(TITLE);
        setText(MESSAGE);
        setButtonText("Reintentar");
        finishSetup();
    }
    
    @Override
    public void timeoutAction(){
        systemActions.returnToMainMenu();
    }
    
    @Override
    public void quitAction(){
        timeoutAction();
    }
    
    @Override
    public void buttonAction(){
        interfaceActions.switchPanel(CardReadPanel.class);
    }
}
