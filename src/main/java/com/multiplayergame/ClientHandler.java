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
  private final Socket mainSocket;
  private final BufferedReader mainIn;
  private final BufferedReader partnerIn;
  private final Socket partnerSocket;
  private final PrintWriter partnerOut;

  public ClientHandler(Socket mainSocket, Socket partnerSocket) throws IOException {

    this.mainSocket = mainSocket;
    this.partnerSocket = partnerSocket;

    this.mainIn = new BufferedReader(new InputStreamReader(mainSocket.getInputStream()));
    this.partnerIn = new BufferedReader(new InputStreamReader(partnerSocket.getInputStream()));
    this.partnerOut = new PrintWriter(partnerSocket.getOutputStream(), true);
  }

  @Override
  public void run() {
    partnerOut.println("You have been matched with a partner. Enjoy your session!");
    try {
      String message;
      while ((message = mainIn.readLine()) != null) {
        if ("quit".equals(message)) {
          LOG.error("Partner has left the chat. Restart the client");
          partnerOut.println("partner disconnected");
          throw new IOException("One client is closed");
        }
        partnerOut.println("Partner: " + message);
      }

    } catch (IOException e) {
      LOG.error(
          "Error handling client connection between '{} - {}': {}",
          mainSocket.getInetAddress(),
          partnerSocket.getInetAddress(),
          e.getMessage());
    } finally {
      Utils.cleanUpConnections(mainSocket, partnerSocket);
    }
  }
}
