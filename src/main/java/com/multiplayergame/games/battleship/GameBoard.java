package com.multiplayergame.games.battleship;

import com.multiplayergame.BoardUtils;
import java.util.List;
import lombok.Getter;

@Getter
public class GameBoard {

  protected List<List<String>> board = BoardUtils.createBoard(8,8);

  // If returns false something has gone wrong inserting the ship
  public boolean insertShip(Ship ship) {
    if (!BoardUtils.isInGrid(board, ship.colPosition, ship.rowPosition) || !BoardUtils.isInGrid(board, ship.xEndPos, ship.yEndPos)) {
      return false;
    }

    if (ship.isVertical) {
      for (int j = ship.rowPosition; j <= ship.yEndPos; j++) {
        if (!"_".equals(board.get(j).get(ship.colPosition))) {
          return false;
        }
      }
      for (int j = ship.rowPosition + 1; j < ship.yEndPos; j++) {
        board.get(j).set(ship.colPosition, "o");
      }
      board.get(ship.rowPosition).set(ship.colPosition, "^");
      board.get(ship.yEndPos).set(ship.xEndPos, "v");
    } else {
      List<String> row = board.get(ship.rowPosition);
      for (int i = ship.colPosition; i <= ship.xEndPos ; i++) {
        if (!"_".equals(row.get(i))) {
          return false;
        }
      }
      for (int i = ship.colPosition + 1; i <= ship.xEndPos - 1; i++) {
        row.set(i, "o");
      }
      row.set(ship.colPosition, "<");
      row.set(ship.xEndPos, ">");
    }
    return true;
  }

  public boolean hasHit(String posStr) {
    return !"_".equals(BoardUtils.getCell(board, posStr));
  }
}
