/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface;

import com.romana.userinterface.commonwidgets.InteractivePanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;

/**
 *
 * @author Rafa
 */
public class NoMoneyPanel extends InteractivePanel{
    
    private static final int INACTIVITY_TIMEOUT = 20;
    private static final int SIZE = 60;
    private final String comment = "Recargue su tarjeta con el operador.";
    private final Style.TitleLabel titleLabel = new Style.TitleLabel("Saldo Insuficiente");
    private final Style.StyledJLabel balanceLabel = new Style.StyledJLabel(SIZE);
    private final Style.StyledJLabel balanceText = new Style.StyledJLabel("Saldo Tarjeta:", SIZE);
    private final Style.StyledJLabel priceText = new Style.StyledJLabel("Precio Servicio:", SIZE);
    private final Style.StyledJLabel priceLabel = new Style.StyledJLabel(SIZE);
    private final Style.StyledJLabel commentLabel = new Style.StyledJLabel(comment, SIZE);
    private static final int TABLE_BORDER = 30;
    private static final int TABLE_WIDTH = 220;
    
    public NoMoneyPanel(){
        super(INACTIVITY_TIMEOUT);
        initComponents();
        
    }
    
    private void initComponents(){
        setLayout(new GridBagLayout());
        titleLabel.setForeground(Color.RED);
        balanceLabel.setBold();
        priceLabel.setBold();
        
        GridBagConstraints gridBagTitle = new GridBagConstraints();
        gridBagTitle.gridy = 0;
        add(titleLabel, gridBagTitle);
        
        Style.RoundedPanel container = new Style.RoundedPanel();
        container.setLayout(new GridBagLayout());
        
        // Container Configuration
        GridBagConstraints gridBagBalanceText = new GridBagConstraints();
        gridBagBalanceText.gridx = 0;
        gridBagBalanceText.gridy = 0;
        gridBagBalanceText.anchor = GridBagConstraints.WEST;
        gridBagBalanceText.insets = new Insets(10, TABLE_BORDER, 10, 0);
        gridBagBalanceText.weightx = 1;
        container.add(balanceText, gridBagBalanceText);
        GridBagConstraints gridBagBalanceLabel = new GridBagConstraints();
        gridBagBalanceLabel.gridx = 1;
        gridBagBalanceLabel.anchor = GridBagConstraints.WEST;
        gridBagBalanceLabel.insets = new Insets(10, 0, 10, TABLE_BORDER);
        container.add(balanceLabel, gridBagBalanceLabel);
        GridBagConstraints gridBagPriceText = new GridBagConstraints();
        gridBagPriceText.gridx = 0;
        gridBagPriceText.gridy = 1;
        gridBagPriceText.anchor = GridBagConstraints.WEST;
        gridBagPriceText.insets = new Insets(0, TABLE_BORDER, 10, 0);
        container.add(priceText, gridBagPriceText);
        GridBagConstraints gridBagPriceLabel = new GridBagConstraints();
        gridBagPriceLabel.gridx = 1;
        gridBagPriceLabel.gridy = 1;
        gridBagPriceLabel.anchor = GridBagConstraints.WEST;
        gridBagPriceLabel.insets = new Insets(0, 0, 10, TABLE_BORDER);
        container.add(priceLabel, gridBagPriceLabel);
        
        GridBagConstraints gridBagContainer = new GridBagConstraints();
        gridBagContainer.gridy = 1;
        gridBagContainer.insets = new Insets(30, 0, 40, 0);
        gridBagContainer.ipadx = TABLE_WIDTH;
        add(container, gridBagContainer);
        
        GridBagConstraints gridBagComment = new GridBagConstraints();
        gridBagComment.gridy = 2;
        add(commentLabel, gridBagComment);
        
        addVerticalStretch(3);
        
        OkButton doneButton = new OkButton();
        GridBagConstraints gridBagButton = new GridBagConstraints();
        gridBagButton.gridy = 4;
        gridBagButton.insets = new Insets(0, 0, 200, 0);
        gridBagButton.anchor = GridBagConstraints.NORTH;
        add(doneButton, gridBagButton);
    }
    
    @Override
    public void actionOnShow(){
        //setNoMoneyValues(200, 420);
    }
    
    
    @Override
    public void timeoutAction(){
        clearValues();
        systemActions.returnToMainMenu();
    }
    
    private void clearValues(){
        balanceLabel.setText("");
        priceLabel.setText("");
    }
    
    public void setNoMoneyValues(int balance, int price){
        balanceLabel.setText("$" + String.valueOf(balance));
        priceLabel.setText("$" + String.valueOf(price));
    }
    
    
    private final class OkButton extends Style.RoundedButton {

        public OkButton() {
            super("OK");
            setFontSize(80);
            setMargins(20, 20, 20, 20);
        }

        @Override
        public void clickAction(MouseEvent e) {
            timeoutAction();
        }
    }
    
    
    
    
    
    
}
