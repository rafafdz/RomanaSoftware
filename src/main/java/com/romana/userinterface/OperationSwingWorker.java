/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.userinterface;

import com.romana.database.DatabaseException;
import com.romana.devices.SerialException;
import com.romana.devices.scale.ScaleException;
import com.romana.userinterface.ErrorMessagePanel.ErrorType;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author rafael
 * @param <T>
 * @param <V>
 */
public abstract class OperationSwingWorker<T, V> extends SwingWorker<T, V>{
    
    private static final Logger LOGGER = Logger.getLogger(OperationSwingWorker.class.getName());
    private final HashMap<Class, ErrorType> errorMap;
    private final UserInterface.InterfaceActions interfaceActions;
    
    
    public OperationSwingWorker(UserInterface.InterfaceActions actions){
        interfaceActions = actions;
        errorMap = new HashMap<>();
        // To do: Use more specific Exceptions!
        errorMap.put(ScaleException.class, ErrorType.SCALE_ERROR);
        errorMap.put(SerialException.class, ErrorType.SERIAL_COM_ERROR);
        errorMap.put(DatabaseException.class, ErrorType.DATABASE_ERROR);
        errorMap.put(InterruptedException.class, ErrorType.INTERRUPED_ERROR);
    }
    
    protected abstract void doneAndCatch() throws Exception;
    
    @Override
    protected void done(){
        try {
            doneAndCatch();
            
        } catch (Exception ex){
            ErrorType newError = ErrorType.UNKNOWN_ERROR;
            
            if (ex instanceof ExecutionException){
                ex = (Exception) ex.getCause();
            }
            
            LOGGER.log(Level.SEVERE, "Exception in SwingWorker", ex);
            
            if(errorMap.containsKey(ex.getClass())){
                
                newError = errorMap.get(ex.getClass());   
            }
            
            interfaceActions.setError(newError);
            interfaceActions.switchPanel(ErrorMessagePanel.class);
        }
    }
}
