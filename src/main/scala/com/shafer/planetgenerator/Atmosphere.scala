package com.shafer.planetgenerator

import org.scalajs.dom.CanvasRenderingContext2D


case class Atmosphere(clouds: Int = Atmosphere.defaultClouds) extends RenderedFeature{

  def drawClouds(planetX: Double, planetY: Double, planetR: Double, canvas: CanvasRenderingContext2D) = {
    val maxWidth  = 120
    val maxHeight = 15
    val minWidth  = 50
    val minHeight = 10

    canvas.fillStyle = Color(255,255,255,0.9).build
    for{
      cloud   <- 0 to clouds by 1
      x      = Math.random() * planetR * 2 + planetX - planetR
      y      = Math.random() * planetR * 2 + planetX - planetR
      width  = Math.random() * (maxWidth  - minWidth) + minWidth
      height = Math.random() * (maxHeight - minHeight) + minHeight
    } yield {
      Experimental.drawEllipse(x, y, width, height)(canvas)
    }
  }

  override def render(planet: Planet)(canvas: CanvasRenderingContext2D): Unit = {
    drawClouds(planet.x, planet.y, planet.radius, canvas)
  }
}

object Atmosphere{
  val defaultClouds = (Math.random() * 15).toInt

}
