package com.multiplayergame;

import com.multiplayergame.games.GameSocket;
import com.multiplayergame.games.rockpaperscissors.RockPaperScissors;
import com.multiplayergame.games.tictactoe.TicTacToe;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
  private static final Logger LOG = LoggerFactory.getLogger(Server.class);
  private final ServerSocket serverSocket;
  private final Queue<Socket> clients = new ConcurrentLinkedQueue<>();

  private final Object queueLockRps = new Object();
  private final Object queueLockTic = new Object();
  private final Object queueLockChat = new Object();
  private final Queue<Socket> clientsRps = new ConcurrentLinkedQueue<>();
  private final Queue<Socket> clientsTic = new ConcurrentLinkedQueue<>();
  private final Queue<Socket> clientsChat = new ConcurrentLinkedQueue<>();

  public Server(int port) throws IOException {
    this.serverSocket = new ServerSocket(port);
    LOG.info("Server started on port: {}", port);
  }

  public static void main(String[] args) {
    Properties properties = Utils.getProperties();
    int port = Integer.parseInt(properties.getProperty("serverPort"));

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
        LOG.info("Client connected: {}", clientSocket.getInetAddress());

        new Thread(() -> handleClientConnection(clientSocket)).start();
      }
    } catch (IOException e) {
      LOG.error("Server exception: {}", e.getMessage());
    } finally {
      closeAllConnections();
    }
  }

  private void handleClientConnection(Socket clientSocket) {
    try {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println("Select what to do (1-Rps, 2-Tic, 3-Chat)");

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String s = in.readLine();

        switch (s) {
          case "1":
            clientsRps.add(clientSocket);
            out.println("Entered queue for Rock paper scissors. player in queue: " + clientsRps.size());
            matchClients(clientsRps, queueLockRps, this::handleMatchedClientsRockPaperScissors);
            break;
          case "2":
            clientsTic.add(clientSocket);
            out.println("Entered queue for Tic tac toe. player in queue: " + clientsTic.size());
            matchClients(clientsTic, queueLockTic, this::handleMatchedClientsTicTacToe);
            break;
          case "3":
            clientsChat.add(clientSocket);
            out.println("Entered queue for Random chat. player in queue: " + clientsChat.size());
            matchClients(clientsChat, queueLockChat, this::handleMatchedClientsChat);
            break;
          default:
            LOG.info("Not a valid statement");
            break;
        }
    } catch (IOException e) {
      LOG.error("Error handling client connection: {}", e.getMessage());
    }
  }
  private void matchClients(Queue<Socket> queue, Object queueLock, BiConsumer<Socket, Socket> handler) {

    synchronized (queueLock) {
      if (queue.size() >= 2) {
        Socket client1 = queue.poll();
        Socket client2 = queue.poll();

        if (client1 != null && client2 != null) {
          LOG.info(
              "Matched clients: {} and {}", client1.getInetAddress(), client2.getInetAddress());
          new Thread(() -> handler.accept(client1, client2)).start();
        }
      }
    }
  }

  private void handleGameMatchedClients(Socket client1, Socket client2, GameSocket game) {
    try {
      game.getOut1().println("You have been matched with a partner. You are player 1.");
      game.getOut2().println("You have been matched with a partner. You are player 2.");

      while (true) {
        if (game.gameLoop()) {
          break;
        }
      }

    } catch (IOException e) {
      LOG.error("IOException in handling matched clients: {}", e.getMessage());
      Thread.currentThread().interrupt();
    } finally {
      LOG.info("Game finished, connection cleared");
      Utils.cleanUpConnections(client1, client2);
    }
  }
  private void handleMatchedClientsTicTacToe(Socket client1, Socket client2) {
    try {
      handleGameMatchedClients(client1, client2, new TicTacToe(client1, client2));
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }
  private void handleMatchedClientsRockPaperScissors(Socket client1, Socket client2) {
    try {
      handleGameMatchedClients(client1, client2, new RockPaperScissors(client1, client2));
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private void handleMatchedClientsChat(Socket client1, Socket client2) {
    try {
      Thread handler1 = new Thread(new ClientHandler(client1, client2));
      Thread handler2 = new Thread(new ClientHandler(client2, client1));

      handler1.start();
      handler2.start();

      handler1.join();
      handler2.join();

    } catch (IOException | InterruptedException e) {
      LOG.error("Error handling matched clients: {}", e.getMessage());
      Thread.currentThread().interrupt();
    } finally {
      Utils.cleanUpConnections(client1, client2);
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
