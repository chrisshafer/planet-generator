package com.shafer.planetgenerator


import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html.Canvas

import scala.scalajs.js
import scala.scalajs.js.JSApp
import Util._
import org.w3c.dom.html.HTMLDivElement

object Generator extends JSApp {

  def main(): Unit = {

    val canvas: Canvas = document.createElement("canvas").asInstanceOf[html.Canvas]
    document.getElementById("container").appendChild(canvas)
    val (width, height, rctx) = initCanvas(canvas)

    val planet: Planet = PlanetClasses.random.apply(width/2, height/2, Math.random() * 150 + 100)
    val starfield: Starfield = Starfield.random(100, width, height, 300)

    canvas.onclick = { event: MouseEvent =>
      planet.render(rctx)
    }
    appendButton(planet, starfield, rctx, width, height)
    renderCycle(0, starfield, planet, rctx, width, height)
  }


  def appendButton(planet: Planet, starfield: Starfield, rctx: CanvasRenderingContext2D, width: Double, height: Double): Unit = {
    val button = dom.document.createElement("button").asInstanceOf[html.Button]
    button.setAttribute("class", "reload-button")
    button.textContent = "reload"
    button.onclick = { event: MouseEvent =>
      val newPlanet = PlanetClasses.random.apply(width/2, height/2, Math.random() * 150 + 100)
      renderCycle(0, starfield, newPlanet, rctx, width, height)
    }
    dom.document.body.appendChild(button)
  }




  def renderCycle(delta: Double, starfield: Starfield, planet: Planet, rctx: CanvasRenderingContext2D, width: Double, height: Double): Unit = {
    timer { () =>
      println(delta)
      println("Clearing")
      rctx.clearRect(0, 0, width, height)
      starfield.render(rctx)(1.0)
      planet.render(rctx)
    }("renderCycle")
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
