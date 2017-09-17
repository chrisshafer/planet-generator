package com.shafer.planetgenerator

object NameGenerator{

  val starSystems = Seq(
    "Eridanus",
    "Cassiopeia",
    "Scorpius",
    "Crux",
    "Cancer",
    "Leo",
    "Andromeda",
    "Taurus",
    "Lyra",
    "Virgo",
    "Sagittarius",
    "Aquarius",
    "Cygnus",
    "Ursa Major",
    "Capricornus",
    "Gemini",
    "Orion",
    "Pisces",
    "Aquila",
    "Boötes",
    "Aries",
    "Libra",
    "Carina",
    "Ara",
    "Draco",
    "Piscis Austrinus",
    "Pegasus",
    "Hydrus",
    "Delphinus",
    "Puppis",
    "Hercules",
    "Pavo",
    "Ursa Minor",
    "Canis Minor",
    "Centaurus",
    "Canis Major",
    "Camelopardalis"
  )

  val postfix = Seq(
    "I",
    "II",
    "III",
    "IV",
    "V",
    "VI",
    "VII",
    "VIII",
    "IX",
    "X",
    "XI"
  )

  def generate: String = {
    s"${Util.random(starSystems).get} ${Util.random(postfix).get}"
  }
}