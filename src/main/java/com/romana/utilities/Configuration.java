package com.romana.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rafael
 */
public class Configuration {

    public static final String SOFTWARE_VERSION = "0.9.7";
    
    private static final Logger LOGGER = Logger.getLogger(Configuration.class.getName());
    private static final String CONFIG_PATH = "config/romana_software.properties";

    // Default definitions
    private static final String BALANCE_PATH = "db/cards_balance.json";
    private static final String WEIGHT_INFO_FILENAME = "info.json";
    private static final String WEB_DOMAIN = "www.romana.tk";
    private static final String CARD_PORT = "TEST";
    private static final String SCALE_PORT = "TEST";
    private static final String TICKET_VENDOR_ID = "TEST";
    private static final String TICKET_PRODUCT_ID = "TEST";
    private static final int TICKET_IN_EP = 1;
    private static final int TICKET_OUT_EP = 1;
    private static final int HOUR_LIMIT_SECOND_PHASE = 24;
    private static final int WEIGHT_THRESHOLD = 300;
    private static final int DIFFERENCE_THRESHOLD = 30;
    private static final int PRICE_SIMPLE = 3000;
    private static final int PRICE_SINGLE_AXIS = 1500;
    private static final int PRICE_TWO_PHASE = 5000;
    private static final int SIZE_X = 1920;
    private static final int SIZE_Y = 1080;
    private static final boolean DEBUG_MODE = true;
    
    private static Configuration configInstance;
    private Properties properties;

    private Configuration() {
        File file = new File(CONFIG_PATH);
        try {
            if (!file.exists()) {
                properties = generateConfigFile();
                LOGGER.log(Level.INFO, "Generating new configuration file at {0}", CONFIG_PATH);
                return;
            }
            properties = loadConfig();
        } catch (IOException | IllegalAccessException ex) {
            LOGGER.log(Level.SEVERE, "Error while loading configuration file", ex);
            System.exit(1);
        }
    }

    public static Configuration getInstance() {
        if (configInstance == null) {
            configInstance = new Configuration();
        }
        return configInstance;
    }

    private static Object getConfig(String configName) { // Not good practice, rely more on singleton
        // Makes use of Singleton
        Configuration instance = getInstance();
        Object value = instance.getProperties().getProperty(configName);

        try {
            if (value == null) {
                if (containsDefaultConfig(configName)) {
                    value = Configuration.class.getDeclaredField(configName).get(null);
                    instance.addNewConfig(configName, String.valueOf(value));
                    instance.storeActualConfig();
                    String propertyLog = configName + " -> " + value;
                    LOGGER.log(Level.INFO, "Adding default parameter to config {0}", propertyLog);
                    return instance.getProperties().getProperty(configName);
                } else {
                    LOGGER.log(Level.SEVERE, "Accesing an unknown configuration: {0}", configName);
                    System.exit(1);
                }
            }

        } catch (IOException | NoSuchFieldException | SecurityException | IllegalAccessException ex) {
            LOGGER.log(Level.SEVERE, "Critical error while acceesing config property", ex);
            System.exit(1);
        }
        return value;
    }

    public static String getStringConfig(String configName) {
        return (String) getConfig(configName);
    }

    public static int getIntConfig(String configName) {
        return Integer.valueOf(getStringConfig(configName));
    }

    public static boolean getBoolConfig(String configName) {
        return Boolean.valueOf(getStringConfig(configName));
    }

    public static boolean isDebugMode() {
        return getBoolConfig("DEBUG_MODE");
    }

    public Properties getProperties() {
        return properties;
    }

    private Properties generateConfigFile() throws IOException, IllegalAccessException {
        Path configPath = Paths.get(CONFIG_PATH);
        File configFile = configPath.toFile();

        Properties prop = new Properties() {
            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<>(super.keySet()));
            }
        };

        if (!configFile.exists()) {
            configPath.getParent().toFile().mkdirs();
            configFile.createNewFile();
        }

        String[] notAllowed = {"SOFTWARE_VERSION", "LOGGER", "CONFIG_PATH", 
            "configInstance", "properties"};
        Set<String> blacklistFields = new HashSet<>(Arrays.asList(notAllowed));

        try (OutputStream output = new FileOutputStream(CONFIG_PATH)) {

            // set the properties value
            for (Field field : Configuration.class.getDeclaredFields()) {
                if (blacklistFields.contains(field.getName())) {
                    continue;
                }

                String fieldValue;
                Class<?> fieldClass = field.getType();
                if (fieldClass.equals(int.class)) { // To do: Use switch instead
                    fieldValue = String.valueOf(field.getInt(null));
                } else if (fieldClass.equals(boolean.class)) {
                    fieldValue = String.valueOf(field.getBoolean(null));
                } else {
                    fieldValue = (String) field.get(null);
                }
                prop.setProperty(field.getName(), fieldValue);

            }
            prop.store(output, null);
        }
        return prop;
    }

    private Properties loadConfig() throws IOException {
        try (InputStream input = new FileInputStream(CONFIG_PATH)) {

            Properties prop = new Properties();
            //load a properties file from class path, inside static method
            prop.load(input);

            return prop;
        }
    }

    private void storeActualConfig() throws IOException {
        try (OutputStream output = new FileOutputStream(CONFIG_PATH)) {
            properties.store(output, null);
        }
    }

    private void addNewConfig(String key, String value) throws IOException {
        properties.setProperty(key, value);
        storeActualConfig();
    }

    private static boolean containsDefaultConfig(String fieldName) {
        return Arrays.stream(Configuration.class.getDeclaredFields())
                .anyMatch(f -> f.getName().equals(fieldName));
    }

    public static void main(String[] args) throws IOException, IllegalAccessException {
        System.out.println(Configuration.getConfig("CARD_PORT"));
    }

}
