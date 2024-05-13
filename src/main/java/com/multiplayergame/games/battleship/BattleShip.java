package com.multiplayergame.games.battleship;

import com.multiplayergame.games.GameSocket;

import java.io.IOException;
import java.net.Socket;

public class BattleShip extends GameSocket {
  protected BattleShip(Socket client1, Socket client2) throws IOException {
    super(client1, client2);
  }
  //TODO:
  // - Main class: game loop
  // - GameBoard class:
  //    - contains game board
  //    - method for placing ships
  //    - check for hits
  //    - update board state
  // - Ship class: ship on the board
  //    - position, size, orientation
  // - Player class: board status (boats positions...), actions method
  // - InputHandler class
  // - GameLogic class: determines win, handles turns
  // - Networking class: try to make this as separate as possible from the logic

 @Override
  public boolean gameLoop() throws IOException {
    return false;
  }
}
