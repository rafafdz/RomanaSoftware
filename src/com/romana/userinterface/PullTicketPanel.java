/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface;

import com.romana.userinterface.commonwidgets.Buttons;
import com.romana.userinterface.commonwidgets.MessageAndButtonPanel;
import java.awt.event.MouseEvent;

/**
 *
 * @author rafael
 */
public class PullTicketPanel extends MessageAndButtonPanel {

    private static final String TITLE = "Retire su ticket";
    private static final String MESSAGE = "Para sacarlo, tírelo hacia usted. No lo tire hacia arriba";
    private static final String PIC_PATH = "/gui_img/ticket_512.png";
    private static final int INACTIVITY_TIMEOUT = 30;
    private final Style.StyledImage picLabel = new Style.StyledImage(PIC_PATH, 350, 350);
    private Style.RoundedButton okButton;

    public PullTicketPanel() {
        super(INACTIVITY_TIMEOUT);
        setup();
    }

    private void setup() {
        setTitle(TITLE);
        setText(MESSAGE);
        setImage(picLabel);
        setTextHtml(true);
        setTextSize(45);
        setImageSpacing(30);
        setSideSpacing(350);

        okButton = new Buttons.ConfirmButton(80, 10) {
            @Override
            public void clickAction(MouseEvent e) {
                systemActions.returnToMainMenu();
            }
        };
        setFirstButton(okButton);
        finishSetup();
    }

    @Override
    public void firstButtonAction() {
        systemActions.returnToMainMenu();
    }
}
