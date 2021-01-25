/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.database;

/**
 *
 * @author Rafa
 */
public class DatabaseException extends Exception {

    public DatabaseException() { 
        super(); 
    }
    
    public DatabaseException(String message) { 
        super(message); 
    }
    
    public DatabaseException(String message, Throwable cause) { 
        super(message, cause); 
    }
    
    public DatabaseException(Throwable cause) { 
        super(cause);
    }  
}
