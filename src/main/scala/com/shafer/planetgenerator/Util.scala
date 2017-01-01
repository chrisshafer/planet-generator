package com.shafer.planetgenerator

import org.scalajs.dom._

case class Color(r: Int, g: Int, b: Int, a: Double){
  override def toString: String = s"rgba($r, $g, $b, $a)"
  def build = this.toString

  def darken(percent: Double): Color = {
    this.copy(
      r = (r - r * percent).toInt,
      g = (g - g * percent).toInt,
      b = (b - b * percent).toInt
    )
  }
}

object Color{

  def random: Color = random(255)

  def random(a: Int): Color = {
    val r: Int = (Math.random() * 255).toInt
    val g: Int = (Math.random() * 255).toInt
    val b: Int = (Math.random() * 255).toInt
    Color(r,g,b,a)
  }

  def randomGreyscale: Color = {
    val scale = (Math.random() * 255).toInt
    Color(scale,scale,scale,1)
  }

  def transparent = Color(0, 0, 0, 0)
  def white = Color(255, 255, 255, 1)
  def black = Color(0, 0, 0, 1)
  def apply(r: Int, g: Int, b: Int): Color = Color(r, g, b, 255)
}

trait RenderedFeature {
  def render(planet: Planet)(canvas: CanvasRenderingContext2D): Unit
}

object Experimental{

  def drawEllipse(x: Double, y: Double, rx: Double, ry: Double)(canvas: CanvasRenderingContext2D): Unit = {
      canvas.save()
      canvas.beginPath()
      canvas.translate(x-rx, y-ry)
      canvas.scale(rx, ry)
      canvas.arc(1, 1, 1, 0, 2 * Math.PI, false)
      canvas.restore()
      canvas.fill()

  }
}

object Util{

  def random[A](seq: Seq[A]): Option[A] = {
    if(seq.nonEmpty){
      val idx = (Math.random() * seq.length).toInt
      Some(seq(idx))
    } else None
  }
}
