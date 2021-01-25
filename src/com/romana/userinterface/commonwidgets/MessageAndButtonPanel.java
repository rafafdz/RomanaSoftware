/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface.commonwidgets;

import com.romana.userinterface.Style;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;

/**
 *
 * @author rafael
 */
public class MessageAndButtonPanel extends InteractivePanel {

    private final Style.TitleLabel titleLabel = new Style.TitleLabel();
    private final Style.RoundedPanel textContainer = new Style.RoundedPanel();
    private final Style.StyledJLabel textLabel = new Style.StyledJLabel(30);
    private Style.StyledImage image;
    private Style.RoundedButton firstButton; // Will be at the left
    private Style.RoundedButton secondButton; // Will be at the right

    private Insets textMargins = new Insets(10, 10, 10, 10);
    private int titleSpacing = 30;
    private int sideSpacing = 200;
    private int textSpacing = 10;
    private int bottomSpacing = 50;
    private int buttonSpacing = 10;
    private int imageSpacing = 10;
    private int minimumTextHeight = 0;
    private boolean textAfterImage = true;
    private final GridCounter counter = new GridCounter();

    public MessageAndButtonPanel() {

    }

    public MessageAndButtonPanel(int timeout) {
        super(timeout);
    }

    public void setText(String text) {
        textLabel.setText(text);
    }

    public void setTextSize(int size) {
        textLabel.setFontSize(size);
    }

    public void setTextHtml(boolean html) {
        textLabel.setHtml(html);
    }

    public void setTextColor(Color color) {
        textLabel.setForeground(color);
    }

    public void setTextAfterImage(boolean after) {
        textAfterImage = after;
    }

    public void setTextContainerRadius(int radius) {
        textContainer.setCornerRadius(radius);
    }

    public void setTextContainerBorderWidth(int width) {
        textContainer.setBorderWidth(width);
    }

    public void setTitle(String text) {
        titleLabel.setText(text);
    }

    public void setTitleColor(Color color) {
        titleLabel.setForeground(color);
    }

    public void setImage(Style.StyledImage image) {
        this.image = image;
    }

    public void setTextMargins(Insets textMargins) {
        this.textMargins = textMargins;
    }

    public void setTitleSpacing(int titleSpacing) {
        this.titleSpacing = titleSpacing;
    }

    public void setImageSpacing(int imageSpacing) {
        this.imageSpacing = imageSpacing;
    }

    public void setTextSpacing(int textSpacing) {
        this.textSpacing = textSpacing;
    }

    public void setSideSpacing(int sideSpacing) {
        this.sideSpacing = sideSpacing;
    }

    public void setBottomSpacing(int bottomSpacing) {
        this.bottomSpacing = bottomSpacing;
    }

    public void setButtonSpacing(int buttonSpacing) {
        this.buttonSpacing = buttonSpacing;
    }

    public void setMinimumTextHeight(int minimumTextHeight) {
        this.minimumTextHeight = minimumTextHeight;
    }

    public void setFirstButton(Style.RoundedButton button) {
        firstButton = button;
    }

    public void setSecondButton(Style.RoundedButton button) {
        secondButton = button;
    }

    // Will make the firstButton appear!
    public void setFirstButtonLabel(Style.StyledJLabel label, Insets margins) {
        firstButton = new Style.RoundedButton(label) {
            @Override
            public void clickAction(MouseEvent e) {
                firstButtonAction();
            }
        };
        firstButton.setMargins(margins);
    }

    public void setSecondButtonLabel(Style.StyledJLabel label, Insets margins) {
        secondButton = new Style.RoundedButton(label) {
            @Override
            public void clickAction(MouseEvent e) {
                secondButtonAction();
            }
        };
        secondButton.setMargins(margins);
    }

    public void finishSetup() {
        removeAll();
        setLayout(new GridBagLayout());

        GridBagConstraints gridBagTitle = new GridBagConstraints();
        gridBagTitle.gridy = counter.getNext();
        gridBagTitle.insets = new Insets(0, 0, titleSpacing, 0);
        add(titleLabel, gridBagTitle);

        if (textAfterImage) {
            addImage();
            addTextContainer();
        } else {
            addTextContainer();
            addImage();
        }
        addVerticalStretch(counter.getNext());

        GridBagConstraints gridBagButtons = new GridBagConstraints();
        gridBagButtons.gridy = counter.getNext();
        gridBagButtons.insets = new Insets(0, 0, bottomSpacing, 0);
        add(new ButtonContainer(), gridBagButtons);
    }

    private void addTextContainer() {
        if (minimumTextHeight > 0) {
            textContainer.setMinimumSize(new Dimension(0, minimumTextHeight));
        }

        textContainer.setTextMode(textLabel, textMargins);
        GridBagConstraints gridBagText = new GridBagConstraints();
        gridBagText.gridy = counter.getNext();
        gridBagText.fill = GridBagConstraints.HORIZONTAL;
        gridBagText.insets = new Insets(0, sideSpacing, textSpacing, sideSpacing);
        add(textContainer, gridBagText);
    }

    private void addImage() {
        if (image != null) {
            GridBagConstraints gridBagImage = new GridBagConstraints();
            gridBagImage.gridy = counter.getNext();
            gridBagImage.insets = new Insets(0, 0, imageSpacing, 0);
            add(image, gridBagImage);
        }
    }

    public void firstButtonAction() {

    }

    public void secondButtonAction() {

    }

    private class GridCounter {

        private int counter = 0;

        public GridCounter() {
        }

        public int getNext() {
            int prevCounter = counter;
            counter++;
            return prevCounter;
        }
    }

    private final class ButtonContainer extends Style.StyledJPanel {

        public ButtonContainer() {
            setLayout(new GridBagLayout());

            if (firstButton == null) {
                return;
            }

            GridBagConstraints gridBagFirst = new GridBagConstraints();
            gridBagFirst.gridx = 0;
            add(firstButton, gridBagFirst);

            if (secondButton == null) {
                return;
            }

            GridBagConstraints gridBagSecond = new GridBagConstraints();
            gridBagSecond.gridx = 1;
            gridBagSecond.insets = new Insets(0, buttonSpacing, 0, 0);
            add(secondButton, gridBagSecond);

        }

    }

}
