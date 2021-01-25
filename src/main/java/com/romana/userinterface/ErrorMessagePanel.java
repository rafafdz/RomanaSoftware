/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface;

import com.romana.userinterface.commonwidgets.MessageAndButtonPanel;
import java.awt.Color;
import java.awt.Insets;
import javax.swing.JFrame;

/**
 * Used to show when there are technical problems and the service has to stop.
 *
 * @author rafael
 */
public class ErrorMessagePanel extends MessageAndButtonPanel {
    
    private static final String TITLE = "Error en el Sistema";
    private static final String MESSAGE = "%s\nHa ocurrido un error en el sistema. No se "
            + "le ha cobrado por el servicio. Consulte al operador para que solucione el problema.";
    private final static int TEXT_SIZE = 70;
    private final static int SIDE_SPACING = 300;
    private final static int BOTTOM_SPACING = 50;
    private final static Style.StyledJLabel BUTTON_LABEL = new Style.StyledJLabel("OK", 40);
    
    private ErrorType actualError;

    public ErrorMessagePanel() {
        super(Style.ERROR_MSG_TIMEOUT);
        initComponents();
    }

    private void initComponents() {
        setTextHtml(true);
        setTitle(TITLE);
        setFirstButtonLabel(BUTTON_LABEL, new Insets(20, 20, 20, 20));
        setTextContainerBorderWidth(2);
        setTextSize(TEXT_SIZE);
        setTextColor(Color.RED);
        setSideSpacing(SIDE_SPACING);
        setBottomSpacing(BOTTOM_SPACING);
        finishSetup();
    }
    
    public void setError(ErrorType error) {
        actualError = error;
        setText(String.format(MESSAGE, error.toString()));
    }

    public ErrorType getError() {
        return actualError;
    }
    
    public void cleanError(){
        actualError = null;
        setText("");
    }
    
    @Override
    public void timeoutAction(){
        systemActions.returnToMainMenu();
    }
    
    @Override
    public void firstButtonAction(){
        timeoutAction();
    }

    public static enum ErrorType {
        SERIAL_COM_ERROR,
        INTERRUPED_ERROR, 
        DATABASE_ERROR,
        UNKNOWN_ERROR
    }
    
    public static void main(String[] args) {
        JFrame mainFrame = new JFrame();
        ErrorMessagePanel entry = new ErrorMessagePanel();

        entry.setError(ErrorType.UNKNOWN_ERROR);
        mainFrame.add(entry);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    
}
