/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface;

import com.romana.devices.SystemOperations;
import com.romana.devices.SystemOperations.WeightType;
import com.romana.devices.WeightInfo;
import com.romana.userinterface.commonwidgets.CustomInput;
import com.romana.userinterface.commonwidgets.EntryPanel;
import com.romana.userinterface.commonwidgets.Keyboard;
import java.awt.AWTException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author rafael
 */
public class ManualWeightInputPanel extends EntryPanel {

    private static final Logger LOGGER = Logger.getLogger(ManualWeightInputPanel.class.getName());
    private static final String TITLE = "Ingresa el pesaje manualmente";

    public ManualWeightInputPanel() {
        super(TITLE, 6);
        setup();
    }

    private void setup() {
        CustomInput entryPanel = getEntryPanel();
        Keyboard keyboard = new Keyboard(entryPanel);
        setVirtualInput(keyboard);
        

        setButtonSpacing(20);
        setEntryPadding(400);
        finishSetup();
    }
    
    @Override
    public void actionOnShow(){
        getEntryPanel().setText("");
    }
    

    @Override
    public void okAction() {
        String weightInput = getEntryText();
        if (isValidWeight(weightInput)){
            
            LOGGER.log(Level.INFO, "Manual weight accepted {0}", weightInput);
            
            systemActions.getActualWeightInfo().setPreWeight(Integer.valueOf(weightInput));
            interfaceActions.switchPanel(ConfirmManualWeightPanel.class);
            clearErrorAndEntry();
            
        } else {
            setError("Ingrese un pesaje válido");
        }
        
    }
    
    private boolean isValidWeight(String weightString) {
        return weightString.matches("^[0-9]*$") && weightString.length() > 2;
    }

    public static void main(String[] args) throws AWTException {
        JFrame mainFrame = new JFrame();

        mainFrame.add(new ManualWeightInputPanel());
        mainFrame.pack();
        mainFrame.setVisible(true);
        //entry.setError("Holaa");
    }
}
