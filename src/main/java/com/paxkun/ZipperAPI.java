package com.paxkun;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipperAPI {

    // Method to zip all files in the specified directory
    public static void zipAllFiles(Path downloadDir) {
        File zipFile = new File("downloads.zip");

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            // Walk through the directory and zip all files
            Files.walk(downloadDir)
                    .filter(Files::isRegularFile) // Only zip regular files
                    .forEach(path -> {
                        try {
                            // Create a zip entry for each file
                            zos.putNextEntry(new ZipEntry(downloadDir.relativize(path).toString()));
                            // Copy the file into the zip entry
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            System.err.println("Error zipping file: " + path);
                        }
                    });

            System.out.println("Zipping complete. Zip file created: " + zipFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error creating zip file: " + e.getMessage());
        }
    }
}
