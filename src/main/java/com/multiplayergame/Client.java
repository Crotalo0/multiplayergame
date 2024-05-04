package com.multiplayergame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

  private static final Logger LOG = LoggerFactory.getLogger(Client.class);

  public static void main(String[] args) {

    String serverAddress = "192.168.1.88";
    int port = 6666;

    try (Socket socket = new Socket(serverAddress, port);

        BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);

        BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter userOut = new PrintWriter(socket.getOutputStream(), true)) {

      LOG.info("Connected to server at '{}:{}'", serverAddress, port);
//      String line;
//      LOG.info("Input now something...");
//      while ((line = reader.readLine()) != null) {
//        out.println(line);
//        if (line.equalsIgnoreCase("quit")) {
//          break;
//        }
//      }
      Thread serverListener = new Thread(() -> {
        try {
          String line;
          while ((line = serverIn.readLine()) != null) {
            LOG.info(line);
          }
        } catch (IOException e) {
          LOG.error("Error reading from server: {}", e.getMessage());
        }
      });
      serverListener.start();

      String line;
      while ((line = userIn.readLine()) != null) {
        serverOut.println(line);
        if (line.equalsIgnoreCase("quit")) {
          break;
        }
      }

    } catch (IOException e) {
      LOG.error("Client error: {}", e.getMessage());
    }
  }
}
