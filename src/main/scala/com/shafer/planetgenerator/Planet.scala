package com.shafer.planetgenerator

import org.scalajs.dom._

case class Planet(x: Double,
                  y: Double,
                  radius: Double,
                  planetBase: PlanetBase,
                  atmosphere: Atmosphere) {

  def render(ctx: CanvasRenderingContext2D) = {
    planetBase.render(this)(ctx)
    atmosphere.render(this)(ctx)
  }
}