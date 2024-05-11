package com.multiplayergame.games.rockpaperscissors;

import com.multiplayergame.games.GameSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RockPaperScissors extends GameSocket {

  private static final Map<String, Integer> outcomePointsMap = new HashMap<>();
  private static final Map<String, String> outcomeResultMap = new HashMap<>();

  public RockPaperScissors(Socket client1, Socket client2) throws IOException {
    super(client1, client2);
    // Define the outcomes for each combination
    outcomePointsMap.put("ROCK_SCISSORS", 0);
    outcomePointsMap.put("ROCK_PAPER", 1);
    outcomePointsMap.put("PAPER_ROCK", 0);
    outcomePointsMap.put("PAPER_SCISSORS", 1);
    outcomePointsMap.put("SCISSORS_PAPER", 0);
    outcomePointsMap.put("SCISSORS_ROCK", 1);

    outcomeResultMap.put("ROCK_SCISSORS", PLAYER_1_WINS);
    outcomeResultMap.put("ROCK_PAPER", PLAYER_2_WINS);
    outcomeResultMap.put("PAPER_ROCK", PLAYER_1_WINS);
    outcomeResultMap.put("PAPER_SCISSORS", PLAYER_2_WINS);
    outcomeResultMap.put("SCISSORS_PAPER", PLAYER_1_WINS);
    outcomeResultMap.put("SCISSORS_ROCK", PLAYER_2_WINS);
  }

  public String determineOutcome(String player1Choice, String player2Choice, int[] points) {
    if (player1Choice.equalsIgnoreCase(player2Choice)) {
      return TIE;
    }
    String key = player1Choice.toUpperCase() + "_" + player2Choice.toUpperCase();
    int winningPlayer = outcomePointsMap.getOrDefault(key, -1);
    String result = outcomeResultMap.getOrDefault(key, "Invalid input");

    if (winningPlayer != -1) {
      points[winningPlayer]++;
    }
    return result;
  }

  public boolean gameLoop() throws IOException {
    out2.println("Wait for player 1");
    String choice1 = getPlayerChoice(in1, out1);
    if (choice1 == null) {
      out2.println("Player 1 has quit the session. Restart the client.");
      return true;
    }
    out1.println("Wait for player 2");
    String choice2 = getPlayerChoice(in2, out2);
    if (choice2 == null) {
      out1.println("Player 2 has quit the session. Restart the client");
      return true;
    }
    String outcome = determineOutcome(choice1, choice2, points);
    sendMessageToBothClients("Game outcome: " + outcome);
    sendMessageToBothClients("POINTS: player1->" + points[0] + " - player2->" + points[1]);
    return false;
  }

  private String getPlayerChoice(BufferedReader in, PrintWriter out) throws IOException {
    while (true) {
      out.println("Input: ");
      String choice = in.readLine();

      if (choice.equalsIgnoreCase("QUIT")) {
        out.println("You chose to quit. Ending the session.");
        return null;
      }

      choice = choice.toUpperCase();

      try {
        RockPaperScissors.Choice.valueOf(choice);
        out.println("You said: " + choice);
        return choice;
      } catch (IllegalArgumentException e) {
        out.println("Invalid input. Please choose ROCK, PAPER, or SCISSORS.");
      }
    }
  }

  public enum Choice {
    ROCK,
    PAPER,
    SCISSORS
  }
}
