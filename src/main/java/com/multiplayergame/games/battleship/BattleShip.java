package com.multiplayergame.games.battleship;

import com.multiplayergame.BoardUtils;
import com.multiplayergame.games.GameSocket;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class BattleShip extends GameSocket {
  protected Player player1;
  protected Player player2;
  private Integer roundNumber = 0;
  protected InputHandler p1handler;
  protected InputHandler p2handler;

  public BattleShip(Socket client1, Socket client2) throws IOException {
    super(client1, client2);
    this.player1 = new Player("player1");
    this.player2 = new Player("player2");

    this.p1handler = new InputHandler(player1);
    this.p2handler = new InputHandler(player2);
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
//   player1.setShipPosition(ShipNames.CARRIER, 0,0, false);
//   player1.setShipPosition(ShipNames.BATTLESHIP, 1, 1, false);
//   player1.setShipPosition(ShipNames.CRUISER,2,2,false);
//   player1.setShipPosition(ShipNames.SUBMARINE,3,3,false);
   player1.setShipPosition(ShipNames.DESTROYER,4,4, false);

//   player2.setShipPosition(ShipNames.CARRIER, 0,0, false);
//   player2.setShipPosition(ShipNames.BATTLESHIP, 1, 1, false);
//   player2.setShipPosition(ShipNames.CRUISER,2,2,false);
//   player2.setShipPosition(ShipNames.SUBMARINE,3,3,false);
   player2.setShipPosition(ShipNames.DESTROYER,4,4, false);

   // TODO: all players have to setups ships

   while (true) {
     if (!playOneRound()) break;
   }
   if (error) return true;
   // TODO: set outcome and points
   sendMessageToBothClients("Game outcome: " + outcome);
   sendMessageToBothClients("POINTS: player1->" + points[0] + " - player2->" + points[1]);

   // reset board
//   player1.resetAll();
//   player2.resetAll();
   this.player1 = new Player("player1");
   this.player2 = new Player("player2");

   this.p1handler = new InputHandler(player1);
   this.p2handler = new InputHandler(player2);

   //   player1.setShipPosition(ShipNames.CARRIER, 0,0, false);
//   player1.setShipPosition(ShipNames.BATTLESHIP, 1, 1, false);
//   player1.setShipPosition(ShipNames.CRUISER,2,2,false);
//   player1.setShipPosition(ShipNames.SUBMARINE,3,3,false);
   player1.setShipPosition(ShipNames.DESTROYER,4,4, false);

//   player2.setShipPosition(ShipNames.CARRIER, 0,0, false);
//   player2.setShipPosition(ShipNames.BATTLESHIP, 1, 1, false);
//   player2.setShipPosition(ShipNames.CRUISER,2,2,false);
//   player2.setShipPosition(ShipNames.SUBMARINE,3,3,false);
   player2.setShipPosition(ShipNames.DESTROYER,4,4, false);

   roundNumber++;
   return false;
  }

  private boolean inputInvalid(String input, PrintWriter out) {
    if (null == input) {
      out.println("Wait for other player...");
      this.error = true;
      return true;
    }
    return false;
  }
  private boolean playOneRound() throws IOException {
    out1.println("You are player1");
    out2.println("You are player2");

    // PLAYER 1
    String choice1;
    if (roundNumber % 2 == 0) {
      out2.println("Wait for player1");
      choice1 = p1handler.getPlayerInput(in1, out1);
      if (inputInvalid(choice1, out2)) return false;

      player1.hitOrMiss(choice1, player2.getPlayerBoard().hasHit(choice1));
      player2.calculateHit(choice1, player2.getPlayerBoard().hasHit(choice1));
    } else {
      out1.println("Wait for player2");
      choice1 = p2handler.getPlayerInput(in2, out2);
      if (inputInvalid(choice1, out1)) return false;

      player2.hitOrMiss(choice1, player1.getPlayerBoard().hasHit(choice1));
      player1.calculateHit(choice1, player1.getPlayerBoard().hasHit(choice1));
    }
    BoardUtils.printBoardsShip(out1, out2, player1, player2);
    if (playerWon()) return false;

    // PLAYER 2
    String choice2;
    if (roundNumber % 2 == 0) {
      out1.println("Wait for player2");
      choice2 = p2handler.getPlayerInput(in2, out2);
      if (inputInvalid(choice2, out1)) return false;

      player2.hitOrMiss(choice2, player1.getPlayerBoard().hasHit(choice2));
      player1.calculateHit(choice2, player1.getPlayerBoard().hasHit(choice2));
    } else {
      out2.println("Wait for player1");
      choice2 = p1handler.getPlayerInput(in1, out1);
      if (inputInvalid(choice2, out2)) return false;

      player1.hitOrMiss(choice2, player2.getPlayerBoard().hasHit(choice2));
      player2.calculateHit(choice2, player2.getPlayerBoard().hasHit(choice2));
    }
    BoardUtils.printBoardsShip(out1, out2, player1, player2);
    return (!playerWon());
  }

  private boolean playerWon() {
    if (player1.allShipSunk()) {
      outcome = "Player2 has won";
      points[1]++;
      return true;
    } else if (player2.allShipSunk()) {
      outcome = "Player1 has won";
      points[0]++;
      return true;
    }

    return false;
  }
}
