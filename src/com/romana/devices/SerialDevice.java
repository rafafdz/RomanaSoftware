/*
 * sources:
 * http://www.mschoeffler.de/2017/12/29/
tutorial-serial-connection-between-java-application-and-arduino-uno/
https://github.com/Fazecast/jSerialComm/blob/master/src/test/java/
com/fazecast/jSerialComm/SerialPortTest.java
http://fazecast.github.io/jSerialComm/javadoc/com/fazecast/
jSerialComm/SerialPort.html
https://github.com/SF2311/ArduinoUI/tree/master/src/sample
 */
package com.romana.devices;

import com.fazecast.jSerialComm.*;
import com.romana.userinterface.UserInterface;
import com.romana.utilities.CommonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstraction for sending and receiving data to a COM port using Serial.
 *
 * @author santiago
 */
public abstract class SerialDevice extends ExternalDevice {

    // TO do: fix logging!!
    private static final Logger LOGGER = Logger.getLogger(UserInterface.class.getName());
    public SerialPort userPort;
    private String comPort;
    private int baudRate;
    private boolean connected = false;

    public SerialDevice() {
    }

    /**
     * This constructor needs the string indicating the serial port that you are connecting to. If
     * you want to see available ports or if a specific port is available use the functions below.
     *
     * @param portPath (String)
     * @param baudRate
     * @throws SerialException
     */
    public SerialDevice(String portPath, int baudRate) throws SerialException {
        setupPort(portPath, baudRate);
    }

    public SerialDevice(String portPath, int baudRate, int maxRetries) throws SerialException {
        tryToConnectDevice(portPath, baudRate, maxRetries);
    }

    /**
     * Tries to connect to the serial device for maxRetries times. If there is no success, throw
     * SerialException
     *
     * @param portPath
     * @param baudRate
     * @param maxRetries
     * @return
     * @throws com.romana.devices.SerialException
     */
    private void tryToConnectDevice(String portPath, int baudRate, int maxRetries)
            throws SerialException {
        for (int i = 0; i < maxRetries; i++) {
            try {
                setupPort(portPath, baudRate);
                return;

            } catch (SerialException ex) {
                Logger.getGlobal().log(Level.WARNING, "Could not open port, retrying", ex);
            }
        }
        throw new SerialException("Could not connect to device at " + portPath);
    }

    /**
     * Used when a device instantly sends a response after some 'input' message.
     *
     * @param input (String)
     * @param timeoutMillis
     * @return String
     * @throws SerialException
     */
    public String communicate(String input, int timeoutMillis) throws SerialException {
        if (!connected) {
            throw new SerialException("Attempting communication on a closed port");
        }
        sendData(input);
        return readData(timeoutMillis);
    }

    public String communicateAndRetry(String input, int millis, int retries)
            throws SerialException {

        try {
            return communicate(input, millis);
        } catch (SerialException ex) {
            LOGGER.log(Level.WARNING, "Communication with {0} failed, retrying", comPort);
            for (int j = 0; j < retries; j++) {
                try {
                    restartPort();
                } catch (SerialException e) {
                    LOGGER.log(Level.WARNING, "Restart failed, retrying", e);
                    CommonUtils.sleep(1000);
                }
            }
        }
        if (!connected) {
            throw new SerialException("Could not communicate with " + comPort + " after retrying");
        } else {
            return communicate(input, millis);
        }
    }

    public void closePort() {
        userPort.closePort();
    }

    public SerialPort getSerialPort() {
        return userPort;
    }

    /**
     * Checks if a specific serial port is available.
     *
     * @param puerto (String)
     * @return boolean
     */
    public boolean checkPort(String puerto) {
        SerialPort ports[] = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            if (puerto.equals(port.getSystemPortName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method return a a list with all available serial ports.
     *
     * @return List String
     */
    public List<String> availablePorts() {
        SerialPort ports[] = SerialPort.getCommPorts();
        List<String> available = new ArrayList();
        for (SerialPort port : ports) {
            available.add(port.getSystemPortName());
        }
        return available;
    }

    /**
     * This functions is for setupPort the connection with a port it assumes the port is available
     * (otherwise an exception is thrown)
     *
     * @param portPath
     * @param baudRate
     * @throws SerialException
     */
    private void setupPort(String portPath, int baudRate) throws SerialException {
        this.comPort = portPath;
        this.baudRate = baudRate;

        userPort = SerialPort.getCommPort(comPort);
        userPort.setBaudRate(baudRate);

        userPort.openPort(1000);
        if (userPort.isOpen()) {
            CommonUtils.sleep(2000); // System.out.println("Port initialized!");
            connected = true;
            LOGGER.log(Level.INFO, "The device at {0} has been connected!", comPort);
        } else {
            throw new SerialException("Port Not Available");
        }
    }

    public void restartPort() throws SerialException {
        String port = userPort.getSystemPortName();
        userPort.closePort();
        userPort.removeDataListener();
        setupPort(comPort, baudRate);
    }

    private void sendData(String message) throws SerialException {

        byte[] buffer = message.getBytes();
        int numSent = userPort.writeBytes(buffer, buffer.length);

        if (message.length() > 0 && numSent <= 0) {
            throw new SerialException("Error sending \"" + message + "\" to " + comPort);
        }
    }

    public String readData(int timeout) throws SerialException {
        String response = "";
        userPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, timeout, 0);
        try {
            int elapsed = 0;
            while (elapsed < timeout) {

                int numRead = userPort.bytesAvailable();

                if (numRead == -1) {
                    throw new SerialException("Communication error with " + comPort);
                }

                while (numRead > 0) {
                    byte[] readBuffer = new byte[numRead];
                    userPort.readBytes(readBuffer, numRead);
                    response += new String(readBuffer);
                    CommonUtils.sleep(20);
                    numRead = userPort.bytesAvailable();
                }

                if (response.length() > 0) {
                    return response;
                }

                CommonUtils.sleep(50);
                elapsed += 50;
            }

            return null; // Returned if timeout reached and no mesage is received

        } catch (SerialException ex) {
            throw new SerialException("Error while reading response:", ex);
        }
    }
    
    public void clearBuffer(){
        int toClear = userPort.bytesAvailable();
        byte[] buffer = new byte[toClear];
        userPort.readBytes(buffer, toClear);
        
        
        // To do: Fix output
        // WARNING: Messy output!
        LOGGER.log(Level.FINE, String.format("Cleared Serial buffer, %s bytes -> %s",
                toClear, CommonUtils.removeNonPrintable(new String(buffer))));
    }

    public byte[] readBytes(int length, int timeout) {
        userPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, timeout, 0);
        byte[] readBuffer = new byte[length];
        int elapsed = 0;
        int byteCount = 0;
        while (elapsed < timeout && byteCount < length) {

            int numRead = userPort.bytesAvailable();

            if (numRead > 0) {
                if (byteCount + numRead > length) {
                    numRead = length - byteCount;
                }

            userPort.readBytes(readBuffer, numRead, byteCount);
            
            byteCount += numRead;    
//<editor-fold defaultstate="collapsed" desc="comment">
//int[] intArray = new int[numRead];

//for (int i = 0; i < readBuffer.length; intArray[i] = readBuffer[i++]);


//System.out.println(Arrays.toString(intArray));

//System.out.println("Read: " + new String(readBuffer));

//                for (byte singleByte : readBuffer) {
//                    String hex = String.format("%02x", singleByte);
//                    charCount++;
//                    response.add(hex);
//                }
//</editor-fold>
            }
            CommonUtils.sleep(20);
            elapsed += 20;
        }
        LOGGER.log(Level.FINEST, String.format("Read %s bytes from serial: %s", 
                byteCount, CommonUtils.bytesToHex(readBuffer)));
        return readBuffer;

    }

    public static void main(String[] args) throws SerialException {
        SerialDevice cards = new SerialDevice("/dev/ttyACM0", 9600, 3) {
            @Override
            public boolean isWorking() {
                return true;
            }
        };
        CommonUtils.sleep(2000);
        // cards.sendData("L");
        // CommonUtils.sleep(100);
        while (true) {
            System.out.println(cards.communicate("L", 500));
            System.err.println("Tarjeta: " + cards.readData(20000));
        }
    }
}
