package com.multiplayergame;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

  private static final Logger LOG = LoggerFactory.getLogger(Client.class);
  private final Socket socket;
  private final BufferedReader serverIn;
  private final BufferedReader userIn;
  private final PrintWriter serverOut;

  public Client(String address, int port) throws IOException {
    this.socket = new Socket(address, port);
    this.serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.serverOut = new PrintWriter(socket.getOutputStream(), true);
    this.userIn = new BufferedReader(new InputStreamReader(System.in));
    LOG.info("Connected to server at '{}:{}'", address, port);
  }

  public static void main(String[] args) throws IOException {
    Properties properties = Utils.getProperties();
    String serverAddress = properties.getProperty("serverIp");
    int port = Integer.parseInt(properties.getProperty("serverPort"));

    Client client = new Client(serverAddress, port);

    client.clientLogic();
  }

  public void clientLogic() throws IOException {
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
  }
}
