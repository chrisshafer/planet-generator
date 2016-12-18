package com.shafer.planetgenerator

import org.scalajs.dom._

case class PlanetBase(color: Color = Color.random(),
                      rocks: Int = (Math.random() * 500).toInt ) extends RenderedFeature{

  private def drawBase(x: Double, y: Double, radius: Double, canvas: CanvasRenderingContext2D) = {
    canvas.beginPath()
    canvas.arc(x, y, radius, 0, 2 * Math.PI, false)
    canvas.fillStyle = color.build
    canvas.fill()
    canvas.stroke()
    canvas.clip()
    drawRocks(x, y, radius, canvas)
  }

  private def drawRocks(planetX: Double, planetY: Double, planetR: Double, canvas: CanvasRenderingContext2D) = {
    val maxSize = 10
    val minSize = 10

    canvas.fillStyle = color.darken(0.5).build
    for{
      rock   <- 0 to rocks by 1
      x      = Math.random() * planetR * 2 + planetX - planetR
      y      = Math.random() * planetR * 2 + planetX - planetR
      width  = Math.random() * (maxSize - minSize) + minSize
      height = Math.random() * (maxSize - minSize) + minSize
    } yield {
      canvas.fillRect(x, y, width, height)
    }
  }

  override def render(planet: Planet)(canvas: CanvasRenderingContext2D): Unit = {
    drawBase(planet.x, planet.y, planet.radius, canvas)
  }
}
