package de.thm.move.models.house

import de.thm.move.MoveSpec
import de.thm.move.views.shapes.ResizableRectangle
import play.api.libs.json.Json
import scala.language.reflectiveCalls

class WindowTest extends MoveSpec {
  private def fixture = new {
    val rect = new ResizableRectangle((0, 0), 10, 20)
    val window = new Window(rect)
    val jsonStr = """{"shape":{"fill-color":"0x000000ff","fill-type":"Solid","border-color":null,"border-width":"1.0","border-type":"Solid","base":{"rotation":0,"scale-x":1,"scale-y":1,"points":[0,0,10,0,10,20,0,20]}}}"""
  }

  "shape" should "return the windows shape" in {
    val f = fixture

    assert(f.window.shape == f.rect, "not the correct shape")
  }

  "write json" should "transform a window to correct json" in {
    val f = fixture
    val json = Json.toJson(f.window)

    assert(json.toString() == f.jsonStr, "window is not transformed to json correctly")
  }

  "read json" should "transform a json string to a correct window" in {
    val jsString = fixture.jsonStr
    val window = Json.fromJson[Window](Json.parse(jsString)).get

    assert(Json.toJson(window).toString() == jsString, "window was not parsed correctly")
  }
}