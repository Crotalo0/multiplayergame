package com.multiplayergame.games.tictactoe;

import com.multiplayergame.games.GameSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TicTacToe extends GameSocket {

  List<String> choices = Arrays.asList("00", "01", "02", "10", "11", "12", "20", "21", "22");
  private String outcome;
  private boolean error;
  private Integer totalMoves = 0;
  private List<List<String>> grid;
  private Integer roundNumber = 1;

  public TicTacToe(Socket client1, Socket client2) throws IOException {
    super(client1, client2);
    grid = createGrid();
  }

  @Override
  public boolean gameLoop() throws IOException {
    while (true) {
      if (!playOneRound()) break;
    }
    if (!error) {
      sendMessageToBothClients("Game outcome: " + outcome);
      sendMessageToBothClients("POINTS: player1->" + points[0] + " - player2->" + points[1]);

      this.grid = createGrid();
      roundNumber++;
      totalMoves = 0;

      return false;
    }
    return true;
  }

  private boolean playOneRound() throws IOException {
    out1.println("You are 'O'");
    out2.println("You are 'X'");

    // PLAYER 1
    String choice1;
    if (roundNumber % 2 == 0) {
      out2.println("Wait for player1");
      choice1 = getPlayerChoice(in1, out1);
      if (null == choice1) {
        out2.println("Player1 has quit the session. Restart the client.");
        error = true;
        return false;
      }
      setCell(choice1, "O");
    } else {
      out1.println("Wait for player2");
      choice1 = getPlayerChoice(in2, out2);
      if (null == choice1) {
        out1.println("Player2 has quit the session. Restart the client.");
        error = true;
        return false;
      }
      setCell(choice1, "X");
    }
    this.totalMoves++;
    sendMessageToBothClients(gridAsString(grid));
    if (playerWonOrTie(roundNumber)) return false;

    // PLAYER 2
    String choice2;
    if (roundNumber % 2 == 0) {
      out1.println("Wait for player2");
      choice2 = getPlayerChoice(in2, out2);
      if (null == choice2) {
        out1.println("Player2 has quit the session. Restart the client.");
        error = true;
        return false;
      }
      setCell(choice2, "X");
    } else {
      out2.println("Wait for player1");
      choice2 = getPlayerChoice(in1, out1);
      if (null == choice2) {
        out2.println("Player1 has quit the session. Restart the client.");
        error = true;
        return false;
      }
      setCell(choice2, "O");
    }
    this.totalMoves++;
    sendMessageToBothClients(gridAsString(grid));
    return (!playerWonOrTie(roundNumber));
  }

  // WIN LOGIC
  private boolean checkForTris() {
    boolean row =
        (!"_".equals(getCell("00"))
                && getCell("00").equals(getCell("01"))
                && getCell("00").equals(getCell("02")))
            || (!"_".equals(getCell("10"))
                && getCell("10").equals(getCell("11"))
                && getCell("10").equals(getCell("12")))
            || (!"_".equals(getCell("20"))
                && getCell("20").equals(getCell("21"))
                && getCell("20").equals(getCell("22")));

    boolean column =
        (!"_".equals(getCell("00"))
                && getCell("00").equals(getCell("10"))
                && getCell("00").equals(getCell("20")))
            || (!"_".equals(getCell("01"))
                && getCell("01").equals(getCell("11"))
                && getCell("01").equals(getCell("21")))
            || (!"_".equals(getCell("02"))
                && getCell("02").equals(getCell("12"))
                && getCell("02").equals(getCell("22")));

    boolean diagonal =
        (!"_".equals(getCell("00"))
                && getCell("00").equals(getCell("11"))
                && getCell("00").equals(getCell("22")))
            || (!"_".equals(getCell("02"))
                && getCell("02").equals(getCell("11"))
                && getCell("02").equals(getCell("20")));

    return row || column || diagonal;
  }

  private boolean playerWonOrTie(Integer roundNumber) {
    Integer moveLimit = 9;
    if (checkForTris()) {
      if (roundNumber % 2 == 0) {
        this.outcome = PLAYER_1_WINS;
        points[0]++;
      } else {
        this.outcome = PLAYER_2_WINS;
        points[1]++;
      }
      return true;
    } else if (totalMoves.equals(moveLimit)) {
      this.outcome = TIE;
      return true;
    }
    return false;
  }

  // USER INPUT LOGIC
  protected String getPlayerChoice(BufferedReader in, PrintWriter out) throws IOException {
    while (true) {
      out.println("Your move (Input ex.: 01 (row 0 and column 1)");
      String choice = in.readLine();
      if (choice.equalsIgnoreCase("QUIT")) {
        out.println("You chose to quit. Ending the session.");
        return null;
      }
      if (!validMove(choice)) {
        out.println("Invalid input. Please choose a valid position like 00, 01, 02, etc...");
        continue;
      }
      return choice;
    }
  }

  // MOVE VALIDATION
  public boolean validMove(String posStr) {

    if (!choices.contains(posStr)) {
      LOG.error("ERROR: out of bound");
      return false;
    }
    String emptyCell = "_";
    if (!emptyCell.equals(getCell(posStr))) {
      LOG.error("ERROR: occupied cell");
      return false;
    }
    List<Integer> pos = getArrayPosFromString(posStr);
    if (pos.get(0) > 2 || pos.get(0) < 0 || pos.get(1) > 2 || pos.get(1) < 0) {
      LOG.error("ERROR: out of bound 2");
      return false;
    }
    return true;
  }

  // GRID UTILS
  private List<List<String>> createGrid() {
    grid.add(Arrays.asList("_", "_", "_"));
    grid.add(Arrays.asList("_", "_", "_"));
    grid.add(Arrays.asList("_", "_", "_"));
    return grid;
  }

  private void setCell(String posStr, String player) {
    List<Integer> pos = getArrayPosFromString(posStr);
    grid.get(pos.get(0)).set(pos.get(1), player);
  }

  private String getCell(String posStr) {
    List<Integer> pos = getArrayPosFromString(posStr);
    return grid.get(pos.get(0)).get(pos.get(1));
  }

  public String gridAsString(List<List<String>> grid) {
    return grid.stream().map(row -> row + " " + "\n").collect(Collectors.joining());
  }

  private List<Integer> getArrayPosFromString(String posStr) {
    return Arrays.asList(
        Integer.parseInt(String.valueOf(posStr.charAt(0))),
        Integer.parseInt(String.valueOf(posStr.charAt(1))));
  }
}
