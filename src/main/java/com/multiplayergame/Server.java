package com.multiplayergame;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.multiplayergame.game.rockpaperscissors.RockPaperScissors;
import com.multiplayergame.game.rockpaperscissors.RockPaperScissors.Choice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
  private static final Logger LOG = LoggerFactory.getLogger(Server.class);
  private final ServerSocket serverSocket;
  private final Object queueLock = new Object();
  private final Queue<Socket> clients = new ConcurrentLinkedQueue<>();

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
          LOG.info(
              "Matched clients: {} and {}", client1.getInetAddress(), client2.getInetAddress());
//          new Thread(() -> handleMatchedClients(client1, client2)).start();
          new Thread(() -> handleMatchedClientsRockPaperScissors(client1, client2)).start();
        }
      }
    }
  }

  private void handleMatchedClientsRockPaperScissors(Socket client1, Socket client2) {
    try (BufferedReader in1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
         PrintWriter out1 = new PrintWriter(client1.getOutputStream(), true);
         BufferedReader in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
         PrintWriter out2 = new PrintWriter(client2.getOutputStream(), true)) {

      int[] points = new int[] {0, 0};

      out1.println("You have been matched with a partner. You are player 1.");
      out2.println("You have been matched with a partner. You are player 2.");

      while (true) {
        if (!playRound(in1, out1, in2, out2, points)) {
          break;
        }
      }

    } catch (IOException e) {
      LOG.error("IOException in handling matched clients: {}", e.getMessage());
      Thread.currentThread().interrupt();
    } finally {
      cleanUpConnections(client1, client2);
    }
  }

  private boolean playRound(BufferedReader in1, PrintWriter out1, BufferedReader in2, PrintWriter out2, int[] points) throws IOException {
    out2.println("Wait for player 1");
    String choice1 = getPlayerChoice(in1, out1);
    if (choice1 == null) {
      out2.println("Player 1 has quit the session. Restart the client.");
      return false;
    }

    out1.println("Wait for player 2");
    String choice2 = getPlayerChoice(in2, out2);
    if (choice2 == null) {
      out1.println("Player 2 has quit the session. Restart the client");
      return false;
    }

    String outcome = RockPaperScissors.determineOutcome(choice1, choice2, points);

    sendMessageToBothClients(out1, out2, "Game outcome: " + outcome);
    sendMessageToBothClients(out1, out2, "POINTS: player1->" + points[0] + " - player2->" + points[1]);

    return true;
  }

  private String getPlayerChoice(BufferedReader in, PrintWriter out) throws IOException {
    while (true) {
      out.println("Input: ");
      String choice = in.readLine();

      if (choice.equalsIgnoreCase("QUIT")) {
        out.println("You chose to quit. Ending the session.");
        return null;
      }

      choice = choice.toUpperCase();

      try {
        RockPaperScissors.Choice.valueOf(choice);
        out.println("You said: " + choice);
        return choice;
      } catch (IllegalArgumentException e) {
        out.println("Invalid input. Please choose ROCK, PAPER, or SCISSORS.");
      }
    }
  }

  /*
  private void handleMatchedClientsRockPaperScissors(Socket client1, Socket client2) {
    try (BufferedReader in1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
         PrintWriter out1 = new PrintWriter(client1.getOutputStream(), true);
         BufferedReader in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
         PrintWriter out2 = new PrintWriter(client2.getOutputStream(), true)) {

      boolean gameRunning = true;
      out1.println("You have been matched with a partner. Please choose ROCK, PAPER, or SCISSORS.");
      out2.println("You have been matched with a partner. Please choose ROCK, PAPER, or SCISSORS.");
      // 0 -> player1 points
      // 1 -> player2 points
      int[] points = new int[] {0, 0};

      while(gameRunning) {

        out1.println("You are player 1");
        out2.println("You are player 2");

        out2.println("Wait for player 1...");
        String choice1;
        while(true) {
          out1.println("Input: ");
          choice1 = in1.readLine().toUpperCase();

          if (choice1.equalsIgnoreCase("QUIT")) {
            out1.println("You chose to quit. Ending the session.");
            out2.println("Player 1 has quit the session. Restart the client");
            throw new RuntimeException("Game aborted");
          }

          try {
            Choice.valueOf(choice1);
            out1.println("You said: " + choice1);
            break;
          } catch (IllegalArgumentException e) {
            out1.println("Invalid input. Please choose ROCK, PAPER, or SCISSORS.");
          }
        }

        out1.println("Wait for player 2...");
        String choice2;
        while(true) {
          out2.println("Input: ");
          choice2 = in2.readLine().toUpperCase();

          if (choice2.equalsIgnoreCase("QUIT")) {
            out2.println("You chose to quit. Ending the session. Restart the client");
            out1.println("Player 2 has quit the session.");
            throw new RuntimeException("Game aborted");
          }

          try {
            Choice.valueOf(choice2);
            out2.println("You said: " + choice2);
            break;
          } catch (IllegalArgumentException e) {
            out2.println("Invalid input. Please choose ROCK, PAPER, or SCISSORS.");
          }
        }

        String outcome = RockPaperScissors.determineOutcome(choice1, choice2, points);

        out1.println("Game outcome: " + outcome);
        out2.println("Game outcome: " + outcome);

        out1.println("POINTS: player1->" + points[0] +" - player2->" + points[1]);
        out2.println("POINTS: player1->" + points[0] +" - player2->" + points[1]);
      }
    } catch (IOException e) {
      LOG.error("IOException in handling matched clients: {}", e.getMessage());
      Thread.currentThread().interrupt();
    } finally {
      cleanUpConnections(client1, client2);
    }
  }
  */

  private void sendMessageToBothClients(PrintWriter o1, PrintWriter o2, String message) {
    o1.println(message);
    o2.println(message);
  }
  private void handleMatchedClients(Socket client1, Socket client2) {
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
      cleanUpConnections(client1, client2);
    }
  }

  private void cleanUpConnections(Socket client1, Socket client2) {
    try {
      // Close client1 socket if it is not already closed
      if (client1 != null && !client1.isClosed()) {
        client1.close();
      }
      // Close client2 socket if it is not already closed
      if (client2 != null && !client2.isClosed()) {
        client2.close();
      }
    } catch (IOException e) {
      // Log any exception that occurs during closure of connections
      LOG.error("Error closing client connections: {}", e.getMessage());
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
