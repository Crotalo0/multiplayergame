package com.multiplayergame.games.battleship;

import com.multiplayergame.BoardUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EnemyBoard {

  protected List<List<String>> board = BoardUtils.createBoard(8,8);

}
