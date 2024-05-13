package com.multiplayergame.games.battleship;

import com.multiplayergame.BoardUtils;
import java.util.List;
import lombok.Getter;

@Getter
public class GameBoard {

  protected List<List<String>> board = BoardUtils.createBoard(8,8);

  public static void main(String[] args){
    GameBoard gb = new GameBoard();
    Ship ship = new Ship("test", 4, 4, 4, false);
    boolean test = gb.insertShip(ship);
    System.out.println(test);
    String board = BoardUtils.gridAsString(gb.getBoard());
    System.out.println(board);
    Ship ship2 = new Ship("test", 5, 4, 3, true);
    boolean test2 = gb.insertShip(ship2);
    System.out.println(test2);
    String board2 = BoardUtils.gridAsString(gb.getBoard());
    System.out.println(board2);
  }

  // If returns false something has gone wrong inserting the ship
  // The pivot point is the upper corner when vertical and left when horizontal
  // o++++>;
  // o
  // +
  // v
  public boolean insertShip(Ship ship) {
    if (!BoardUtils.isInGrid(board, ship.xPos, ship.yPos) || !BoardUtils.isInGrid(board, ship.xEndPos, ship.yEndPos)) {
      return false;
    }

    if (ship.isVertical) {
      for (int j = ship.yPos; j <= ship.yEndPos; j++) {
        if (!"_".equals(board.get(j).get(ship.xPos))) {
          return false;
        }
      }
      for (int j = ship.yPos + 1; j < ship.yEndPos; j++) {
        board.get(j).set(ship.xPos, "+");
      }
      board.get(ship.yPos).set(ship.xPos, "o");
      board.get(ship.yEndPos).set(ship.xEndPos, "v");
    } else {
      List<String> row = board.get(ship.yPos);
      for (int i = ship.xPos; i <= ship.xEndPos ; i++) {
        if (!"_".equals(row.get(i))) {
          return false;
        }
      }
      for (int i = ship.xPos + 1; i <= ship.xEndPos - 1; i++) {
        row.set(i, "+");
      }
      row.set(ship.xPos, "o");
      row.set(ship.xEndPos, ">");
    }
    return true;
  }

  public boolean hasHit(String posStr) {
    return !"_".equals(BoardUtils.getCell(board, posStr));
  }

}
