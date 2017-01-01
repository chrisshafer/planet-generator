package com.shafer.planetgenerator

import com.shafer.planetgenerator.delaunay.{Point, Delaunay}
import org.scalajs.dom._

case class PlanetBase(color: Color = Color.random,
                      roughness: Int = (Math.random() * 20 + 100).toInt,
                      craters: Int = (Math.random() * 4 + 3).toInt) extends RenderedFeature{

  val maxCraterSize = 30
  val minCraterSize = 10
  val craterEdgeRoughness = .70
  val craterResolution = 30

  private def drawBase(x: Double, y: Double, radius: Double, canvas: CanvasRenderingContext2D) = {
    canvas.beginPath()
    canvas.arc(x, y, radius, 0, 2 * Math.PI, false)
    canvas.fillStyle   = color.build
    canvas.fill()
    canvas.clip()
    drawSurface(x, y, radius, canvas)
    drawCraters(x, y, radius, canvas)
  }


  private def drawCraters(planetX: Double, planetY: Double, planetR: Double, canvas: CanvasRenderingContext2D)= {
    for{
      _ <- 0 to craters by 1
      xpos           = Math.random() * planetR * 2 + planetX - planetR
      ypos           = Math.random() * planetR * 2 + planetY - planetR
      radius           = (Math.random() * (maxCraterSize - minCraterSize) ) + minCraterSize
    } yield {
      val craterPoints = (0 to craterResolution by 1).map{ _ =>
        Math.random()*Math.PI*2
      }.sorted.map{ randomAngle =>
        val roughenedRadius = (radius + (Math.random() * craterEdgeRoughness * radius))
        val x = Math.cos(randomAngle) * roughenedRadius + xpos
        val y = Math.sin(randomAngle) * roughenedRadius + ypos
        Point(x, y)
      }

      if(craterPoints.length > 2){
        val tricolor = color.darken(.20).build
        canvas.lineWidth = 0
        canvas.fillStyle = tricolor
        canvas.strokeStyle = tricolor
        canvas.beginPath()
        canvas.moveTo(xpos , ypos)
        val (first: Point, tail: Seq[Point]) = (craterPoints.head , craterPoints.tail)

        canvas.moveTo(first.x, first.y)
        tail.map{ edgePoint =>
          canvas.lineTo(edgePoint.x , edgePoint.y)
        }
        canvas.lineTo(first.x,first.y)

        canvas.fill()
        canvas.stroke()
      }
    }

  }

  private def drawSurface(planetX: Double, planetY: Double, planetR: Double, canvas: CanvasRenderingContext2D) = {

    val surfaceTexture = for{
      roughness   <- 0 to roughness by 1
      x           = Math.random() * planetR * 2 + planetX - planetR
      y           = Math.random() * planetR * 2 + planetY - planetR
    } yield {
      Point(x,y)
    }

    val triangles = Delaunay.triangulate(surfaceTexture.toList)
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
    }

  }

  override def render(planet: Planet)(canvas: CanvasRenderingContext2D): Unit = {
    drawBase(planet.x, planet.y, planet.radius, canvas)
  }
}
