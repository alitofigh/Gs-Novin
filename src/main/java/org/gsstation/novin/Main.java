package org.gsstation.novin;


import org.gsstation.novin.server.DataReceiverServer;

import java.io.IOException;

/**
 * Created by A_Tofigh at 7/13/2024
 */

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        DataReceiverServer server = new DataReceiverServer();
        try {
            System.out.println("Starting server...");
            server.start(8589);
            System.out.println("Server started!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
