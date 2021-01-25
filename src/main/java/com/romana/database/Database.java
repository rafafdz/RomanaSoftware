/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.database;

import com.romana.utilities.CommonUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple, clean and inefficient Database implementation using GSON serialization
 * @author Rafa
 */
public class Database {
    
    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());
    private HashMap<String, Object> map;
    private String filePath;
   
    /**
     *  Creates a new database attached to a file in the path. If the file
     *  does not exist, create a new one.
     * @param path 
     * @throws com.romana.database.DatabaseException 
     */
    public Database(String path) throws DatabaseException{
        map = new HashMap<>();
        
        filePath = path;
        File databaseFile = new File(filePath);
        if (!databaseFile.exists()) {
            Path parent = databaseFile.toPath().getParent();
            if (parent != null){
                parent.toFile().mkdirs();
            }
            LOGGER.log(Level.INFO, "Creating new database file {0}", path);
            try {
                generateNewDatabase(databaseFile);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Could not generate database file", ex);
                throw new DatabaseException("Could not create the database " + 
                        databaseFile.getPath());
            }
        } else {
            reload();
        }
    }
    
    /**
     * Returns the object associated with key from memory.
     * @param key
     * @return 
     */
    public Object get(String key){
         return map.get(key);
    }
    
    public void put(String key, Object object){
        map.put(key, object);
    }
    
    public final void reload() throws DatabaseException{
        try {
            map = CommonUtils.deserializeFromFile(filePath, HashMap.class);
        } catch (IOException ex) {
            throw new DatabaseException("Cant reload database", ex);
        }
    }
    
    public void save() throws DatabaseException{
        try {
            CommonUtils.serializeToFile(map, filePath);
        } catch (IOException ex) {
            throw new DatabaseException("Cant save database", ex);
        }
    }
    
    public HashMap<String, Object> getHashMap(){
        return map;
    }
    
    private void generateNewDatabase(File file) throws IOException, DatabaseException{
        file.createNewFile();
        save();
    }
    
    public static Database loadFromFile(String path) throws DatabaseException{
        Database newDatabase = new Database(path);
        newDatabase.reload();
        return newDatabase;
    }
    
//    public static void main(String[] args) {
//        try {
//            Database db = Database.loadFromFile("db/primeraDB.db");
//            System.out.println(db.get("WenaPerro"));
//        } catch (DatabaseException ex) {
//            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
//        }     
//    }
}
