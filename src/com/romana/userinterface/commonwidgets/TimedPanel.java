/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface.commonwidgets;

import com.romana.userinterface.Style;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 *
 * @author rafael
 */
public class TimedPanel extends Style.StyledJPanel {

    private static final Logger LOGGER = Logger.getLogger(TimedPanel.class.getName());
    private ActionListener timeoutAction = (ActionEvent e) -> {
    };
    private Integer timeout = 100000;
    private Timer timer;
    private boolean timerEnabled = false;

    public TimedPanel() {
    }

    public TimedPanel(Integer timeoutSeconds) {
        setupTimer(timeoutSeconds);
    }

    public final void setupTimer(int seconds) {
        timerEnabled = true;
        timeout = seconds * 1000;
        
        String logMsg = String.format("Executing action after timeout of %s millis", timeout);
        this.timeoutAction = (ActionEvent e) -> {
            LOGGER.log(Level.INFO, logMsg);
            timeoutAction();
        };
        timer = new Timer(timeout, timeoutAction);
        timer.setRepeats(false);
    }

    public void startTimer() {
        if (timeout != null) {
            timer.start();
        }
    }
    
    public void restartTimer(){
        if (timerEnabled){
            LOGGER.log(Level.FINEST, "Timer of {0} restarted", getClass().getName());
            timer.restart();
        }
    }

    public void stopTimer() {
        if (timerEnabled){
            LOGGER.log(Level.FINEST, "Timer of {0} stopped", getClass().getName());
            timer.stop();
        }
    }

    public boolean hasTimer() {
        return timer != null;
    }

    // To be overriden!
    public void timeoutAction() {
    }

    ;
}
