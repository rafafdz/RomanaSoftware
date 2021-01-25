/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface;

import com.romana.userinterface.commonwidgets.MessageAndButtonPanel;
import com.romana.utilities.Configuration;
import java.awt.Insets;

/**
 *
 * @author rafael
 */
public class FirstPhaseFinishedPanel extends MessageAndButtonPanel{
    
    private static final String TITLE = "Primera Fase Terminada";
    private static final String MESSAGE = "Se completó el primer pesaje.</br>Usted dispone de <b>%s horas </b> para " +
            "volver a pesarse. Si se demora más perderá el pesaje.";
    private static final int INACTIVITY_TIMEOUT = 30;
    private static final String PIC_PATH = "/gui_img/time_running_512.png";
    private final Style.StyledImage picLabel = new Style.StyledImage(PIC_PATH, 320, 320);
    
    
    public FirstPhaseFinishedPanel(){
        super(INACTIVITY_TIMEOUT);
        setup();
    }
    
    private void setup(){
        setTitle(TITLE);
        setTextHtml(true);
        setTextAfterImage(false);
        setImage(picLabel);
        setTextSize(52);
        setFirstButtonLabel(new Style.StyledJLabel("Terminar", 60), new Insets(10, 20, 10, 20));
        
        int hourLimit = Configuration.getIntConfig("HOUR_LIMIT_SECOND_PHASE");
        setText(String.format(MESSAGE, hourLimit));
        finishSetup();
    }
    
    @Override
    public void firstButtonAction(){
        interfaceActions.switchPanel(PullTicketPanel.class);
    }
}
