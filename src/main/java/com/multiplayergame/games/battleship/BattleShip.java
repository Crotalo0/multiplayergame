package com.multiplayergame.games.battleship;

import com.multiplayergame.BoardUtils;
import com.multiplayergame.games.GameSocket;

import java.io.BufferedReader;
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
   Thread player1Thread = new Thread(() -> {
     try {
       out1.println("Choose ship position");
       addShips(in1, out1, player1, p1handler);
       out1.println("Wait for player2");
     } catch (IOException e) {
       e.printStackTrace();
     }
   });

   Thread player2Thread = new Thread(() -> {
     try {
       out2.println("Choose ship position");
       addShips(in2, out2, player2, p2handler);
     } catch (IOException e) {
       e.printStackTrace();
     }
   });

   player1Thread.start();
   player2Thread.start();

   try {
     player1Thread.join();
     player2Thread.join();
   } catch (InterruptedException e) {
     e.printStackTrace();
   }

   while (true) {
     if (!playOneRound()) break;
   }
   if (error) return true;

   sendMessageToBothClients("Game outcome: " + outcome);
   sendMessageToBothClients("POINTS: player1->" + points[0] + " - player2->" + points[1]);
   // reset board
   player1.resetBoards();
   player2.resetBoards();

   roundNumber++;
   return false;
  }

  private void addShips(BufferedReader in, PrintWriter out, Player player, InputHandler ph) throws IOException {
    for (Ship ship : player.getShips()) {
      out.println(BoardUtils.gridAsString(player.getPlayerBoard().getBoard()));
      while (true) {
        out.println("Place ship(" + ship.toString() + ")");
        String pos = ph.getPlayerShipPos(in, out);
        if (pos == null) {
          out.println("Invalid input");
          continue;
        }
        Integer x = Character.getNumericValue(pos.charAt(0));
        Integer y = Character.getNumericValue(pos.charAt(1));
        boolean orientation = pos.charAt(2) == 'y';

        if (player.setShipPosition(ShipNames.valueOf(ship.getName().toUpperCase()), x, y, orientation)) {
          break;
        } else {
          out.println("Ship not added, check your board");
        }
      }
    }
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
    BoardUtils.printBoardsShip(out1, out2, player1, player2);
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
