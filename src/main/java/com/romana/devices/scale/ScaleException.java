/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.devices.scale;


public class ScaleException extends Exception {

    public ScaleException() { 
        super(); 
    }
    
    public ScaleException(String message) { 
        super(message); 
    }
    
    public ScaleException(String message, Throwable cause) { 
        super(message, cause); 
    }
    
    public ScaleException(Throwable cause) { 
        super(cause);
    }  
}
