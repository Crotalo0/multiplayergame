package com.multiplayergame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
  private static final Logger LOG = LoggerFactory.getLogger(Server.class);
  private final ServerSocket serverSocket;
  private final Object queueLock = new Object();
  // temp
  int counterName = 0;
  private final Queue<Socket> clients = new ConcurrentLinkedQueue<>();

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
//        String newName = newName();
//        new Thread(() -> handleClient(clientSocket, newName)).start();
        matchClients();

      }
    } catch (IOException e) {
      LOG.error("Server exception: {}", e.getMessage());
    } finally {
      closeAllConnections();
    }
  }

  private void matchClients() {
    synchronized (queueLock) {
      if (clients.size() >= 2) {
        Socket client1 = clients.poll();
        Socket client2 = clients.poll();

        if (client1 != null && client2 != null) {
          LOG.info("Matched clients: {} and {}", client1.getInetAddress(), client2.getInetAddress());
          new Thread(() -> handleMatchedClients(client1, client2)).start();
        }
      }
    }
  }

  private void handleMatchedClients(Socket client1, Socket client2) {
    try (BufferedReader in1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
         PrintWriter out1 = new PrintWriter(client1.getOutputStream(), true);
         BufferedReader in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
         PrintWriter out2 = new PrintWriter(client2.getOutputStream(), true)) {
      out1.println("You have been matched with a partner. Enjoy your session!");
      out2.println("You have been matched with a partner. Enjoy your session!");

      while (true) {
        if (in1.ready()) {
          String message = in1.readLine();
          if (message != null) {
            out2.println("Partner: " + message);
          }
        }

        if (in2.ready()) {
          String message = in2.readLine();
          if (message != null) {
            out1.println("Partner: " + message);
          }
        }
      }
    } catch (IOException e) {
      LOG.error("Error in matched clients handler: {}", e.getMessage());
    } finally {
      try {
        client1.close();
        client2.close();
      } catch (IOException e) {
        LOG.error("Error closing client connections: {}", e.getMessage());
      }
    }
  }

  private String newName() {
    // temp
    String name = "client" + counterName;
    counterName++;
    return name;
  }

  private void handleClient(Socket clientSocket, String name) {
    try (BufferedReader in = new BufferedReader(
        new InputStreamReader(clientSocket.getInputStream()))) {
      String line;
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      while ((line = in.readLine()) != null) {
        LOG.info("Received from {}: {}", name, line);
        out.println("Server received: " + line);
        if ("quit".equalsIgnoreCase(line)) {
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