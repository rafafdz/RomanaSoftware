/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface.commonwidgets;

import com.romana.userinterface.Style;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Configurable JTextField compatible with virtualInputDevices
 * @author rafael
 */
public final class CustomInput extends Style.RoundedPanel {

        private static final Logger LOGGER = Logger.getGlobal();
    
        private final int maxLength;
        private final JTextField inputField = new JTextField();
        private Insets innerMargins = new Insets(20, 20, 20, 20);

        public CustomInput(int maxCharLength) {
            super(20, 5); // Set border configuration
            maxLength = maxCharLength;
            initComponents();
        }
        
        public final void initComponents(){
            setLayout(new GridBagLayout());
            
            inputField.setFont(new Font("Consolas", Font.BOLD, 80));
            inputField.setBorder(new EmptyBorder(0, 0, 0, 0));
            inputField.setBackground(Style.BACKGROUND_MAIN_COLOR);
            inputField.setForeground(Style.FONT_MAIN_COLOR);

            GridBagConstraints gridBagInput = new GridBagConstraints();
            gridBagInput.fill = GridBagConstraints.HORIZONTAL;
            gridBagInput.insets = innerMargins;
            gridBagInput.weightx = 1;
            gridBagInput.weighty = 1;
            add(inputField, gridBagInput);

            AbstractDocument doc = (AbstractDocument) inputField.getDocument();
            doc.setDocumentFilter(new UppercaseDocumentFilter());
        }

        public void setInputFont(Font font) {
            inputField.setFont(font);
        }

        public void requestEntryFocus() {
            inputField.requestFocus();
        }
        
        public void setText(String text){
            inputField.setText(text);
        }
        
        public String getText(){
            return inputField.getText();
        }
        
        public void setTextCentered(){
            inputField.setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        public void addCharacter(String character){
            String actualText = getText();
            if(actualText.length() < maxLength) {
                setText(actualText + character);
            }
        }
        
        public void deleteLastCharacter(){
            String actualText = getText();
            int actualTextLength = actualText.length();
            if (actualTextLength > 0){
                String newText = actualText.substring(0, actualTextLength - 1);
                setText(newText);
            }
        }
        
        public void setInnerMargins(int top, int left, int bottom, int right){
            innerMargins = new Insets(top, left, bottom, right);
            initComponents();
        }
        
        private final class UppercaseDocumentFilter extends DocumentFilter {

            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset,
                    String text, AttributeSet attr) {
                try {
                    fb.insertString(offset, text.toUpperCase(), attr);

                } catch (BadLocationException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length,
                    String text, AttributeSet attrs) {
                try {
                    if ((offset + text.length()) <= maxLength) {
                        fb.replace(offset, length, text.toUpperCase(), attrs);
                    }
                } catch (BadLocationException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        }
    }