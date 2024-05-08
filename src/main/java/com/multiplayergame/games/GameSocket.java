package com.multiplayergame.games;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import lombok.Data;

@Data
public abstract class GameSocket {
  protected static final String PLAYER_1_WINS = "Player 1 wins";
  protected static final String PLAYER_2_WINS = "Player 2 wins";
  protected final BufferedReader in1;
  protected final PrintWriter out1;
  protected final BufferedReader in2;
  protected final PrintWriter out2;
  protected int[] points = new int[] {0, 0};
  private Socket client1;
  private Socket client2;

  public GameSocket(Socket client1, Socket client2) throws IOException {
    this.client1 = client1;
    this.client2 = client2;
    this.in1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
    this.out1 = new PrintWriter(client1.getOutputStream(), true);
    this.in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
    this.out2 = new PrintWriter(client2.getOutputStream(), true);
  }
  public abstract boolean playRound() throws IOException;

  protected void sendMessageToBothClients(String message) {
    out1.println(message);
    out2.println(message);
  }
}
