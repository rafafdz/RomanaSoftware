package com.romana.userinterface;

import com.romana.devices.WeightInfo;
import com.romana.userinterface.commonwidgets.CustomInput;
import com.romana.userinterface.commonwidgets.EntryPanel;
import com.romana.userinterface.commonwidgets.Numpad;
import java.awt.AWTException;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author rafael
 */
public class AxisEntryPanel extends EntryPanel{
    
    private static final Logger LOGGER = Logger.getGlobal();
    private static final String TITLE = "Ingrese cuantos ejes quiere pesar";
    
    public AxisEntryPanel(){
        super(TITLE, 1); // Title and max characters
        setup();
    }
    
    private void setup(){
        CustomInput entryPanel = getEntryPanel();
        Numpad numpad = new Numpad(entryPanel);
        setVirtualInput(numpad);
        
        setButtonSpacing(5);
        setEntryPadding(160);
        centerEntryText();
        finishSetup();
    }
    
    @Override
    public void okAction(){
        int axisNumber = Integer.valueOf(getEntryText());
        WeightInfo actualWeight = systemActions.getActualWeightInfo();
        actualWeight.setAxisNumber(axisNumber);
        
        interfaceActions.switchPanel(SummaryPanel.class);
        clearErrorAndEntry();
    }
    
    public static void main(String[] args) throws AWTException {
        JFrame mainFrame = new JFrame();
        
        AxisEntryPanel panel = new AxisEntryPanel();
        
        mainFrame.add(panel);
        mainFrame.pack();
        mainFrame.setVisible(true);
        //entry.setError("Holaa");
    }
}