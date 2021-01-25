/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.utilities;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author rafael
 */
public class LoggerCreator {
    
    public static Logger create(String name, String logFile, Level logLevel){
        Logger newLogger = Logger.getLogger(name);
        newLogger.setUseParentHandlers(false);
        
        Handler consoleHandler = new ConsoleHandler();
        newLogger.addHandler(consoleHandler);
        
        newLogger.setLevel(logLevel);
        consoleHandler.setLevel(logLevel);
        
        try {
            Path filePath = Paths.get(logFile);
            Path parent = filePath.getParent();
            if (parent != null){
                parent.toFile().mkdirs();
            }
            Handler fileHandler = new FileHandler(logFile, true);
            fileHandler.setFormatter(new SimpleFormatter());
            newLogger.addHandler(fileHandler); // Disabled while developing
            fileHandler.setLevel(logLevel);
            
        } catch (IOException | SecurityException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Could not create file " + logFile, ex);
        }
        
        return newLogger;
    }
}
