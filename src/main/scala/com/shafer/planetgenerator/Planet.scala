package com.shafer.planetgenerator

import org.scalajs.dom._

import scala.scalajs.js.Date

case class Planet(x: Double,
                  y: Double,
                  radius: Double,
                  planetBase: PlanetBase,
                  atmosphere: Atmosphere) {

  def render(ctx: CanvasRenderingContext2D) = {
    val time = Date.now()
    ctx.save()
    planetBase.render(this)(ctx)
    atmosphere.render(this)(ctx)
    ctx.restore()

    // inner-space aliasing
    ctx.save()
    ctx.beginPath()
    ctx.arc(x, y, radius -2, 0, 2 * Math.PI, false)
    ctx.strokeStyle = Color(27, 27, 27).build
    ctx.lineWidth = 5
    ctx.stroke()
    ctx.restore()
    println(s"${Date.now() - time}ms")
  }
}