package com.paxkun;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.http.staticfiles.StaticFileConfig;

/**
 * WebAPI handles the frontend and REST API requests.
 * It serves the web GUI and manages API routes under /api/.
 */
public class WebAPI {

    private static Javalin app;

    /**
     * Initializes and starts the web server.
     * Serves the web GUI and API routes under /api/.
     */
    public static void startWebServer() {
        app = Javalin.create(config -> {
            // ✅ Set up static file serving
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "public";
                staticFileConfig.location = Location.CLASSPATH;
            });
        }).start(7000);

        // ✅ Serve the main frontend page
        app.get("/", ctx -> ctx.render("public/index.html"));

        // ✅ API routes (all under /api/)
        app.get("/api/health", ctx -> ctx.result("✅ WebAPI is running!"));

        // Route to trigger downloading files
        app.post("/api/startDownload", ctx -> {
            String url = ctx.formParam("url");
            String fileType = ctx.formParam("fileType");
            if (url == null || url.isEmpty() || fileType == null || fileType.isEmpty()) {
                ctx.status(400).result("❌ Invalid parameters.");
                return;
            }
            SearchAPI.startSearch(url, fileType);
            ctx.result("🔍 Searching and downloading files...");
        });

        // Route to check download status
        app.get("/api/status", ctx -> ctx.result(StatusAPI.getStatus()));

        // Route to cancel download
        app.post("/api/cancel", ctx -> {
            CancelAPI.cancelDownload();
            ctx.result("⛔ Download canceled.");
        });

        // Route to serve the ZIP file
        app.get("/api/download", ctx -> {
            ctx.header("Content-Disposition", "attachment; filename=downloads.zip");
            ctx.contentType("application/zip");
            ctx.result(ZipperAPI.getZipFile());
        });

        System.out.println("🚀 Web server started on http://localhost:7000");
    }
}
