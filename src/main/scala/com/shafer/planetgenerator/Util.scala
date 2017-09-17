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


  def lighten(percent: Double): Color = {
    this.copy(
      r = (r + r * percent).toInt,
      g = (g + g * percent).toInt,
      b = (b + b * percent).toInt
    )
  }

  def inverted: Color = {
    this.copy(
     r = (r * -1) + 255,
     g = (g * -1) + 255,
     b = (b * -1) + 255
    )
  }
}

object Color{

  def random: Color = random(256)

  def random(a: Int): Color = {
    val r: Int = (Math.random() * 256).toInt
    val g: Int = (Math.random() * 256).toInt
    val b: Int = (Math.random() * 256).toInt
    Color(r,g,b,a)
  }

  def randomGreyscale(min: Int = 0, max: Int = 256): Color = {
    val scale = ((Math.random() * (max - min)) + min).toInt match {
      case tooHot if tooHot > 255 => 255
      case tooCold if tooCold < 0 => 0
      case justRight => justRight
    }
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
      val idx = (Math.random() * seq.length + 1).toInt - 1
      Some(seq(idx))
    } else None
  }
}
