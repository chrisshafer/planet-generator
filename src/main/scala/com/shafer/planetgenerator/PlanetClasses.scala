package com.shafer.planetgenerator

import com.shafer.planetgenerator.IrradiatedPlanet.radiusWeightedGaussianInt
import com.shafer.planetgenerator.delaunay.Point

import scala.util.Random

trait GenerativePlanet {
  val baseRadius: Double
  val radiusVariance: Double
  
  def randomRadius = Math.random() * radiusVariance + baseRadius
  def generatePlanet(position: Point): Planet
  
  def radiusWeightedGaussianInt(mean: Double, stdev: Double, radius: Double): Int = {
    (Util.gaussianRandom(mean, stdev) * (radius / baseRadius)).round.toInt
  }
}

object BarrenPlanet extends GenerativePlanet {
  
  val baseRadius    : Double = 150
  val radiusVariance: Double = 250

  def generatePlanet(position: Point) = {
    val radius = randomRadius

    Planet(
      planetBase = PlanetBase(
        PlanetBaseTextured.random(position, radius,
          Color.randomGreyscale(150, 200),
          roughness = radiusWeightedGaussianInt(150, 75, radius),
          numberOfCraters = radiusWeightedGaussianInt(10, 7, radius))
      ),
      atmosphere = Atmosphere.none
    )(position, radius)
  }
}

object IrradiatedPlanet extends GenerativePlanet {
  
  val baseRadius    : Double = 150
  val radiusVariance: Double = 100

  def generatePlanet(position: Point) = {
    val radius = randomRadius

    val planetColor = Color.random
    Planet(
      planetBase = PlanetBase(
        PlanetBaseGradient.random(position, radius, (planetColor, Color.random))
      ),
      atmosphere = Atmosphere.random(
        position, radius,
        cloudsNumber = radiusWeightedGaussianInt(5, 5, radius),
        cloudColor = () => {
          planetColor.inverted.copy(a = 0.5)
        })
    )(position, radius)
  }
}

object GasPlanet extends GenerativePlanet {
  
  val baseRadius    : Double = 300
  val radiusVariance: Double = 100

  def generatePlanet(position: Point) = {
    val radius = randomRadius
    val planetColor = Color.random
    Planet(
      planetBase = PlanetBase(
        PlanetBaseGradient.random(position, radius, (planetColor, Color.random))
      ),
      atmosphere = Atmosphere.random(
        position, 
        radius, 
        cloudsNumber = radiusWeightedGaussianInt(5, 5, radius), 
        cloudColor = () => Color.white.copy(a = 0.3)
      )
    )(position, radius)
  }
}

object FracturedPlanet extends GenerativePlanet {

  val baseRadius    : Double = 200
  val radiusVariance: Double = 150

  def generatePlanet(position: Point) = {
    val radius = randomRadius
    Planet(
      planetBase = PlanetBase(
        PlanetBaseTextured.random(position, radius = radius,
          Color.random,
          roughness = radiusWeightedGaussianInt(125, 25, radius),
          numberOfCraters = -1
        )
      ),
      atmosphere = Atmosphere.random(position, radius, radiusWeightedGaussianInt(7, 2, radius), () => Color.white)
    )(position, radius)
  }
}

object PlanetClasses {
  def random: GenerativePlanet = Util.random(Seq(BarrenPlanet, IrradiatedPlanet, GasPlanet, FracturedPlanet)).get
}
