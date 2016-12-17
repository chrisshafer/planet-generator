package com.shafer.planetgenerator


import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html.Canvas

import scala.scalajs.js.JSApp

object Observer extends JSApp {

  def main(): Unit = {

    val canvas: Canvas = document.createElement("canvas").asInstanceOf[html.Canvas]
    document.getElementById("container").appendChild(canvas)
    val rctx = initCanvas(canvas)
    drawCircle(canvas.width/2, canvas.height/2, 20)(rctx)
  }

  def drawCircle(x: Double, y: Double, radius: Double)(canvas: CanvasRenderingContext2D) = {
    canvas.beginPath()
    canvas.arc(x, y, radius, 0, 2 * Math.PI, false)
    canvas.fillStyle = "white"
    canvas.fill()
    canvas.stroke()
  }

  def initCanvas(canvas: html.Canvas): dom.CanvasRenderingContext2D = {
    canvas.width  = dom.document.getElementById("container").clientWidth
    canvas.height = dom.document.getElementById("container").clientWidth
    canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
  }
}
