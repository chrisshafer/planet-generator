package com.shafer.planetgenerator

import com.shafer.planetgenerator.delaunay.Point
import org.scalajs.dom.raw.CanvasRenderingContext2D
import Util._


case class Star(topLeft: Point, bound: Seq[Point], rotation: Double)
object Star{
  
  def atPoint(point: Point): Star = {
    val size = Util.gaussianRandom(3.0, 1.0)
    val path = Seq(
      Point(size + Math.random() * 3, 0.0),
      Point(size + Math.random() * 3, size + Math.random() * 3),
      Point(0.0, size + Math.random()*3),
      Point(0, 0) // close
    )
    Star(point, path, Math.random() * 2.0 * Math.PI)
  }
}

case class Starfield(starLocations: Seq[Star], color: Color) {

  def render(canvas: CanvasRenderingContext2D)(delta: Double): Unit = {
    starLocations.map{ star =>
      
      val degreesOfRotation = Math.random() * 360.0
      val radians = (degreesOfRotation * (Math.PI / 180))

      star.copy(rotation = radians)
    }.foreach(renderStar(canvas))
  }

  def renderStar(canvas: CanvasRenderingContext2D)(star: Star): Unit = {
    canvasOp(canvas){ ctx =>
      ctx.fillStyle = color.build
      ctx.translate(star.topLeft.x, star.topLeft.y)
      ctx.rotate(star.rotation)
      ctx.beginPath()
      ctx.moveTo(0.0, 0.0)
      star.bound.foreach{ case Point(x, y) =>
        ctx.lineTo(x, y)
      }
      ctx.fill()
      ctx.closePath()
    }
  }
}

object Starfield {

  def pow2(thing: Double) = thing * thing
  def random(number: Int, maxWidth: Double, maxHeight: Double, exclusionRadius: Double = 0) = {

    def exclude(point: Point) = (pow2(point.x - maxWidth/2) + pow2(point.y - maxHeight/2)) < pow2(exclusionRadius)

    val stars = (0 until number by 1).map{ _ =>
     Point(
        x = Math.random() * maxWidth,
        y = Math.random() * maxHeight
      )
    }.filterNot(exclude)
     .map(Star.atPoint)

    Starfield(stars, Color.white)
  }
}
