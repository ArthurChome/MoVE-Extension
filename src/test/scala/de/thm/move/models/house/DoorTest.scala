package de.thm.move.models.house

import de.thm.move.MoveSpec
import de.thm.move.views.shapes.ResizableRectangle
import play.api.libs.json.Json
import scala.language.reflectiveCalls

class DoorTest extends MoveSpec {
  def fixture = new {
    //quick fix because CI can't load actual image, cheat by giving it a resizable rectangle (will still be loaded from json as image -> moved to no-ci folder)
    //val img = new DoorImage
    val img = new ResizableRectangle((0, 0), 10, 20)
    val door = new Door(img)
    val jsonStr = """{"shape":{"rotation":0,"scale-x":1,"scale-y":1,"points":[0,0,50,0,50,47.5,0,47.5]}}"""
    val jsonStrFaked = """{"shape":{"rotation":0,"scale-x":1,"scale-y":1,"points":[0,0,10,0,10,20,0,20]}}"""
  }

  "shape" should "return the doors shape" in {
    val f = fixture

    assert(f.door.shape == f.img, "not the correct shape")
  }

  "write json" should "transform a door to correct json" in {
    val f = fixture
    val json = Json.toJson(f.door)

    assert(json.toString() == f.jsonStrFaked, "door is not transformed to json correctly")
  }

  "read json" should "transform a json string to a correct door" in {
    val jsString = fixture.jsonStr
    val door = Json.fromJson[Door](Json.parse(jsString)).get

    assert(Json.toJson(door).toString() == jsString, "door was not parsed correctly")
  }
}
