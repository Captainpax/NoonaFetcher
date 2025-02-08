package com.paxkun;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import lombok.Getter;

@Getter
public class Server {

    private final Javalin app; // Javalin instance for the server

    public Server() {
        this.app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH); // Serve static files
        });
    }

    public void startServer() {
        app.start(7000);
        System.out.println("🚀 Server started on http://localhost:7000");
    }

    public void stopServer() {
        app.stop();
        System.out.println("🛑 Server stopped.");
    }
}
