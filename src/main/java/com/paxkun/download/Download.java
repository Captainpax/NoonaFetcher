package com.paxkun.download;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Download {

    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private static final BlockingQueue<String> statusQueue = new LinkedBlockingQueue<>();

    public Download() {
        startStatusMonitor();
        //Synchronous

        //Async
//        imageUrls.forEach(url ->
//                CompletableFuture.runAsync(() -> downloadImage(url), executor)
//                        .thenRun(() -> statusQueue.add("Downloaded: " + url))
//                        .exceptionally(ex -> {
//                            statusQueue.add("Failed: " + url + " - " + ex.getMessage());
//                            return null;
//                        })
//        );
    }

    public static boolean urlExists(String urlStr) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlStr).openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);
            return (200 <= responseCode && responseCode < 400);
        } catch (IOException e) {
            return false;
        }
    }

    public void downloadChapter(@NotNull List<String> imageUrls, String cbzFilename, String inputFolder) {
        Path cbzPath = Path.of("downloads", cbzFilename);
        Path inputPath = Path.of(inputFolder);  // Input folder for images

        try {
            Files.createDirectories(cbzPath.getParent());

            try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(cbzPath))) {
                int index = 1;
                for (String imageUrl : imageUrls) {
                    boolean downloaded = false; // Track whether the image was successfully downloaded
                    int attempts = 0;

                    // Retry downloading up to 3 times
                    while (attempts < 3 && !downloaded) {
                        // Check if the URL is an actual URL (not a file path)
                        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                            try (InputStream in = new URL(imageUrl).openStream()) {
                                String imageName = String.format("%03d.png", index++);
                                ZipEntry entry = new ZipEntry(imageName);
                                zipOut.putNextEntry(entry);
                                in.transferTo(zipOut);
                                zipOut.closeEntry();
                                System.out.println("Added: " + imageName);
                                downloaded = true; // Mark as downloaded
                            } catch (IOException e) {
                                attempts++;
                                System.err.println("Failed to download: " + imageUrl + " (Attempt " + attempts + "/3)");
                                if (attempts == 3) {
                                    System.err.println("Failed to download after 3 attempts: " + imageUrl);
                                }
                            }
                        } else {
                            // For local images in the folder
                            Path imagePath = inputPath.resolve(imageUrl);  // Resolve the image path
                            if (Files.exists(imagePath)) {
                                try (InputStream in = Files.newInputStream(imagePath)) {
                                    String imageName = String.format("%03d.png", index++);
                                    ZipEntry entry = new ZipEntry(imageName);
                                    zipOut.putNextEntry(entry);
                                    in.transferTo(zipOut);
                                    zipOut.closeEntry();
                                    System.out.println("Added: " + imageName);
                                    downloaded = true; // Mark as downloaded
                                } catch (IOException e) {
                                    attempts++;
                                    System.err.println("Failed to read: " + imagePath + " (Attempt " + attempts + "/3)");
                                    if (attempts == 3) {
                                        System.err.println("Failed to read after 3 attempts: " + imagePath);
                                    }
                                }
                            } else {
                                System.err.println("Image not found: " + imagePath);
                                downloaded = true; // No need to retry if the image is not found
                            }
                        }
                    }
                }
            }

            System.out.println("Saved CBZ: " + cbzPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static void startStatusMonitor() {
        Thread.ofVirtual().start(() -> {
            while (true) {
                try {
                    String status = statusQueue.take();
                    System.out.println(status);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }
}
