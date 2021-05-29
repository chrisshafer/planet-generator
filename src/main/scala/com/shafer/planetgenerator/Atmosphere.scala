package com.shafer.planetgenerator

import com.shafer.planetgenerator.delaunay.{Point, Delaunay}
import org.scalajs.dom.CanvasRenderingContext2D
import Util._

case class Cloud(points: Seq[(Point, Point, Point)]){
  def render(color: Color)(canvas: CanvasRenderingContext2D) = {
    points.foreach { triangle =>
      val tricolor = color.darken(Math.random() * .15)

      if (tricolor.a < 1.0) canvas.strokeStyle = Color.transparent.build
      else canvas.strokeStyle = tricolor.build

      canvas.fillStyle = tricolor.build
      canvas.beginPath()
      canvas.moveTo(triangle._1.x, triangle._1.y)
      canvas.lineTo(triangle._2.x, triangle._2.y)
      canvas.lineTo(triangle._3.x, triangle._3.y)
      canvas.fill()
      canvas.lineWidth = 0
      canvas.stroke()
      canvas.closePath()
    }
  }
}

case class Atmosphere(
  clouds: Seq[Cloud],
  cloudColor: () => Color = Atmosphere.defaultColor
) extends RenderedFeature{

  def drawClouds(planetX: Double, planetY: Double, planetR: Double, canvas: CanvasRenderingContext2D) = {
    canvasOp(canvas) { ctx =>
      ctx.beginPath()
      ctx.arc(planetX, planetY, planetR, 0, 2 * Math.PI, false)
      ctx.clip()
      clouds.foreach { cloud =>
        cloud.render(cloudColor())(ctx)
      }
      ctx.closePath()
    }
  }

  override def render(planet: Planet)(canvas: CanvasRenderingContext2D): Unit = {
    drawClouds(planet.x, planet.y, planet.radius, canvas)
  }
}

object Atmosphere {

  private val defaultColor  = () => Color(255,255,255,1)
  private val defaultClouds = (Math.random() * 15).toInt
  def none = Atmosphere(Seq.empty)

  val maxWidth  = 150
  val maxHeight = 30
  val minWidth  = 100
  val minHeight = 20
  val numberOfVectors = 30

  def random(planetX: Double, planetY: Double, planetR: Double, cloudsNumber: Int = defaultClouds, cloudColor: () => Color) = {
    val clouds = (0 to cloudsNumber by 1).map( _ => randomCloud(planetX, planetY, planetR))
    Atmosphere(clouds, cloudColor)
  }

  private def randomCloud( planetX: Double, planetY: Double, planetR: Double) = {
    val width = Math.random() * (maxWidth - minWidth) + minWidth
    val height = Math.random() * (maxHeight - minHeight) + minHeight
    val xloc = Math.random() * planetR * 2 + planetX - planetR
    val yloc = Math.random() * planetR * 2 + planetY - planetR

    val points = for {
      _ <- 0 to numberOfVectors by 1
      randomAngle = Math.random() * (Math.PI * 2)
      x = (Math.sqrt(Math.random()) * Math.cos(randomAngle)) * width + xloc
      y = (Math.sqrt(Math.random()) * Math.sin(randomAngle)) * height + yloc
    } yield {
      Point(x, y)
    }

    val triangles = Delaunay.triangulate(points.toList)
    Cloud(triangles)
  }
}
