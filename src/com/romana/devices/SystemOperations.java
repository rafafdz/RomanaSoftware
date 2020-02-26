/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.devices;

import com.romana.database.DatabaseException;
import static com.romana.devices.ScaleResponse.ScaleCode.*;
import com.romana.utilities.CommonUtils;
import com.romana.utilities.Configuration;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Defines high-Level System routines, Integrating multiples devices.
 *
 * @author rafael
 */
public class SystemOperations {

    private static final Logger LOGGER = Logger.getLogger(SystemOperations.class.getName());
    private static final String FINISHED_PATH = "weights/finished";
    private static final String PROCESS_PATH = "weights/process";
    private static final String WEIGHT_FILENAME = Configuration.getStringConfig("WEIGHT_INFO_FILENAME");
    private static final String CARD_PORT = Configuration.getStringConfig("CARD_PORT");
    private static final String SCALE_PORT = Configuration.getStringConfig("SCALE_PORT");
    private static final String TICKET_VENDOR_ID = Configuration.getStringConfig("TICKET_VENDOR_ID");
    private static final String TICKET_PRODUCT_ID = Configuration.getStringConfig("TICKET_PRODUCT_ID");
    private static final int TICKET_IN_EP = Configuration.getIntConfig("TICKET_IN_EP");
    private static final int TICKET_OUT_EP = Configuration.getIntConfig("TICKET_OUT_EP");

    private static final int WEIGHT_THRESHOLD = Configuration.getIntConfig("WEIGHT_THRESHOLD");
    private static final int DIFFERENCE_THRESHOLD = Configuration.getIntConfig("DIFFERENCE_THRESHOLD");
    private ScaleDevice scaleDevice;
    private CardDevice cardDevice;
    private TicketDevice ticketDevice;

    public SystemOperations() {
        generateWeightDirs();
    }

    public SystemOperations(String port) throws SerialException {
        generateWeightDirs();
    }

    public void setScaleDevice(ScaleDevice scaleDevice) {
        this.scaleDevice = scaleDevice;
    }

    public void setCardDevice(CardDevice cardDevice) {
        this.cardDevice = cardDevice;
    }

    public void setTicketDevice(TicketDevice ticketDevice) {
        this.ticketDevice = ticketDevice;
    }
    
    
    public ScaleDevice getScaleDevice(){
        return scaleDevice;
    }

    public void initializeAllDevices() throws SerialException {

        // TO DO: CREATE SEPARATE CLASSES or clean up at least!
        if (CARD_PORT.equals("TEST")) {
            this.cardDevice = new CardDevice() {

                @Override
                public String getCardID() {
                    return "TESTCARD0";
                }

                @Override
                public boolean isWorking() {
                    return true;
                }
            };
        } else {
            cardDevice = new CardDevice(CARD_PORT);
        }

        if (SCALE_PORT.equals("TEST")) {
            this.scaleDevice = new ScaleDevice() {
                @Override
                public int getWeight() {
                    CommonUtils.sleep(1000);
                    return (int) CommonUtils.randomDouble(1000.0, 2500.0);
                }

                @Override
                public boolean isWorking() {
                    return true;
                }
            };
        } else if (SCALE_PORT.equals("MANUAL")) {
            this.scaleDevice = new ScaleDevice() {
                @Override
                public int getWeight() {
                    
                    int weight = getCurrentWeight();
                    setManualWeight(0);
                    return weight;
                }

                @Override
                public boolean isWorking() {
                    return true;
                }
            };
        } else {
            this.scaleDevice = new ScaleDevice(SCALE_PORT);
        }

        if (TICKET_VENDOR_ID.equals("TEST")) {
            this.ticketDevice = new TicketDevice() {

                @Override
                public boolean printSimpleTicket(String plate, String url, int totalPrice, int weight) {
                    return true;
                }

                @Override
                public boolean printTwoPhaseFinalTicket(String plate, String url, int totalPrice, int firstWeight,
                        int lastWeight, String firstDate, String lastDate) {
                    return true;
                }

                @Override
                public boolean printAxisTicket(String plate, String url, int totalPrice, int... weights) {
                    return true;
                }

                @Override
                public boolean isWorking() {
                    return true;
                }
            };
        } else {
            this.ticketDevice = new TicketDevice(TICKET_VENDOR_ID, TICKET_PRODUCT_ID,
                    TICKET_IN_EP, TICKET_OUT_EP);
        }

    }
    // public ScaleResponse simpleWeight(String plate, String cardId)

    public ScaleResponse simpleWeight(WeightInfo newSimple) throws SerialException,
            DatabaseException {

        int weight = scaleDevice.getWeight();
        LOGGER.log(Level.INFO, "Weight Simple read {0} kg", weight);
        if (!isValidWeight(weight)) {
            return new ScaleResponse(WEIGHT_TOO_LOW);
        }

        newSimple.addWeight(weight);
        // Code for taking pictures and adding them to the folder
        newSimple.finishProcess();

        generateDirAndSerialize(newSimple);
        return new ScaleResponse(OPERATION_OK, newSimple);
    }

    public ScaleResponse startTwoPhase(WeightInfo newTwoPhase) throws SerialException,
            DatabaseException {
        // There can be only one Two-Phase process ongoing per plate!

        int firstWeight = scaleDevice.getWeight();

        if (!isValidWeight(firstWeight)) {
            return new ScaleResponse(WEIGHT_TOO_LOW);
        }

        newTwoPhase.addWeight(firstWeight);

        // Take pictures
        String plate = newTwoPhase.getPlate();
        Path basePath = Paths.get(PROCESS_PATH).resolve(plate);
        File baseFile = basePath.toFile();
        if (!baseFile.exists()) {
            baseFile.mkdirs();
        } else {
            return new ScaleResponse(PLATE_PROCESS_ONGOING);
        }

        try {
            File newWeightFile = basePath.resolve(WEIGHT_FILENAME).toFile();
            newWeightFile.createNewFile();
            CommonUtils.serializeToFile(newTwoPhase, newWeightFile.toString());
            LOGGER.log(Level.INFO, "Serializing Weight file for {0}", plate);
            return new ScaleResponse(OPERATION_OK, newTwoPhase);

        } catch (IOException ex) {
            throw new DatabaseException("Error while creating TwoPhase file", ex);
        }
    }

    public void discardTwoPhase(WeightInfo twoPhase) {
        // To do: DO NOT Discard, save incomplete!
        String plate = twoPhase.getPlate();
        Path dirPath = Paths.get(PROCESS_PATH, plate);
        Path filePath = dirPath.resolve(WEIGHT_FILENAME);
        filePath.toFile().delete();
        dirPath.toFile().delete();
    }

    /**
     * Continue the weighing process. Assumes that all the plate / card verifications have already
     * been done. The truck has to be on the scale while calling this method.
     *
     * @param toContinue
     * @return
     * @throws com.romana.devices.SerialException
     * @throws com.romana.database.DatabaseException
     */
    public ScaleResponse finishTwoPhase(WeightInfo toContinue) throws SerialException,
            DatabaseException {

        String plate = toContinue.getPlate();

        int secondWeight = scaleDevice.getWeight();
        if (!isValidWeight(secondWeight)) {
            return new ScaleResponse(WEIGHT_TOO_LOW);
        }

        toContinue.addWeight(secondWeight);
        toContinue.finishProcess();

        // To do: Take Photo!
        Path folderPath = Paths.get(PROCESS_PATH).resolve(plate);
        String filePath = folderPath.resolve(WEIGHT_FILENAME).toString();

        try {
            CommonUtils.serializeToFile(toContinue, filePath);

        } catch (IOException ex) {
            throw new DatabaseException("Error while rewriting TwoPhase file", ex);
        }

        String folderName = CommonUtils.formattedFolderDate(toContinue.getCreationDate());
        Path destiny = Paths.get(FINISHED_PATH).resolve(folderName);
        boolean moveSuccess = moveFolder(folderPath, destiny);

        if (!moveSuccess) {
            throw new DatabaseException("Could not move folder to destiny");
        }

        folderPath.toFile().delete();
        return new ScaleResponse(OPERATION_OK, toContinue);
    }

    public ScaleResponse nextAxis(WeightInfo weightAxis) throws SerialException {

        CommonUtils.sleep(500); // Wait for stabilization of value
        int weight = scaleDevice.getWeight();
        if (!isValidWeight(weight)) {
            return new ScaleResponse(WEIGHT_TOO_LOW);
        }

        int weightSum = weightAxis.getWeightSum();

        int difference = Math.abs(weight - weightSum);
        if (difference < DIFFERENCE_THRESHOLD) {
            return new ScaleResponse(NOT_ENOUGH_DIFFERENCE);
        }

        weightAxis.addWeight(difference); // Only the difference!
        return new ScaleResponse(OPERATION_OK);
    }

    public ScaleResponse finishAxis(WeightInfo weightAxis) throws DatabaseException {

        weightAxis.finishProcess();
        generateDirAndSerialize(weightAxis);
        return new ScaleResponse(OPERATION_OK);
    }

    private void generateDirAndSerialize(WeightInfo info) throws DatabaseException {

        String folderName = CommonUtils.formattedFolderDate(info.getCreationDate());

        Path dirPath = Paths.get(FINISHED_PATH).resolve(folderName);
        boolean dirCreated = dirPath.toFile().mkdirs();
        if (!dirCreated) {
            throw new DatabaseException("Could not create weight folder " + folderName);
        }

        String newWeightFile = dirPath.resolve(WEIGHT_FILENAME).toString();
        try {
            CommonUtils.serializeToFile(info, newWeightFile);
        } catch (IOException ex) {
            throw new DatabaseException(ex);
        }
    }

    private boolean isValidWeight(double weight) {
        // If the scale is givig fake reading due to communication problems, there
        // should be an exception thrown by scaleDevice.getWeight();

        return weight >= WEIGHT_THRESHOLD;
    }

    public WeightInfo getOngoingWeightInfo(String plate) {
        if (!hasOngoingTwoPhase(plate)) {
            return null;
        }
        String infoPath = Paths.get(PROCESS_PATH).resolve(plate).resolve(WEIGHT_FILENAME).toString();

        try {
            return CommonUtils.deserializeFromFile(infoPath, WeightInfo.class
            );
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public boolean hasOngoingTwoPhase(String plate) {
        File folderFile = Paths.get(PROCESS_PATH).resolve(plate).toFile();
        return folderFile.isDirectory();
    }

    public boolean plateMatchesCard(String plate, String card) {
        WeightInfo ongoingWeight = getOngoingWeightInfo(plate);
        if (ongoingWeight == null) {
            return false;
        }
        return card.equals(ongoingWeight.getCardId());
    }

    private void generateWeightDirs() {
        File finished = new File(FINISHED_PATH);
        File process = new File(PROCESS_PATH);
        if (!finished.exists()) {
            finished.mkdirs();
            LOGGER.log(Level.INFO, "Creating the path {0}", finished.toString());
        }
        if (!process.exists()) {
            process.mkdirs();
            LOGGER.log(Level.INFO, "Creating the path {0}", process.toString());
        }

        Paths.get(PROCESS_PATH).toFile().mkdirs();
    }

    private boolean moveFolder(Path source, Path destiny) {
        if (!source.toFile().isDirectory()) {
            return false;
        }
        destiny.toFile().mkdirs();

        for (File file : source.toFile().listFiles()) {
            try {
                Path sourcePath = Paths.get(file.getPath());
                Path destinyPath = destiny.resolve(file.getName());
                Files.move(sourcePath, destinyPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Problem while moving files", ex);
                return false;
            }
        }
        return true;
    }

    public String getCardId() throws SerialException {
        return cardDevice.getCardID();
    }

    public boolean printSimpleTicket(String plate, String url, int totalPrice, int weight) {
        return ticketDevice.printSimpleTicket(plate, url, totalPrice, weight);
    }

    public boolean printTwoPhaseFirstTicket(String plate, String url, int totalPrice, int firstWeight,
            String firstDate) {
        return ticketDevice.printTwoPhaseFirstTicket(plate, url, totalPrice, firstWeight, firstDate);
    }

    public boolean printTwoPhaseFinalTicket(String plate, String url, int totalPrice, int firstWeight,
            int lastWeight, String firstDate, String lastDate) {
        return ticketDevice.printTwoPhaseFinalTicket(plate, url, totalPrice,
                firstWeight, lastWeight, firstDate, lastDate);
    }

    public boolean printAxisTicket(String plate, String url, int totalPrice, int... weights) {
        return ticketDevice.printAxisTicket(plate, url, totalPrice, weights);
    }

    public static enum WeightType {
        SIMPLE,
        TWO_PHASE,
        AXIS
    }

    public static void main(String[] args) throws SerialException, DatabaseException {
//        SystemOperations ops = new SystemOperations("/dev/ttyACM0");
//        WeightInfo weight = ops.startAxis("holaaPrueba", "BBCITA").info;
//        System.out.println(ops.nextAxis(weight));
//        System.out.println(ops.finishAxis(weight));        
    }
}
