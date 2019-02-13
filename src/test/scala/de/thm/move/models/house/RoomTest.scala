package de.thm.move.models.house

import de.thm.move.MoveSpec
import de.thm.move.views.shapes.{ResizablePolygon, ResizableRectangle}
import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable.ListBuffer
import scala.language.reflectiveCalls

class RectangleRoomTest extends MoveSpec {
  // A ResizableRectangle whose coordinates should not matter in the tests below
  val testRectangle = new ResizableRectangle((1,1), 99, 99)

  "RectangleRoom" should "return the right coordinates for its walls" in {
    val rR = new RectangleRoom(new ResizableRectangle((3, 4), 10, 11))
    rR.listWalls should be (
      List(((3.0,4.0),(3.0,15.0)),
        ((3.0,4.0),(13.0,4.0)),
        ((13.0,4.0),(13.0,15.0)),
        ((13.0,15.0),(3.0,15.0))))
  }

  it should "return the right rectangle through the field 'colorizable'" in {
    val rR = new RectangleRoom(testRectangle)
    rR.shape should be (testRectangle)
  }
}

class PolygonRoomTest extends MoveSpec {
  // A ResizablePolygon whose coordinates should not matter in the tests below
  val testPolygon = new ResizablePolygon(List(1,1, 100,1, 100,100, 1,100))

  "PolygonRoom" should "return the right coordinates for its walls" in {
    val pR = new PolygonRoom(new ResizablePolygon(List(3,4, 1,14, 17,13, 23,16, 8,2)))
    pR.listWalls should be (ListBuffer(((3, 4), (8, 2)),
                                       ((3, 4), (1, 14)),
                                       ((1, 14), (17, 13)),
                                       ((17, 13), (23, 16)),
                                       ((23, 16), (8, 2))))
  }

  it should "return the right polygon through the field 'colorizable'" in {
    val pR = new PolygonRoom(testPolygon)
    pR.shape should be (testPolygon)
  }
}

class PolygonJsonTest extends MoveSpec {
  val rP = new PolygonRoom(new ResizablePolygon(List(0.0, 1.0, 3.0, 5.0)))

  val jPoly: JsValue = Json.toJson(rP)

  "Polygon toJson" should "correctly convert a polygon room to a JsValue" in {
    assert(jPoly.toString() == """{"room-type":"polygon-room","shape":{"fill-color":"0x000000ff","fill-type":"Solid","border-color":null,"border-width":"1.0","border-type":"Solid","base":{"rotation":0,"scale-x":1,"scale-y":1,"points":[0,1,3,5]}}}""")
  }

  "Polygon fromJson" should "correctly parse a PolygonRoom with the same walls" in {
    val polyR: PolygonRoom = Json.fromJson[PolygonRoom](jPoly).get
    assert(rP.listWalls() == polyR.listWalls(), "Room.listWalls do not correspond")
  }
}

class RectangleJsonTest extends MoveSpec {
  val rR = new RectangleRoom(new ResizableRectangle((3, 4), 10, 11))
  val jRect: JsValue = Json.toJson(rR)

  "Rectangle toJson" should "correctly convert a rectangle room to a JsValue" in {
    assert(jRect.toString() == """{"room-type":"rectangle-room","shape":{"fill-color":"0x000000ff","fill-type":"Solid","border-color":null,"border-width":"1.0","border-type":"Solid","base":{"rotation":0,"scale-x":1,"scale-y":1,"points":[3,4,13,4,13,15,3,15]}}}""")
  }

  "Rectangle fromJson" should "correctly parse a RectangleRoom with the same walls" in {
    val rectR: RectangleRoom = Json.fromJson[RectangleRoom](jRect).get
    assert(rR.listWalls() == rectR.listWalls(), "Room.listWalls do not correspond")
  }
}

class RoomJsonTest extends MoveSpec {
  val rP = new PolygonRoom(new ResizablePolygon(List(0.0, 1.0, 3.0, 5.0)))
  val House = new House

  House.addRoom(rP)

  val rRoom: Room = House.rooms()(0)
  val jRoom: JsValue = Json.toJson(rRoom)

  "Room toJson" should "correctly convert a polygon room to a JsValue" in {
    assert(jRoom.toString() == """{"room-type":"polygon-room","shape":{"fill-color":"0x000000ff","fill-type":"Solid","border-color":null,"border-width":"1.0","border-type":"Solid","base":{"rotation":0,"scale-x":1,"scale-y":1,"points":[0,1,3,5]}}}""")
  }

  "Room fromJson" should "correctly parse a PolygonRoom with the same walls" in {
    val polyR: Room = Json.fromJson[Room](jRoom).get
    assert(rP.listWalls() == polyR.listWalls(), "Room.listWalls do not correspond")
  }

  "Room fronJson" should "fail when an unknown room type is found in json" in {
    //unknown-room is not a room-type
    val wrongJson = """{"room-type":"unknown-room","shape":{"fill-color":"0x000000ff","fill-type":"Solid","border-color":null,"border-width":"1.0","border-type":"Solid","base":{"rotation":0,"scale-x":1,"scale-y":1,"points":[0,1,3,5]}}}"""
    assertThrows[NoSuchElementException](Json.fromJson[Room](Json.parse(wrongJson)).get)
  }
}
