package com.multiplayergame.games.battleship;

import com.multiplayergame.BoardUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@Setter
@Getter
public class InputHandler {
  protected Player player;

  public InputHandler(Player player) {
    this.player = player;
  }

  public String getPlayerShipPos(BufferedReader in, PrintWriter out)
      throws IOException {
    while (true) {
      String pos = in.readLine();
      if (pos.equalsIgnoreCase("QUIT")) {
        out.println("You chose to quit. Ending the session.");
        return null;
      }
      if (!pos.matches("[0-7][0-7][ny]")) {
        out.println("Invalid pos (ex. 00y)");
        return null;
      }
      return pos;
    }
  }

  public String getPlayerInput(BufferedReader in, PrintWriter out) throws IOException {
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

  public boolean validMove(String posStr) {
    if (!posStr.matches("[0-7][0-7]")) {
      return false;
    }
    String cell = BoardUtils.getCell(player.getEnemyBoard().getBoard(), posStr);
    return "_".equals(cell);
  }
}