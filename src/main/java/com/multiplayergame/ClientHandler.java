package com.multiplayergame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ClientHandler implements Runnable {
  private static final Logger LOG = LoggerFactory.getLogger(ClientHandler.class);
  private final Socket clientSocket;
  private final BufferedReader in;
  private final BufferedReader partnerIn;
  private final Socket partnerSocket;
  private final PrintWriter partnerOut;
  private final PrintWriter clientOut;

  public ClientHandler(Socket clientSocket, Socket partnerSocket) throws IOException {
    this.clientSocket = clientSocket;
    this.partnerSocket = partnerSocket;
    this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    this.partnerIn = new BufferedReader(new InputStreamReader(partnerSocket.getInputStream()));
    this.partnerOut = new PrintWriter(partnerSocket.getOutputStream(), true);
    this.clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
  }

  @Override
  public void run() {
    partnerOut.println("You have been matched with a partner. Enjoy your session!");
    try {
      String message = in.readLine();
      if ("quit".equals(message)) {
        throw new IOException("Other client is closed");
      }
      partnerOut.println("Partner: " + message);
    } catch (IOException e) {
      LOG.error("IOException in client handler: {}", e.getMessage());
    } finally {
      try {
        if (!clientSocket.isClosed()) {
          partnerOut.println("partner disconnected");
          clientSocket.close();
        }
      } catch (IOException e) {
        LOG.error("Error closing client connection: {}", e.getMessage());
      }
    }
  }
}
