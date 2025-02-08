package com.paxkun;

import lombok.Getter;

public class Main {

    @Getter
    private static Server server; // Global server instance

    public static void main(String[] args) {
        server = new Server(); // Initialize server
        server.startServer(); // Start Javalin server
    }
}
