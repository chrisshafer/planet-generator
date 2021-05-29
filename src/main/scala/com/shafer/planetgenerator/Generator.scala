package com.shafer.planetgenerator

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html.Canvas

import scala.scalajs.js
import scala.scalajs.js.JSApp
import Util._
import com.shafer.planetgenerator.Generator.{appendButton, fillCanvas}
import com.shafer.planetgenerator.delaunay.Point
import org.w3c.dom.html.HTMLDivElement

case class Scene(
  screenWidth: Double,
  screenHeight: Double,
  scale: Double,
  planet: Planet,
  starfield: Starfield,
  backgroundColor: Color
){

  val scaledWidth = screenWidth / scale
  val scaledHeight = screenHeight / scale

  def render(rctx: CanvasRenderingContext2D) = {
    canvasOp(rctx){ ctx =>
      ctx.scale(scale, scale)

      timer { () =>
        println("Clearing")
        fillCanvas(ctx, scaledWidth, scaledHeight, backgroundColor)
        starfield.render(rctx)(1.0)
        planet.render(rctx)
      }("renderCycle")
    }
  }
}

object Scene {

  def random(screenWidth: Double, screenHeight: Double, scale: Double, backgroundColor: Color = Color(20, 20, 20)) = {
    val scaledWidth = screenWidth / scale
    val scaledHeight = screenHeight / scale

    val planet: Planet       = PlanetClasses.random.generatePlanet(Point(scaledWidth / 2.0, scaledHeight / 2.0))
    val starfield: Starfield = Starfield.random(100, scaledWidth, scaledHeight, 300)

    Scene(screenWidth, screenHeight, scale, planet, starfield, backgroundColor)
  }
}

object Generator extends JSApp {

  def fillCanvas(rctx: CanvasRenderingContext2D, width: Double, height: Double, color: Color) = {
    canvasOp(rctx){ ctx =>
      ctx.fillStyle = color.build
      ctx.fillRect(0, 0, width, height)
    }
  }

  def main(): Unit = {
    val canvas: Canvas = document.createElement("canvas").asInstanceOf[html.Canvas]
    document.getElementById("container").appendChild(canvas)
    val scale = 4.0
    val (width, height, rctx) = initCanvas(canvas, scale)

    val scene = Scene.random(width, height, scale)
    appendButton(scene, rctx)
    scene.render(rctx)
  }

  def appendButton(scene: Scene, rctx: CanvasRenderingContext2D): Unit = {
    val button = dom.document.createElement("button").asInstanceOf[html.Button]
    button.setAttribute("class", "reload-button")
    button.textContent = "reload"
    button.onclick = { event: MouseEvent =>
      val newPlanet = PlanetClasses.random.generatePlanet(Point(scene.scaledWidth / 2.0, scene.scaledHeight / 2.0))
      scene.copy(planet = newPlanet).render(rctx)
    }
    dom.document.body.appendChild(button)
  }


  def initCanvas(canvas: html.Canvas, scale: Double = 4.0): (Int, Int, dom.CanvasRenderingContext2D) = {
    val width  = (dom.document.getElementById("container").clientWidth * scale).round.toInt
    val height = (dom.document.getElementById("container").clientHeight * scale).round.toInt

    canvas.setAttribute("style", "max-width: 100%; max-height: 100%;")
    canvas.width  = width
    canvas.height = height
    val context = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
    (width, height, context)
  }
}
