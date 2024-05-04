package com.multiplayergame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
  private static final Logger LOG = LoggerFactory.getLogger(Server.class);
  private final ServerSocket serverSocket;
  private final List<Socket> clients = new ArrayList<>();

  public Server(int port) throws IOException {
    this.serverSocket = new ServerSocket(port);
    LOG.info("Server started on port: {}", port);
  }

  public static void main(String[] args) {
    int port = 6666;
    try {
      Server server = new Server(port);
      server.start();
    } catch (IOException e) {
      LOG.error("Server error: {}", e.getMessage());
    }
  }

  public void start() {
    try {
      while (true) {
        Socket clientSocket = serverSocket.accept();
        clients.add(clientSocket);
        LOG.info("Client connected: {}", clientSocket.getInetAddress());
        new Thread(() -> handleClient(clientSocket)).start();
      }
    } catch (IOException e) {
      LOG.error("Server exception: {}", e.getMessage());
    } finally {
      closeAllConnections();
    }
  }

  private void handleClient(Socket clientSocket) {
    try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
      String line;
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      while ((line = in.readLine()) != null) {
        LOG.info("Received: {}", line);
        out.println("Server received: " + line);

        if (line.equalsIgnoreCase("quit")) {
          closeAllConnections();
          break;
        }
      }
    } catch (IOException e) {
      LOG.error("Client handler exception: {}", e.getMessage());
    }
  }

  private void closeAllConnections() {
    LOG.info("Closing all connections...");
    try {
      for (Socket client : clients) {
        if (!client.isClosed()) {
          client.close();
        }
      }
      clients.clear();
      serverSocket.close();
    } catch (IOException e) {
      LOG.error("Error closing connections: {}", e.getMessage());
    }
  }
}