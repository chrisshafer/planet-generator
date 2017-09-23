package com.shafer.planetgenerator

import com.shafer.planetgenerator.delaunay.{Point, Delaunay}
import org.scalajs.dom._
import Util._

trait PlanetBaseFill {
  def render(x: Double, y: Double, radius: Double)(ctx: CanvasRenderingContext2D)
}

case class PlanetBaseGradient(x0: Double, y0: Double, x1: Double, y1: Double, colorOne: Color, colorTwo: Color) extends PlanetBaseFill {

  def render(x: Double, y: Double, radius: Double)(ctx: CanvasRenderingContext2D) = {
    canvasOp(ctx) { canvas =>
      val gradient = canvas.createLinearGradient(x0 = x0, y0 = y0, x1 = x1, y1 = y1)
      gradient.addColorStop(0.0, colorOne.build)
      gradient.addColorStop(1.0, colorTwo.build)

      canvas.beginPath()
      canvas.arc(x, y, radius, 0, 2 * Math.PI, false)
      canvas.fillStyle = gradient
      canvas.fill()
      canvas.clip()

    }
  }
}


object PlanetBaseGradient {

  def random(x: Double, y: Double, radius: Double, colors: (Color, Color)) = {
    val x0 = x - radius + (radius * Math.random()) * 2
    val x1 = x - radius + (radius * Math.random()) * 2
    val y0 = y - radius - (radius * 0.1)
    val y1 = y + radius + (radius * 0.1)
    apply(x0 = x0, y0 = y0, x1 = x1, y1 = y1, colors._1, colors._2)
  }
}

case class PlanetBaseTextured(craters: Seq[Crater], texture: Triangles, color: Color) extends PlanetBaseFill  {

  def render(x: Double, y: Double, radius: Double)(ctx: CanvasRenderingContext2D) = {
    canvasOp(ctx) { canvas =>
      println("Rendering base")
      canvas.beginPath()
      canvas.arc(x, y, radius, 0, 2 * Math.PI, false)
      canvas.fillStyle = color.build
      canvas.fill()
      canvas.clip()
      texture.renderTriangles(color)(canvas)
      craters.foreach(_.render(color)(canvas))
    }
  }

}

object PlanetBaseTextured {
  val maxCraterSize = 30
  val minCraterSize = 10
  val craterEdgeRoughness = .70
  val craterResolution = 25


  def random(x: Double,
            y: Double,
            radius: Double,
            color: Color = Color.random,
            roughness: Int = (Math.random() * 20 + 100).toInt,
            numberOfCraters: Int = (Math.random() * 4 + 3).toInt) = {
    new PlanetBaseTextured(
      craters = timer( () => randomCraters(numberOfCraters, x, y, radius))("genCraters"),
      texture = timer( () => randomTexture(roughness, x, y, radius))("Gen texture"),
      color = color
    )
  }

  private def randomCraters(numberOfCraters: Int, planetX: Double, planetY: Double, planetR: Double) = {
    for{
      _ <- 0 to numberOfCraters by 1
      xpos           = Math.random() * planetR * 2 + planetX - planetR
      ypos           = Math.random() * planetR * 2 + planetY - planetR
      radius           = (Math.random() * (maxCraterSize - minCraterSize) ) + minCraterSize
    } yield {
      val craterPoints = (0 to craterResolution by 1).map { _ =>
        Math.random() * Math.PI * 2
      }.sorted.map { randomAngle =>
        val roughenedRadius = (radius + (Math.random() * craterEdgeRoughness * radius))
        val x = Math.cos(randomAngle) * roughenedRadius + xpos
        val y = Math.sin(randomAngle) * roughenedRadius + ypos
        Point(x, y)
      }
      Crater(xpos, ypos, craterPoints)
    }
  }

  private def randomTexture(roughness: Int, planetX: Double, planetY: Double, planetR: Double) = {
    val surfaceTexture = for{
      roughness   <- 0 to roughness by 1
      x           = Math.random() * planetR * 2 + planetX - planetR
      y           = Math.random() * planetR * 2 + planetY - planetR
    } yield {
      Point(x,y)
    }

    val triangles = Delaunay.triangulate(surfaceTexture.toList)
    Triangles(triangles)
  }

}

case class PlanetBase(fill: PlanetBaseFill) extends RenderedFeature {

  override def render(planet: Planet)(canvas: CanvasRenderingContext2D): Unit = {
    fill.render(planet.x, planet.y, planet.radius)(canvas)
  }
}


case class Crater(xpos: Double, ypos: Double, points: Seq[Point]){

  def render( color: Color)(canvas: CanvasRenderingContext2D) = {
    if(points.length > 2){
      val tricolor = color.darken(.30).build
      canvas.lineWidth = 0
      canvas.fillStyle = tricolor
      canvas.strokeStyle = tricolor
      canvas.beginPath()
      canvas.moveTo(xpos , ypos)
      val (first: Point, tail: Seq[Point]) = (points.head , points.tail)

      canvas.moveTo(first.x, first.y)
      tail.map{ edgePoint =>
        canvas.lineTo(edgePoint.x , edgePoint.y)
      }
      canvas.lineTo(first.x,first.y)

      canvas.fill()
      canvas.stroke()
      canvas.closePath()
    }
  }

}
case class Triangles(triangles: Seq[(Point, Point, Point)]){

  def renderTriangles(color: Color)(canvas: CanvasRenderingContext2D ) = {

    triangles.foreach{ triangle =>
      val tricolor = color.darken(Math.random() * .15 ).build
      canvas.lineWidth = 1
      canvas.fillStyle = tricolor
      canvas.strokeStyle = tricolor
      canvas.beginPath()
      canvas.moveTo(triangle._1.x , triangle._1.y)
      canvas.lineTo(triangle._2.x , triangle._2.y)
      canvas.lineTo(triangle._3.x , triangle._3.y)
      canvas.fill()
      canvas.stroke()
      canvas.closePath()
    }
  }
}
