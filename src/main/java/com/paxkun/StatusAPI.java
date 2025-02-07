package com.paxkun;

import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * StatusAPI handles logging, WebSocket-based live status updates, and API routes for status tracking.
 */
@Slf4j
public class StatusAPI {
    private final Javalin app;
    private static final Set<WsContext> activeConnections = ConcurrentHashMap.newKeySet();

    @Getter
    private static StatusAPI instance;

    public StatusAPI(Server server) {
        this.app = server.getApp();
        instance = this;
        initializeRoutes();
    }

    private void initializeRoutes() {
        // WebSocket for live logs
        app.ws("/api/status", ws -> {
            ws.onConnect(ctx -> {
                activeConnections.add(ctx);
                log.info("Client connected to status WebSocket.");
            });
            ws.onClose(ctx -> {
                activeConnections.remove(ctx);
                log.info("Client disconnected from status WebSocket.");
            });
        });

        // Route to check server health
        app.get("/api/health", ctx -> ctx.result("Server is running."));

        // Route to check if a download is in progress
        app.get("/api/progress", ctx -> {
            boolean inProgress = DownloadAPI.isDownloading();
            ctx.json("{\"downloading\": " + inProgress + "}");
        });

        log.info("StatusAPI initialized.");
    }

    /**
     * Broadcast a log message to all connected WebSocket clients.
     *
     * @param message The log message.
     */
    public static void broadcastLog(String message) {
        log.info(message);
        activeConnections.forEach(ctx -> ctx.send("{\"logMessage\": \"" + message + "\"}"));
    }
}
