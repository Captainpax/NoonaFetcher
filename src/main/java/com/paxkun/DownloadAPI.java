package com.paxkun;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles downloading files found by the SearchAPI.
 */
public class DownloadAPI {

    private static final ExecutorService downloadExecutor = Executors.newFixedThreadPool(3); // Limit downloads to 3 concurrent threads
    private static Path rootDownloadDirectory;
    private static int totalFiles = 0;
    private static int filesDownloaded = 0;

    /**
     * Initiates the download process for the provided file links.
     *
     * @param fileLinks The list of files to download.
     * @param baseUrl   The base URL for directory structuring.
     */
    public static void startDownload(Set<String> fileLinks, String baseUrl) {
        if (fileLinks.isEmpty()) {
            StatusAPI.updateLog("⚠️ No files to download.");
            return;
        }

        try {
            rootDownloadDirectory = createDownloadDirectory(baseUrl);
            totalFiles = fileLinks.size();
            filesDownloaded = 0;

            StatusAPI.updateLog("⬇️ Starting downloads for " + totalFiles + " files...");

            // Execute downloads concurrently
            for (String fileLink : fileLinks) {
                downloadExecutor.submit(() -> downloadFile(fileLink));
            }

        } catch (IOException e) {
            StatusAPI.updateLog("❌ Download initialization error: " + e.getMessage());
        }
    }

    /**
     * Downloads a single file from the given URL.
     *
     * @param fileUrl The file URL to download.
     */
    private static void downloadFile(String fileUrl) {
        if (CancelAPI.isCancelRequested()) {
            StatusAPI.updateLog("⛔ Download canceled.");
            return;
        }

        try {
            URL url = new URL(fileUrl);
            String fileName = new File(url.getPath()).getName();
            Path filePath = rootDownloadDirectory.resolve(fileName);

            StatusAPI.updateLog("⬇️ Downloading: " + fileName);

            try (InputStream is = url.openStream();
                 OutputStream os = Files.newOutputStream(filePath)) {
                is.transferTo(os);
            }

            filesDownloaded++;
            int progress = (filesDownloaded * 100) / totalFiles;
            StatusAPI.updateProgress(progress);
            StatusAPI.updateLog("✅ Downloaded: " + fileName);

            // Check if all downloads are complete
            if (filesDownloaded == totalFiles) {
                StatusAPI.updateLog("📦 All downloads completed. Preparing ZIP file...");
                ZipperAPI.zipAllFiles(rootDownloadDirectory);
            }

        } catch (IOException e) {
            StatusAPI.updateLog("❌ Download error: " + e.getMessage());
        }
    }

    /**
     * Creates a directory for storing downloaded files based on the base URL.
     *
     * @param url The base URL to generate a directory name.
     * @return The created directory path.
     * @throws IOException If directory creation fails.
     */
    private static Path createDownloadDirectory(String url) throws IOException {
        Path path = Paths.get("downloads", url.replaceAll("[^a-zA-Z0-9]", "_"));
        Files.createDirectories(path);
        return path;
    }
}
