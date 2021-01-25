/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface;

import com.romana.devices.SystemOperations.WeightType;
import com.romana.devices.WeightInfo;
import com.romana.userinterface.commonwidgets.Buttons;
import com.romana.userinterface.commonwidgets.InteractivePanel;
import com.romana.userinterface.commonwidgets.TablePanel;
import com.romana.utilities.Configuration;
import java.awt.AWTException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;

/**
 *
 * @author Rafa
 */
public class SummaryPanel extends InteractivePanel {

    private static final int INACTIVITY_TIMEOUT = 30;
    private final Style.TitleLabel titleLabel = new Style.TitleLabel("Resumen de Cobros");
    private final SalesContainer salesContainer = new SalesContainer();
    private final TotalPricePanel totalPricePanel = new TotalPricePanel();
    private final ButtonContainer buttonContainer = new ButtonContainer();
    private final int SIDE_SPACING = 380;
    
    private static final int PRICE_SIMPLE = Configuration.getIntConfig("PRICE_SIMPLE");
    private static final int PRICE_AXIS = Configuration.getIntConfig("PRICE_SINGLE_AXIS");
    private static final int PRICE_TWO_PHASE = Configuration.getIntConfig("PRICE_TWO_PHASE");
    
    public SummaryPanel() {
        super(INACTIVITY_TIMEOUT);
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        salesContainer.generateTable();

        GridBagConstraints gridBagTitle = new GridBagConstraints();
        gridBagTitle.gridy = 0;
        add(titleLabel, gridBagTitle);
        
        GridBagConstraints gridBagSales = new GridBagConstraints();
        gridBagSales.gridy = 1;
        gridBagSales.ipady = 000;
        gridBagSales.fill = GridBagConstraints.HORIZONTAL;
        gridBagSales.insets = new Insets(25, SIDE_SPACING, 10, SIDE_SPACING);
        add(salesContainer, gridBagSales);
        
        GridBagConstraints gridBagTotal = new GridBagConstraints();
        gridBagTotal.gridy = 2;
        gridBagTotal.insets = new Insets(20, 0, 10, SIDE_SPACING);
        gridBagTotal.anchor = GridBagConstraints.EAST;
        add(totalPricePanel, gridBagTotal);
        
        GridBagConstraints gridBagButtons = new GridBagConstraints();
        gridBagButtons.gridy = 3;
        gridBagButtons.insets = new Insets(45, 0, 0, 0);
        add(buttonContainer, gridBagButtons);
        
        addVerticalStretch(4);
    }
    
    @Override
    public void actionOnShow(){
        WeightInfo actualWeight = systemActions.getActualWeightInfo();
        generateFromWeightInfo(actualWeight);
    }
    
    // Simulate a Quit key on Timeout
    @Override
    public void timeoutAction(){
        clickedQuitAction();
    }
    
    private void clickedOkAction(){
        clearSalesInfo();
        interfaceActions.switchPanel(CardReadPanel.class);
    }
    
    private void clickedQuitAction(){
        clearSalesInfo();
        systemActions.returnToMainMenu();
    }
    
    public void generateFromWeightInfo(WeightInfo info){
        
        // To do: get this information from a global configuration
        WeightType type = info.getType();
        
        String serviceName = "";
        int price = 0;
        int quantity = 0;
        
        switch (type){
            case SIMPLE:
                serviceName = "PESAJE SIMPLE";
                price = PRICE_SIMPLE;
                quantity = 1;
                break;
                
            case TWO_PHASE:
                if (info.isSecondPhase()){
                    serviceName = "PESAJE DOS FASES (SEGUNDA FASE)";
                    price = 0;
                    quantity = 1;

                } else {
                    serviceName = "PESAJE DOS FASES (PRIMERA FASE)";
                    price = PRICE_TWO_PHASE;
                    quantity = 1;
                }
                break;
                
            case AXIS:
                serviceName = "PESAJE EJE";
                price = PRICE_AXIS;
                quantity = info.getAxisNumber();
                break;
        }
        
        salesContainer.addSale(serviceName, price, quantity);
        salesContainer.generateTable();
        
        int totalPrice = salesContainer.getTotalPrice();
        
        if (!info.isSecondPhase()){ // In order not to save a 0 as total price!
            info.setTotalPrice(totalPrice);
        }
        totalPricePanel.setTotalPrice(totalPrice);
        
    }
    
    private void clearSalesInfo(){
        salesContainer.clearSales();
    }

    private final class SalesContainer extends TablePanel {

        private final int HEAD_SIZE = 35;
        private final int ELEMENT_SIZE = 30;
        private final Insets HEAD_MARGINS = new Insets(10, 25, 10, 25);
        private int totalPrice = 0; 

        public SalesContainer() {
            super(3); // Column number
            setHeadSize(HEAD_SIZE);
            setElementSize(ELEMENT_SIZE);
            setHeadInsets(HEAD_MARGINS);
            setMaximizedColumn(0);
            setRowSeparation(20);
            setHeads("Servicio", "Precio", "Cantidad");
        }
        
        public void addSale(String serviceName, int price, int quantity) {
            totalPrice += price * quantity;
            addRow(serviceName, String.valueOf(price), String.valueOf(quantity));
        }

        public void clearSales() {
            totalPrice = 0;
            clearRows();
        }

        public int getTotalPrice() {
            return totalPrice;
        }
    }

    
    private final class TotalPricePanel extends Style.RoundedPanel {
        
        private final Style.StyledJLabel totalTextLabel;
        private final Style.StyledJLabel totalPriceLabel; 
        private final int BORDER_SEP = 30;
        
        
        public TotalPricePanel(){
            setLayout(new GridBagLayout());
            
            totalTextLabel = new Style.StyledJLabel("Precio total:", 50);
            totalPriceLabel = new Style.StyledJLabel(50);
            totalPriceLabel.setBold();
            
            GridBagConstraints gridBagPriceText = new GridBagConstraints();
            gridBagPriceText.gridx = 0;
            gridBagPriceText.weightx = 1;
            gridBagPriceText.insets = new Insets(10, BORDER_SEP, 10, 0);
            gridBagPriceText.anchor = GridBagConstraints.WEST;
            add(totalTextLabel, gridBagPriceText);
        
            GridBagConstraints gridBagPrice = new GridBagConstraints();
            gridBagPrice.gridx = 1;
            gridBagPrice.anchor = GridBagConstraints.EAST;
            gridBagPrice.insets = new Insets(10, BORDER_SEP, 10, BORDER_SEP);
            add(totalPriceLabel, gridBagPrice);
            
        }
        
        public void setTotalPrice(int totalPrice){
            totalPriceLabel.setText(String.format("$%s", totalPrice));
        }
        
        public void clearTotalPrice(){
            totalPriceLabel.setText("");
        }
    }
    private final class ButtonContainer extends Style.StyledJPanel{
        
        private static final int BUTTON_SPACING = 50;
        
        public ButtonContainer(){
            setLayout(new GridBagLayout());
            
            Buttons.QuitButton quitButton = new Buttons.QuitButton(82, 23){
                @Override
                public void clickAction(MouseEvent e){
                    clickedQuitAction();
                }
            };
            
            Buttons.ConfirmButton okButton = new Buttons.ConfirmButton(88, 20){
                @Override
                public void clickAction(MouseEvent e){
                    clickedOkAction();
                }
            };
            
            GridBagConstraints gridBagQuit = new GridBagConstraints();
            gridBagQuit.gridx = 0;
            gridBagQuit.insets = new Insets(5, 0, 5, BUTTON_SPACING);
            gridBagQuit.anchor = GridBagConstraints.WEST;
            add(quitButton, gridBagQuit);
            
            GridBagConstraints gridBagOk = new GridBagConstraints();
            gridBagOk.gridx = 1;
            gridBagOk.insets = new Insets(5, BUTTON_SPACING, 5, 0);
            gridBagOk.anchor = GridBagConstraints.EAST;
            add(okButton, gridBagOk);
            
        }
    }
    
    
    public static void main(String[] args) throws AWTException {
//        SwingUtilities.invokeLater(() -> {
//            JFrame mainFrame = new JFrame();
//            SummaryPanel summary = new SummaryPanel();
//            summary.addSale("PESAJE DE EJE", 3000, 6);
//            summary.addSale("kek", 423087879, 12);
//            summary.clearSalesInfo();
//            mainFrame.add(summary);
//            mainFrame.pack();
//            mainFrame.setVisible(true);
//        });
        
        
    }

}
