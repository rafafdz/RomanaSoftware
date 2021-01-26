/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.devices.ticket;

import com.romana.devices.ExternalDevice;
import com.romana.utilities.CommonUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rafael
 */
public class TicketDevice extends ExternalDevice {

    private static final Logger LOGGER = Logger.getLogger(TicketDevice.class.getName());
    private static String TICKET_FOLDER = "external/ticket_python";
    private static String PYTHON_SCRIPT = "romana_ticket.py";

    private String vendorId;
    private String deviceId;
    private int inEp;
    private int outEp;

    public TicketDevice() {
    };
    
    
    public TicketDevice(String vendorId, String deviceId, int inEp, int outEp){
        this.vendorId = vendorId;
        this.deviceId = deviceId;
        this.inEp = inEp;
        this.outEp = outEp;
    }

    private int executeCommand(String command) {
        File pythonDir = new File(TICKET_FOLDER);

        String[] executeCommand = new String[]{"/bin/bash", "-c", command};
        ProcessBuilder probuilder = new ProcessBuilder(executeCommand);
        probuilder.directory(pythonDir);
        try {
            Process process = probuilder.start();
            // Show script output
//        InputStream is = process.getInputStream();
//        InputStreamReader isr = new InputStreamReader(is);
//        BufferedReader br = new BufferedReader(isr);
//        String line;
//        System.out.printf("Output of running %s is:\n", Arrays.toString(executeCommand));
//        while ((line = br.readLine()) != null) {
//            System.out.println(line);
//        }

            return process.waitFor();

        } catch (InterruptedException | IOException ex) {
            LOGGER.log(Level.SEVERE, "Critical error at executing command" + command, ex);
            return -1;
        }
    }

    private int executeTicketCommand(String command){
        // To do: Fix hang when there is no paper!
        String newCommand = String.format("source venv/bin/activate && python %s %s %s %s %s %s",
                PYTHON_SCRIPT, vendorId, deviceId, inEp, outEp, command);
        System.out.println("Command " + newCommand);
        return executeCommand(newCommand);
    }

    private String generateBaseArg(String plate, String url, int totalPrice) {
        // Hardcoded to print 2 copies of each!
        return String.format("--plate %s --url %s --price %s --copies 2", plate, url, totalPrice);
    }
    
    public boolean printSimpleTicket(String plate, String url, int totalPrice, int weight){
        String base = generateBaseArg(plate, url, totalPrice);
        String newCommand = String.format("%s -wt SIMPLE -sw %s", base, weight);
        return executeTicketCommand(newCommand) == 0;
    }
    
    public boolean printTwoPhaseFirstTicket(String plate, String url, int totalPrice, 
            int firstWeight, String firstDate){
        
        String base = generateBaseArg(plate, url, totalPrice);
        String newPart = String.format("-wt TWO_PHASE_FIRST -fw %s -fd '%s'", firstWeight, 
                firstDate);
        String newCommand = base + " " + newPart;
        return executeTicketCommand(newCommand) == 0;
    }
    
    
    public boolean printTwoPhaseFinalTicket(String plate, String url, int totalPrice, int firstWeight,
            int lastWeight, String firstDate, String lastDate){
        
        String base = generateBaseArg(plate, url, totalPrice);
        String newPart = String.format("-wt TWO_PHASE_FINAL -fw %s -fd '%s' -lw %s -ld '%s'", firstWeight, 
                firstDate, lastWeight, lastDate);
        String newCommand = base + " " + newPart;
        return executeTicketCommand(newCommand) == 0;
    }
    
    public boolean printAxisTicket(String plate, String url, int totalPrice, int... weights){
        
        String base = generateBaseArg(plate, url, totalPrice);
        // Join all weights with a space
        String[] sarr = Arrays.stream(weights).mapToObj(String::valueOf).toArray(String[]::new);
        String result = String.join(" ", sarr);
        
        String newCommand = String.format("%s -wt AXIS -w %s", base, result);
        return executeTicketCommand(newCommand) == 0;
    }

    @Override
    public boolean isWorking() {
        return executeTicketCommand("--check_status") == 0;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        TicketDevice ticket = new TicketDevice("0x0416", "0x5011", 1, 1);
        ticket.printSimpleTicket("POTO22", "www.pilichula.net", 123, 23);
    }
}
