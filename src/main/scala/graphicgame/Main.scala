package graphicgame

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.image.ImageView
import scalafx.scene.image.Image

/**
 * This is a stub for the graphical game.
 */
object Main extends JFXApp {
	stage = new JFXApp.PrimaryStage {
		title = "My Game" // Change this to match the theme of your game.
		scene = new Scene(1000, 800) {
		  // Put your code here.
		}
	}
}
