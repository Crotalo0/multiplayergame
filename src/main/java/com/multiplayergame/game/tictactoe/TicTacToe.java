package com.multiplayergame.game.tictactoe;

import com.multiplayergame.game.GameSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TicTacToe extends GameSocket {
  List<String> choices = Arrays.asList(
      "00","01","02",
      "10", "11", "12",
      "20", "21", "22");
  private final List<List<String>> grid;

  public TicTacToe(Socket client1, Socket client2) throws IOException {
    super(client1, client2);
    grid = createGrid();
  }

  public static List<List<String>> createGrid() {
    List<List<String>> grid =  new ArrayList<>(3);
    grid.add(Arrays.asList("_","_","_"));
    grid.add(Arrays.asList("_","_","_"));
    grid.add(Arrays.asList("_","_","_"));
    return grid;
  }

  @Override
  public boolean playRound() throws IOException {
    String outcome = null;
    out1.println("You are player 1 (O)");
    out2.println("You are player 2 (X)");
    boolean error = false;

    while (true) {
      out2.println("Wait for player 1");
      String choice1 = getPlayerChoice(in1, out1);
      if (choice1 == null) {
        out2.println("Player 1 has quit the session. Restart the client.");
        error = true;
        break;
      }
      setCell(choice1, "O");
      sendMessageToBothClients(gridAsString(grid));
      if (someoneWon()) {
        outcome = "Player 1 WON";
        points[0]++;
        break;
      }

      out1.println("Wait for player 2");
      String choice2 = getPlayerChoice(in2, out2);
      if (choice2 == null) {
        out1.println("Player 2 has quit the session. Restart the client");
        error = true;
        break;
      }
      setCell(choice2, "X");
      sendMessageToBothClients(gridAsString(grid));
      if (someoneWon()) {
        outcome = "Player 2 WON";
        points[1]++;
        break;
      }
    }
    if (!error) {
      sendMessageToBothClients("Game outcome: " + outcome);
      sendMessageToBothClients("POINTS: player1->" + points[0] + " - player2->" + points[1]);
      return true;
    }
    return false;
  }

  private boolean someoneWon() {
    boolean row = (!"_".equals(getCell("00")) && getCell("00").equals(getCell("01")) && getCell("00").equals(getCell("02"))) ||
        (!"_".equals(getCell("10")) && getCell("10").equals(getCell("11")) && getCell("10").equals(getCell("12"))) ||
        (!"_".equals(getCell("20")) && getCell("20").equals(getCell("21")) && getCell("20").equals(getCell("22")));

    boolean col = (!"_".equals(getCell("00")) && getCell("00").equals(getCell("10")) && getCell("00").equals(getCell("20"))) ||
        (!"_".equals(getCell("01")) && getCell("01").equals(getCell("11")) && getCell("01").equals(getCell("21"))) ||
        (!"_".equals(getCell("02")) && getCell("02").equals(getCell("12")) && getCell("02").equals(getCell("22")));

    boolean diag = (!"_".equals(getCell("00")) && getCell("00").equals(getCell("11")) && getCell("00").equals(getCell("22"))) ||
        (!"_".equals(getCell("02")) && getCell("02").equals(getCell("11")) && getCell("02").equals(getCell("20")));

    return row || col || diag;
  }

  protected String getPlayerChoice(BufferedReader in, PrintWriter out) throws IOException {
    while (true) {
      out.println("Input: ");
      String choice = in.readLine();
      if (choice.equalsIgnoreCase("QUIT")) {
        out.println("You chose to quit. Ending the session.");
        return null;
      }
      // repeat input there
      if ( !validMove(choice) ) {
        out.println("Invalid input. Please choose a valid position like 00, 01, 02, etc...");
        continue;
      }
      out.println("Your move: " + "(x: " + choice.charAt(0) + ", y: " + choice.charAt(1) +")");
      return choice;
    }
  }

  public String gridAsString(List<List<String>> grid) {
    return grid.stream().map(row -> row + " " + "\n").collect(Collectors.joining());
  }

  public boolean validMove(String posStr) {

    if ( !choices.contains(posStr) )  {
      System.out.println("ERROR: out of bound");
      return false;
    }
    if ( !"_".equals(getCell(posStr)) ) {
      System.out.println("ERROR: occupied cell");
      return false;
    }
    List<Integer> pos = getArrayPosFromString(posStr);
    if ( pos.get(0) > 2 || pos.get(0) < 0 || pos.get(1) > 2 || pos.get(1) < 0 ) {
      System.out.println("ERROR: out of bound 2");
      return false;
    }
    return true;
  }

  private void setCell(String posStr, String player ) {
    List<Integer> pos = getArrayPosFromString(posStr);
    grid.get(pos.get(0)).set(pos.get(1), player);
  }

  private String getCell(String posStr) {
    List<Integer> pos = getArrayPosFromString(posStr);
    return grid.get(pos.get(0)).get(pos.get(1));
  }

  private List<Integer> getArrayPosFromString(String posStr) {
    return Arrays.asList(
        Integer.parseInt(String.valueOf(posStr.charAt(0))),
        Integer.parseInt(String.valueOf(posStr.charAt(1)))
    );
  }
}
