/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface.commonwidgets;

import com.romana.userinterface.Style;
import java.util.logging.Logger;

/**
 *
 * @author rafael
 */
public class Buttons {
    
    private static final Logger LOGGER = Logger.getGlobal();

    public static class QuickImageButton extends Style.RoundedButton {

        public QuickImageButton(String imagePath, int imageSize, int margin) {
            super();
            Style.StyledImage image = new Style.StyledImage(imagePath, imageSize, imageSize);
            setImage(image);
            setup(margin);
        }

        private void setup(int margin) {
            setMargins(margin, margin, margin, margin);
        }
    }

    public static class ConfirmButton extends QuickImageButton {

        private static final String PIC_PATH = "/gui_img/check_512.png";

        public ConfirmButton(int imageSize, int margin) {
            super(PIC_PATH, imageSize, margin);
        }
    }

    public static class QuitButton extends QuickImageButton {

        private static final String PIC_PATH = "/gui_img/cross_512.png";

        public QuitButton(int imageSize, int margin) {
            super(PIC_PATH, imageSize, margin);
        }
    }
    
}
