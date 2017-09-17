package com.shafer.planetgenerator


import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html.Canvas

import scala.scalajs.js
import scala.scalajs.js.JSApp

object Generator extends JSApp {

  def main(): Unit = {

    val canvas: Canvas = document.createElement("canvas").asInstanceOf[html.Canvas]
    document.getElementById("container").appendChild(canvas)
    val (width, height, rctx) = initCanvas(canvas)

    val planet: Planet = PlanetClasses.gasPlanet.apply(width/2, height/2, 200)

    canvas.onclick = { event: MouseEvent =>
      planet.render(rctx)
    }
    planet.render(rctx)
  }

  def initCanvas(canvas: html.Canvas): (Int, Int, dom.CanvasRenderingContext2D) = {
    val width = dom.document.getElementById("container").clientWidth
    val height = dom.document.getElementById("container").clientHeight

    canvas.width  = dom.document.getElementById("container").clientWidth
    canvas.height = dom.document.getElementById("container").clientHeight
    val context = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
    (width, height, context)
  }
}
