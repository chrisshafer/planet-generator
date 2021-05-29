package com.shafer.planetgenerator

import org.scalajs.dom._

import scala.scalajs.js.Date
import Util._
import com.shafer.planetgenerator.delaunay.Point

case class Planet(
  position: Point,
  radius: Double,
  planetBase: PlanetBase,
  atmosphere: Atmosphere,
  name: String,
) {

  val namePadding     = 20
  val backgroundColor = Color(20, 20, 20)

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
    ctx.arc(position.x, position.y, radius -0.5, 0, 2 * Math.PI, false)
    ctx.lineWidth = 3
    ctx.stroke()
    ctx.closePath()
    ctx.restore()
  }

  def renderName(ctx: CanvasRenderingContext2D) = {
    ctx.save()
    ctx.font = "25pt Orbitron"
    ctx.textAlign = "center"
    ctx.fillStyle = Color(200, 200, 200, 1.0).build
    ctx.fillText(name, position.x, position.y + radius + namePadding + 15 + 5)
    ctx.restore()
  }
}

object Planet{
  def apply(planetBase: PlanetBase, atmosphere: Atmosphere, name: String = NameGenerator.generate)
           (position: Point, radius: Double): Planet = {
    Planet(position, radius, planetBase, atmosphere, name)
  }
}