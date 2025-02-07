package com.paxkun;

public class Main {

    public static void main(String[] args) {
        // Start logging (if needed)
        StatusAPI.startLogging();

        // Start WebAPI (serving frontend)
        WebAPI.startWebServer();

        // Start StatusAPI (handling status routes)
        StatusAPI.startStatusServer();
    }
}
