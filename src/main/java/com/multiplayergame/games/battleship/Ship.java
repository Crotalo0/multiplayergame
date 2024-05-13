package com.multiplayergame.games.battleship;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Ship {
  protected String name;
  protected Integer size;
  @Setter protected Integer xPos;
  @Setter protected Integer yPos;
  @Setter protected boolean isVertical;
  protected Integer xEndPos;
  protected Integer yEndPos;

  protected Integer hits;
  @Setter protected boolean isSunk;
  protected List<String> occupiedCells;

  public Ship(String name, Integer size) {
    this.name = name;
    this.size = size;
    this.hits = 0;
    this.occupiedCells = new ArrayList<>(size);
  }

  public void recordHit() {
    hits++;
    if (hits >= size) {
      this.isSunk = true;
    }
  }

  private void calculateOtherVertexPosition() {
    if (this.isVertical) {
      this.xEndPos = this.xPos;
      this.yEndPos = this.yPos + this.size - 1;
    } else {
      this.xEndPos = this.xPos + this.size - 1;
      this.yEndPos = this.yPos;
    }
  }

  private void calculateOccupiedCells() {
    Integer tempX = this.xPos;
    Integer tempY = this.yPos;
    for (int i = 0; i < this.size; i++) {
      StringBuilder pos = new StringBuilder(2);
      if (this.isVertical) {
        pos.append(tempX).append(tempY+i);
      } else {
        pos.append(tempX+i).append(tempY);
      }
      occupiedCells.add(pos.toString());
    }
  }

  public void initShip(){
    this.calculateOccupiedCells();
    this.calculateOtherVertexPosition();
  }
}
