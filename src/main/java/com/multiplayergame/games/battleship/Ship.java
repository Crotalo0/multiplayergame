package com.multiplayergame.games.battleship;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ship {
  protected String name;
  protected Integer size;
  protected Integer xPos;
  protected Integer yPos;
  protected Integer xEndPos;
  protected Integer yEndPos;
  protected boolean isVertical;

  public Ship(String name, Integer size, Integer xPos, Integer yPos, boolean isVertical) {
    this.name = name;
    this.size = size;
    this.xPos = xPos;
    this.yPos = yPos;
    this.isVertical = isVertical;
    calculateOtherVertexPosition();
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
}
