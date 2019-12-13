/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author rafael
 */
public class CommonUtils {

    private static final Logger LOGGER = Logger.getGlobal();

    private static final String DATE_PATTERN = "dd-MM-yyyy HH:mm:ss";
    private static final String HOUR_PATTERN = "HH:mm";
    private static final String FOLDER_DATE = "dd-MM-yyyy_HH-mm-ss";

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_PATTERN);
    private static final SimpleDateFormat HOUR_FORMATTER = new SimpleDateFormat(HOUR_PATTERN);
    private static final SimpleDateFormat FOLDER_FORMATTER = new SimpleDateFormat(FOLDER_DATE);

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String formatDate(Date date) {
        return DATE_FORMATTER.format(date);
    }

    public static String formattedDateNow() {
        return formatDate(new Date());
    }

    public static String formattedHourNow() {
        return HOUR_FORMATTER.format(new Date());
    }

    public static double hourDifferenceDecimal(Date date1, Date date2) {
        long diffInMillies = dateDiff(date1, date2);
        System.out.println(diffInMillies);
        return (double) diffInMillies / (1000 * 60 * 60);
    }

    public static long hourDifferenceDates(Date date1, Date date2) {
        long diffInMillies = dateDiff(date1, date2);
        return TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static long minuteDifferenceDates(Date date1, Date date2) {
        long diffInMillies = dateDiff(date1, date2);
        return TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static long secondDifferenceDates(Date date1, Date date2) {
        long diffInMillies = dateDiff(date1, date2);
        return TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    private static long dateDiff(Date date1, Date date2) {
        return Math.abs(date2.getTime() - date1.getTime());
    }

    public static String formattedFolderDate(Date date) {
        return FOLDER_FORMATTER.format(date);
    }

    public static String getFileString(Path path) throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, StandardCharsets.UTF_8);
    }

    public static String getResourceFileString(String resourcePath) throws IOException {
        try {
            String path = resourcePath.replace('\\', '/'); // Fix for windows
            InputStream stream = resourceStream(path);
            String content = new BufferedReader(new InputStreamReader(stream))
                    .lines().collect(Collectors.joining("\n"));
            return content;
        } catch (NullPointerException ex) {
            throw new IOException("Could not open file at " + resourcePath);
        }
    }

    public static boolean isImageFile(Path path) {
        String[] splitted = path.toString().split("\\.");
        if (splitted.length <= 1) {
            return false;
        } else {
            String extension = splitted[splitted.length - 1];
            return extension.matches("jpg|jpeg|img|png");
        }
    }

    public static void writeStringToFile(String path, String fileContent) {

        File file = new File(path);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try {
            file.createNewFile();

            try (PrintWriter out = new PrintWriter(new File(path))) {
                out.println(fileContent);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Could not write file", ex);
        }
    }

    public static void serializeToFile(Object object, String filepath) throws IOException {
        Path path = Paths.get(filepath);
        Path parent = path.getParent();
        if (parent != null) {
            parent.toFile().mkdirs();
        }

        try (FileWriter writer = new FileWriter(filepath)) {
            GSON.toJson(object, writer);
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public static void printCurrentThread() {
        System.out.println(Thread.currentThread().getName());
    }

    public static double randomDouble(double min, double max) {
        return min + new Random().nextDouble() * (max - min);
    }

    public static double roundTwoDecimals(double number) {
        return (double) Math.round(number * 100d) / 100d;
    }

    public static URL resourceURL(String name) {
        URL urlPath = new Object().getClass().getResource(name);
        return urlPath;
    }

    public static String webUrlFromDate(Date date) {
        long seconds = Math.round((double) (date.getTime() / 1000));
        return Long.toString(seconds, 36).toUpperCase();
    }

    public static Date dateFromWebUrl(String webUrl) {
        long seconds = Long.parseLong(webUrl, 36) * 1000;
        return new Date(seconds);
    }

    public static String[] byteArrayToHexArray(byte[] byteArray) {
        String[] response = new String[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            byte currentByte = byteArray[i];
            String hex = String.format("%02x", currentByte);
            response[i] = hex;
        }
        return response;
    }

    public static int[] byteArrayToIntArray(byte[] byteArray) {
        int[] response = new int[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            int currentInt = (int) byteArray[i];
            response[i] = currentInt;
        }
        return response;
    }

    public static String unsignedByteToString(byte[] unsignedArray) {
        StringBuilder str = new StringBuilder();
        for (byte unsigned : unsignedArray) {

            int fixed = Byte.toUnsignedInt(unsigned);
            str.append((char) fixed);
        }
        return str.toString();
    }

    public static byte[] getResourceBytes(String name) throws IOException {
        String path = name.replace('\\', '/'); // Fix for windows
        InputStream is = resourceStream(path);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[8192];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    public static InputStream resourceStream(String name) {
        return new Object().getClass().getResourceAsStream(name);
    }

    public static String removeNonPrintable(String str) {
        return str.replaceAll("\\p{C}", "?");
    }

    public static boolean resourceExists(String name) {
        try {
            getResourceBytes(name);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public static <T extends Object> T deserializeFromFile(String filepath, Class<T> classOfT)
            throws IOException {
        String fileContent = getFileString(Paths.get(filepath));
        return GSON.fromJson(fileContent, classOfT);
    }

    public static int findPopular(int[] intArray) {

        if (intArray == null || intArray.length == 0) {
            return 0;
        }

        Arrays.sort(intArray);

        int previous = intArray[0];
        int popular = intArray[0];
        int count = 1;
        int maxCount = 1;

        for (int i = 1; i < intArray.length; i++) {
            if (intArray[i] == previous) {
                count++;
            } else {
                if (count > maxCount) {
                    popular = intArray[i - 1];
                    maxCount = count;
                }
                previous = intArray[i];
                count = 1;
            }
        }
        return count > maxCount ? intArray[intArray.length - 1] : popular;
    }
    
    
    public static int findPopularArrayList(ArrayList<Integer> arrayList){
        int[] intArray = arrayList.stream().mapToInt(i -> i).toArray();
        return findPopular(intArray);
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = HEX_ARRAY[v >>> 4];
            hexChars[j * 3 + 1] = HEX_ARRAY[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }
    

    public static class TimeInterval {

        private long startTime = 0;
        private long finalTime = 0;

        public TimeInterval() {
            startTime = System.currentTimeMillis();
        }

        public void stop() {
            if (finalTime == 0) {
                finalTime = System.currentTimeMillis();
            }
        }

        public double getSeconds() {
            stop();
            return roundTwoDecimals((double) (finalTime - startTime) / 1000);
        }

        @Override
        public String toString() {
            stop();
            return String.valueOf(finalTime - startTime);
        }

    }

    public static void main(String[] args) throws IOException, ParseException {
//        TimeInterval interval = new CommonUtils.TimeInterval();
//        sleep(1440);
//        System.out.println(interval.getSeconds());
//        System.out.println(interval);
        byte[] bytes = {(byte)0x90, 121, 101, 45, 62};
        System.out.println(bytesToHex(bytes));
        ArrayList<Integer> kek = new ArrayList<>();
        kek.add(23);
        System.out.println(kek.size());
        kek.clear();
        System.out.println(kek.size());
    }
}
