enablePlugins(ScalaJSPlugin)
import sbt.Keys._

val projectName  = "planet-generator"
val scalaV       = "2.11.8"
val org          = "com.shafer.planetgenerator"

name := projectName

scalaVersion := scalaV

version      := "0.1-SNAPSHOT"

organization := org

persistLauncher in Compile := true

persistLauncher in Test := false

jsDependencies ++= Seq() // Web Jars

libraryDependencies ++= Seq("org.scala-js" %%% "scalajs-dom" % "0.9.0") // JS Only


