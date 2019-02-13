package de.thm.move.models.house

import de.thm.move.views.shapes._
import de.thm.move.types.Point
import de.thm.move.models.house.Room.{RoomShape, Wall}
import javafx.scene.shape.Shape
import play.api.libs.json
import play.api.libs.json._

object Room {
  //Walls are represented by their begin and end-points.
  type Wall = (Point, Point)
  //Minimal type of the Rooms Shape
  type RoomShape = ResizableShape with ColorizableShape with VertexLike

  implicit val writes: Writes[Room] =
    new Writes[Room]{
      def writes(o: Room): JsValue = Json.obj(
        "room-type" -> o.name,
        "shape" -> writeShape(o.shape)
      )
    }

  implicit val reads: Reads[Room] = new Reads[Room] {
    def reads(js: JsValue): JsResult[Room] = {
      (js \ "room-type").validate[String].get match {
        case "rectangle-room" => js.validate[RectangleRoom]
        case "polygon-room" => js.validate[PolygonRoom]
        case _ => JsError()
      }
    }
  }
}

/**
  * The following object represents a Room.
  * @param shape Object representing the visual representation of the room.
  */

abstract class Room(val shape: RoomShape) {
  /**
    * Returns the type name of a room.
    * @return string
    */
  def name: String

  /**
    * Lists all walls that the object contains.
    * @return A list of pairs with coordinates that describe the end-points of a wall.
    */
  def listWalls(): List[Wall] = shape.sides()
}

/**
  * This class represents rectangle shaped rooms.
  * @param resizableRectangle The rectangle which draws the room.
  */
class RectangleRoom(resizableRectangle: ResizableRectangle) extends Room(resizableRectangle){
  def name = "rectangle-room"
}

object RectangleRoom {
  implicit val reads: Reads[RectangleRoom] = new Reads[RectangleRoom] {
    def reads(js: JsValue): JsResult[RectangleRoom] = {
      JsSuccess(new RectangleRoom(readShape((js \ "shape").get, ResizableRectangle.apply).get))
    }
  }
}

/**
  * This class represents polygon shaped rooms.
  * @param resizablePolygon The polygon which draws the room.
  */
class PolygonRoom(resizablePolygon: ResizablePolygon) extends Room(resizablePolygon){
  def name = "polygon-room"
}

object PolygonRoom {
  implicit val reads: Reads[PolygonRoom] = new Reads[PolygonRoom] {
    def reads(js: JsValue): JsResult[PolygonRoom] = {
      JsSuccess(new PolygonRoom(readShape((js \ "shape").get, (pnts:List[Double]) => new ResizablePolygon(pnts)).get))
    }
  }
}
