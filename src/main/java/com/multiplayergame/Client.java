package com.multiplayergame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

  private static final Logger LOG = LoggerFactory.getLogger(Client.class);

  public static void main(String[] args) {

    String serverAddress = "localhost"; // Change to server IP if needed
    int port = 6666; // Set the port number you want to use

    try (Socket socket = new Socket(serverAddress, port);
         BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
         PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

      LOG.info("Connected to server at {} : {}", serverAddress, port);

      // Read input from the console and send it to the server
      String line;
      while ((line = reader.readLine()) != null) {
        out.println(line);
        if (line.equalsIgnoreCase("quit")) {
          break;
        }
      }
    } catch (IOException e) {
      LOG.error("Client error: {}", e.getMessage());
    }
  }
}
