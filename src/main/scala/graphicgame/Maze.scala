package graphicgame

/**
 * This is the supertype for all of the types of cells in your game.
 */
sealed trait CellType {
  def passable(entity: Entity): Boolean
}
case object Floor extends CellType {
  def passable(entity: Entity): Boolean = true
}
case object Wall extends CellType {
  def passable(entity: Entity): Boolean = false
}
// If you have things like doors, stairs, or other types of cells, put them here. I've included some ideas.
//case class Door(key: Int) extends CellType {
//  def passable(entity: Entity): Boolean = false // replace with a check if they have the right key.
//}
//
//case class Stairs(destLevel: Level, destX: Double, destY: Double) extends CellType {
//  def passable(entity: Entity): Boolean = true // replace with code that check if this entity type can follow stairs.
//}

/**
 * This is the abstract supertype of different maze types. I don't recommend editing this. If you want your
 * own type of maze, make a subtype.
 */
trait Maze extends Serializable {
  /**
   * Tells you the width of the maze in cells.
   */
  def width: Int

  /**
   * Tells you the height of the maze in cells.
   */
  def height: Int

  /**
   * Tells you if there is a wall at a particular row and column location.
   * @param row The row to check.
   * @param col The column to check.
   */
  def apply(row: Int, col: Int): CellType

  /**
   * Tells if this maze is supposed to wrap around. Likely false for everything except the random maze. 
   */
  def wrap: Boolean

  /**
   * This tells you if a rectangular region is clear or not. You should use this to tell if an entity can
   * occupy a particular location.
   * @param cx The center of the rectangular region in x.
   * @param cy The center of the rectangular region in y.
   * @param width The width of the rectangular region.
   * @param height The height of the rectangular region.
   */
  final def isClear(cx: Double, cy: Double, width: Double, height: Double, entity: Entity): Boolean = {
    val sx = (cx - width / 2).floor.toInt
    val sy = (cy - height / 2).floor.toInt
    val ex = (cx + width / 2).floor.toInt
    val ey = (cy + height / 2).floor.toInt
    (sx to ex).forall(x => (sy to ey).forall(y => apply(x, y).passable(entity)))
  }
}
