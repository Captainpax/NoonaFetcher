package com.paxkun;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * Main class to initialize and run the web downloader application.
 */
public class Main {

    @Getter @Setter
    private static Server server;

    public static void main(String[] args) {
        // Initialize and start the server
        server = new Server();
        server.initializeEndpoints();
        server.start();
    }
}
