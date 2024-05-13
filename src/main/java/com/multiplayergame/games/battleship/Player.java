package com.multiplayergame.games.battleship;

import java.util.ArrayList;
import java.util.List;

import com.multiplayergame.BoardUtils;
import lombok.Getter;

@Getter
public class Player {

  protected String name;
  protected GameBoard playerBoard;
  protected EnemyBoard enemyBoard;
  protected List<Ship> ships;

  public Player(String name) {
    this.name = name;
    this.playerBoard = new GameBoard();
    this.enemyBoard = new EnemyBoard();
    this.ships = new ArrayList<>();
    initShips();
   }

  private void initShips() {
    ships.add(new Ship(ShipNames.CARRIER.getName(),ShipNames.CARRIER.getLength()));
    ships.add(new Ship(ShipNames.BATTLESHIP.getName(), ShipNames.BATTLESHIP.getLength()));
    ships.add(new Ship(ShipNames.CRUISER.getName(), ShipNames.CRUISER.getLength()));
    ships.add(new Ship(ShipNames.SUBMARINE.getName(), ShipNames.SUBMARINE.getLength()));
    ships.add(new Ship(ShipNames.DESTROYER.getName(), ShipNames.DESTROYER.getLength()));
  }

  // use in server to set position of all ships using in from clients
  public void setShipPosition(ShipNames shipName, Integer x, Integer y, boolean vertical) {
    for (Ship ship : ships) {
      if (shipName.getName().equals(ship.getName())) {
        ship.setXPos(x);
        ship.setYPos(y);
        ship.setVertical(vertical);
        ship.initShip();
        playerBoard.insertShip(ship);
      }
    }
  }
  public void hitOrMiss(String posStr, boolean hit) {
    String value = hit ? "%" : "x";
    BoardUtils.setCell(enemyBoard.getBoard(), posStr, value);
  }

  public boolean checkForLoss() {
    for (Ship ship : ships) {
      if (!ship.isSunk()) {
        return false;
      }
    }
    return true;
  }

  public void calculateHit(String posStr) {
    for (Ship ship : ships) {
      if (ship.occupiedCells.contains(posStr)) ship.recordHit();
    }
  }

}