package com.paxkun;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * WebAPI handles frontend serving, REST API routes, and file downloads.
 */
@Slf4j
public class WebAPI {
    private final Javalin app;

    @Getter
    private static WebAPI instance;

    public WebAPI(Server server) {
        this.app = server.getApp();
        instance = this;
        initializeRoutes();
    }

    private void initializeRoutes() {
        // Serve static files for the frontend
        app.config.staticFiles.add("/public", Location.CLASSPATH);

        // Home route (serves index.html)
        app.get("/", ctx -> ctx.render("/public/index.html"));

        // Route to start a download process
        app.post("/api/start", ctx -> {
            String url = ctx.formParam("url");
            String fileType = ctx.formParam("fileType");

            if (url == null || url.isEmpty() || fileType == null || fileType.isEmpty()) {
                ctx.status(400).result("Invalid URL or File Type");
                return;
            }

            StatusAPI.broadcastLog("🚀 Starting download for: " + url);
            DownloadAPI.startDownload(url, fileType);
            ctx.result("Download started...");
        });

        // Route to download the zip file
        app.get("/api/download", ctx -> {
            if (!ZipperAPI.getZipFile().exists()) {
                ctx.status(404).result("❌ ZIP file not found.");
                return;
            }

            ctx.header("Content-Disposition", "attachment; filename=\"downloads.zip\"");
            ctx.contentType("application/zip");
            ctx.result(ZipperAPI.getZipFileInputStream());
            StatusAPI.broadcastLog("📤 Downloading ZIP file...");
        });

        log.info("WebAPI initialized.");
    }
}
