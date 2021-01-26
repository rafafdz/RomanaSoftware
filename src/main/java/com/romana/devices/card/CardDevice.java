/*
 * sources in CardDatabase.java and SerialDevice.java
 */
package com.romana.devices.card;

import com.romana.devices.SerialDevice;
import com.romana.devices.SerialException;
import com.romana.utilities.CommonUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstraction for common operations relating a Serial device.
 *
 * @author rafael
 */
public class CardDevice extends SerialDevice {

    private static final Logger LOGGER = Logger.getLogger(CardDevice.class.getName());
    private static final int READ_TIMEOUT = 20000;
    
    
    public CardDevice(){}

    public CardDevice(String portPath) throws SerialException {
        super(portPath, 9600, 3);
    }

    public String getCardID() throws SerialException {
        LOGGER.log(Level.INFO, "Sending read signal to Card Reader");
        String response = communicateAndRetry("L", 500, 3);
        System.out.println(response);
  
        if (!"S".equals(response)) {
            throw new SerialException("Card Reader did not respond to Listen signal");
        }
        return readData(READ_TIMEOUT); // 20 seconds of timeout!
    }
    
    @Override
    public boolean isWorking() {
        try {
            String msg = communicate("C", 500);
            return "G".equals(msg);
        } catch (SerialException ex) {
            LOGGER.log(Level.SEVERE, "Exception while asking for Arduino status", ex);
            return false;
        }
    }
    
    // The caller should get the id and the access manually to the database!

//    /**
//     * listen for card id and gives it to database. Thread Blocking!
//     *
//     * @return int
//     * @throws com.romana.serial.SerialException
//     * @throws com.romana.database.DatabaseException
//     */
//    public Integer readCardBalance() throws SerialException, DatabaseException {
//        String cardId = getCardID();
//        database.reload();
//        return database.readCardBalance(cardId);
//    }
//
//    public CardDatabase getCardDatabase() {
//        return database;
//    }
//    /**
//     * This method is so that the operator can recharge the cards credit.
//     *
//     * @param amount (int)
//     * @return CardResponse
//     * @throws com.romana.serial.SerialException
//     * @throws com.romana.database.DatabaseException
//     */
//    public CardResponse addMoney(int amount) throws SerialException, DatabaseException {
//
//        String cardId = getCardID();
//        if (cardId == null) {
//            return new CardResponse(NO_CARD_ENTERED);
//        }
//        return database.addMoney(cardId, amount);
//    }
//
//    /**
//     * calls the listener and gives the id card to the database if found
//     */
//    private CardResponse discountMoney(int amount) throws SerialException, DatabaseException {
//        String cardId = getCardID();
//        if (cardId == null) {
//            return new CardResponse(NO_CARD_ENTERED);
//        }
//        return database.discountMoney(cardId, amount);
//    }
//
//    /**
//     * calls the listener and gives the card id to the database if found
//     *
//     * @return CardResponse
//     * @throws com.romana.serial.SerialException
//     * @throws com.romana.database.DatabaseException
//     */
//    public CardResponse newCard() throws SerialException, DatabaseException {
//
//        String cardId = getCardID();
//        if (cardId == null) {
//            return new CardResponse(NO_CARD_ENTERED);
//        }
//        return database.registerCard(cardId);
//    }
//
//    public void closeCardReader() {
//        cardReaderDevice.closePort();
//    }

        //<editor-fold defaultstate="collapsed" desc="Legacy code -> To be replaced">
        //    /**
        //     * WARNING this function assumes that you already ask for id that exists.
        //     *
        //     * @return String
        //     * @throws JSONException
        //     * @throws IOException
        //     */
        //    public String plateInProgress() throws
        //            JSONException, IOException {
        //        return database.plateInProgress(currentInfo);
        //    }
        //
        //    // To do -> Should be moved!
        //    /**
        //     * WARNING not for checking This method edits the json file, adds new id and plate (or edit the
        //     * previous one) if it's the first time. Delete process if not. Use this method after
        //     * checkSecondStage.
        //     *
        //     * @param plate (String)
        //     * @return CardResponse
        //     * @throws java.lang.InterruptedException
        //     */
        //    public CardResponse twoStageWeighing(String plate) throws SerialException {
        //
        //        CardResponse stage = database.secondWeighing(currentInfo, plate);
        //        if (stage == FIRST_STAGE) {
        //            return discountCurrentCard(twoStagePrice);
        //        } else { // (stage == SECOND_STAGE)
        //            return discountCurrentCard(secondStagePrice);
        //        }
        //    }
        //
        //    /**
        //     * WARNING Only for checking if card id is being used with other plate notice that you should
        //     * only accept one two-stage weighing at a time otherwise notify the client that he will loose
        //     * the previous one. Doesn't change the json information. Use this method to ask before using
        //     * twoStageWeighing.
        //     *
        //     * @param plate (String)
        //     * @return CardResponse
        //     * @throws java.io.IOException
        //     * @throws java.lang.InterruptedException
        //     */
        //    public CardResponse checkSecondStage(String plate) throws IOException,
        //            InterruptedException, SerialException,
        //            SerialException, SerialException,
        //            RealDisconnectionException {
        //
        //        getInformation("L", 30000, '-', resetTimes);
        //        if (-1 == currentInfo.indexOf('-')) {
        //            return NO_CARD_ENTERED;
        //        }
        //        return database.checkWeighing(currentInfo, plate);
        //    }
        //
        //    /**
        //     *
        //     * @return CardResponse
        //     * @throws IOException
        //     * @throws InterruptedException
        //     * @throws SerialException
        //     * @throws RealDisconnectionException
        //     */
        //    public CardResponse individual() throws IOException,
        //            InterruptedException, SerialException,
        //            SerialException, SerialException,
        //            RealDisconnectionException {
        //
        //        System.out.println("Estoy en individual");
        //        System.out.println(discountMoney(individualPrice));
        //
        //        return discountMoney(individualPrice);
        //    }
        //
        //    /**
        //     *
        //     * @param axis_number (int)
        //     * @return CardResponse
        //     * @throws IOException
        //     * @throws InterruptedException
        //     * @throws SerialException
        //     * @throws SerialException
        //     * @throws SerialException
        //     * @throws RealDisconnectionException
        //     */
        //    public CardResponse axisWeighing(int axis_number) throws IOException,
        //            InterruptedException, SerialException,
        //            SerialException, SerialException,
        //            RealDisconnectionException {
        //
        //        return discountMoney(axisPrice * axis_number);
        //    }
        //</editor-fold>
    
    public static void main(String[] args) throws SerialException {
        CommonUtils.printCurrentThread();
        CardDevice reader = new CardDevice("COM4");
        System.out.println(reader.getCardID());
        System.out.println(reader.isWorking());
        //System.out.println(operations.readCardBalance());
        //operations.closeCardReader();
    }
}
