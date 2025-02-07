package com.paxkun;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class WebAPI {

    public static void startWebServer() {
        // Start the Javalin server and configure static file handling
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH);  // Serve static files from the classpath
        }).start(7000);  // Start on port 7000

        // Serve the main page (index.html) at the root
        app.get("/", ctx -> {
            ctx.render("/public/index.html");  // This assumes the index.html is in src/main/resources/public
        });

        // Optionally, you can define more routes or adjust as needed
    }
}
