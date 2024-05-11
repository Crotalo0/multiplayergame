package com.multiplayergame.games;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public abstract class GameSocket {
  protected static final Logger LOG = LoggerFactory.getLogger(GameSocket.class);
  protected static final String PLAYER_1_WINS = "Player1 wins";
  protected static final String PLAYER_2_WINS = "Player2 wins";
  protected static final String TIE = "There is a tie";
  protected final BufferedReader in1;
  protected final PrintWriter out1;
  protected final BufferedReader in2;
  protected final PrintWriter out2;
  protected int[] points = new int[] {0, 0};
  protected Socket client1;
  protected Socket client2;

  protected GameSocket(Socket client1, Socket client2) throws IOException {
    this.client1 = client1;
    this.client2 = client2;
    this.in1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
    this.out1 = new PrintWriter(client1.getOutputStream(), true);
    this.in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
    this.out2 = new PrintWriter(client2.getOutputStream(), true);
  }

  public abstract boolean gameLoop() throws IOException;

  protected void sendMessageToBothClients(String message) {
    out1.println(message);
    out2.println(message);
  }
}
