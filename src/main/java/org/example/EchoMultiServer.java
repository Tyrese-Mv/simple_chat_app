package org.example;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class EchoMultiServer {
    private ServerSocket serverSocket;
    private List<EchoClientHandler> clientHandlers;

    public EchoMultiServer() {
        clientHandlers = new ArrayList<>();
    }

    public static void main(String[] args) {
        EchoMultiServer echoServer = new EchoMultiServer();
        int port = 8888; // Change this to the desired port number
        try {
            echoServer.start(port);
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("added new client");
            EchoClientHandler clientHandler = new EchoClientHandler(clientSocket);
            clientHandlers.add(clientHandler);
            clientHandler.start();
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private class EchoClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (".".equals(inputLine)) {
                        break;
                    }
                    broadcast(inputLine);
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    in.close();
                    out.close();
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client connection: " + e.getMessage());
                }
            }
        }
    }

    private void broadcast(String message) {
        for (EchoClientHandler handler : clientHandlers) {
            try {
                handler.out.println(message);
            } catch (Exception e) {
                System.err.println("Error broadcasting message to client: " + e.getMessage());
            }
        }
    }
}
