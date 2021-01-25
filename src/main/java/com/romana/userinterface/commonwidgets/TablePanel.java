package com.romana.userinterface.commonwidgets;

import com.romana.userinterface.Style;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.JSeparator;

/**
 *
 * @author rafael
 */
public class TablePanel extends Style.RoundedPanel{
    
    private int headSize = 35;
    private int elementSize = 30;
    private int rowSeparation = 20;
    private Integer maximizedColumn;
    private Insets headInsets = new Insets(10, 25, 10, 25);
    private boolean headBold = false;
    
    private String[] heads;
    private final ArrayList<String[]> rows = new ArrayList<>();
    private final int columnNumber;
    
    public TablePanel(int columnNumber){
        this.columnNumber = columnNumber;
        heads = new String[columnNumber];
        
    }
    public void setHeadSize(int headSize) {
        this.headSize = headSize;
    }

    public void setElementSize(int elementSize) {
        this.elementSize = elementSize;
    }
    
    public void setHeadInsets(int top, int left, int bottom, int right){
        headInsets = new Insets(top, left, bottom, right);
    }
    
    public void setHeadInsets(Insets insets){
        headInsets = insets;
    }
    
    public void setHeads(String ... heads){
        this.heads = heads;
    }
 
    public void setMaximizedColumn(int columnNumber){
        maximizedColumn = columnNumber;
    }

    public void setRowSeparation(int rowSeparation) {
        this.rowSeparation = rowSeparation;
    }
    
    public void clearRows(){
        rows.clear();
    }
    
    public void addRow(String ... columns){
        rows.add(columns);
    }
    
    public ArrayList<String[]> getRows(){
        return rows;
    }
    
    public void setHeadBold(){
        headBold = true;
    }
    
    
    public void generateTable(){
        removeAll();
        setLayout(new GridBagLayout());

        // Head Layout
        int headCounter = 0;
        for (String head : heads){
            Style.StyledJLabel newHead = new Style.StyledJLabel(head, headSize);
            GridBagConstraints gridBagHead = new GridBagConstraints();
            gridBagHead.gridx = headCounter;
            gridBagHead.insets = headInsets;
            gridBagHead.anchor = GridBagConstraints.CENTER;
            if (maximizedColumn != null && headCounter == maximizedColumn){
                gridBagHead.weightx = 1;
            }
            if (headBold){
                newHead.setBold();
            }
            add(newHead, gridBagHead);
            headCounter++;
        }
        
        // Separator
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.WHITE);
        GridBagConstraints gridBagSeparator = new GridBagConstraints();
        gridBagSeparator.gridy = 1;
        gridBagSeparator.gridwidth = columnNumber;
        gridBagSeparator.fill = GridBagConstraints.HORIZONTAL;
        add(separator, gridBagSeparator);


        // Table Elements
        int rowCounter = 2;
            for (String[] columns : rows) {
                int columnCounter = 0;
                for (String column : columns) {
                    Style.StyledJLabel newElement;
                    newElement = new Style.StyledJLabel(column, elementSize);

                    GridBagConstraints gridBagElement = new GridBagConstraints();
                    gridBagElement.gridx = columnCounter;
                    gridBagElement.gridy = rowCounter;
                    
                    if (rowCounter == 2){
                        gridBagElement.insets = new Insets(rowSeparation, 0, rowSeparation, 0);
                    } else {
                        gridBagElement.insets = new Insets(0, 0, rowSeparation, 0);
                    }
                    add(newElement, gridBagElement);
                    columnCounter++;
                }
                rowCounter++;
            }
        
        // Stretch
        addVerticalStretch(rowCounter);
    }
}
