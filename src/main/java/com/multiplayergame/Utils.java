package com.multiplayergame;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

  private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

  private Utils() {
    throw new IllegalStateException("Utility class");
  }

  public static Properties getProperties() {
    Properties properties = new Properties();
    try (InputStream inputStream =
        Client.class.getClassLoader().getResourceAsStream("application.properties")) {
      properties.load(inputStream);
    } catch (IOException e) {
      LOG.error("Error loading properties file: {}", e.getMessage());
    }
    return properties;
  }

  public static void cleanUpConnections(Socket client1, Socket client2) {
    try {
      if (client1 != null && !client1.isClosed()) {
        client1.close();
      }
      if (client2 != null && !client2.isClosed()) {
        client2.close();
      }
    } catch (IOException e) {
      LOG.error("Error closing client connections: {}", e.getMessage());
    }
  }


}
