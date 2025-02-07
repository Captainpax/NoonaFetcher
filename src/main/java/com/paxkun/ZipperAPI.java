package com.paxkun;

import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Handles zipping of downloaded files into a single archive for user download.
 */
@Slf4j
public class ZipperAPI {

    private static final File zipFile = new File("downloads.zip");

    /**
     * Zips all files inside the given directory.
     *
     * @param downloadDir The directory containing files to be zipped.
     */
    public static void zipAllFiles(Path downloadDir) {
        log.info("📦 Starting zipping process...");
        StatusAPI.broadcastLog("📦 Zipping files...");

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            Files.walk(downloadDir)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            zos.putNextEntry(new ZipEntry(downloadDir.relativize(path).toString()));
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            log.error("❌ Error zipping file: {}", path, e);
                            StatusAPI.broadcastLog("❌ Error zipping file: " + path);
                        }
                    });

            log.info("✅ Zipping complete! Ready for download.");
            StatusAPI.broadcastLog("✅ Zipping complete! You can now download the ZIP.");
            StatusAPI.notifyZipComplete();
        } catch (IOException e) {
            log.error("❌ Zipping error", e);
            StatusAPI.broadcastLog("❌ Zipping error: " + e.getMessage());
        }
    }

    /**
     * Provides access to the generated ZIP file.
     *
     * @return File object representing the ZIP file.
     */
    public static File getZipFile() {
        return zipFile;
    }

    /**
     * Provides an InputStream for the generated ZIP file.
     *
     * @return FileInputStream of the ZIP file.
     * @throws FileNotFoundException if the ZIP file is not found.
     */
    public static FileInputStream getZipFileInputStream() throws FileNotFoundException {
        return new FileInputStream(zipFile);
    }
}
