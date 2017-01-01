package com.shafer.planetgenerator

import com.shafer.planetgenerator.delaunay.{Point, Delaunay}
import org.scalajs.dom.CanvasRenderingContext2D


case class Atmosphere(clouds: Int = Atmosphere.defaultClouds) extends RenderedFeature{

  def drawClouds(planetX: Double, planetY: Double, planetR: Double, canvas: CanvasRenderingContext2D) = {
    val maxWidth  = 150
    val maxHeight = 30
    val minWidth  = 100
    val minHeight = 20
    val numberOfVectors = 30

    val color = Color(255,255,255,1)

    (0 to clouds).foreach { cloud =>
      val width = Math.random() * (maxWidth - minWidth) + minWidth
      val height = Math.random() * (maxHeight - minHeight) + minHeight
      val xloc   = Math.random() * planetR * 2 + planetX - planetR
      val yloc   = Math.random() * planetR * 2 + planetY - planetR

      val points = for {
        _ <- 0 to numberOfVectors by 1
        randomAngle = Math.random() * (Math.PI * 2)
        x = (Math.sqrt(Math.random()) * Math.cos(randomAngle)) * width  + xloc
        y = (Math.sqrt(Math.random()) * Math.sin(randomAngle)) * height + yloc
      } yield {
        Point(x, y)
      }

      val triangles = Delaunay.triangulate(points.toList)
      triangles.foreach { triangle =>
        val tricolor = color.darken(Math.random() * .15).build
        canvas.strokeStyle = tricolor
        canvas.fillStyle = tricolor
        canvas.beginPath()
        canvas.moveTo(triangle._1.x, triangle._1.y)
        canvas.lineTo(triangle._2.x, triangle._2.y)
        canvas.lineTo(triangle._3.x, triangle._3.y)
        canvas.fill()
        canvas.lineWidth = 0
        canvas.stroke()
      }
    }

  }

  override def render(planet: Planet)(canvas: CanvasRenderingContext2D): Unit = {
    drawClouds(planet.x, planet.y, planet.radius, canvas)
  }
}

object Atmosphere{
  private val defaultClouds = (Math.random() * 15).toInt
  def none = Atmosphere(-1)
}
