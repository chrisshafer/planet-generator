package com.shafer.planetgenerator

import org.scalajs.dom._

import scala.scalajs.js.Date
import Util._

case class Planet(x: Double,
                  y: Double,
                  radius: Double,
                  planetBase: PlanetBase,
                  atmosphere: Atmosphere,
                  name: String) {

  val namePadding = 20
  val backgroundColor = Color(27, 27, 27)
  def render(ctx: CanvasRenderingContext2D) = {
    ctx.save()
    timer( () => planetBase.render(this)(ctx) )("planetBase")
    timer( () => atmosphere.render(this)(ctx) )("atmosphere")
    ctx.restore()
    renderAliasOverlayFix(ctx)
    renderName(ctx)
  }

  def renderAliasOverlayFix(ctx: CanvasRenderingContext2D) = {
    ctx.save()
    ctx.strokeStyle = backgroundColor.build
    ctx.beginPath()
    ctx.arc(x, y, radius -0.5, 0, 2 * Math.PI, false)
    ctx.lineWidth = 3
    ctx.stroke()
    ctx.closePath()
    ctx.restore()
  }

  def renderName(ctx: CanvasRenderingContext2D) = {
    ctx.save()
    ctx.font = "25pt Orbitron"
    ctx.textAlign = "center"
    ctx.fillStyle = backgroundColor.lighten(2.00).build
    ctx.fillText(name, x, y + radius + namePadding + 15 + 5)
    ctx.restore()
  }
}

object Planet{
  def apply(planetBase: PlanetBase, atmosphere: Atmosphere, name: String = NameGenerator.generate)
           (x: Double, y: Double, radius: Double): Planet = {
    
    Planet(x, y, radius, planetBase, atmosphere, name)
  }
}