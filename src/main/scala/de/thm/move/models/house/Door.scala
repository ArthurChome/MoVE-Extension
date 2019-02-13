package de.thm.move.models.house

import java.net.URI

import de.thm.move.controllers.drawing.WallSnapping.WallSnapStrategy.WallSnapType
import de.thm.move.models.house
import de.thm.move.models.house.Door.DoorShape
import de.thm.move.views.shapes.{ResizableImage, ResizableRectangle, ResizableShape}
import javafx.scene.image.Image
import play.api.libs.json._

object Door {
  //Type of figure drawing a door
  type DoorShape = house.BaseShape with WallSnapType


  implicit val writes: Writes[Door] = new Writes[Door] {
    def writes(door: Door): JsObject = Json.obj(
      "shape" -> writeBaseShape(door.shape)
    )
  }

  implicit val reads: Reads[Door] = new Reads[Door] {
    override def reads(json: JsValue): JsResult[Door] = {
      val shape = readBaseShape[DoorImage]((json \"shape").get, points => {
        //Get the coordinates of the image through implicit rectangle
        val tempRect = ResizableRectangle(points)
        val image = new DoorImage

        image.setXY(tempRect.getTopLeft)
        image
      }).get

      JsSuccess(new Door(shape))
    }
  }
}

/**
  * Represents doors.
  * @param shape The shape/image which draws the door.
  */
class Door(val shape: DoorShape) {
}

//TODO remove magic constants

object DoorImage {
  val imgPath: Either[URI, Array[Byte]] = Left(new URI("file:///images/door.png"))
  val image = new Image("/images/door.png")
}

//This class is an implementation of a Doors shape with the default image.
class DoorImage extends ResizableImage(DoorImage.imgPath, DoorImage.image) {
  setWidth(50)
}
