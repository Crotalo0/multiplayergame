package com.multiplayergame;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

  private static final Logger LOG = LoggerFactory.getLogger(Client.class);

  public static void main(String[] args) {

    Properties properties = new Properties();
    try (InputStream inputStream =
        Client.class.getClassLoader().getResourceAsStream("application.properties")) {
      properties.load(inputStream);
    } catch (IOException e) {
      LOG.error("Error loading properties file: {}", e.getMessage());
      return;
    }

    String serverAddress = properties.getProperty("serverIp");
    int port = Integer.parseInt(properties.getProperty("serverPort"));

    try (Socket socket = new Socket(serverAddress, port);
        BufferedReader serverIn =
            new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in))) {

      LOG.info("Connected to server at '{}:{}'", serverAddress, port);
      Thread serverListener =
          new Thread(
              () -> {
                try {
                  String line;
                  while ((line = serverIn.readLine()) != null) {
                    if (line.equalsIgnoreCase("partner disconnected")) {
                      LOG.info("The other person has disconnected.");
                      // Perform additional handling if necessary (e.g., close connection, exit,
                      // etc.)
                      System.exit(0);
                    }
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
          LOG.info("Quitting...");
          System.exit(0);
        }
      }

    } catch (IOException e) {
      LOG.error("Client error: {}", e.getMessage());
    }
  }
}
