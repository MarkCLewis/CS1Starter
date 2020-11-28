name := "CS1"
version := "1.0"
scalaVersion := "2.12.12"
run / fork := true
run / connectInput := true

run / fork := true
run / connectInput := true

// Determine OS version of JavaFX binaries
lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ => throw new Exception("Unknown platform!")
}
lazy val javaFXModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")

libraryDependencies ++= Seq(
	"org.scala-lang.modules" %% "scala-xml" % "1.3.0",
	"org.scalafx" %% "scalafx" % "11-R16",
	"com.novocode" % "junit-interface" % "0.11" % Test,
	"org.scalactic" %% "scalactic" % "3.2.2",
	"org.scalatest" %% "scalatest" % "3.2.2" % "test"
)

libraryDependencies ++= javaFXModules.map( m =>
  "org.openjfx" % s"javafx-$m" % "11" classifier osName
)

