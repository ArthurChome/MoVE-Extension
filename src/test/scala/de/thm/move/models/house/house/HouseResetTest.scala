package de.thm.move.models.house.house

import de.thm.move.MoveSpec
import de.thm.move.types.Point
import de.thm.move.util.GeometryUtils
import org.scalatest.BeforeAndAfterEach
import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable.ArrayBuffer

//Tests operate on a cleared an repopulated House.
class HouseResetTest extends MoveSpec with BeforeAndAfterEach{
  import HouseTest._

  //Resets and populates the House
  override def beforeEach(): Unit = repopulate()

  "closestWall" should "find the closest point on the closest wall to an arbitrary point if the closest point is also an end-point of the wall" in {
    //Point is right next to top-left end-point (7.0,11.0) of the rectangleRoom
    val p: Point = (6.3,10.0)
    //closestWalls returns both the closest wall and the point on this wall closest to the input point
    assert(House.closestWall(p).contains(((7.0,11.0),(7.0,28.0)), (7.0,11.0)), //order is hardcoded here, end-point is a vertex for two lines
      "An incorrect point is returned, expected end-point.")
  }

  it should "find the closest point on the closest wall to an arbitrary point if the closest point falls somewhere in the middle of the wall" in {
    //Point next to a wall of the polygon room
    val p: Point = (25, 25) //arbitrary line next to a wall
    assert(House.closestWall(p).contains(((19.0,23.0),(29.0,31.0)),(23.634146341463417,26.707317073170735)), "expected the closest point to the closest wall")
  }

  it should "find the closest point on the closest wall to a point if this point is part of a wall" in {
    //Point on a wall, arbitrarily take the middle point of a wall.
    val p: Point = GeometryUtils.middleOfLine((19, 23), (37, 41))
    assert(House.closestWall(p).contains((((19.0, 23.0), (37.0, 41.0)), (28.0, 32.0))), "expected the middle point of this wall")
  }

  "doors" should "return all doors in the House in an Array" in {
    assert(House.doors() sameElements Array(HouseTest.door), "doors are not listed correctly")
  }

  "windows" should "return all windows in the House in an Array" in {
    assert(House.windows() sameElements Array(HouseTest.window), "windows are not listed correctly")
  }

  "measurements" should "return all measurements in the House in an Array" in {
    assert(House.measurements() sameElements Array(HouseTest.measurement), "measurements are not listed correctly")
  }

  "remDoor" should "remove a door from the House" in {
    House.remDoor(HouseTest.door)
    assert(House.doors().isEmpty, "door was not removed")
  }

  "remWindow" should "remove a window from the House" in {
    House.remWindow(HouseTest.window)
    assert(House.windows().isEmpty, "window was not removed")
  }

  "remMeasurement" should "remove a measurement from the House" in {
    House.remMeasurement(HouseTest.measurement)
    assert(House.measurements().isEmpty, "measurement was not removed")
  }

  "House to json" should "convert a House to a correct json representation" in {
    val jVal: JsValue = Json.toJson(House)
    val jStr = """{"rooms":[{"room-type":"rectangle-room","shape":{"fill-color":"0x000000ff","fill-type":"Solid","border-color":null,"border-width":"1.0","border-type":"Solid","base":{"rotation":0,"scale-x":1,"scale-y":1,"points":[7,11,20,11,20,28,7,28]}}},{"room-type":"polygon-room","shape":{"fill-color":"0x000000ff","fill-type":"Solid","border-color":null,"border-width":"1.0","border-type":"Solid","base":{"rotation":0,"scale-x":1,"scale-y":1,"points":[19,23,29,31,37,41]}}}],"windows":[{"shape":{"fill-color":"0x000000ff","fill-type":"Solid","border-color":null,"border-width":"1.0","border-type":"Solid","base":{"rotation":0,"scale-x":1,"scale-y":1,"points":[80,80,90,80,90,100,80,100]}}}],"doors":[{"shape":{"rotation":0,"scale-x":1,"scale-y":1,"points":[0,0,50,0,50,50,0,50]}}],"measurements":[{"line-shape":{"fill-color":null,"fill-type":"Solid","border-color":"0x000000ff","border-width":"4.0","border-type":"Solid","base":{"rotation":0,"scale-x":1,"scale-y":1,"points":[20,20,100,100]}},"text-shape":{"rotation":0,"scale-x":1,"scale-y":1,"points":[20,20]},"measured-value":"512"}]}"""

    assert(jVal.toString() == jStr,
      "House dit not parse to Json correctly")
  }

  "getShapesFromHouse" should "retrieve all shapes in the house" in {
    val shapes = ArrayBuffer(roomA.shape, roomB.shape, window.shape, door.shape, measurement.group)
    assert(House.getShapesFromHouse equals shapes, "did not receive all shapes correctly")
  }
}
