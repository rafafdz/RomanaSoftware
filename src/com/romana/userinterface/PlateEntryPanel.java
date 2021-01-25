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
public class PlateEntryPanel extends EntryPanel {

    private static final Logger LOGGER = Logger.getLogger(PlateEntryPanel.class.getName());
    private static final String TITLE = "Ingrese su patente";

    public PlateEntryPanel() {
        super(TITLE, 7);
        setup();
    }

    private void setup() {
        CustomInput entryPanel = getEntryPanel();
        Keyboard keyboard = new Keyboard(entryPanel);
        setVirtualInput(keyboard);
        

        setButtonSpacing(20);
        setEntryPadding(330);
        finishSetup();
    }
    
    @Override
    public void actionOnShow(){
        getEntryPanel().setText(""); // For developing only!
    }
    

    @Override
    public void okAction() {
        String actualPlate = getEntryText();
        if (isValidPlate(actualPlate)){
            
            LOGGER.log(Level.INFO, "Plate accepted {0}", actualPlate);
            WeightInfo actualWeight = systemActions.getActualWeightInfo();
            actualWeight.setPlate(actualPlate);
            
            interfaceActions.getHeader().setSubtitleText("Patente " + actualPlate, 20);
            
            if (actualWeight.getType() == WeightType.AXIS){
                interfaceActions.switchPanel(AxisEntryPanel.class);
                interfaceActions.getAxisEntryFocus();
                return;
            }
            
            if (actualWeight.getType() == WeightType.TWO_PHASE){
                SystemOperations sysOps = systemActions.getSystemOperations();
                
                if (sysOps.hasOngoingTwoPhase(actualPlate)){
                    WeightInfo loaded = sysOps.getOngoingWeightInfo(actualPlate);
                    
                    LOGGER.log(Level.INFO, "Loading first phase info for plate {0}", actualPlate);
                    systemActions.setActualWeightInfo(loaded);
                }
            }
            interfaceActions.switchPanel(SummaryPanel.class);
            clearErrorAndEntry();
            
        } else {
            setError("Ingrese una patente válida");
        }
        
    }
    
    private boolean isValidPlate(String plate) {
        String onlyConsonants = "bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ";
        String oldChilean = String.format("[a-zA-Z]{2}\\d{4}");
        String Chilean = String.format("[%s]{4}\\d{2}", onlyConsonants);
        String oldArgentinian = "\\w{2}\\d{3}\\w{2}";
        String Argentinian = String.format("[%s]{3}\\d{3}", onlyConsonants);

        return (plate.matches(oldChilean) || plate.matches(Chilean)
                || plate.matches(oldArgentinian) || plate.matches(Argentinian));
    }

    public static void main(String[] args) throws AWTException {
        JFrame mainFrame = new JFrame();

        mainFrame.add(new PlateEntryPanel());
        mainFrame.pack();
        mainFrame.setVisible(true);
        //entry.setError("Holaa");
    }
}
