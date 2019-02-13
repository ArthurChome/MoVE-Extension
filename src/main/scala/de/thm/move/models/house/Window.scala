package de.thm.move.models.house

import de.thm.move.controllers.drawing.WallSnapping.WallSnapStrategy.WallSnapType
import de.thm.move.models.house.Window.WindowShape
import de.thm.move.views.shapes.ResizableRectangle
import play.api.libs.json._

object Window {
  //Windows are drawn as rectangles
  type WindowShape = ResizableRectangle with WallSnapType

  implicit val writes: Writes[Window] = new Writes[Window] {
    def writes(window: Window): JsObject = Json.obj {
      "shape" -> writeShape(window.shape)
    }
  }

  implicit val reads: Reads[Window] = new Reads[Window] {
    override def reads(js: JsValue): JsResult[Window] = {
      val shape = readShape((js \ "shape").get, points => ResizableRectangle(points)).get
      JsSuccess(new Window(shape))
    }
  }
}

//Window model
class Window(val shape: WindowShape) {
}

//The actual shape for windows in the application
class WindowRect extends ResizableRectangle((0, 0), 50, 10) //TODO remove magic constants
