package com.shafer.planetgenerator

import com.shafer.planetgenerator.delaunay.Point
import org.scalajs.dom.raw.CanvasRenderingContext2D
import Util._
case class Starfield(starLocations: Seq[Point], color: Color) {


  def render(canvas: CanvasRenderingContext2D): Unit = {
    starLocations.foreach(renderStar(canvas))
  }

  def renderStar(canvas: CanvasRenderingContext2D)(point: Point): Unit = {
    canvasOp(canvas){ ctx =>
      ctx.fillStyle = color.build
      ctx.translate(point.x, point.y)
      ctx.rotate(Math.random() * 2.0 * Math.PI)
      ctx.beginPath()
      ctx.moveTo(0.0, 0.0)
      ctx.lineTo(5 + Math.random()*3, 0.0)
      ctx.lineTo(5 + Math.random()*3, 5 + Math.random()*3)
      ctx.lineTo(0.0, 5 + Math.random()*3)
      ctx.lineTo(0.0, 0.0)
      ctx.fill()
      ctx.closePath()
    }
  }
}

object Starfield{


  def random(number: Int, maxWidth: Double, maxHeight: Double) = {

    val points = (0 until number by 1).map{ _ =>
      Point(
        x = Math.random() * maxWidth,
        y = Math.random() * maxHeight
      )
    }

    Starfield(points, Color.white)
  }

}
