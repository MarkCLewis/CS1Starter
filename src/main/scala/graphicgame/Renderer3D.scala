package graphicgame

import scalafx.Includes._
import scalafx.scene.Scene
import scala.collection.mutable
import scalafx.scene.shape.Shape
import scalafx.scene.shape.Box
import scalafx.scene.paint.PhongMaterial
import scalafx.scene.image.Image
import scalafx.scene.paint.Color
import scalafx.scene.PerspectiveCamera
import scalafx.scene.AmbientLight
import scalafx.scene.PointLight
import scalafx.geometry.Point3D
import scalafx.scene.shape.Sphere
import scalafx.scene.transform.Rotate
import scalafx.scene.transform.Translate
import scalafx.scene.shape.Shape3D

/**
 * This is a renderer that uses the 3D capabilities of JavaFX to let you render your game in 3D.
 * 3D games can't wrap, so make sure you have that option turned off in your maze.
 */
class Renderer3D(scene: Scene, maze: Maze, isoDistAngle: Option[(Double, Double)]) {
  private val sceneEntities = mutable.Map[String, Shape3D]()

  // Change images and materials to match the desired theme of your game
  private val wallImage = Renderer3D.loadImage("/images/banish.gif")
  private val floorImage = Renderer3D.loadImage("/images/pebble_round.jpg")
  private val ceilingImage = Renderer3D.loadImage("/images/yellow_wood_planks.jpg")
  private val enemyImage = Renderer3D.loadImage("/images/enemy.png")
  private val enemyMat = new PhongMaterial()
  enemyMat.diffuseMap = enemyImage
  private val generatorMat = new PhongMaterial(Color.Green)
  private val bulletMat = new PhongMaterial(Color.Red)
  private val playerMat = new PhongMaterial(Color.Aqua)

  private val cam = new PerspectiveCamera(true)

  placeCamera()
  placeMazeGeometry()

  private def placeCamera(): Unit = {
//    cam.fieldOfView = 110  // Use this line to get a wider field of view
    scene.camera = cam
    scene.content += cam
  }

  /**
   * Adds the lighting. You can play around with this. For example, there is a PointLight type
   * that you could try having follow your player around.
   */
  private def addLighting(): Unit = {
    val ambient = new AmbientLight(Color.White)
    scene.content += ambient
  }

  /**
   * Places the maze geometry as well as the floor and possibly ceiling.
   */
  private def placeMazeGeometry(): Unit = {
    while (wallImage.backgroundLoading) {}
    val mat = new PhongMaterial()
    mat.setDiffuseMap(wallImage)

    for (x <- 0 to maze.width; y <- 0 to maze.height) {
      if (maze(x, y) == Wall) {
        val box = new Box(1, 1, 1)
        box.translateX = x
        box.translateY = y
        box.material = mat
        scene.content += box
      }
    }

    val floor = new Box(maze.width, maze.height, 1)
    val floorMat = new PhongMaterial()
    floorMat.diffuseMap = floorImage
    floor.translateX = maze.width / 2
    floor.translateY = maze.height / 2
    floor.translateZ = 1.0
    floor.material = floorMat
    scene.content += floor
    if(isoDistAngle.isEmpty) {
      val ceiling = new Box(maze.width, maze.height, 1)
      val ceilingMat = new PhongMaterial()
      ceilingMat.diffuseMap = ceilingImage
      ceiling.translateX = maze.width / 2
      ceiling.translateY = maze.height / 2
      ceiling.translateZ = -1.0
      ceiling.material = ceilingMat
      scene.content += ceiling
    }
  }

  /**
   * Call this with the entities from your game each update. All entities have to have a unique name for this
   * to work. It entities have the same name you will get very odd behavior. You can modify the types and add
   * types, but you probably shouldn't modify the code that moves the camera.
   */
//  def updateEntities(entities: Seq[Entity]): Unit = {
//    // Add/update the entities
//    for (e <- entities) {
//      e match {
//        case p: Player3D =>
//          if(!sceneEntities.contains(e.name)) {
//            val player = new Sphere(e.width/2)
//            player.material = playerMat
//            scene.content += player
//            sceneEntities(e.name) = player
//          }
//          sceneEntities(e.name).translateX = e.x-0.5
//          sceneEntities(e.name).translateY = e.y-0.5
//
//          isoDistAngle.map { case (dist, theta) =>
//            cam.transforms = List(new Translate(p.x-0.5, p.y-0.5, 0), new Rotate(theta, new Point3D(1, 0, 0)), new Translate(0, 0, -dist))
//          }.getOrElse {
//            cam.transforms = List(new Translate(p.x-0.5, p.y-0.5), new Rotate(p.facing, new Point3D(0, 0, 1)), new Rotate(90, new Point3D(1, 0, 0)))
//          }
//        case _: Enemy =>
//          if(!sceneEntities.contains(e.name)) {
//            val enemy = new Sphere(e.width/2)
//            enemy.material = enemyMat
//            scene.content += enemy
//            sceneEntities(e.name) = enemy
//          }
//          sceneEntities(e.name).translateX = e.x-0.5
//          sceneEntities(e.name).translateY = e.y-0.5
//        case _: Generator =>
//          if(!sceneEntities.contains(e.name)) {
//            val generator = new Box(e.width, e.height, 0.5)
//            generator.material = generatorMat
//            scene.content += generator
//            sceneEntities(e.name) = generator
//          }
//          sceneEntities(e.name).translateX = e.x-0.5
//          sceneEntities(e.name).translateY = e.y-0.5
//        case _: Bullet =>
//          if(!sceneEntities.contains(e.name)) {
//            val bullet = new Sphere(e.width/2)
//            bullet.material = bulletMat
//            scene.content += bullet
//            sceneEntities(e.name) = bullet
//          }
//          sceneEntities(e.name).translateX = e.x-0.5
//          sceneEntities(e.name).translateY = e.y-0.5
//        case _ =>
//      }
//      
//      // Remove anything that is no longer part of entities
//      val victims = sceneEntities.keySet diff entities.map(_.name).toSet
//      for(v <- victims) {
//        scene.content -= sceneEntities(v)
//        sceneEntities -= v
//      }
//    }
//  }
}

object Renderer3D {
    /**
   * This method assumes that you are putting your images in src/main/resources. This directory is
   * packaged into the JAR file. Eclipse doesn't use the JAR file, so this will go to the file in
   * the directory structure if it can't find the resource in the classpath. The argument should be the
   * path inside of the resources directory.
   */
  def loadImage(path: String): Image = {
    val res = getClass.getResource(path)
    if(res == null) {
      new Image("file:src/main/resources"+path)
    } else {
      new Image(res.toExternalForm())
    }
  }
}
