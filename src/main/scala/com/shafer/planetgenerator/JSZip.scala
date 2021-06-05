package com.shafer.planetgenerator

import scala.scalajs.js.annotation.JSGlobal
import scala.scalajs.js

@scala.scalajs.js.native
@JSGlobal
class JSZip extends scala.scalajs.js.Object {

  def file(fileName: String, content: Any): Unit = js.native

  def file(fileName: String, content: Any, options: js.Dictionary[Any]): Unit = js.native
  
  def generateAsync(params: js.Dictionary[js.Any]): js.Promise[String] = js.native
}
