/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface.commonwidgets;

import com.romana.userinterface.UserInterface.InterfaceActions;
import com.romana.userinterface.UserInterface.SystemActions;

/**
 *
 * @author rafael
 */
public class InteractivePanel extends TimedPanel {

    protected InterfaceActions interfaceActions;
    protected SystemActions systemActions;

    public InteractivePanel(int timeoutSeconds) {
        super(timeoutSeconds);
    }

    public InteractivePanel() {
    }

    ;
    
    public void setInterfaceActions(InterfaceActions actions) {
        interfaceActions = actions;
    }

    public void setSystemActions(SystemActions systemActions) {
        this.systemActions = systemActions;
    }
    
    public final void showAction(){
        if (hasTimer()){
            restartTimer();
        }
        actionOnShow();
    }
    
    public final void hideAction(){
        if (hasTimer()){
            stopTimer();
        }
        actionOnHide();
    }
    
    public final void ShowDebugAction(){
        if (hasTimer()){
            stopTimer(); // In debug mode there is no timeout!
        }
        actionOnShowDebug();
    }    
    
    /**
     * To be Overriden. Executes actions when panel is shown.
     */
    public void actionOnShow(){};
    
    /**
     * Executed instead of actionOnShow when debug mode is set. By default calls actionOnShow
     */
    public void actionOnShowDebug(){
        actionOnShow();
    }
    
    
    /**
     * Analog to actionOnShow. Executed when changing from this panel to another
     */
    public void actionOnHide(){};
    
    
    
    
}
