/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface.commonwidgets;

import com.romana.userinterface.Style;

/**
 * Simply sets up a StyledJLabel with ErrorColor
 *
 * @author rafael
 */
public class ErrorLabel extends Style.StyledJLabel {

    public ErrorLabel() {
        setup();
    }

    public ErrorLabel(int fontSize) {
        super(fontSize);
        setup();
    }

    public ErrorLabel(String text) {
        super(text);
        setup();
    }

    public ErrorLabel(String text, int fontSize) {
        super(text, fontSize);
        setup();
    }

    private void setup() {
        setForeground(Style.ERROR_COLOR);
    }

    public void showError(String text) {
        setText(text);
    }
    

    public void clearError() {
        setText("");
    }
}
