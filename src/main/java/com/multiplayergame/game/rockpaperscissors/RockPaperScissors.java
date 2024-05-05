package com.multiplayergame.game.rockpaperscissors;

import java.util.HashMap;
import java.util.Map;

public class RockPaperScissors {

  public enum Choice {
    ROCK,
    PAPER,
    SCISSORS
  }

  private static final Map<String, Integer> outcomePointsMap;
  private static final Map<String, String> outcomeResultMap;

  static {
    String player1win = "Player 1 wins";
    String player2win = "Player 2 wins";

    outcomePointsMap = new HashMap<>();
    outcomeResultMap = new HashMap<>();

    // Define the outcomes for each combination
    outcomePointsMap.put("ROCK_SCISSORS", 0);
    outcomePointsMap.put("ROCK_PAPER", 1);
    outcomePointsMap.put("PAPER_ROCK", 0);
    outcomePointsMap.put("PAPER_SCISSORS", 1);
    outcomePointsMap.put("SCISSORS_PAPER", 0);
    outcomePointsMap.put("SCISSORS_ROCK", 1);

    outcomeResultMap.put("ROCK_SCISSORS", player1win);
    outcomeResultMap.put("ROCK_PAPER", player2win);
    outcomeResultMap.put("PAPER_ROCK", player1win);
    outcomeResultMap.put("PAPER_SCISSORS", player2win);
    outcomeResultMap.put("SCISSORS_PAPER", player1win);
    outcomeResultMap.put("SCISSORS_ROCK", player2win);
  }

  public static String determineOutcome(String player1Choice, String player2Choice, int[] points) {
    if (player1Choice.equalsIgnoreCase(player2Choice)) {
      return "Tie";
    }

    // Concatenate choices to form the key for the map
    String key = player1Choice.toUpperCase() + "_" + player2Choice.toUpperCase();

    // Get the outcome from the map
    int winningPlayer = outcomePointsMap.getOrDefault(key, -1);
    String result = outcomeResultMap.getOrDefault(key, "Invalid input");

    // Update points based on the winning player
    if (winningPlayer != -1) {
      points[winningPlayer]++;
    }
    return result;
  }
}
