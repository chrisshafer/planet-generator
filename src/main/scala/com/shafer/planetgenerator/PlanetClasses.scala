package com.shafer.planetgenerator

object PlanetClasses {

  type PlanetFunction = (Double, Double, Double) => Planet

  def barrenPlanet: PlanetFunction = (x: Double, y: Double, r: Double) => Planet(
   planetBase = PlanetBase(
      Color.randomGreyscale(150,200),
      roughness = (Math.random() * 250).toInt + 100,
      craters = (Math.random * 10 + 20).toInt
   ),
   atmosphere = Atmosphere.none
  )(x, y, r)

  def gasPlanet: PlanetFunction = (x: Double, y: Double, r: Double) => {
    val planetColor = Color.random
    Planet(
      planetBase = PlanetBase(
        color = planetColor,
        roughness = -1,
        craters = -1
      ),
      atmosphere = Atmosphere(clouds = 30, cloudColor = () => {
        planetColor.inverted
      })
    )(x, y, r)
  }

  def fracturedPlanet: PlanetFunction = (x: Double, y: Double, r: Double) => Planet(
    planetBase = PlanetBase(
      color = Color.random,
      roughness = (Math.random() * 100).toInt + 120,
      craters = -1
    ),
    atmosphere = Atmosphere(5)
  )(x, y, r)


  def random: PlanetFunction = Util.random(Seq(fracturedPlanet, gasPlanet, barrenPlanet)).get

}
