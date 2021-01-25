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
public class NoCardPanel extends MessageTwoButton {
    
    private static final String TITLE = "Tarjeta no Detectada";
    private static final String MESSAGE = "No se acercó ninguna tarjeta.";
    private static final int INACTIVITY_TIMEOUT = 10;
    
    
    public NoCardPanel(){
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
    public void quitAction(){
        systemActions.returnToMainMenu();
    }
    
    @Override
    public void buttonAction(){
        interfaceActions.switchPanel(CardReadPanel.class);
    }
    
    
}
