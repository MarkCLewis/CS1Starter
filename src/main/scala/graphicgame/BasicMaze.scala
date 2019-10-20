package graphicgame

class BasicMaze(cells: Array[Array[CellType]]) extends Maze {
  def height: Int = cells.length
  def width: Int = cells(0).length
  def wrap: Boolean = false
  def apply(row: Int, col: Int): CellType = {
		if(row < 0 || row >= width || col < 0 || col >= height) Wall else cells(row)(col)
	}
}
