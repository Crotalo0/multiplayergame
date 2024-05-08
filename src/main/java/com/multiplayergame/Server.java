package com.multiplayergame;

import com.multiplayergame.games.rockpaperscissors.RockPaperScissors;
import com.multiplayergame.games.tictactoe.TicTacToe;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
  private static final Logger LOG = LoggerFactory.getLogger(Server.class);
  private final ServerSocket serverSocket;
  private final Object queueLock = new Object();
  private final Queue<Socket> clients = new ConcurrentLinkedQueue<>();

  // specific queues
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
        clients.add(clientSocket);
        LOG.info("Client connected: {}", clientSocket.getInetAddress());

        while (!clients.isEmpty()) {
          // TODO: add input logic
          Socket client = clients.poll();
          PrintWriter out = new PrintWriter(client.getOutputStream(), true);
          out.println("Select what to do (1-Rps, 2-Tic, 3-Chat)");
          BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
          String s = in.readLine();
          switch (s) {
            case "1":
              clientsRps.add(client);
              matchClientsRps(clientsRps);
              break;
            case "2":
              clientsTic.add(client);
              matchClientsTic(clientsTic);
              break;
            case "3":
              clientsChat.add(client);
              matchClientsChat(clientsChat);
              break;
            default:
              LOG.info("Not a valid statement");
              break;
          }
        }
      }
    } catch (IOException e) {
      LOG.error("Server exception: {}", e.getMessage());
    }
  }

  private void matchClientsRps(Queue<Socket> queue) {
    synchronized (queueLock) {
      if (queue.size() >= 2) {
        Socket client1 = queue.poll();
        Socket client2 = queue.poll();

        if (client1 != null && client2 != null) {
          LOG.info(
              "Matched clients: {} and {}", client1.getInetAddress(), client2.getInetAddress());
          new Thread(() -> handleMatchedClientsRockPaperScissors(client1, client2)).start();
        }
      }
    }
  }

  private void matchClientsTic(Queue<Socket> queue) {
    synchronized (queueLock) {
      if (queue.size() >= 2) {
        Socket client1 = queue.poll();
        Socket client2 = queue.poll();

        if (client1 != null && client2 != null) {
          LOG.info(
              "Matched clients: {} and {}", client1.getInetAddress(), client2.getInetAddress());
          new Thread(() -> handleMatchedClientsTicTacToe(client1, client2)).start();
        }
      }
    }
  }

  private void matchClientsChat(Queue<Socket> queue) {
    synchronized (queueLock) {
      if (queue.size() >= 2) {
        Socket client1 = queue.poll();
        Socket client2 = queue.poll();

        if (client1 != null && client2 != null) {
          LOG.info(
              "Matched clients: {} and {}", client1.getInetAddress(), client2.getInetAddress());
          new Thread(() -> handleMatchedClientsChat(client1, client2)).start();
        }
      }
    }
  }

  private void handleMatchedClientsTicTacToe(Socket client1, Socket client2) {
    try {
      TicTacToe game = new TicTacToe(client1, client2);
      game.getOut1().println("You have been matched with a partner. You are player 1.");
      game.getOut2().println("You have been matched with a partner. You are player 2.");

      // After one round, make that player 1 gets X and plays for second
      while (true) {
        if (!game.playRound()) {
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

  private void handleMatchedClientsRockPaperScissors(Socket client1, Socket client2) {
    try {
      RockPaperScissors game = new RockPaperScissors(client1, client2);

      game.getOut1().println("You have been matched with a partner. You are player 1.");
      game.getOut2().println("You have been matched with a partner. You are player 2.");

      while (true) {
        if (!game.playRound()) {
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
