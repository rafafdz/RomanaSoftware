/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface.commonwidgets;

import com.romana.userinterface.Style;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Made to reuse code between Plate and AxisPanel
 *
 * @author rafael
 */
public class EntryPanel extends InteractivePanel {

    private final Style.TitleLabel titleLabel;
    private final CustomInput entryPanel;
    private int entryPadding = 50;
    private int buttonSpacing = 30; // Default values
    private VirtualInputPanel inputPanel;
    private final ErrorLabel errorLabel = new ErrorLabel("", 35);
    private static final int INACTIVITY_TIMEOUT = 60; //Go to main menu after one minute

    public EntryPanel(String title, int maxChars) {
        super(INACTIVITY_TIMEOUT);
        this.entryPanel = new CustomInput(maxChars);
        this.titleLabel = new Style.TitleLabel(title);
    }

    public void setTitleText(String text) {
        titleLabel.setText(text);
    }

    public void setEntryPadding(int padding) {
        entryPadding = padding;
    }

    public void setVirtualInput(VirtualInputPanel input) {
        inputPanel = input;
    }

    public void setButtonSpacing(int spacing) {
        buttonSpacing = spacing;
    }

    public void setEntryMargin(int top, int left, int bottom, int right) {
        entryPanel.setInnerMargins(top, left, bottom, right);
    }

    public void centerEntryText() {
        entryPanel.setTextCentered();
    }
    
    public void requestEntryFocus(){
        entryPanel.requestEntryFocus();
    }
    
    public String getEntryText(){
        return entryPanel.getText();
    }
    
    public CustomInput getEntryPanel(){
        return entryPanel;
    }

    public void finishSetup() {
        removeAll();
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagInstruction = new GridBagConstraints();
        gridBagInstruction.gridx = 1;
        gridBagInstruction.gridy = 0;
        gridBagInstruction.gridwidth = 2;
        add(titleLabel, gridBagInstruction);

        GridBagConstraints gridBagError = new GridBagConstraints();
        gridBagError.gridx = 1;
        gridBagError.gridy = 1;
        gridBagError.gridwidth = 2;
        gridBagError.weighty = 1;
        gridBagError.insets = new Insets(0, 0, 20, 0);
        gridBagError.anchor = GridBagConstraints.SOUTH;
        add(errorLabel, gridBagError);

        GridBagConstraints gridBagEntry = new GridBagConstraints();
        gridBagEntry.gridx = 1;
        gridBagEntry.gridy = 2;
        gridBagEntry.gridwidth = 2;
        //gridBagEntry.weighty = 1;
        //gridBagEntry.anchor = GridBagConstraints.SOUTH;
        gridBagEntry.insets = new Insets(0, 0, 20, 0);
        gridBagEntry.ipadx = entryPadding;
        add(entryPanel, gridBagEntry);

        ExitButton exitButton = new ExitButton();
        GridBagConstraints gridBagExit = new GridBagConstraints();
        gridBagExit.gridx = 1;
        gridBagExit.gridy = 3;
        gridBagExit.weightx = 1;
        gridBagExit.weighty = 1;
        gridBagExit.anchor = GridBagConstraints.NORTHEAST;
        gridBagExit.insets = new Insets(0, 0, 0, buttonSpacing);
        add(exitButton, gridBagExit);

        OkButton okButton = new OkButton();
        GridBagConstraints gridBagConfirm = new GridBagConstraints();
        gridBagConfirm.gridx = 2;
        gridBagConfirm.gridy = 3;
        gridBagConfirm.weightx = 1;
        gridBagConfirm.weighty = 1;
        gridBagConfirm.anchor = GridBagConstraints.NORTHWEST;
        gridBagConfirm.insets = new Insets(0, buttonSpacing, 0, 0);
        add(okButton, gridBagConfirm);

        if (inputPanel != null) {
            GridBagConstraints gridBagKeyboard = new GridBagConstraints();
            gridBagKeyboard.gridx = 1;
            gridBagKeyboard.gridy = 4;
            gridBagKeyboard.gridwidth = 2;
            add(inputPanel, gridBagKeyboard);
        }
        addHorizontalStretch(0);
        addHorizontalStretch(3);
    }
    
    @Override
    public void timeoutAction(){
        clearErrorAndEntry();
        systemActions.returnToMainMenu();
    }


    public void setError(String errorText) {
        errorLabel.showError(errorText);
    }

    public void clearErrorAndEntry(){
        errorLabel.clearError();
        entryPanel.setText("");
    }

    public void getEntryFocus() {
        System.out.println("Pidiendo focus desde EDT? ->" + SwingUtilities.isEventDispatchThread());
        entryPanel.requestEntryFocus();
    }

    public void okAction() {
    }

    ;
    
    public void backAction() {
    }

    ;
    
    private class OkButton extends Buttons.ConfirmButton {

        private static final int IMAGE_SIZE = 70;
        private static final int MARGIN = 12;

        public OkButton() {
            super(IMAGE_SIZE, MARGIN);
        }

        @Override
        public void clickAction(MouseEvent e) {
            
            if (getEntryText().equals("")) {
                setError("Ingrese información");
            } else {
                okAction();
            }
        }
    }

    private class ExitButton extends Buttons.QuitButton {

        private static final int IMAGE_SIZE = 60;
        private static final int MARGIN = 17;

        public ExitButton() {
            super(IMAGE_SIZE, MARGIN);
        }

        @Override
        public void clickAction(MouseEvent e) {
            clearErrorAndEntry();
            systemActions.returnToMainMenu();
            backAction(); // To do: See if this is used!
        }
    }

    public static void main(String[] args) throws AWTException {
        JFrame mainFrame = new JFrame();
        EntryPanel entry = new EntryPanel("Aca probando", 3);

        // Input panel
        VirtualInputPanel inputPanel = new Keyboard(entry.getEntryPanel());
        entry.setVirtualInput(inputPanel);
        entry.setButtonSpacing(2);
        entry.finishSetup();

        mainFrame.add(entry);
        mainFrame.pack();
        mainFrame.setVisible(true);
        entry.setError("Holaa");
    }

}
