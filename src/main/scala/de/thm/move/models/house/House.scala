package de.thm.move.models.house
import de.thm.move.types.Point
import de.thm.move.models.house.Room.Wall
import de.thm.move.util.GeometryUtils._
import de.thm.move.views.shapes.ResizableShape
import play.api.libs.json
import play.api.libs.json._

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * Hold the currently loaded House, can be reset (ex. user start new file)
  */
object House {
  var current: House = new House

  /**
    * Changes the current House.
    * @param newHouse The new current.
    */
  def resetCurrent(newHouse: House): Unit = {
    current = newHouse
  }

  implicit val writes: Writes[House] = new Writes[House] {
    def writes(house: House): JsObject = Json.obj(
      "rooms" -> house.rooms(),
      "windows" -> house.windows(),
      "doors" -> house.doors(),
      "measurements" -> house.measurements()
    )
  }

  implicit val reads: Reads[House] = new Reads[House] {
    override def reads(js: JsValue): JsResult[House]  = {
      val rooms = (js \ "rooms").validate[Array[Room]].get
      val windows = (js \ "windows").validate[Array[Window]].get
      val doors = (js \ "doors").validate[Array[Door]].get
      val measurements = (js \ "measurements").validate[Array[Measurement]].get
      val newHouse = new House

      rooms.foreach(newHouse.addRoom)
      windows.foreach(newHouse.addWindow)
      doors.foreach(newHouse.addDoor)
      measurements.foreach(newHouse.addMeasurement)

      json.JsSuccess[House](newHouse)
    }
  }
}

/**
  * The House objects contains representations (models) of all drawn objects: rooms, doors, windows and measurements.
  * These representations can be added/retrieved from the House.
  */
class House {
  private val _rooms: ArrayBuffer[Room] = ArrayBuffer()
  private val _windows: ArrayBuffer[Window] = ArrayBuffer()
  private val _doors: ArrayBuffer[Door] = ArrayBuffer()
  private val _measurements: ArrayBuffer[Measurement] = ArrayBuffer()

  /**
    * Adds a new Room to the House.
    * @param new_room The Room to be added.
    */
  def addRoom(new_room: Room): Unit = _rooms += new_room

  /**
    * Delete a Room from the House.
    * @param room The Room to be removed.
    */
  def remRoom(room: Room): Unit = _rooms -= room

  /**
    * Lists all the rooms in an Array
    * @return Array containing all the Rooms of the House.
    */
  def rooms(): Array[Room] = _rooms.toArray

  /**
    * Similar functions to add/remove/list all other models.
    */
  def addWindow(new_window: Window): Unit = _windows += new_window
  def remWindow(window: Window): Unit = _windows -= window
  def windows(): Array[Window] = _windows.toArray
  def addDoor(new_door: Door): Unit = _doors += new_door
  def remDoor(door: Door): Unit = _doors -= door
  def doors(): Array[Door] = _doors.toArray
  def addMeasurement(new_measurement: Measurement): Unit = _measurements += new_measurement
  def remMeasurement(measurement: Measurement): Unit = _measurements -= measurement
  def measurements(): Array[Measurement] = _measurements.toArray

  /**
    * Lists all walls in the house.
    * @return A List containing all walls.
    */
  def walls(): List[Wall] = {
    val walls: ListBuffer[Wall] = ListBuffer()
    _rooms.foreach((room: Room) => {
      walls ++= room.listWalls()
    })
    walls.toList
  }

  /**
    * Finds the closest point on a Rooms'wall to a certain point.
    * @param point Usually the location of the mouse.
    * @return Returns an Option, if point exists returns a pair consisting of the walls'end-points and the actual point.
    */
  def closestWall(point: Point): Option[(Wall, Point)] = {
    val houseWalls = walls()
    if (houseWalls.isEmpty) {
      return None //There are no walls.
    }

    //List of walls and distances to these walls.
    val distances = houseWalls.map((wall: Wall) => {
      (wall, distanceToLine(point, wall))
    })

    val (closestWall, _) = distances.min(Ordering.by((x: (Wall, Double)) => {x._2})) //order by distances

    Some(closestWall, closestPointOnLine(point, closestWall))
  }

  /**
    * Iterates over all features of a House and retrieves the shapes of this given house.
    * @return An array of ResizableShapes
    */
  def getShapesFromHouse: ArrayBuffer[ResizableShape] = {
    var shapes: ArrayBuffer[ResizableShape] = ArrayBuffer()
    for (room <- _rooms) {shapes += room.shape}
    for (window <- _windows) {shapes += window.shape}
    for (door <- _doors) {shapes += door.shape}
    for (measurement <- _measurements){shapes += measurement.group}
    shapes
  }
}
