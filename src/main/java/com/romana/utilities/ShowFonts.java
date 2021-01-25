/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.utilities;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Rafa
 */
public class ShowFonts {

    public static void main(String[] args) {

        String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        JPanel panel = new JPanel(new GridLayout(0, 2, 0, 0));
        //panel.setPreferredSize(new Dimension(1280, 300));

        int cont = 0;

        for (String font : fonts) {
            Font normalFont = new Font(font, Font.PLAIN, 20);
            JLabel normalLabel = new JLabel("Texto de Prueba");
            normalLabel.setFont(normalFont);
            normalLabel.setMinimumSize(new Dimension(200, 5));
            Font boldFont = new Font(font, Font.BOLD, 20);
            JLabel boldLabel = new JLabel(font);
            boldLabel.setFont(boldFont);

            panel.add(normalLabel);
            panel.add(boldLabel);
            cont += 1;

        }

        System.out.println(cont);

        JFrame mainFrame = new JFrame();
        JScrollPane scroll = new JScrollPane(panel);
        mainFrame.add(scroll);
        mainFrame.pack();
        mainFrame.setVisible(true);

    }

}
