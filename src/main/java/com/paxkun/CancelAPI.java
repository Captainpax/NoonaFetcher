package com.paxkun;

import io.javalin.Javalin;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * CancelAPI handles cancellation logic for stopping downloads.
 */
@Slf4j
public class CancelAPI {
    private final Javalin app;
    @Getter
    private static CancelAPI instance;
    private static boolean cancelRequested = false;

    public CancelAPI(Server server) {
        this.app = server.getApp();
        instance = this;
        initializeRoutes();
    }

    private void initializeRoutes() {
        app.post("/api/cancel", ctx -> {
            if (cancelRequested) {
                ctx.result("Cancellation already requested.");
                return;
            }
            cancelRequested = true;
            StatusAPI.broadcastLog("⛔ Download process canceled.");
            ctx.result("Download process has been canceled.");
            log.info("Download cancellation requested.");
        });

        log.info("CancelAPI initialized.");
    }

    public static boolean isCancelRequested() {
        return cancelRequested;
    }

    public static void resetCancelRequest() {
        cancelRequested = false;
    }
}
