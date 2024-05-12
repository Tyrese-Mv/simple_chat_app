package org.example;

import java.io.*;
import java.net.*;

public class EchoClient {
    private static class ClientRunnable implements Runnable {
        private String host;
        private int port;

        public ClientRunnable(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() {
            try (Socket echoSocket = new Socket(host, port);
                 PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()))) {
                BufferedReader stdIni = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Enter your username: ");
                String username = stdIni.readLine();

                // Send the username to the server
                out.println(username+" has joined...");
                Thread receiveThread = new Thread(() -> {
                    try {
                        String serverMessage;
                        while ((serverMessage = in.readLine()) != null) {
                            System.out.println("\n"+serverMessage);
                        }
                    } catch (IOException e) {
                        System.err.println("Error receiving message from server: " + e.getMessage());
                    }
                });
                receiveThread.start();

                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                String userInput;
                while ((userInput = stdIn.readLine()) != null) {
                    out.println(userInput);
                    if (userInput.equals(".")) {
                        break;
                    }
                }
            } catch (UnknownHostException e) {
                System.err.println("Unknown host: " + host);
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to: " + host);
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {
        String host = "192.168.18.68"; // Change this to the server's IP address
        int port = 8888;

        // Create a new thread for the client
        Thread clientThread = new Thread(new ClientRunnable(host, port));
        clientThread.start();
    }
}
