package com.shafer.planetgenerator


import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html.Canvas

import scala.scalajs.js.JSApp

object Observer extends JSApp {

  def main(): Unit = {

    val canvas: Canvas = document.createElement("canvas").asInstanceOf[html.Canvas]
    document.getElementById("container").appendChild(canvas)
    val (width, height, rctx) = initCanvas(canvas)

    val planet = Planet(width/2, height/2, 200, PlanetBase())

    planet.render(rctx)
  }

  def initCanvas(canvas: html.Canvas): (Int, Int, dom.CanvasRenderingContext2D) = {
    val width = dom.document.getElementById("container").clientWidth
    val height = dom.document.getElementById("container").clientHeight

    canvas.width  = dom.document.getElementById("container").clientWidth
    canvas.height = dom.document.getElementById("container").clientHeight

    (width, height, canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D])
  }
}
