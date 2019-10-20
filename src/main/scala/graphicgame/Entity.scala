package graphicgame

trait Entity extends Serializable {
  def name: String // Only used for 3D renderer, otherwise remove.
  def x: Double
  def y: Double
  def width: Double
  def height: Double
  
  def update(delay: Double): Unit
  def postCheck(): Unit // You can delete this if you don't use it.
  def stillHere(): Boolean // This is how you should remove entites from the level.
}

object Entity {
  def intersect(e1: Entity, e2: Entity): Boolean = {
    ((e1.x-e2.x).abs < (e1.width+e2.width)/2) && ((e1.y-e2.y).abs < (e1.height+e2.height)/2)
  }
}