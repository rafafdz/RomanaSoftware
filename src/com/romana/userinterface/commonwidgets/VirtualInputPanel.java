/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface.commonwidgets;

import com.romana.userinterface.Style;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.KeyStroke;

// Used to reuse code between keyboard and numpad!
public class VirtualInputPanel extends Style.StyledJPanel {

    private Color backgroundColor = Style.BACKGROUND_KEYBOARD;

    private Color backgroundKey = Style.BACKGROUND_KEY;
    private Font keyTextFont = Style.KEYBOARD_FONT;
    private int keyCornerRadius = 20;
    private Dimension keyDimension;
    private final CustomInput inputPanel;

    private final String backPath = "/gui_img/backspace_512.png";
    private final Style.StyledImage backImage;

    public VirtualInputPanel(CustomInput inputPanel, int keySize) {
        keyDimension = new Dimension(keySize, keySize);
        this.inputPanel = inputPanel;

        int backspaceImageSize = (int) (keySize * 0.7);
        backImage = new Style.StyledImage(backPath, backspaceImageSize, backspaceImageSize);
        setup();
    }

    private void setup() {
        setBackground(backgroundColor);
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getBackgroundKey() {
        return this.backgroundKey;
    }

    public void setBackgroundKey(Color backgroundKey) {
        this.backgroundKey = backgroundKey;
    }

    public void setKeyTextFont(Font keyTextFont) {
        this.keyTextFont = keyTextFont;
    }

    public void setKeyCornerRadius(int keyCornerRadius) {
        this.keyCornerRadius = keyCornerRadius;
    }

    public void setKeyDimension(Dimension keyDimension) {
        this.keyDimension = keyDimension;
    }

    public Dimension getKeyDimension() {
        return keyDimension;
    }

    private void applyKeyStyle(TextFieldKey key) {
        key.setTextFont(keyTextFont);
        key.setBackgroundColor(backgroundKey);
        key.setCornerRadius(keyCornerRadius);
        key.borderEnabled(false);
        key.setMinimumSize(keyDimension);
    }

    class StyledCharacterKey extends CharacterKey {

        public StyledCharacterKey(String character) {
            super(inputPanel, character);
            applyKeyStyle((TextFieldKey) this);
        }
    }

    class StyledBackspaceKey extends BackspaceKey {

        public StyledBackspaceKey() {
            super(inputPanel, backImage);
            applyKeyStyle((TextFieldKey) this);
        }
    }
}

/**
 * Generalized key, useful for creating custom keyboards using java Swing Robot Class. Not used in
 * this project since will generate input in the focused window, causing unexpected behavior in
 * multi input/screen setups.
 *
 * @author rafael
 */
class RobotKey extends Style.RoundedButton {

    private String keyText;
    private int keyCode;
    private Style.StyledImage buttonImage;
    private Robot robot;
    private boolean shiftNeeded = false;

    public RobotKey(Robot robot, String character) {
        super(character);
        this.robot = robot;
        keyText = character;
        boolean isLetter = keyText.matches("[a-zA-Z]");
        boolean isUpper = keyText.equals(keyText.toUpperCase());
        shiftNeeded = keyText != null && isLetter && isUpper;
        setKeyCodeFromString(keyText);
    }

    public RobotKey(Robot robot, String text, int keyCode, boolean shiftNeeded) {
        super(text);
        this.robot = robot;
        this.keyText = text;
        this.keyCode = keyCode;
        this.shiftNeeded = shiftNeeded;
    }

    public RobotKey(Robot robot, Style.StyledImage image, int keyCode, boolean shiftNeeded) {
        super(image);
        this.buttonImage = image;
        this.robot = robot;
        this.keyCode = keyCode;
        this.shiftNeeded = shiftNeeded;
    }

    private void setKeyCodeFromString(String character) {
        String upperChar = character.toUpperCase();
        KeyStroke ks = KeyStroke.getKeyStroke(upperChar.charAt(0), 0);
        keyCode = ks.getKeyCode();
        //System.out.println("Setting keycode " + keyCode);
    }

    @Override
    public void clickAction(MouseEvent e) {
        if (shiftNeeded) {
            robot.keyPress(KeyEvent.VK_SHIFT);
        }
        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);

        if (shiftNeeded) {
            robot.keyRelease(KeyEvent.VK_SHIFT);
        }
    }
}

abstract class TextFieldKey extends Style.RoundedButton {

    private final CustomInput inputPanel;
    private String keyText;

    public TextFieldKey(CustomInput input, String text) {
        super(text);
        inputPanel = input;
        keyText = text;
    }

    public TextFieldKey(CustomInput input, Style.StyledImage image) {
        super(image);
        inputPanel = input;
    }

    public CustomInput getInputPanel() {
        return inputPanel;
    }

    public String getKeyText() {
        return keyText;
    }

    @Override
    public void clickAction(MouseEvent e) {
        modifyInputOnClick();
    }

    public abstract void modifyInputOnClick();

}

class CharacterKey extends TextFieldKey {

    public CharacterKey(CustomInput input, String text) {
        super(input, text);
    }

    @Override
    public void modifyInputOnClick() {
        CustomInput textField = getInputPanel();
        String character = getKeyText();
        textField.addCharacter(character);
    }
}

class BackspaceKey extends TextFieldKey {

    public BackspaceKey(CustomInput input, Style.StyledImage image) {
        super(input, image);
    }

    @Override
    public void modifyInputOnClick() {
        CustomInput textField = getInputPanel();
        textField.deleteLastCharacter();
    }
}
