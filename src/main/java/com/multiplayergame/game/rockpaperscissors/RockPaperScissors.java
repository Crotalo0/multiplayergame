package com.multiplayergame.game.rockpaperscissors;

public class RockPaperScissors {

  public enum Choice {
    ROCK,
    PAPER,
    SCISSORS
  }
  public static String determineOutcome(String player1, String player2) {
    String[] win = new String[] {"player 1 wins", "player 2 wins"};

    Choice player1Choice = Choice.valueOf(player1);
    Choice player2Choice = Choice.valueOf(player2);

    if (player1Choice == player2Choice) {
      return "Tie";
    }
    switch (player1Choice) {
      case ROCK:
        return (player2Choice == Choice.SCISSORS) ? win[0] : win[1];
      case PAPER:
        return (player2Choice == Choice.ROCK) ? win[0] : win[1];
      case SCISSORS:
        return (player2Choice == Choice.PAPER) ? win[0] : win[1];
      default:
        throw new IllegalArgumentException("Invalid choices for the game.");
    }
  }
}
