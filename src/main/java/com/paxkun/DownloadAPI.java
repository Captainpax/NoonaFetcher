package com.paxkun;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

public class DownloadAPI {

    private static Set<String> fileLinks = new HashSet<>();
    private static Path downloadDir;
    private static int totalFiles = 0;
    private static int filesDownloaded = 0;

    // Method to start the download process
    public static void startDownload(String url, String fileType) {
        try {
            // Clear previous download data
            fileLinks.clear();
            filesDownloaded = 0;

            // Create a new directory for this download
            downloadDir = createDownloadDirectory(url);

            // Search for files to download
            searchFiles(url, fileType);

            // If no files were found, return
            if (fileLinks.isEmpty()) {
                System.out.println("No files found for the given URL and file type.");
                return;
            }

            // Download the files
            downloadFiles();

        } catch (Exception e) {
            System.err.println("Error starting download: " + e.getMessage());
        }
    }

    // Method to search for files of the given file type on the URL
    private static void searchFiles(String url, String fileType) {
        try {
            // Simulate fetching the links (this should be replaced with actual JSoup or other logic to get the file links)
            System.out.println("Searching for files in: " + url);

            // Simulating found links (replace this with actual file-finding logic)
            fileLinks.add(url + "/file1" + fileType);
            fileLinks.add(url + "/file2" + fileType);
            fileLinks.add(url + "/file3" + fileType);

            totalFiles = fileLinks.size();
            System.out.println("Found " + totalFiles + " files to download.");

        } catch (Exception e) {
            System.err.println("Error searching for files: " + e.getMessage());
        }
    }

    // Method to download the files
    private static void downloadFiles() {
        try {
            for (String fileLink : fileLinks) {
                String fileName = new File(fileLink).getName();
                Path filePath = downloadDir.resolve(fileName);

                System.out.println("Downloading: " + fileName);
                downloadFile(fileLink, filePath);

                filesDownloaded++;
                int progress = (filesDownloaded * 100) / totalFiles;
                System.out.println("Progress: " + progress + "%");

                System.out.println("Downloaded: " + fileName);
            }

            System.out.println("All files downloaded. Zipping...");

            // Start zipping process once all files are downloaded
            ZipperAPI.zipAllFiles(downloadDir);

        } catch (Exception e) {
            System.err.println("Error downloading files: " + e.getMessage());
        }
    }

    // Method to download a single file
    private static void downloadFile(String fileLink, Path filePath) throws IOException {
        URL url = new URL(fileLink);
        try (InputStream in = url.openStream(); OutputStream out = Files.newOutputStream(filePath)) {
            in.transferTo(out);
        }
    }

    // Method to create a download directory based on the URL
    private static Path createDownloadDirectory(String url) throws IOException {
        Path path = Paths.get("downloads", url.replaceAll("[^a-zA-Z0-9]", "_"));
        Files.createDirectories(path);
        return path;
    }
}
