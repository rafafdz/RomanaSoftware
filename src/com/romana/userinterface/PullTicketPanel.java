/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface;

import com.romana.devices.SerialException;
import com.romana.devices.SystemOperations;
import com.romana.devices.WeightInfo;
import com.romana.userinterface.commonwidgets.Buttons;
import com.romana.userinterface.commonwidgets.MessageAndButtonPanel;
import com.romana.utilities.CommonUtils;
import com.romana.utilities.Configuration;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rafael
 */
public class PullTicketPanel extends MessageAndButtonPanel {

    private static final Logger LOGGER = Logger.getLogger(PullTicketPanel.class.getName());
    private static final String TITLE = "Retire su ticket";
    private static final String MESSAGE = "Para sacarlo, tírelo hacia usted. No lo tire hacia arriba";
    private static final String PIC_PATH = "/gui_img/ticket_512.png";
    private static final int INACTIVITY_TIMEOUT = 30;
    private final Style.StyledImage picLabel = new Style.StyledImage(PIC_PATH, 350, 350);
    private Style.RoundedButton okButton;

    public PullTicketPanel() {
        super(INACTIVITY_TIMEOUT);
        setup();
    }

    private void setup() {
        setTitle(TITLE);
        setText(MESSAGE);
        setImage(picLabel);
        setTextHtml(true);
        setTextSize(45);
        setImageSpacing(30);
        setSideSpacing(350);

        okButton = new Buttons.ConfirmButton(80, 10) {
            @Override
            public void clickAction(MouseEvent e) {
                systemActions.returnToMainMenu();
            }
        };
        setFirstButton(okButton);
        finishSetup();
    }
   
    
    private void setContinueEnabled(boolean enabled){
        Style.RoundedButton button = getFirstButton();
        button.setEnabled(enabled);
    }
    
    @Override
    public void actionOnShow() {
        setContinueEnabled(false);
        new PrintTicketWorker().execute();
    }
    
    @Override
    public void firstButtonAction() {
        systemActions.returnToMainMenu();
    }
    
    private class PrintTicketWorker extends OperationSwingWorker<Boolean, Object> {

        public PrintTicketWorker() {
            super(interfaceActions);
        }

        @Override
        protected Boolean doInBackground() throws SerialException {
            LOGGER.log(Level.FINE, "Started PrintTicket worker");

            SystemOperations systemOps = systemActions.getSystemOperations();
            WeightInfo actualWeight = systemActions.getActualWeightInfo();
            
            String plate = actualWeight.getPlate();
            String url = Configuration.getStringConfig("WEB_DOMAIN") + "/" + actualWeight.getUrl();
            int totalPrice = actualWeight.getTotalPrice();
            
            switch (actualWeight.getType()) {
                case SIMPLE:
                    int weight = actualWeight.getFirstWeight();
                    return systemOps.printSimpleTicket(plate, url, totalPrice, weight);
                    
                case TWO_PHASE:
                    int firstWeight = actualWeight.getFirstWeight();
                    int lastWeight = actualWeight.getSecondWeight();
                    Date firstDate = actualWeight.getFirstWeightDate();
                    Date lastDate = actualWeight.getSecondWeightDate();
                    String firstDateFormatted = CommonUtils.formatDate(firstDate);
                    String lastDateFormatted = CommonUtils.formatDate(lastDate);
             
                    return systemOps.printTwoPhaseTicket(plate, url, totalPrice, firstWeight, 
                            lastWeight, firstDateFormatted, lastDateFormatted);
                    
                case AXIS:
                    return true; // TO DO!
            }
            return false;
        }

        // Executed in the EDT
        @Override
        protected void doneAndCatch() throws InterruptedException, ExecutionException { // To do: Throw exceptions
            boolean success = get();

            if (!success){
                LOGGER.log(Level.WARNING, "Ticket Print Failed");
                return;
            }
            
            LOGGER.log(Level.INFO, "Ticket printed Succesfully");
            setContinueEnabled(true);
        }

    }
    
    
    
}
