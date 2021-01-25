package com.romana.webserver;

import com.romana.utilities.CommonUtils;
import java.io.OutputStream;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.FormatFlagsConversionMismatchException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpRequestHandler implements HttpHandler {

    private static final Logger LOGGER = Logger.getLogger(HtmlGenerator.class.getName());
    private static final int HTTP_OK_STATUS = 200;
    private static final String BASE_PATH = new File("").getAbsolutePath();
    private static final Path WEBPAGE_PATH = Paths.get("/webpage");
    private static final Path WEIGHTS_PATH = Paths.get(BASE_PATH, "weights", "finished");
    private static final byte[] RESOURCE_NOT_FOUND = "<html>Archivo no encontrado</html>".getBytes();
    public String actualClient;

    @Override
    public void handle(HttpExchange t) throws IOException {
        // Get what user puts after the slash

        String address = t.getRemoteAddress().getHostString();
        if (!address.equals(actualClient)) {
            actualClient = address;
            LOGGER.log(Level.INFO, "Received request from: {0}", actualClient);
        }

        URI uri = t.getRequestURI();
        byte[] response = createResponseFromURI(uri);
        t.sendResponseHeaders(HTTP_OK_STATUS, response.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(response);
        }
    }

    private byte[] createResponseFromURI(URI uri) {
        String subpath = uri.getPath(); // This Ignores if there is a Query!
        Path requestedPath = Paths.get(subpath);
        LOGGER.log(Level.FINE, "Requested resource at {0}", subpath);

        Path resourcePath;
        // Redirect URL to local files Section

        if (IsRootFolder(requestedPath)) {
            String webUrl = subpath.substring(1); // Remove the slash
            Date date = CommonUtils.dateFromWebUrl(webUrl);
            String folderName = CommonUtils.formattedFolderDate(date);
            
            resourcePath = Paths.get(WEIGHTS_PATH.toString(), folderName);

        } else if (requestedPath.getNameCount() > 1) {
            resourcePath = Paths.get(WEIGHTS_PATH.toString(), requestedPath.toString());

        } else if (!subpath.equals("/")) {
            resourcePath = WEBPAGE_PATH.resolve(requestedPath.getFileName());

        } else {
            LOGGER.log(Level.INFO, "Returning not found {0}", requestedPath);
            return RESOURCE_NOT_FOUND;
        }

        LOGGER.log(Level.FINE, "Modified path to {0}", resourcePath);

        boolean isWebpageFile = resourcePath.startsWith(WEBPAGE_PATH);
        if (isWebpageFile) {
            if (!CommonUtils.resourceExists(resourcePath.toString())) {
                LOGGER.log(Level.INFO, "Webpage file not found {0}", resourcePath);
                return RESOURCE_NOT_FOUND;
            }
        } else {
            if (Files.notExists(resourcePath) || !resourcePath.startsWith(Paths.get(BASE_PATH))) {
                LOGGER.log(Level.INFO, "Weight file not found {0}", resourcePath);
                return RESOURCE_NOT_FOUND;
            }
        }

        // Serve files after getting the right local Path from URL
        try {
            File fileFromPath = resourcePath.toFile();
            if (fileFromPath.isDirectory() && resourcePath.startsWith(WEIGHTS_PATH)) {
                String response = HtmlGenerator.create(WEBPAGE_PATH.resolve("response.html"), resourcePath);
                return response.getBytes();
            } else if (isWebpageFile) {
                String stringPath = resourcePath.toString();
                return CommonUtils.getResourceBytes(stringPath); // Returns CSS, Javascript, images, etc
            } else {
                return Files.readAllBytes(resourcePath);
            }

        } catch (IOException | FormatFlagsConversionMismatchException ex) {
            LOGGER.log(Level.WARNING, "Error while reading file", ex);
            return RESOURCE_NOT_FOUND;
        }
    }

    private String createResponseFromQueryParams(URI uri) {
        //Get the request query
        String query = uri.getQuery();

        if (query == null) {
            return "Ingrese un Query";
        }
        HashMap<String, String> queryMap = queryToMap(query);
        return queryMap.toString();
    }

    // As seen on https://stackoverflow.com/questions/32207948/with-com-sun-net-httpserver-why-image-is-not-showing-up-in-java-generated-html
    public HashMap<String, String> queryToMap(String query) {
        HashMap<String, String> result = new HashMap();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }

    private boolean IsRootFolder(Path path) {
        String pathString = path.toString();
        return pathString.startsWith(File.separator) && !pathString.contains(".") && pathString.length() > 1;
    }
}
