/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.devices;

/**
 * This class should be abstract. Defines methods that any external device must implement
 * @author rafael
 */
public abstract class ExternalDevice {
    
    // Used as an ID
    private String deviceName;

    public ExternalDevice(){};
    
    public ExternalDevice(String name){
        this.deviceName = name;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    
    public abstract boolean isWorking();
    
}
