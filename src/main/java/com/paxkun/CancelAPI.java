package com.paxkun;

public class CancelAPI {

    private static boolean cancelRequested = false;

    // Method to cancel the download process
    public static void cancelDownload() {
        try {
            cancelRequested = true;
            System.out.println("Download process has been canceled.");
            // Logic to stop download thread can be added here if needed.
        } catch (Exception e) {
            System.err.println("Error canceling the download: " + e.getMessage());
        }
    }

    // Method to check if cancellation was requested
    public static boolean isCancelRequested() {
        return cancelRequested;
    }

    // Reset the cancellation request flag
    public static void resetCancelRequest() {
        cancelRequested = false;
    }
}
