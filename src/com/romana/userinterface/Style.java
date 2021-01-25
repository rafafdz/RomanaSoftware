/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface;

import com.romana.utilities.CommonUtils;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Rafa
 */
public final class Style {

    // To do: Eliminate class dependency using a 'Style object' that encapsulates
    // Configurations
    private static final Logger LOGGER = Logger.getGlobal();
    public static final Color BACKGROUND_MAIN_COLOR = Color.BLACK;
    public static final Color BACKGROUND_SECOND_COLOR = Color.GRAY;
    public static final Color BACKGROUND_HEADER = Color.DARK_GRAY;
    public static final Color BACKGROUND_PRESSED_COLOR = Color.DARK_GRAY;
    public static final Color BACKGROUND_KEYBOARD = BACKGROUND_MAIN_COLOR;
    public static final Color BACKGROUND_KEY = new Color(75, 75, 75);
    public static final Color FONT_MAIN_COLOR = Color.WHITE;
    public static final Color ERROR_COLOR = Color.RED;
    public static final String CLOCK_FONT_FAMILY = "Trebuchet MS";
    public static final String HEADER_TITLE_FONT = "Trebuchet MS";
    public static final int TITLE_FONT_SIZE = 65;
    public static final int FONT_SIZE = 20;
    public static final String FONT_FAMILY = "Arial";
    public static final Font DEFAULT_FONT = new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE);
    public static final Font TYPE_NUMBER_FONT = new Font("Ubuntu", Font.BOLD, 60);
    public static final Font KEYBOARD_FONT = new Font("Arial", Font.BOLD, 35);
    public static final Font NUMPAD_FONT = new Font("Arial", Font.BOLD, 45);
    public static final Dimension PREFERRED_DIMENSION = new Dimension(1280, 720);
    public static final int ERROR_MSG_TIMEOUT = 10; // 10 seconds, To do: Move to glabal conf.

    public static class StyledJLabel extends JLabel {
        
        private boolean htmlMode = false;

        public StyledJLabel() {
            super();
            setDefaults();
        }

        public StyledJLabel(String text) {
            super(text);
            setDefaults();
        }

        public StyledJLabel(int fontSize) {
            super();
            setDefaults();
            setFont(DEFAULT_FONT.deriveFont((float) fontSize));
        }

        public StyledJLabel(String text, int fontSize) {
            super(text);
            setDefaults();
            setFontSize(fontSize);
        }

        public void setFontSize(int fontSize) {
            Font newFont = getFont().deriveFont((float) fontSize);
            setFont(newFont);
        }
        
        public void setBold(){
            Font newFont = getFont().deriveFont(Font.BOLD);
            setFont(newFont);
        }
        
        public void setHtml(boolean html){
            htmlMode = true;
            String actualText = getText();
            setText(actualText);
        }
        
        @Override
        public void setText(String text){
            if (htmlMode) {
                String replaced = text.replace("\n", "<br/>");
                super.setText("<html>" + replaced + "</html>");
            } else {
                super.setText(text);
            }
        }

        private void setDefaults() {
            setFont(DEFAULT_FONT);
            setForeground(FONT_MAIN_COLOR);
            setBackground(BACKGROUND_MAIN_COLOR);
        }
    }

    public static final class StyledImage extends StyledJLabel {

        public StyledImage(String path) {
            super();
            BufferedImage image = readImage(path);
            setIcon(new ImageIcon(image));
        }

        public StyledImage(String path, int width, int height){
            Image scaled = scaleImage(readImage(path), width, height);
            setIcon(new ImageIcon(scaled));
        }

        private BufferedImage readImage(String path) {
            try {
                URL picPath = CommonUtils.resourceURL(path);
                BufferedImage myPicture = ImageIO.read(picPath);
                return myPicture;
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
                return null;
            }
        }

        public Image scaleImage(Image image, int width, int height) {
            return image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        }
    }

    public static class StyledJPanel extends JPanel {

        public StyledJPanel(LayoutManager layout) {
            super(layout);
            setDefaults();
        }

        public void setDefaultBackground() {
            setBackground(BACKGROUND_MAIN_COLOR);
        }

        public StyledJPanel() {
            super();
            setDefaults();
        }

        private void setDefaults() {
            setPreferredSize(PREFERRED_DIMENSION);
            setForeground(FONT_MAIN_COLOR);
            setDefaultBackground();
        }
        
        public void setTextMode(StyledJLabel label, Insets margins){
            setLayout(new GridBagLayout());
            GridBagConstraints gridBagText = new GridBagConstraints();
            gridBagText.insets = margins;
            gridBagText.weightx = 1;
            gridBagText.weighty = 1;
            gridBagText.fill = GridBagConstraints.BOTH;
            add(label, gridBagText);
        }
        
        public void addHorizontalStretch(int gridx){
            JPanel stretch = new JPanel(new BorderLayout());
            GridBagConstraints gridBagStretch = new GridBagConstraints();
            gridBagStretch.gridx = gridx;
            gridBagStretch.weightx = 1;
            gridBagStretch.weightx = 1;
            add(stretch, gridBagStretch);
        }
        
        public void addVerticalStretch(int gridy){
            JPanel stretch = new JPanel(new BorderLayout());
            GridBagConstraints gridBagStretch = new GridBagConstraints();
            gridBagStretch.gridy = gridy;
            gridBagStretch.weightx = 1;
            gridBagStretch.weighty = 1;
            add(stretch, gridBagStretch);
        }
    }

    /**
     * As seen on
     * https://stackoverflow.com/questions/15025092/border-with-rounded-corners-transparency
     */
    public static class RoundedPanel extends StyledJPanel {

        private int cornerRadius = 15; // Default corner values
        private int borderWidth = 3;
        private boolean drawBorder = true;

        public RoundedPanel(LayoutManager layout, int radius) {
            super(layout);
            cornerRadius = radius;
        }

        public RoundedPanel(LayoutManager layout, int radius, Color bgColor) {
            super(layout);
            cornerRadius = radius;
        }

        public RoundedPanel(int radius, int borderWidth) {
            super();
            cornerRadius = radius;
            this.borderWidth = borderWidth;
        }

        public RoundedPanel(int radius, Color bgColor) {
            super();
            cornerRadius = radius;
        }

        public RoundedPanel() {
            super();
        }

        public void setBorderColor(Color fg) {
            super.setForeground(fg);
        }

        public void setCornerRadius(int radius) {
            cornerRadius = radius;
        }

        public void setBorderWidth(int width) {
            borderWidth = width;
        }

        public void borderEnabled(boolean enabled) {
            drawBorder = enabled;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int rectWidth = getWidth() - borderWidth;
            int rectHeight = getHeight() - borderWidth;
            int startPos = (int) borderWidth / 2;

            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            graphics.setColor(getBackground());

            graphics.fillRoundRect(startPos, startPos, rectWidth - 1, rectHeight - 1, cornerRadius, cornerRadius); //paint background

            if (drawBorder) {
                graphics.setColor(getForeground());
                Stroke oldStroke = graphics.getStroke();
                graphics.setStroke(new BasicStroke(borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                graphics.drawRoundRect(startPos, startPos, rectWidth, rectHeight, cornerRadius, cornerRadius); //paint border
                graphics.setStroke(oldStroke);
            }
        }
    }

    public static class RoundedButton extends Style.RoundedPanel {

        private Style.StyledJLabel buttonLabel;
        private final GridBagConstraints gridBagText = new GridBagConstraints();
        private Color backgroundPressedColor = BACKGROUND_PRESSED_COLOR;
        private Color backgroundColor = BACKGROUND_MAIN_COLOR;
        private boolean changeBackground = true;

        public RoundedButton(String buttonText) {
            buttonLabel = new Style.StyledJLabel(buttonText);
            initComponents();
        }
        
        public RoundedButton(Style.StyledJLabel buttonLabel){
            this.buttonLabel = buttonLabel;
            initComponents();
        }

        public RoundedButton(Style.StyledImage image) {
            buttonLabel = image;
            initComponents();
        }

        public RoundedButton() {
            buttonLabel = new Style.StyledJLabel();
            initComponents();
        }

        public String getText() {
            return buttonLabel.getText();
        }

        public void setText(String text) {
            buttonLabel.setText(text);
        }

        public void setFontSize(int fontSize) {
            buttonLabel.setFontSize(fontSize);
        }

        public void setTextFont(Font font) {
            buttonLabel.setFont(font);
        }

        public void setMargins(int top, int left, int bottom, int right) {
            gridBagText.insets = new Insets(top, left, bottom, right);
            setupLayout();
        }
        
        public void setMargins(Insets margins){
            gridBagText.insets = margins;
            setupLayout();
        }

        public void setChangeBackground(boolean changeBackground) {
            this.changeBackground = changeBackground;
        }

        public final void setImage(Style.StyledImage image) {
            buttonLabel = image;
        }

        private void initComponents() {
            setOpaque(false);
            addMouseListener(new MouseListener());
            setupLayout();
        }

        private void setupLayout() {
            setLayout(new GridBagLayout());
            add(buttonLabel, gridBagText);
        }

        public void setBackgroundColor(Color bg) {
            super.setBackground(bg);
            backgroundColor = bg;
        }
        
        public void setPanel(JPanel panel, Insets margins){
            setLayout(new GridBagLayout());
            GridBagConstraints gridBagPanel = new GridBagConstraints();
            gridBagPanel.weightx = 1;
            gridBagPanel.weighty = 1;
            gridBagPanel.insets = margins;
            gridBagPanel.fill = GridBagConstraints.BOTH;
            add(panel, gridBagPanel);
        }

        public void setBackgroundPressedColor(Color bg) {
            backgroundPressedColor = bg;
        }

        public void clickAction(MouseEvent e) {
        }

        ;
        
        private class MouseListener extends MouseAdapter {

            @Override
            public void mousePressed(MouseEvent e) {
                if (changeBackground) {
                    setBackground(backgroundPressedColor);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (changeBackground) {
                    setBackground(backgroundColor);
                }
                clickAction(e);
            }
        }
    }
    
    public static final class TitleLabel extends StyledJLabel{
        
        private final static Border DEFAULT_BORDER = new EmptyBorder(25, 0, 10, 0);
        
        public TitleLabel(String text) {
            super(TITLE_FONT_SIZE);
            setText(text);
            setup();
        }
        
        public TitleLabel(){
            super(TITLE_FONT_SIZE);
            setText("");
            setup();
        }
        
        public void setMargins(int top, int left, int bottom, int right){
            setBorder(new EmptyBorder(top, left, bottom, right));
        }
        
        private void setup(){
            setBorder(DEFAULT_BORDER);
        }
        
    }

    public static void main(String[] args) {
        JFrame mainFrame = new JFrame();

        JPanel panel = new JPanel(new BorderLayout());

        RoundedButton rounded = new RoundedButton(new Style.StyledImage("/gui_img/card_512.png")) {
            @Override
            public void clickAction(MouseEvent e) {
            }
        };
        rounded.setBackgroundColor(Color.yellow);
        rounded.setFontSize(80);
        rounded.setMargins(100, 500, 100, 500);
        rounded.setBorderColor(Color.RED);
        rounded.setBorderWidth(20);
        rounded.setCornerRadius(40);
        rounded.borderEnabled(true);
        rounded.setOpaque(false);

        panel.add(rounded);
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));

        mainFrame.add(panel);
        mainFrame.pack();
        mainFrame.setSize(rounded.getMinimumSize());
        mainFrame.setVisible(true);
    }
}
