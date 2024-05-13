package com.multiplayergame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BoardUtils {
  private BoardUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static List<List<String>> createBoard(int rows, int columns) {
    List<List<String>> board = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      List<String> row = new ArrayList<>();
      for (int j = 0; j < columns; j++) {
        row.add("_");
      }
      board.add(row);
    }
    return board;
  }

  public static void setCell(List<List<String>> grid, String posStr, String value) {
    List<Integer> pos = getArrayPosFromString(posStr);
    grid.get(pos.get(0)).set(pos.get(1), value);
  }

  public static String getCell(List<List<String>> grid, String posStr) {
    List<Integer> pos = getArrayPosFromString(posStr);
    return grid.get(pos.get(0)).get(pos.get(1));
  }

  public static String gridAsString(List<List<String>> grid) {
    StringBuilder result = new StringBuilder();

    result.append("   ");
    for (int i = 0; i < grid.get(0).size(); i++) {
      result.append(i).append(" ");
    }
    result.append("\n");

    for (int i = 0; i < grid.size(); i++) {
      result.append(String.format("%2d ", i)); // Row legend
      for (String cell : grid.get(i)) {
        result.append(cell).append(" ");
      }
      result.append("\n");
    }
    return result.toString();
  }

  public static List<Integer> getArrayPosFromString(String posStr) {
    return Arrays.asList(
        Integer.parseInt(String.valueOf(posStr.charAt(0))),
        Integer.parseInt(String.valueOf(posStr.charAt(1))));
  }

  public static boolean isInGrid(List<List<String>> grid, Integer xPos, Integer yPos) {
    return !( xPos > grid.size() || yPos > grid.size() || xPos < 0 || yPos < 0 );
  }

}
