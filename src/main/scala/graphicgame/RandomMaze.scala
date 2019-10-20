package graphicgame

import scalafx.scene.shape.Shape

/**
 * This is an implementation of a random maze. I don't recommend that you edit this file much.
 * The main at the bottom has an example of how you should create a Maze. See the documentation comments on the
 * apply method.
 */
class RandomMaze private(val cellSize: Int, val wrap: Boolean, wallsInput: Array[Array[Int]]) extends Maze {
  require(cellSize > 1, "The cell size must be at least 2.")
  require(wallsInput.length > 0 && wallsInput(0).length > 0, "Dimensions of maze must both be greater than 0.")
  private val walls = wallsInput.map(row => row.map(i => i).toArray).toArray

  /**
   * Tells you the width of the maze in cells.
   */
  def width = walls(0).length * cellSize
  
  /**
   * Tells you the height of the maze in cells.
   */
  def height = walls.length * cellSize

  private def getWall(r: Int, c: Int): Int = {
    if (wrap) {
      walls((r + walls.length) % walls.length)((c + walls(0).length) % walls(0).length)
    } else {
      if (r < 0 || r >= walls.length || c < 0 || c >= walls(0).length) 2 else walls(r)(c)
    }
  }
  
  /**
   * Tells you if there is a wall at a particular row and column location.
   * @param row The row to check.
   * @param col The column to check.
   */
  def apply(row: Int, col: Int): CellType = {
    if(isOpen(row, col)) Floor else Wall
  }
  
  private def isOpen(row: Int, col: Int): Boolean = { 
    import RandomMaze._
    val fracRow = (row + height) % cellSize
    val fracCol = (col + width) % cellSize
    if (wrap) {
      if (fracRow > 0 && fracCol > 0) true
      else {
        val cellRow = ((row + height) / cellSize) % walls.length
        val cellCol = ((col + height) / cellSize) % walls(0).length
        (fracRow, fracCol) match {
          case (0, 0) => getWall(cellRow, cellCol) == 0 && (getWall(cellRow - 1, cellCol) & VerticalWall) == 0 && (getWall(cellRow, cellCol - 1) & HorizontalWall) == 0
          case (0, _) => (getWall(cellRow, cellCol) & HorizontalWall) == 0
          case (_, 0) => (getWall(cellRow, cellCol) & VerticalWall) == 0
          case _ => true
        }
      }
    } else {
      if (row < 0 || row >= height || col < 0 || col >= width) false
      else {
        val cellRow = row / cellSize
        val cellCol = col / cellSize
        (fracRow, fracCol) match {
          case (0, 0) => getWall(cellRow, cellCol) == 0 && (getWall(cellRow - 1, cellCol) & VerticalWall) == 0 && (getWall(cellRow, cellCol - 1) & HorizontalWall) == 0
          case (0, _) => (getWall(cellRow, cellCol) & HorizontalWall) == 0
          case (_, 0) => (getWall(cellRow, cellCol) & VerticalWall) == 0
          case _ => true
        }
      }
    }
  }
}

object RandomMaze {
  private val HorizontalWall = 1
  private val VerticalWall = 2
  private val offsets = Array(0 -> -1, 1 -> 0, 0 -> 1, -1 -> 0)

  /**
   * This method builds a maze. It starts by making a minimum spanning tree, then it removes additional walls at random based on
   * the user specified openness.
   * @param cellSize this is how many blocks across each "cell" should be. A wall covers one block at the top or left of a cell.
   * @param wrap tells if the maze is supposed to wrap around horizontally and vertically.
   * @param numRows how many rows of cells are in the maze.
   * @param numCols how many columns of cells are in the maze.
   * @param openFactor what percentage of walls should be removed. Should generally be between 0.5 and 0.9. Values over 1.0 are rejected.
   */
  def apply(cellSize: Int, wrap: Boolean, numRows: Int, numCols: Int, openFactor: Double): Maze = {
    require(openFactor<1.0, "The value of openFactor has to be less than 1.0.")
    val walls = Array.fill(numRows, numCols)(HorizontalWall | VerticalWall)
    var count = 0
    val connected = Array.fill(numRows, numCols)(false)
    connected(0)(0) = true
    val adjacent = collection.mutable.ArrayBuffer(0 -> 1, 1 -> 0)
    val neighbors = collection.mutable.Map[(Int, Int), List[(Int, Int)]](
      (0 -> 1) -> List(0 -> 0),
      (1 -> 0) -> List(0 -> 0))
    while (count < numRows * numCols - 1) {
      val index = util.Random.nextInt(adjacent.length)
      val square @ (srow, scol) = adjacent(index)
      adjacent(index) = adjacent.last
      adjacent.trimEnd(1)
      val nindex = util.Random.nextInt(neighbors(square).length)
      val (nrow, ncol) = neighbors(square)(nindex)

      // Remove wall
      (ncol - scol, nrow - srow) match {
        case (0, -1) => walls(srow)(scol) &= ~HorizontalWall
        case (1, 0) => walls(nrow)(ncol) &= ~VerticalWall
        case (0, 1) => walls(nrow)(ncol) &= ~HorizontalWall
        case (-1, 0) => walls(srow)(scol) &= ~VerticalWall
        case _ => throw new RuntimeException("Got to bad case in building of maze.")
      }
      neighbors -= square
      connected(srow)(scol) = true

      // Add new neighbors that aren't connected
      for ((orow, ocol) <- offsets) {
        val crow = srow + orow
        val ccol = scol + ocol
        if (crow >= 0 && crow < numRows && ccol >= 0 && ccol < numCols && !connected(crow)(ccol)) {
          if (neighbors.contains(crow -> ccol)) neighbors(crow -> ccol) ::= srow -> scol
          else {
            adjacent += crow -> ccol
            neighbors(crow -> ccol) = List(srow -> scol)
          }
        }
      }

      count += 1
    }

    // Remove additional walls based on desired openness
    val additional = ((openFactor - 0.5) * (numRows * numCols)).toInt
    count = 0
    while (count < additional) {
      if (wrap && math.random < 2.0 / numRows) {
        if (math.random < 0.5) {
          val row = util.Random.nextInt(walls.length)
          if ((walls(row)(0) & VerticalWall) > 0) {
            walls(row)(0) &= ~VerticalWall
            count += 1
          }
        } else {
          val col = util.Random.nextInt(walls(0).length)
          if ((walls(0)(col) & HorizontalWall) > 0) {
            walls(0)(col) &= ~HorizontalWall
            count += 1
          }
        }
      } else {
        val row = util.Random.nextInt(walls.length)
        val col = util.Random.nextInt(walls(0).length)
        if (walls(row)(col) > 0 && (wrap || (row > 0 && col > 0))) {
          if (math.random < 0.5) {
            if ((walls(row)(col) & VerticalWall) > 0) {
              walls(row)(col) &= ~VerticalWall
              count += 1
            }
          } else {
            if ((walls(row)(col) & HorizontalWall) > 0) {
              walls(row)(col) &= ~HorizontalWall
              count += 1
            }
          }
        }
      }
    }
    new RandomMaze(cellSize, wrap, walls)
  }

  /**
   * This is just a sample that makes a maze and prints it out so that you can see what it looks like.
   */
  def main(args: Array[String]): Unit = {
    val maze = RandomMaze(3, false, 20, 20, 0.6)
    for (r <- -5 until maze.height + 5) {
      for (c <- -5 until maze.width + 5) {
        if (maze(r, c) == Wall) print('#') else print(' ')
      }
      println()
    }
  }
}
