/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.devices;

/**
 * source https://stackoverflow.com/questions/1754315/
 * how-to-create-custom-exceptions-in-java
 * @author santiago
 */

public class SerialException extends Exception {

    public SerialException() { 
        super(); 
    }
    
    public SerialException(String message) { 
        super(message); 
    }
    
    public SerialException(String message, Throwable cause) { 
        super(message, cause); 
    }
    
    public SerialException(Throwable cause) { 
        super(cause);
    }  
}
