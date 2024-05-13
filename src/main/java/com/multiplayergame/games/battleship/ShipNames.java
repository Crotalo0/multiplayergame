package com.multiplayergame.games.battleship;

import lombok.Getter;

@Getter
public enum ShipNames {
  CARRIER("Carrier", 5),
  BATTLESHIP("Battleship", 4),
  CRUISER("Cruiser", 3),
  SUBMARINE("Submarine", 3),
  DESTROYER("Destroyer", 2);

  private final int length;
  private final String name;

  ShipNames(String name, int length) {
    this.length = length;
    this.name = name;
  }
}
