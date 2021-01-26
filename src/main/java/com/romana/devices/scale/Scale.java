/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.devices.scale;

/**
 *
 * @author rafael
 */
public interface Scale {
    
    public int getWeight() throws ScaleException;
    
    public boolean isWorking();
}
