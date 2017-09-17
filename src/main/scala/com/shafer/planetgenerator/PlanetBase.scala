package com.shafer.planetgenerator

import com.shafer.planetgenerator.delaunay.{Point, Delaunay}
import org.scalajs.dom._

case class PlanetBase(color: Color = Color.random,
                      roughness: Int = (Math.random() * 20 + 100).toInt,
                      craters: Int = (Math.random() * 4 + 3).toInt,
                      colorGradient: Option[(Color, Color)] = None ) extends RenderedFeature {

  val maxCraterSize = 30
  val minCraterSize = 10
  val craterEdgeRoughness = .70
  val craterResolution = 30

  private def drawBase(x: Double, y: Double, radius: Double, canvas: CanvasRenderingContext2D) = {
    println("Rendering base")
    canvas.beginPath()
    canvas.arc(x, y, radius, 0, 2 * Math.PI, false)
    canvas.fillStyle   = color.build
    canvas.fill()
    canvas.clip()
    drawSurface(x, y, radius, canvas)
    drawCraters(x, y, radius, canvas)
  }

  private def drawBaseGradient(x: Double, y:Double,
                               radius: Double, canvas: CanvasRenderingContext2D)(colors: (Color, Color)) = {
    println("Rendering gradient")
    println(colors._1.build)
    println(colors._2.build)

    val x0 = x - radius + (radius * Math.random()) * 2
    val x1 = x - radius + (radius * Math.random()) * 2
    val y0 = y - radius - ( radius * 0.1)
    val y1 = y + radius + ( radius * 0.1)
    println(x, y)
    println(radius)
    println( x0, x1, y0, y1)
    val gradient = canvas.createLinearGradient(x0 = x0, y0 = y0, x1 = x1, y1 = y1)
    gradient.addColorStop(0.0, colors._1.build)
    gradient.addColorStop(1.0, colors._2.build)

    canvas.beginPath()
    canvas.arc(x, y, radius, 0, 2 * Math.PI, false)
    canvas.fillStyle = gradient
    canvas.fill()
    canvas.clip()
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
    planet.planetBase.colorGradient match {
      case Some(grad) => drawBaseGradient(planet.x, planet.y, planet.radius, canvas)(grad)
      case None       => drawBase(planet.x, planet.y, planet.radius, canvas)
    }
  }
}
