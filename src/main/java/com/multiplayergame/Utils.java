package com.multiplayergame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {
  private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

  public static Properties getProperties() {
    Properties properties = new Properties();
    try (InputStream inputStream =
        Client.class.getClassLoader().getResourceAsStream("application.properties")) {
      properties.load(inputStream);
    } catch (IOException e) {
      LOG.error("Error loading properties file: {}", e.getMessage());
      throw new RuntimeException("Error loading property file");
    }
    return properties;
  }
}
