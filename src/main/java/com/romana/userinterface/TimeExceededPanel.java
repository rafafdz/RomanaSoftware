/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface;

import com.romana.devices.SystemOperations;
import com.romana.devices.WeightInfo;
import com.romana.userinterface.commonwidgets.MessageAndButtonPanel;
import com.romana.utilities.CommonUtils;
import com.romana.utilities.Configuration;
import java.awt.Insets;
import java.util.Date;

/**
 *
 * @author rafael
 */
public class TimeExceededPanel extends MessageAndButtonPanel {

    private static final String TITLE = "Tiempo Agotado";
    private static final int INACTIVITY_TIMEOUT = 30;
    private static final String PIC_PATH = "/gui_img/time_up_512.png";
    private static final String TEXT_TEMPLATE = "Usted tenia %s horas para realizar el segundo pesaje." + 
            "<br/>Usted se ha atrasado por <b>%s horas, %s minutos y %s segundos</b>, por lo " +
            " que no se puede terminar el pesaje";
    private final Style.StyledImage picLabel = new Style.StyledImage(PIC_PATH, 300, 300);

    public TimeExceededPanel() {
        super(INACTIVITY_TIMEOUT);
        setup();
    }

    private void setup() {
        setTitle(TITLE);
        setImage(picLabel);
        setTextHtml(true);
        setTextSize(50);
        setFirstButtonLabel(new Style.StyledJLabel("Salir", 60), new Insets(10, 20, 10, 20));
        finishSetup();
    }
    
    
    @Override
    public void actionOnShow(){
        WeightInfo actualWeight = systemActions.getActualWeightInfo();
        
        Date firstDate = actualWeight.getFirstWeightDate();
        Date secondDate = new Date();
        
        int timeLimit = Configuration.getIntConfig("HOUR_LIMIT_SECOND_PHASE");
        long hours = CommonUtils.hourDifferenceDates(firstDate, secondDate) - timeLimit;
        long minutes = (CommonUtils.minuteDifferenceDates(firstDate, secondDate) - timeLimit * 60 - 
                hours * 60);
        long seconds = (CommonUtils.secondDifferenceDates(firstDate, secondDate) - timeLimit * 60 *
                60 - minutes * 60);
        setText(String.format(TEXT_TEMPLATE, timeLimit, hours, minutes, seconds));
    }
    

    @Override
    public void firstButtonAction() {
        SystemOperations sysOps = systemActions.getSystemOperations();
        WeightInfo actualWeightInfo = systemActions.getActualWeightInfo();
        sysOps.discardTwoPhase(actualWeightInfo);
        systemActions.returnToMainMenu();
    }

    @Override
    public void timeoutAction() {
        firstButtonAction();
    }
}
