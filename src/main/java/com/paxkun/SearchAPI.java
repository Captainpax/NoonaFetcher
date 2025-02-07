package com.paxkun;

import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import java.util.concurrent.atomic.AtomicBoolean;

public class StatusAPI {

    // WebSocket context for sending logs to the frontend
    public static WsContext progressWsContext = null;

    // Atomic flag to track if a download is running or not
    private static final AtomicBoolean isDownloading = new AtomicBoolean(false);

    /**
     * Method to start logging and set up WebSocket for live updates.
     */
    public static void startLogging() {
        // For now, just a simple log to indicate logging has started.
        System.out.println("Logging started...");
    }

    /**
     * Method to log messages.
     * This will log messages to the console and can send messages to the frontend through WebSocket.
     *
     * @param message The message to be logged.
     */
    public static void updateLog(String message) {
        // Log the message to the console
        System.out.println(message);

        // Optionally send this log message to the frontend via WebSocket if connected
        if (progressWsContext != null) {
            progressWsContext.send("{\"logMessage\":\"" + message + "\"}");
        }
    }

    /**
     * Start the status server to handle API routes related to downloading and status tracking.
     */
    public static void startStatusServer() {
        Javalin app = Javalin.create(config -> {
            // Set up WebSocket for log streaming
            config.wsFactory(config -> {
                config.onConnect(ctx -> progressWsContext = ctx);
                config.onClose(ctx -> progressWsContext = null);
            });
        }).start(7000);  // Start the server on port 7000

        // Route to check status
        app.get("/api/status", ctx -> {
            if (isDownloading.get()) {
                ctx.result("Download in progress...");
            } else {
                ctx.result("No download in progress.");
            }
        });

        // Route to start the download process
        app.post("/api/startDownload", ctx -> {
            if (isDownloading.get()) {
                ctx.result("A download is already in progress.");
            } else {
                isDownloading.set(true);
                // Start the download logic (you can tie this to the logic of your download)
                ctx.result("Download started.");
                // Call download-related methods here (e.g., call a method to search files or start downloads)
            }
        });

        // Route to cancel the download process
        app.post("/api/cancelDownload", ctx -> {
            if (isDownloading.get()) {
                isDownloading.set(false);
                // Add cancellation logic here
                updateLog("Download canceled.");
                ctx.result("Download canceled.");
            } else {
                ctx.result("No download to cancel.");
            }
        });

        // Add more routes as necessary for other API calls related to status, download, etc.
    }
}
