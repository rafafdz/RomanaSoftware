/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface;

import com.romana.devices.SystemOperations.WeightType;
import com.romana.devices.WeightInfo;
import com.romana.userinterface.commonwidgets.InteractivePanel;
import com.romana.utilities.Configuration;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Rafa
 */
public class TypePanel extends InteractivePanel {

    private static final Logger LOGGER = Logger.getLogger(TypePanel.class.getName());

    private final int SIDE_SPACING = 200;
    private final Style.StyledJLabel versionLabel = new Style.StyledJLabel(20);

    public TypePanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        String[] titles = {"Pesaje Simple", "Pesaje de Dos Fases", "Pesaje Por Ejes"};
        String[] descriptions = {"Se entrega el peso actual del camión.",
            "Se pesa en dos veces distintas y luego se entrega la diferencia.",
            "Varios pesajes en distintos ejes del camión."
        };
        WeightType[] types = {WeightType.SIMPLE, WeightType.TWO_PHASE, WeightType.AXIS};

        for (int i = 0; i < types.length; i++) {
            String number = String.valueOf(i + 1);
            String title = titles[i];
            String description = descriptions[i];
            WeightType type = types[i];
            OptionPanel optionPanel = new OptionPanel(number, title, description, type);
            GridBagConstraints gridBagOption = new GridBagConstraints();
            gridBagOption.gridy = i;
            gridBagOption.fill = GridBagConstraints.HORIZONTAL;
            gridBagOption.insets = new Insets(30, SIDE_SPACING, 0, SIDE_SPACING);
            gridBagOption.weightx = 1;
            gridBagOption.weighty = 1;
            add(optionPanel, gridBagOption);
        }
               
        GridBagConstraints gridBagVersion = new GridBagConstraints();
        gridBagVersion.gridy = types.length;
        gridBagVersion.anchor = GridBagConstraints.EAST;
        add(versionLabel, gridBagVersion);
    }

    @Override
    public void actionOnShow() {
        interfaceActions.getHeader().setTitleText("Bienvenido", 60);
    }
    
    @Override
    public void actionOnShowDebug(){
        super.actionOnShowDebug();
        versionLabel.setText("VERSION: " + Configuration.SOFTWARE_VERSION);
    }

    private class NumberPanel extends Style.RoundedPanel {

        private final String text;
        private Style.StyledJLabel textLabel;

        private NumberPanel(String text) {
            super(25, 4);
            this.text = text;
            initComponents();
        }

        private void initComponents() {
            setOpaque(false);
            setBackground(Color.GRAY);
            textLabel = new Style.StyledJLabel(text);
            textLabel.setFont(Style.TYPE_NUMBER_FONT);
            textLabel.setBorder(new EmptyBorder(0, 17, 0, 17));
            setLayout(new BorderLayout());
            add(textLabel);
        }
    }

    private class OptionPanel extends Style.RoundedButton {

        private final Style.StyledJLabel optionTitle;
        private final Style.StyledJLabel optionDescription;
        private final NumberPanel numberPanel;
        private final WeightType weightType;
        private final String rawTitle;

        private OptionPanel(String number, String title, String description, WeightType type) {
            super();
            rawTitle = title;
            numberPanel = new NumberPanel(number);
            optionTitle = new Style.StyledJLabel(title, 60);
            optionTitle.setHtml(true);
            optionTitle.setFont(optionTitle.getFont().deriveFont(Font.BOLD));
            optionDescription = new Style.StyledJLabel(description, 30);
            optionDescription.setHtml(true);
            weightType = type;
            initComponents();
        }

        private void initComponents() {
            setOpaque(false);
            setLayout(new GridBagLayout());

            GridBagConstraints gridBagNumber = new GridBagConstraints();
            gridBagNumber.gridx = 0;
            gridBagNumber.gridy = 0;
            gridBagNumber.gridheight = 2;
            gridBagNumber.insets = new Insets(15, 20, 15, 15);
            gridBagNumber.fill = GridBagConstraints.HORIZONTAL;
            gridBagNumber.anchor = GridBagConstraints.WEST;
            add(numberPanel, gridBagNumber);

            GridBagConstraints gridBagTitle = new GridBagConstraints();
            gridBagTitle.gridx = 1;
            gridBagTitle.gridy = 0;
            gridBagTitle.insets = new Insets(15, 5, 0, 5);
            gridBagTitle.fill = GridBagConstraints.HORIZONTAL;
            gridBagTitle.anchor = GridBagConstraints.WEST;
            add(optionTitle, gridBagTitle);

            GridBagConstraints gridBagDescription = new GridBagConstraints();
            gridBagDescription.gridx = 1;
            gridBagDescription.gridy = 1;
            gridBagDescription.insets = new Insets(0, 5, 15, 5);
            gridBagDescription.fill = GridBagConstraints.HORIZONTAL;
            gridBagDescription.anchor = GridBagConstraints.WEST;
            gridBagDescription.weightx = 1;
            add(optionDescription, gridBagDescription);
        }

        @Override
        public void clickAction(MouseEvent e) {

            // Generates a new weigh Process!
            WeightInfo newProcess = new WeightInfo(weightType);
            LOGGER.log(Level.FINE, "Generated new Weight Process of type {0}", newProcess.getType());

            systemActions.setActualWeightInfo(newProcess);

            interfaceActions.getHeader().setTitleText(rawTitle, 60);
            interfaceActions.switchPanel(PlateEntryPanel.class);
            interfaceActions.getPlateEntryFocus();
        }
    }

    public static void main(String[] args) {
        JFrame mainFrame = new JFrame();
        mainFrame.add(new TypePanel());
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}
