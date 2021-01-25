/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface.commonwidgets;

import com.romana.userinterface.Style;
import java.awt.Insets;
import javax.swing.JFrame;

/**
 *
 * @author rafael
 */
public class MessageTwoButton extends MessageAndButtonPanel{
    
    
    private static final String QUIT_PATH = "/gui_img/cross_512.png";
    private static final int TEXT_SIZE = 50;
    private static final int SIDE_SPACING = 300;
    private static final int BOTTOM_SPACING = 50;
    private static final int BUTTON_SPACING = 40;
    
    private final Style.StyledJLabel buttonLabel = new Style.StyledJLabel(45);
    
    
    private Style.StyledImage quitImage;
    
    public MessageTwoButton(int timeout) {
        super(timeout);
        setup();
    }
    
    private void setup(){
        setTextHtml(true);
        setTextSize(TEXT_SIZE);
        setSideSpacing(SIDE_SPACING);
        setBottomSpacing(BOTTOM_SPACING);
        setButtonSpacing(BUTTON_SPACING);
        
        quitImage = new Style.StyledImage(QUIT_PATH, 80, 80);
        setFirstButtonLabel(quitImage, new Insets(15, 15, 15, 15));
        setSecondButtonLabel(buttonLabel, new Insets(28, 15, 28, 15));
    }
    
    
    public void setButtonText(String text) {
        buttonLabel.setText(text);
        
    }
    
    public void buttonAction(){
        
    }
    
    public void quitAction(){
        
    }
    
    @Override
    public void firstButtonAction(){
        quitAction();
    }
    
    @Override
    public void secondButtonAction(){
        buttonAction();
    }
    
    public static void main(String[] args) {
        JFrame mainFrame = new JFrame();
        MessageTwoButton entry = new MessageTwoButton(10);
        entry.setButtonText("Reintentar");
        entry.setText("Wena perro como va la vida hermano, yo estoy fino aca con la progra");
                       
        mainFrame.add(entry);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
    
    
    
}
