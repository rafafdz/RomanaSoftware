package com.romana.webserver;

import com.romana.utilities.LoggerCreator;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebServer {

    private HttpServer httpServer;
    private static final Logger LOGGER = Logger.getLogger(WebServer.class.getName());
    
    private static final String ROOT_LOGGER_NAME = WebServer.class.getPackage().getName();
    private static final String LOG_FILE = "log/webserver.log"; // System config
    private static final Logger WEB_SERVER_LOGGER = LoggerCreator.create(WebServer.ROOT_LOGGER_NAME, 
                                                                         WebServer.LOG_FILE,
                                                                         Level.FINE);

    public WebServer(int port, String context, HttpHandler handler) {
                                                 
        try {
            //Create WebServer which is listening on the given port 
            httpServer = HttpServer.create(new InetSocketAddress(port), 0);
            //Create a new context for the given context and handler
            httpServer.createContext(context, handler);
            //Create a default executor
            httpServer.setExecutor(null);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Exception Thrown: {0}", ex.getLocalizedMessage());
        }
    }

    public void start() {
        this.httpServer.start();
    }
    
    public static void main(String[] args) {
        String CONTEXT = "/";
        int PORT = 8000;

        WebServer romanaServer = new WebServer(PORT, CONTEXT, new HttpRequestHandler());
        romanaServer.start();
        LOGGER.log(Level.INFO, "Server is started and listening on port {0}", PORT);

    }
}
