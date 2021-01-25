/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface.commonwidgets;

import com.romana.userinterface.Style;
import java.awt.GridLayout;

/**
 *
 * @author rafael
 */
public class AxisNumpad extends VirtualInputPanel {

        private final int buttonSpacing = 10;

        public AxisNumpad(CustomInput inputPanel){
            super(inputPanel, 110); //keySize
            initComponents();
        }

        private void initComponents() {
            setKeyTextFont(Style.NUMPAD_FONT);
            setLayout(new GridLayout(3, 3, buttonSpacing, buttonSpacing));

            for (int i = 1; i < 9; i++) {
                StyledCharacterKey newKey = new StyledCharacterKey(String.valueOf(i));
                add(newKey);
            }
            add(new StyledBackspaceKey());
        }
    }