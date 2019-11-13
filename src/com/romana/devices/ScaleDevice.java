/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.devices;

import com.fazecast.jSerialComm.SerialPort;
import com.romana.userinterface.UserInterface;
import com.romana.utilities.CommonUtils;
import com.romana.utilities.CommonUtils.TimeInterval;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author rafael
 */
public class ScaleDevice extends SerialDevice {

    
    // TO do: fix logging!!
    private static final Logger LOGGER = Logger.getLogger(UserInterface.class.getName());

    
    public ScaleDevice() {

    }

    public ScaleDevice(String port) throws SerialException {
        super(port, 4800, 3);
        // Specific maipu scale configuration
        SerialPort serialPort = getSerialPort();
        serialPort.setNumDataBits(7);
        serialPort.setNumStopBits(2);
        serialPort.setParity(SerialPort.EVEN_PARITY);
    }

    public int getWeight() throws SerialException {
        LOGGER.log(Level.INFO, "Started to get Weight");
        TimeInterval interval = new CommonUtils.TimeInterval();
        
        flushWeights();
        ArrayList<Integer> weightList = new ArrayList<>();
        SerialException last = null;
        for (int i = 0; i < 120; i++) {
            try {
                int weight = getWeightSingle();
                if (weight != 0){
                    weightList.add(weight);
                }
                
            } catch (SerialException ex){
                LOGGER.log(Level.WARNING, "Could not get weight: {0}", ex.getMessage());
                last = ex;
            }
            
            if (weightList.size() >= 15){
                break;
            }
//            CommonUtils.sleep(30);
        }
        
        if (weightList.isEmpty()){
            throw new SerialException("Impossible to get weight", last);
        }
        int[] weightArray = weightList.stream().mapToInt(i -> i).toArray();
        int finalWeight = CommonUtils.findPopular(weightArray);
        LOGGER.log(Level.INFO, String.format("Final Weight Chosen: %s, took %s seconds", 
                finalWeight, interval.getSeconds()));
       
        return finalWeight;
        
    }
    
    private int getWeightSingle() throws SerialException {
        clearBuffer();
        byte[] bytesRead = readBytes(35, 500); // 35 -> Minimum to get a full reading

        String readable = readableStringFromBytes(bytesRead);
        WeightResponse response = parseWeightFromString(readable);

        LOGGER.log(Level.FINE, String.format("Read %s kg with code %s",
                response.weight, response.code));
        return response.weight;
    }
    
    private void flushWeights() throws SerialException{
        int iterations = 20;
        LOGGER.log(Level.FINE, "Started Flushing Weights. {0} iterations.", iterations);
        TimeInterval interval = new CommonUtils.TimeInterval();
 
        for (int i = 0; i < iterations; i++) {
            byte[] data = readBytes(35, 500);
            // WARNING: Messy output due to non printable ascii
            LOGGER.log(Level.FINE, "Read while flushing: {0}",  
                    CommonUtils.removeNonPrintable(new String(data)));
        }
        LOGGER.log(Level.FINE, "Flush finished: {0} seconds", interval.getSeconds());
    }

    private String readableStringFromBytes(byte[] byteArray) {
        int[] ints = CommonUtils.byteArrayToIntArray(byteArray);
        
        // To do: use & 0xFF to remove twos compliment.
        // https://stackoverflow.com/questions/11380062/what-does-value-0xff-do-in-java
        // As seen in String to hex

        StringBuilder str = new StringBuilder();

        for (int i = 0; i < ints.length; i++) {
            int actualInt = ints[i];

            // Fix negative numbers due to twos complement -> matching python
            if (actualInt < 0) {
                actualInt = 128 + actualInt;
            }
            // Remove non readable ascii characters
            if (actualInt < 32) {
                continue;
            }
            char converted = (char) actualInt;
            str.append(converted);
        }
        return str.toString();
    }

    private WeightResponse parseWeightFromString(String reading) throws SerialException {
        int weight;
        String code;

        Pattern weightPattern = Pattern.compile("[\\s]{2}[0-9]+\\s");
        Matcher weightMatcher = weightPattern.matcher(reading);

        if (weightMatcher.find()) {
            String matched = weightMatcher.group(0).replaceAll("\\s", "");
            weight = Integer.valueOf(matched);
        } else {
            throw new SerialException("Parsing error " + reading);
        }

        Pattern codePattern = Pattern.compile("[a-zA-Z][\\)]");
        Matcher codeMatcher = codePattern.matcher(reading);

        if (codeMatcher.find()) {
            code = codeMatcher.group(0).replaceAll("\\)", "");
        } else {
            throw new SerialException("Parsing error " + reading);
        }

     
        LOGGER.log(Level.FINE, String.format("Parsed %s - %s  from %s", weight, code, reading));
        return new WeightResponse(weight, code);
    }

    private class WeightResponse {

        public int weight;
        public String code;

        public WeightResponse(int weight, String code) {
            this.weight = weight;
            this.code = code;
        }
    }

    @Override
    public boolean isWorking() {
        // To do: Detect working scale!
        return true;
    }

    public static void main(String[] args) throws SerialException {
        ScaleDevice scale = new ScaleDevice("/dev/sensor_romana");

        while (true) {
            try {
                byte[] read = scale.readBytes(35, 500);
                String readable = new String(read);
                String parsed = scale.readableStringFromBytes(read);
                System.out.println("####################### " + CommonUtils.formattedHourNow() + " ####################");
//                System.out.println(String.format("Original: %s  | Parsed: %s", readable, parsed));
                System.out.println(String.format("Parsed: %s", parsed));
                int weight = scale.parseWeightFromString(parsed).weight;
                System.out.println(String.format("Got weight: %s", weight));
                
            } catch (SerialException ex) {
                System.out.println(String.format("Got weight: Error(%s)", ex.getMessage()));
            }
            System.out.println("------------------------------------------------");
        }
    }
}
