package com.shafer.planetgenerator

object PlanetClasses {

  type PlanetFunction = (Double, Double, Double) => Planet

  def barrenPlanet: PlanetFunction = (x: Double, y: Double, r: Double) => Planet(
   planetBase = PlanetBase(
     PlanetBaseTextured.random(x, y, r,
       Color.randomGreyscale(150, 200),
       roughness = (Math.random() * 250).toInt + 100,
       numberOfCraters = (Math.random * 10 + 7).toInt)
    ),
   atmosphere = Atmosphere.none
  )(x, y, r)

  def irradiatedPlanet: PlanetFunction = (x: Double, y: Double, r: Double) => {
    val planetColor = Color.random
    Planet(
      planetBase = PlanetBase(
        PlanetBaseGradient.random(x, y, r, (planetColor, Color.random))
      ),
      atmosphere = Atmosphere.random(
        x, y, r,
        cloudsNumber = 25,
        cloudColor = () => {
        planetColor.inverted.copy(a = 0.5)
      })
    )(x, y, r)
  }

  def gasPlanet: PlanetFunction = (x: Double, y: Double, r: Double) => {
    val planetColor = Color.random
    Planet(
      planetBase = PlanetBase(
        PlanetBaseGradient.random(x, y, r, (planetColor, Color.random))
      ),
      atmosphere = Atmosphere.random(x, y, r, cloudsNumber = 5, cloudColor = () => Color.white.copy(a = 0.3))
    )(x, y, r)
  }

  def fracturedPlanet: PlanetFunction = (x: Double, y: Double, r: Double) => Planet(
    planetBase = PlanetBase(
      PlanetBaseTextured.random(x, y, r,
        Color.random,
        roughness = (Math.random() * 100).toInt + 60,
        numberOfCraters = -1
      )
    ),
    atmosphere = Atmosphere.random(x, y, r, 5, () => Color.white)
  )(x, y, r)


  def random: PlanetFunction = Util.random(Seq(fracturedPlanet, irradiatedPlanet, barrenPlanet, gasPlanet)).get

}
