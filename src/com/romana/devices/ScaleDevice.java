/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.devices;

import com.fazecast.jSerialComm.SerialPort;
import com.romana.utilities.CommonUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author rafael
 */
public class ScaleDevice extends SerialDevice {

    private static final Logger LOGGER = Logger.getLogger(ScaleDevice.class.getName());

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
        SerialException last = null;
        for (int i = 0; i < 15; i++) {
            try {
                return getWeightSingle();
            } catch (SerialException ex){
                CommonUtils.sleep(1000);
                last = ex;
            }
        }
        throw new SerialException("Impossible to get weight", last);
    }
    
    private int getWeightSingle() throws SerialException {
        clearBuffer();
        byte[] bytesRead = readBytes(35, 500); // 35 -> Minimum 

        String readable = readableStringFromBytes(bytesRead);
        WeightResponse response = parseWeightFromString(readable);

        LOGGER.log(Level.INFO, String.format("Read %s kg with code %s",
                response.weight, response.code));
        return response.weight;
    }

    private String readableStringFromBytes(byte[] byteArray) {
        int[] ints = CommonUtils.byteArrayToIntArray(byteArray);

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

        System.out.println("READING " + reading);

        if (weightMatcher.find()) {
            String matched = weightMatcher.group(0).replaceAll("\\s", "");
            weight = Integer.valueOf(matched);
            //System.out.println("Weight " + weight);
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
        return true;
    }

    public static void main(String[] args) throws SerialException {
        ScaleDevice scale = new ScaleDevice("/dev/ttyUSB0");

        while (true) {
            try {
                System.out.println(CommonUtils.formattedHourNow() + " " + scale.getWeight());
            } catch (SerialException ex) {
                System.out.println("Error " + ex.getMessage());
            }
            System.out.println("------------------------------------------------");
            CommonUtils.sleep(1000);
        }
    }
}
