/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.webserver;

import com.google.gson.Gson;
import com.romana.devices.WeightInfo;
import com.romana.utilities.CommonUtils;
import com.romana.utilities.Configuration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
/**
 *
 * @author rafael
 */
public class HtmlGenerator {

    private static final Logger LOGGER = Logger.getLogger(HtmlGenerator.class.getName());
    private static final String INFO_FILENAME = Configuration.getStringConfig("WEIGHT_INFO_FILENAME");
    private static final String PLATE_TAG = "~patente~";
    private static final String PRICE_TAG = "~monto~";
    private static final String WEIGHT_TYPE_TAG = "~tipo_pesaje~";
    private static final String WEIGHT_RESUME_TAG = "~resumen_pesaje~";
    private static final String WEIGHT_TABLE_TAG = "~tabla_pesaje~";
    private static final String GALLERY_TAG = "~galeria~";
    private static final String COMMENT_AXIS = "Eje Numero %s";
    private static final String COMMENT_PHASES_START = "Pesaje Inicial";
    private static final String COMMENT_PHASES_FINISH = "Pesaje Final";
    private static final String COMMENT_INDIVIDUAL = "Pesaje Total";
    private static final String SPAN_ROW_TEMPLATE = "<span class=\"dot\"  + " +
                                                    "onclick=\"currentSlide(%d)\"></span>\n";
    
    private static final Path IMG_TAG_PATH = Paths.get("/webpage/img_tag_template.html");
    private static final Path GALLERY_PATH = Paths.get("/webpage/gallery_template.html");
    
    private static final String IMAGE_TAG_TEMPLATE = loadTemplate(IMG_TAG_PATH);
    private static final String GALLERY_TEMPLATE = loadTemplate(GALLERY_PATH);
    
    public static String create(Path htmlPath, Path weightFolder) {
        try {
            String template = loadTemplate(htmlPath); // Change this!
            WeightInfo weightData = getWeightInfo(weightFolder.resolve(INFO_FILENAME));

            template = template.replaceFirst(PLATE_TAG, weightData.getPlate());
            template = template.replaceFirst(WEIGHT_TYPE_TAG, weightData.getType().toString());
            template = template.replaceFirst(PRICE_TAG, Integer.toString(weightData.getTotalPrice()));
            template = template.replaceFirst(WEIGHT_RESUME_TAG,
                    Double.toString(weightData.getWeightResume()));
            template = template.replaceFirst(WEIGHT_TABLE_TAG, generateWeightTable(weightData));
            template = template.replaceFirst(GALLERY_TAG, generateImageDiv(weightFolder));
            return template;
            
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Weighing file not found", ex);
            return "Elemento no encontrado, consulte al administrador";
        }
    }

    private static Path[] getImagesFilenames(Path folderPath) {
        try (Stream<Path> paths = Files.walk(folderPath)) {
            Path[] filtered = paths.filter(CommonUtils::isImageFile).toArray(Path[]::new);
            return filtered;
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error while getting images", ex);
            return new Path[0]; // Returns an empty array
        }
    }

    private static WeightInfo getWeightInfo(Path filePath) throws IOException {
        Gson gson = new Gson();
        String fileContents = CommonUtils.getFileString(filePath);
        return gson.fromJson(fileContents, WeightInfo.class);
    }

    private static String generateWeightTable(WeightInfo info) {
        
        int rowCount = info.getWeights().size();
        String htmlRows = "\n";
        for (int i = 0; i < rowCount; i++) {
            WeightInfo.DatedWeight actualDatedWeight = info.getWeights().get(i);
            
            double actualWeight = actualDatedWeight.weight;
            String actualDate = CommonUtils.formatDate(actualDatedWeight.date);

            String newRow = "<tr>\n";
            String comment = "";
            switch (info.getType()) {

                case SIMPLE:
                    comment = COMMENT_INDIVIDUAL;

                case TWO_PHASE:
                    if (i == 0) {
                        comment = COMMENT_PHASES_START;
                    } else {
                        comment = COMMENT_PHASES_FINISH;
                    }
                case AXIS:
                    comment = String.format(COMMENT_AXIS, i + 1);
            }

            newRow += genTableData(comment);
            newRow += genTableData(Double.toString(actualWeight));
            newRow += genTableData(actualDate);

            newRow += "</tr>\n";
            htmlRows += newRow;
        }

        return htmlRows;
    }
    
    private static String generateImageDiv(Path folderPath){
        String imageDiv = "";
        String dotsDiv = "";
        Path[] imagesFilenames = getImagesFilenames(folderPath);
        int imageQuantity = imagesFilenames.length;
        for (int i = 0; i < imageQuantity; i++) {
            Path imageFilename = imagesFilenames[i];
            
            int nameCount = imageFilename.getNameCount();
            String relativePath = imageFilename.subpath(nameCount - 2, nameCount).toString();
            relativePath = relativePath.replace('\\','/');
            String numberText = String.format("%d / %d", i + 1, imageQuantity);

            String imgSubDiv;
            imgSubDiv = IMAGE_TAG_TEMPLATE.replaceAll("~counter~", numberText);
            
            imgSubDiv = imgSubDiv.replaceAll("~src~", relativePath);
            imgSubDiv = imgSubDiv.replaceAll("~text~", imageFilename.getFileName().toString());    
            imageDiv += imgSubDiv + "\n";
        }
        
        for (int i = 0; i < imageQuantity; i++) {
            dotsDiv += String.format(SPAN_ROW_TEMPLATE, i + 1);           
        }
        
        String fullDiv = GALLERY_TEMPLATE.replaceAll("~imagenes~", imageDiv);
        fullDiv = fullDiv.replaceAll("~puntos~", dotsDiv);
                
        return fullDiv;
    }

    private static String genTableData(String text) {
        return ("<td>" + text + "</td>" + "\n");
    }
    
    private static String loadTemplate(Path filePath){
        try {
            return CommonUtils.getResourceFileString(filePath.toString());
        
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Format file not found", ex);
            return "";
        }
    }
    
    public static void main(String[] args) throws IOException {
        System.out.println(Paths.get("/webpage", "kek").toString());
        System.out.println(CommonUtils.getResourceFileString("/webpage/pagestyle.css"));
    }
}
