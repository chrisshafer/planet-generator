enablePlugins(ScalaJSPlugin)
import sbt.Keys._

val projectName  = "planet-generator"
val scalaV       = "3.0.0"
val org          = "com.shafer.planetgenerator"

name := projectName

scalaVersion := scalaV

version      := "0.1-SNAPSHOT"

organization := org

mainClass in Compile := Some("com.shafer.planetgenerator.Generator")

scalaJSUseMainModuleInitializer in Compile := true
scalaJSUseMainModuleInitializer in Test := false

libraryDependencies ++= Seq(("org.scala-js" %%% "scalajs-dom" % "1.1.0").cross(CrossVersion.for3Use2_13))


