package com.multiplayergame.games.tictactoe;

import com.multiplayergame.BoardUtils;
import com.multiplayergame.games.GameSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class TicTacToe extends GameSocket {

  List<String> choices = Arrays.asList("00", "01", "02", "10", "11", "12", "20", "21", "22");
  private Integer totalMoves = 0;
  private List<List<String>> grid;
  private Integer roundNumber = 1;

  public TicTacToe(Socket client1, Socket client2) throws IOException {
    super(client1, client2);
    grid = BoardUtils.createBoard(3,3);
  }

  @Override
  public boolean gameLoop() throws IOException {
    while (true) {
      if (!playOneRound()) break;
    }
    if (!error) {
      sendMessageToBothClients("Game outcome: " + outcome);
      sendMessageToBothClients("POINTS: player1->" + points[0] + " - player2->" + points[1]);

      this.grid = BoardUtils.createBoard(3, 3);
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
      BoardUtils.setCell(grid, choice1, "O");
    } else {
      out1.println("Wait for player2");
      choice1 = getPlayerChoice(in2, out2);
      if (null == choice1) {
        out1.println("Player2 has quit the session. Restart the client.");
        error = true;
        return false;
      }
      BoardUtils.setCell(grid, choice1, "X");
    }
    this.totalMoves++;
    sendMessageToBothClients(BoardUtils.gridAsString(grid));
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
      BoardUtils.setCell(grid, choice2, "X");
    } else {
      out2.println("Wait for player1");
      choice2 = getPlayerChoice(in1, out1);
      if (null == choice2) {
        out2.println("Player1 has quit the session. Restart the client.");
        error = true;
        return false;
      }
      BoardUtils.setCell(grid, choice2, "O");
    }
    this.totalMoves++;
    sendMessageToBothClients(BoardUtils.gridAsString(grid));
    return (!playerWonOrTie(roundNumber));
  }

  // WIN LOGIC
  private boolean checkForTris() {
    boolean row =
        (!"_".equals(BoardUtils.getCell(grid, "00"))
                && BoardUtils.getCell(grid,"00").equals(BoardUtils.getCell(grid, "01"))
                && BoardUtils.getCell(grid,"00").equals(BoardUtils.getCell(grid,"02")))
            || (!"_".equals(BoardUtils.getCell(grid,"10"))
                && BoardUtils.getCell(grid,"10").equals(BoardUtils.getCell(grid,"11"))
                && BoardUtils.getCell(grid,"10").equals(BoardUtils.getCell(grid,"12")))
            || (!"_".equals(BoardUtils.getCell(grid,"20"))
                && BoardUtils.getCell(grid,"20").equals(BoardUtils.getCell(grid,"21"))
                && BoardUtils.getCell(grid,"20").equals(BoardUtils.getCell(grid,"22")));

    boolean column =
        (!"_".equals(BoardUtils.getCell(grid, "00"))
                && BoardUtils.getCell(grid, "00").equals(BoardUtils.getCell(grid, "10"))
                && BoardUtils.getCell(grid, "00").equals(BoardUtils.getCell(grid, "20")))
            || (!"_".equals(BoardUtils.getCell(grid, "01"))
                && BoardUtils.getCell(grid, "01").equals(BoardUtils.getCell(grid, "11"))
                && BoardUtils.getCell(grid, "01").equals(BoardUtils.getCell(grid, "21")))
            || (!"_".equals(BoardUtils.getCell(grid, "02"))
                && BoardUtils.getCell(grid, "02").equals(BoardUtils.getCell(grid, "12"))
                && BoardUtils.getCell(grid, "02").equals(BoardUtils.getCell(grid, "22")));

    boolean diagonal =
        (!"_".equals(BoardUtils.getCell(grid, "00"))
                && BoardUtils.getCell(grid, "00").equals(BoardUtils.getCell(grid, "11"))
                && BoardUtils.getCell(grid, "00").equals(BoardUtils.getCell(grid, "22")))
            || (!"_".equals(BoardUtils.getCell(grid, "02"))
                && BoardUtils.getCell(grid, "02").equals(BoardUtils.getCell(grid, "11"))
                && BoardUtils.getCell(grid, "02").equals(BoardUtils.getCell(grid, "20")));

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
    if (!emptyCell.equals(BoardUtils.getCell(grid, posStr))) {
      LOG.error("ERROR: occupied cell");
      return false;
    }
    List<Integer> pos = BoardUtils.getArrayPosFromString(posStr);
    if (pos.get(0) > 2 || pos.get(0) < 0 || pos.get(1) > 2 || pos.get(1) < 0) {
      LOG.error("ERROR: out of bound 2");
      return false;
    }
    return true;
  }
}
