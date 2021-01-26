/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.devices.scale;

import com.google.gson.Gson;
import com.romana.devices.SerialException;
import com.romana.userinterface.UserInterface;
import com.romana.utilities.CommonUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rafael
 */
public class RemoteScale implements Scale {

    private static final Logger LOGGER = Logger.getLogger(UserInterface.class.getName());
    private static final String API_HOST = "localhost";
    private static final int API_PORT = 3005;

    @Override
    public int getWeight() throws ScaleException {
        try {
            
            CommonUtils.TimeInterval interval = new CommonUtils.TimeInterval();
            Map data = sendGet("/weight");
            int weight = ((Double) data.get("weight")).intValue();
            LOGGER.log(Level.INFO, "Got weight {0} from remote API", weight);
            LOGGER.log(Level.FINE, "Request took {0} seconds", interval.getSeconds());
            return weight;
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Got exception while getting remote weight", ex);
            throw new ScaleException("Remote communication with scale failed");
        }
    }

    @Override
    public boolean isWorking() {
        try {
            Map data = sendGet("/status");            
            return (boolean) data.get("working");
        } catch (IOException ex) {
            return false;
        }
    }

    public Map sendGet(String path) throws IOException {
        String url = String.format("http://%s:%d%s", API_HOST, API_PORT, path);

        HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
        httpClient.setRequestMethod("GET");

        // Send the request
        int responseCode = httpClient.getResponseCode();
        
        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                httpClient.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }
        
        Map jsonObject = new Gson().fromJson(content.toString(), Map.class);
        return jsonObject;

    }

    public static void main(String[] args) throws ScaleException {
        RemoteScale scale = new RemoteScale();
        System.out.println(scale.getWeight());
    }
}
