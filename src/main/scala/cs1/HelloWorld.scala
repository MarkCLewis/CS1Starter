package cs1

import scala.io.StdIn._

object HelloWorld {
	def main(args: Array[String]): Unit = {
		println("What is your name?")
		val name = readLine().trim()
		println(s"Hello $name!")
	}
}
