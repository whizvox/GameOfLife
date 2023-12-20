package me.whizvox.gameoflife.simulation;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class World {

  private final Object mutex;
  private int cycles;
  private Set<Position> cells;
  private Set<Position> nextCells;

  public World() {
    mutex = this;
    cycles = 0;
    cells = new HashSet<>();
    nextCells = new HashSet<>();
  }

  private int getNeighborCount(Position pos) {
    int count = 0;
    for (int xOff = -1; xOff <= 1; xOff++) {
      for (int yOff = -1; yOff <= 1; yOff++) {
        if (xOff != 0 || yOff != 0) {
          Position neighborPos = new Position(pos.x() + xOff, pos.y() + yOff);
          if (cells.contains(neighborPos)) {
            count++;
          }
        }
      }
    }
    return count;
  }

  public int getCycles() {
    return cycles;
  }

  public void forEachCell(Consumer<Position> consumer) {
    synchronized (mutex) {
      cells.forEach(consumer);
    }
  }

  public void activate(int x, int y) {
    synchronized (mutex) {
      cells.add(new Position(x, y));
    }
  }

  public void deactivate(int x, int y) {
    synchronized (mutex) {
      cells.remove(new Position(x, y));
    }
  }

  public boolean toggle(int x, int y) {
    Position pos = new Position(x, y);
    synchronized (mutex) {
      if (cells.contains(pos)) {
        cells.remove(pos);
        return false;
      } else {
        cells.add(pos);
        return true;
      }
    }
  }

  public void tick() {
    nextCells.clear();
    cells.forEach(pos -> {
      int neighbors = getNeighborCount(pos);
      if (neighbors >= 2 && neighbors <= 3) {
        nextCells.add(pos);
      }
      for (int xOff = -1; xOff <= 1; xOff++) {
        for (int yOff = -1; yOff <= 1; yOff++) {
          if (xOff != 0 || yOff != 0) {
            Position nPos = new Position(pos.x() + xOff, pos.y() + yOff);
            int localNeighbors = getNeighborCount(nPos);
            if (localNeighbors == 3) {
              nextCells.add(nPos);
            }
          }
        }
      }
    });
    Set<Position> temp = cells;
    synchronized (mutex) {
      cycles++;
      cells = nextCells;
      nextCells = temp;
    }
  }

  public void reset() {
    synchronized (mutex) {
      cycles = 0;
      cells.clear();
      nextCells.clear();
    }
  }

}
