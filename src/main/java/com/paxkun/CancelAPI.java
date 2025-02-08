package com.paxkun;

public class CancelAPI {

    private static boolean cancelRequested = false;

    public static void cancelDownload() {
        cancelRequested = true;
        System.out.println("⛔ Download process has been canceled.");

        // Stop the server by accessing it from Main
        if (Main.getServer() != null) {
            Main.getServer().stopServer();
        }
    }

    public static boolean isCancelRequested() {
        return cancelRequested;
    }

    public static void resetCancelRequest() {
        cancelRequested = false;
    }
}
