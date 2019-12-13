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
    private int lastWeight;

    private static int RESET_THRESHOLD = 50;

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

        ArrayList<Integer> weightList = new ArrayList<>();
        SerialException last = null;

        int chosenWeight = 0;

        for (int i = 0; i < 120; i++) {
            try {
                int weight = getWeightSingle();

                if (Math.abs(weight - chosenWeight) > RESET_THRESHOLD) {
                    LOGGER.log(Level.INFO, String.format("Reset Threshold reached: "
                            + "Previous %s. Current %s", chosenWeight, weight));
                    weightList.clear();
                }

                weightList.add(weight);
                chosenWeight = CommonUtils.findPopularArrayList(weightList);

            } catch (SerialException ex) {
                LOGGER.log(Level.WARNING, "Could not get weight: {0}", ex.getMessage());
                last = ex;
            }

            int candidateCount = weightList.size();
            boolean zeroCondition = chosenWeight <= 20 && candidateCount == 30;
            boolean previousCondition = Math.abs(lastWeight - chosenWeight) < 40 && candidateCount == 35;
            boolean normalCondition = Math.abs(lastWeight - chosenWeight) >= 40 && chosenWeight > 20 && candidateCount == 10;
            if (zeroCondition ^ normalCondition ^ previousCondition
                    && !(zeroCondition && normalCondition && previousCondition)) {
                break;
            }
        }

        if (weightList.isEmpty()) {
            throw new SerialException("Impossible to get weight", last);
        }

        LOGGER.log(Level.INFO, String.format("Final Weight Chosen: %s, took %s seconds",
                chosenWeight, interval.getSeconds()));

        this.lastWeight = chosenWeight;
        return chosenWeight;

    }
    
    private byte[] readWeightBytes(){
        clearFullBuffer();
        byte[] bytesRead = readBytes(35, 500); // 35 -> Minimum to get a full reading
        return bytesRead;
    }
    
    private int getWeightSingle() throws SerialException {
        byte[] bytesRead = readWeightBytes();
        String reading = new String(bytesRead);
        WeightResponse response = parseWeightFromString(reading);

        LOGGER.log(Level.FINE, String.format("Read %s kg with code %s",
                response.weight, response.code));
        return response.weight;
    }

    private void flushWeights() throws SerialException {
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

    private WeightResponse parseWeightFromString(String reading) throws SerialException {
        int weight;
        String code;

        // Find two spaces, numbers and spaces
        Pattern weightPattern = Pattern.compile("\\).[\\s]{2}[0-9]{5}\\s");
        Matcher weightMatcher = weightPattern.matcher(reading);

        if (weightMatcher.find()) {
            String matched = weightMatcher.group(0);
            weight = Integer.valueOf(matched.substring(4, 9));
            code = Character.toString(matched.charAt(1));
        } else {
            throw new SerialException("Parsing error " + CommonUtils.removeNonPrintable(reading));
        }

        LOGGER.log(Level.FINE, String.format("Parsed %s - %s  from %s", weight, code,
                CommonUtils.removeNonPrintable(reading)));
        return new WeightResponse(weight, code);
    }
    
    @Override
    public boolean isWorking() {
        
        byte[] response = readWeightBytes();
        return !(response.length == 0 || response[0] == 0);
    }

    private class WeightResponse {

        public int weight;
        public String code;

        public WeightResponse(int weight, String code) {
            this.weight = weight;
            this.code = code;
        }
    }

    public static void main(String[] args) throws SerialException {
        ScaleDevice scale = new ScaleDevice("/dev/sensor_romana");

        while (true) {
            try {
                byte[] read = scale.readWeightBytes();

                String reading = new String(read);
                System.out.println("####################### " + CommonUtils.formattedHourNow() + " ####################");
//                System.out.println(String.format("Original: %s  | Parsed: %s", readable, parsed));
                System.out.println(String.format("Parsed: %s", CommonUtils.removeNonPrintable(reading)));
                int weight = scale.parseWeightFromString(reading).weight;
                System.out.println(String.format("Got weight: %s", weight));

            } catch (SerialException ex) {
                System.out.println(String.format("Got weight: Error(%s)", ex.getMessage()));
            }
            System.out.println("------------------------------------------------");

//            final byte[] reading = {(byte) 0x02, (byte) 0x29, (byte) 0x38, (byte) 0x20, (byte) 0x20, (byte) 0x31, (byte) 0x37, (byte) 0x31, (byte) 0x38, (byte) 0x30, (byte) 0x20, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x0D, (byte) 0x3F, (byte) 0x02, (byte) 0x29, (byte) 0x38, (byte) 0x20, (byte) 0x20, (byte) 0x31, (byte) 0x37, (byte) 0x31, (byte) 0x38, (byte) 0x30, (byte) 0x20, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x0D};
//            ScaleDevice scale = new ScaleDevice(){
//                @Override
//                public byte[] readBytes(int x, int y){
//                    return reading;
//                }
//                
//                @Override
//                public void clearBuffer(){
//                    return;
//                }
//            };
//           
//           System.out.println(scale.getWeight());
        }
    }
}
