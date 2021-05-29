package com.shafer.planetgenerator

import com.shafer.planetgenerator.delaunay.Point

trait GenerativePlanet {
  def generatePlanet(position: Point): Planet
}

object BarrenPlanet extends GenerativePlanet {
  def generatePlanet(position: Point) = {
    val radius = Math.random() * 150 + 100

    Planet(
      planetBase = PlanetBase(
        PlanetBaseTextured.random(position, radius,
          Color.randomGreyscale(150, 200),
          roughness = (Math.random() * 250).toInt + 100,
          numberOfCraters = (Math.random * 10 + 7).toInt)
      ),
      atmosphere = Atmosphere.none
    )(position, radius)
  }
}

object IrradiatedPlanet extends GenerativePlanet {
  def generatePlanet(position: Point) = {
    val radius = Math.random() * 150 + 100

    val planetColor = Color.random
    Planet(
      planetBase = PlanetBase(
        PlanetBaseGradient.random(position, radius, (planetColor, Color.random))
      ),
      atmosphere = Atmosphere.random(
        position, radius,
        cloudsNumber = 25,
        cloudColor = () => {
          planetColor.inverted.copy(a = 0.5)
        })
    )(position, radius)
  }
}

object GasPlanet extends GenerativePlanet {
  def generatePlanet(position: Point) = {
    val radius = Math.random() * 150 + 100
    val planetColor = Color.random
    Planet(
      planetBase = PlanetBase(
        PlanetBaseGradient.random(position, radius, (planetColor, Color.random))
      ),
      atmosphere = Atmosphere.random(position, radius, cloudsNumber = 5, cloudColor = () => Color.white.copy(a = 0.3))
    )(position, radius)
  }
}

object FracturedPlanet extends GenerativePlanet {
  def generatePlanet(position: Point) = {
    val radius = Math.random() * 150 + 100
    Planet(
      planetBase = PlanetBase(
        PlanetBaseTextured.random(position, radius = radius,
          Color.random,
          roughness = (Math.random() * 100).toInt + 60,
          numberOfCraters = -1
        )
      ),
      atmosphere = Atmosphere.random(position, radius, 5, () => Color.white)
    )(position, radius)
  }
}

object PlanetClasses {
  def random: GenerativePlanet = Util.random(Seq(BarrenPlanet, IrradiatedPlanet, GasPlanet, FracturedPlanet)).get
}
