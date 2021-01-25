/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface;

import com.romana.utilities.CommonUtils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 *
 * @author Rafa
 */
public class HeaderPanel extends Style.StyledJPanel {

    // To do: Remove all static variables and methods!
    private Style.StyledJLabel clockLabel = new Style.StyledJLabel("00:00");
    private Style.StyledJLabel headerTitle = new Style.StyledJLabel();
    private Style.StyledJPanel logoPanel = new LogoPanel();
    private String pretitle;
    private String title;
    private String subtitle;
    private int pretitleSize;
    private int titleSize;
    private int subtitleSize;
    // To do: Replace html single label with multiple labels and gridlayout
    private final String TITLE_TEMPLATE = "<html><div style=\"text-align:center;\">"
            + "<p style=\"font-size:%dpx;\">%s</p><br/>"
            + "<p style=\"font-size:%dpx;"
            + "margin-top:-92;margin-bottom:-80\">%s</p><br/>"
            + "<p style=\"font-size:%dpx;\">%s</p>"
            + "</div></html>";

    public HeaderPanel() {
        initComponents();
    }

    private void initComponents() {
        setBackground(Style.BACKGROUND_HEADER);
        setLayout(new GridBagLayout());

        clockLabel.setFont(new Font(Style.CLOCK_FONT_FAMILY, Font.PLAIN, 70));
        headerTitle.setFont(new Font(Style.HEADER_TITLE_FONT, Font.BOLD, 70));
        //headerTitle.setBorder(new LineBorder(Color.yellow));
        headerTitle.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints gridBagClock = new GridBagConstraints();
        gridBagClock.gridx = 0;
        gridBagClock.insets = new Insets(5, 15, 15, 15);
        add(clockLabel, gridBagClock);

        GridBagConstraints gridBagTitle = new GridBagConstraints();
        gridBagTitle.gridx = 1;
        //gridBagTitle.insets = new Insets(15, 15, 15, 15);
        gridBagTitle.fill = GridBagConstraints.HORIZONTAL;
        gridBagTitle.anchor = GridBagConstraints.CENTER;
        gridBagTitle.weightx = 1;
        add(headerTitle, gridBagTitle);

        GridBagConstraints gridBagLogo = new GridBagConstraints();
        gridBagLogo.gridx = 2;
        gridBagLogo.insets = new Insets(5, 15, 15, 5);
        add(logoPanel, gridBagLogo);

        updateTitleLabel();

        Timer t = new Timer(1000, new UpdateClockAction());
        t.start();
    }

    public void updateHourAndResize(String hour) {
        clockLabel.setText(hour);
        logoPanel.setPreferredSize(new Dimension(clockLabel.getWidth(), logoPanel.getHeight()));
    }

    public void setPretitleText(String text, int size) {
        pretitle = text;
        pretitleSize = size;
        updateTitleLabel();
    }

    public void setTitleText(String text, int size) {
        title = text;
        titleSize = size;
        updateTitleLabel();
    }

    public void setSubtitleText(String text, int size) {
        subtitle = text;
        subtitleSize = size;
        updateTitleLabel();
    }

    private void updateTitleLabel() {
        String newTitle = String.format(TITLE_TEMPLATE, pretitleSize, pretitle,
                                        titleSize, title, subtitleSize, subtitle);
        headerTitle.setText(newTitle);
    }

    private class UpdateClockAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String newHour = CommonUtils.formattedHourNow();
            if (!newHour.equals(clockLabel.getText())) {
                updateHourAndResize(newHour);
            }
        }
    }

    private class LogoPanel extends Style.StyledJPanel {

        private final Style.StyledJLabel textLabel = new Style.StyledJLabel();
        private Style.StyledJLabel picLabel;
        private final Font textFont = new Font("Gill Sans MT", Font.PLAIN, 30);
        private static final String LOGO_SUBTITLE = "MAIPU";
        private static final String TRUCK_PATH = "/gui_img/cargo_truck_256.png";

        public LogoPanel() {
            initComponents();
        }

        private void initComponents() {
            setLayout(new GridBagLayout());
            setBackground(Style.BACKGROUND_HEADER);

            String text = "<html><div style=\"text-align:center;\"><span style=\"font-size:20px;\">Romana</span>"
                    + "<br/><span style=\"font-size:10px;\">" + LOGO_SUBTITLE + "</span></div></html>";
            textLabel.setFont(textFont);
            textLabel.setText(text);
            textLabel.setHorizontalAlignment(SwingConstants.CENTER);
          
            picLabel = new Style.StyledImage(TRUCK_PATH, 100, 100);
            
            GridBagConstraints gridBagPicture = new GridBagConstraints();
            gridBagPicture.gridy = 0;
            //gridBagPicture.insets = new Insets(15, 15, 15, 15);
            //gridBagPicture.weighty = 1;
            add(picLabel, gridBagPicture);

            GridBagConstraints gridBagText = new GridBagConstraints();
            gridBagText.gridy = 1;
            gridBagText.insets = new Insets(-15, 15, 5, 15);
            //gridBagText.weighty = 1;
            add(textLabel, gridBagText);
        }

    }

    public static void main(String[] args) {
        JFrame mainFrame = new JFrame();
        mainFrame.setLayout(new BorderLayout());
        HeaderPanel header = new HeaderPanel();
        mainFrame.add(header);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}
