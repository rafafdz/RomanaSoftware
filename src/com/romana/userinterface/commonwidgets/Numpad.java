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
public class Numpad extends VirtualInputPanel {

        private final int buttonSpacing = 10;

        public Numpad(CustomInput inputPanel){
            super(inputPanel, 110); //keySize
            initComponents();
        }

        private void initComponents() {
            setKeyTextFont(Style.NUMPAD_FONT);
            setLayout(new GridLayout(3, 3, buttonSpacing, buttonSpacing));

            for (int i = 2; i < 10; i++) {
                StyledCharacterKey newKey = new StyledCharacterKey(String.valueOf(i));
                add(newKey);
            }
            add(new StyledBackspaceKey());
        }
    }