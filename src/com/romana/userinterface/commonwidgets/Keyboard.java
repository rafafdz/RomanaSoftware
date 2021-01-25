/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface.commonwidgets;

import com.romana.userinterface.Style;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 *
 * @author rafael
 */
public class Keyboard extends VirtualInputPanel {

        private final int colSpacing = 5;
        private final int rowSpacing = 10;

        private final String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        private final String[] firstLetters = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"};
        private final String[] secondLetters = {"A", "S", "D", "F", "G", "H", "J", "K", "L"};
        private final String[] thirdLetters = {"Z", "X", "C", "V", "B", "N", "M"};

        public Keyboard(CustomInput inputPanel){
            super(inputPanel, 90);
            initComponents();
        }

        private void initComponents() {
            setLayout(new GridBagLayout());

            Style.StyledJPanel numberKeys = generateButtonRow(numbers);
            Style.StyledJPanel numberRow = addHorizontalSpaces(numberKeys, 0, 0);
            GridBagConstraints gridBagNumbers = new GridBagConstraints();
            gridBagNumbers.gridy = 0;
            gridBagNumbers.insets = new Insets(0, 0, rowSpacing, 0);
            add(numberRow, gridBagNumbers);

            Style.StyledJPanel firstRowKeys = generateButtonRow(firstLetters);
            Style.StyledJPanel firstRow = addHorizontalSpaces(firstRowKeys, 0, 0);
            GridBagConstraints gridBagFirst = new GridBagConstraints();
            gridBagFirst.gridy = 1;
            gridBagFirst.insets = new Insets(0, 0, rowSpacing, 0);
            add(firstRow, gridBagFirst);

            Style.StyledJPanel secondRowKeys = generateButtonRow(secondLetters);
            Style.StyledJPanel secondRow = addHorizontalSpaces(secondRowKeys, 30, 30);
            GridBagConstraints gridBagSecond = new GridBagConstraints();
            gridBagSecond.gridy = 2;
            gridBagSecond.insets = new Insets(0, 0, rowSpacing, 0);
            add(secondRow, gridBagSecond);

            Style.StyledJPanel thirdRowKeys = generateButtonRow(thirdLetters);

            StyledBackspaceKey backspace = new StyledBackspaceKey();

            GridBagConstraints gridBagBack = new GridBagConstraints();
            gridBagBack.gridx = thirdLetters.length;
            gridBagBack.insets = new Insets(0, colSpacing, 0, colSpacing);

            thirdRowKeys.add(backspace, gridBagBack);

            Style.StyledJPanel thirdRow = addHorizontalSpaces(thirdRowKeys, 60, 60);
            GridBagConstraints gridBagThird = new GridBagConstraints();
            gridBagThird.gridy = 3;
            gridBagThird.insets = new Insets(0, 0, rowSpacing, 0);
            add(thirdRow, gridBagThird);
        }

        // To do: delete this, is not useful
        private Style.StyledJPanel addHorizontalSpaces(Style.StyledJPanel keyPanel,
                int leftPadding,
                int rightPadding) {

            Style.StyledJPanel panel = new Style.StyledJPanel(new GridBagLayout());
            GridBagConstraints gridBagLeft = new GridBagConstraints();
            gridBagLeft.gridx = 0;
            gridBagLeft.ipadx = leftPadding;
            gridBagLeft.weightx = 1;
            panel.add(new Style.StyledJPanel(), gridBagLeft);

            GridBagConstraints gridBagPanel = new GridBagConstraints();
            gridBagPanel.gridx = 1;
            panel.add(keyPanel, gridBagPanel);

            GridBagConstraints gridBagRight = new GridBagConstraints();
            gridBagRight.gridx = 2;
            gridBagRight.ipadx = rightPadding;
            gridBagRight.weightx = 1;
            panel.add(new Style.StyledJPanel(), gridBagRight);
            return panel;

        }

        private Style.StyledJPanel generateButtonRow(String[] letters) {
            Style.StyledJPanel container = new Style.StyledJPanel(new GridBagLayout());

            int count = 0;
            for (String letter : letters) {
                StyledCharacterKey newKey = new StyledCharacterKey(letter);

                GridBagConstraints gridBagLetter = new GridBagConstraints();
                gridBagLetter.gridx = count;
                gridBagLetter.insets = new Insets(0, colSpacing, 0, colSpacing);

                container.add(newKey, gridBagLetter);
                count++;
            }
            return container;
        }
    }
