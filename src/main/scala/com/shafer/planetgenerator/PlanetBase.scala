package com.shafer.planetgenerator

import com.shafer.planetgenerator.delaunay.{Point, Delaunay}
import org.scalajs.dom._

case class PlanetBase(color: Color = Color.random(),
                      rocks: Int = (Math.random() * 500).toInt,
                      roughness: Int = (Math.random() * 20 + 100).toInt) extends RenderedFeature{

  private def drawBase(x: Double, y: Double, radius: Double, canvas: CanvasRenderingContext2D) = {
    canvas.beginPath()
    canvas.arc(x, y, radius, 0, 2 * Math.PI, false)
    canvas.fillStyle   = color.build
    canvas.fill()
    canvas.clip()
    drawSurface(x, y, radius, canvas)
  }

  private def drawRocks(planetX: Double, planetY: Double, planetR: Double, canvas: CanvasRenderingContext2D) = {
    val maxSize = 10
    val minSize = 10

    canvas.fillStyle = color.darken(0.5).build
    for{
      rock   <- 0 to rocks by 1
      x      = Math.random() * planetR * 2 + planetX - planetR
      y      = Math.random() * planetR * 2 + planetY - planetR
      width  = Math.random() * (maxSize - minSize) + minSize
      height = Math.random() * (maxSize - minSize) + minSize
    } yield {
      canvas.fillRect(x, y, width, height)
    }
  }

  private def drawSurface(planetX: Double, planetY: Double, planetR: Double, canvas: CanvasRenderingContext2D) = {

    val points = for{
      roughness   <- 0 to roughness by 1
      x           = Math.random() * planetR * 2 + planetX - planetR
      y           = Math.random() * planetR * 2 + planetY - planetR
    } yield {
      Point(x,y)
    }
    val triangles = Delaunay.triangulate(points.toList)
    triangles.foreach{ triangle =>
      val tricolor = color.darken(Math.random() * .15 ).build
      canvas.lineWidth = 1
      canvas.fillStyle = tricolor
      canvas.strokeStyle = "black"
      canvas.beginPath()
      canvas.moveTo(triangle._1.x , triangle._1.y)
      canvas.lineTo(triangle._2.x , triangle._2.y)
      canvas.lineTo(triangle._3.x , triangle._3.y)
      canvas.fill()
      canvas.stroke()
    }

  }

  override def render(planet: Planet)(canvas: CanvasRenderingContext2D): Unit = {
    drawBase(planet.x, planet.y, planet.radius, canvas)
  }
}
