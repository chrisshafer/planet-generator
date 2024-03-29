package com.shafer.planetgenerator

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html.Canvas
import com.shafer.planetgenerator.Generator.fillCanvas
import com.shafer.planetgenerator.delaunay.Point
import Util._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.util.{Failure, Random, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.timers.SetIntervalHandle

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

  def regenerate = Scene.random(screenWidth, screenHeight, scale, backgroundColor)
}

object Scene {

  def random(screenWidth: Double, screenHeight: Double, scale: Double, backgroundColor: Color = Color(20, 20, 20)) = {
    val scaledWidth = screenWidth / scale
    val scaledHeight = screenHeight / scale

    val planet: Planet       = PlanetClasses.random.generatePlanet(Point(scaledWidth / 2.0, scaledHeight / 2.0))
    val starfield: Starfield = Starfield.random(Random.nextInt(100) + 100, scaledWidth, scaledHeight, planet.radius + 100)

    Scene(screenWidth, screenHeight, scale, planet, starfield, backgroundColor)
  }
}

object Generator {

  def fillCanvas(rctx: CanvasRenderingContext2D, width: Double, height: Double, color: Color) = {
    canvasOp(rctx){ ctx =>
      ctx.fillStyle = color.build
      ctx.fillRect(0, 0, width, height)
    }
  }

  @JSExport
  def main(args: Array[String]): Unit = {
    val canvas: Canvas = document.createElement("canvas").asInstanceOf[html.Canvas]
    val container = document.getElementById("container")

    val scale = 4.0
    val canvasWidth  = (container.clientWidth * scale).round.toInt
    val canvasHeight = (container.clientHeight * scale).round.toInt
    val rctx = initCanvas(canvasWidth, canvasHeight, canvas)

    container.appendChild(canvas)
    
    val scene = Scene.random(canvasWidth, canvasHeight, scale)
    hookReloadButton(scene, rctx)
    hookDownloaderButton()
    hookDownload()
    scene.render(rctx)
  }

  def hookDownloaderButton(): Unit = {
    val button = dom.document.getElementById("downloader-button").asInstanceOf[html.Button]
    button.onclick = { (event: MouseEvent) =>
      toggleModal
    }
  }
  
  def generateToEphemeral(ratioX: Int, ratioY: Int, pixelDensity: Double): (Canvas, CanvasRenderingContext2D) = {
    val canvas: Canvas = document.createElement("canvas").asInstanceOf[html.Canvas]
    
    val canvasWidth  = (ratioX * pixelDensity).round.toInt
    val canvasHeight = (ratioY * pixelDensity).round.toInt
    
    val rctx = initCanvas(canvasWidth, canvasHeight, canvas)
    val scene = Scene.random(canvasWidth, canvasHeight, pixelDensity)

    scene.render(rctx)
    canvas -> rctx
  }
  
  def clearDownloaderLog() = {
    
    val log = dom.document.getElementById("downloader-log")
    if(log != null){
      log.parentNode.removeChild(log)
    }
  }
  
  private def appendMessageToLog(message: String, log: html.Div) = {
    val newMessage = dom.document.createElement("span").asInstanceOf[html.Span]
    newMessage.innerHTML = message
    log.appendChild(newMessage)
    log.scrollTop = log.clientHeight
  }
  
  def appendToLog(message: String) = {
    val maybeLog = dom.document.getElementById("downloader-log").asInstanceOf[html.Div]

    val definitelyLog = if(maybeLog == null){
      val parent = dom.document.getElementById("downloader-modal")
      val log = dom.document.createElement("div").asInstanceOf[html.Div]
      log.setAttribute("id", "downloader-log")
      parent.appendChild(log)
      log
    } else maybeLog
    println(message)
    appendMessageToLog(message, definitelyLog)
  }
  
  private var registeredDownloadTimer: SetIntervalHandle = null
  
  def bulkDownload(countOfPlanets: Int, ratioX: Int, ratioY: Int, pixelDensity: Double): Unit = {
    import scala.scalajs.js.JSConverters._
    clearDownloaderLog()
    appendToLog(s"Generating ${countOfPlanets} planets")
    val zipper = new JSZip()
    
    var count = 0
    (1 to countOfPlanets).foreach(idx =>
      js.timers.setTimeout(100){
        val planet = generateToEphemeral(ratioX, ratioY, pixelDensity)
        zipper.file(s"${idx}.jpeg", planet._1.toDataURL("image/jpeg").replace("data:image/jpeg;base64,", ""), js.Dictionary("base64" -> true))
        appendToLog(s"generated planet ${idx}")
        count += 1
      }
    )
    
    def tryRunDownload: Unit = {
      if(count == countOfPlanets){
        appendToLog("Generating zip file")
        val asyncZipResult = zipper.generateAsync(js.Dictionary("type" -> "base64")).toFuture
        asyncZipResult.onComplete {
          case Failure(exception) =>
            exception.printStackTrace()
            appendToLog("Sorry! There has been an error generating the zip file :(")
          case Success(value)     => 
        }

        asyncZipResult.foreach{ content =>

          var link = document.createElement("a").asInstanceOf[html.Anchor]
          link.setAttribute("download", "planets.zip")
          link.href = "data:application/zip;base64,"+content

          appendToLog("Successfully generated zip file")
          appendToLog("Triggering download \uD83E\uDD73")
          link.click()
        }
        if(registeredDownloadTimer != null){
          js.timers.clearInterval(registeredDownloadTimer)
        }
      }
    }
    
    // poll for doneness
    registeredDownloadTimer = js.timers.setInterval(200)(tryRunDownload)
  }
  
  def hookDownload(): Unit = {
   val form = dom.document.getElementById("downloader-form").asInstanceOf[html.Form]
    form.addEventListener("submit", { e =>
      e.preventDefault()
      val dyn = e.target.asInstanceOf[js.Dynamic]
      
      val countOfPlanets = dyn.elements.countOfPlanets.value.asInstanceOf[String].toInt
      val ratioX         = dyn.elements.ratioX.value.asInstanceOf[String].toInt
      val ratioY         = dyn.elements.ratioY.value.asInstanceOf[String].toInt
      val pixelDensity   = dyn.elements.pixelDensity.value.asInstanceOf[String].toDouble
      bulkDownload(countOfPlanets, ratioX, ratioY, pixelDensity)
    })
  }
  
  def toggleModal = {
    val modal = dom.document.getElementById("downloader-modal")
    val currentState = modal.getAttribute("class")
    val nextState = if(currentState == "show") "hide" else "show"
    modal.setAttribute("class", nextState)
  }
  
  def hookReloadButton(scene: Scene, rctx: CanvasRenderingContext2D): Unit = {
    val button = dom.document.getElementById("reload-button").asInstanceOf[html.Button]
    button.onclick = { (event: MouseEvent) =>
      scene.regenerate.render(rctx)
    }
  }
  
  def initCanvas(width: Int, height: Int, canvas: html.Canvas): dom.CanvasRenderingContext2D = {
    canvas.setAttribute("style", "max-width: 100%; max-height: 100%;")
    canvas.width  = width
    canvas.height = height
    val context = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
    context
  }
}
