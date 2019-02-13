package de.thm.move.models.house.house

import de.thm.move.MoveSpec
import de.thm.move.views.shapes.ResizablePolygon
import de.thm.move.models.house._
import javafx.scene.paint.Color
import play.api.libs.json.Json
import scala.language.reflectiveCalls

//Tests the house-util functions in the House Package object
class HousePackageObjectSpec extends MoveSpec{
  private def emptyFixture = new Object {
    //Shape without color/rotation info
    val emptyShape = new ResizablePolygon(List(10, 20, 50, 50, 0, 10, 10, 20))
    val emptyJsStr = """{"fill-color":"0x000000ff","fill-type":"Solid","border-color":null,"border-width":"1.0","border-type":"Solid","base":{"rotation":0,"scale-x":1,"scale-y":1,"points":[10,20,50,50,0,10,10,20]}}"""

    //Represents the base shape (without colors)
    val baseJsStr = """{"rotation":0,"scale-x":1,"scale-y":1,"points":[10,20,50,50,0,10,10,20]}"""
  }

  private def fixture = new Object {
    //Shape with color/rotation info
    val shape = new ResizablePolygon(List(10, 20, 50, 50, 0, 10, 10, 20))
    val jsStr = """{"fill-color":"0x000000ff","fill-type":"Solid","border-color":"0xffffffff","border-width":"1.0","border-type":"Solid","base":{"rotation":0,"scale-x":1,"scale-y":1,"points":[10,20,50,50,0,10,10,20]}}"""
    //Change null value
    shape.setStrokeColor(Color.web("0xffffffff"))
  }

  "writeBaseShape" should "generate valid json from base shapes" in {
    val f = emptyFixture
    val jsStr = writeBaseShape(f.emptyShape).toString()

    assert(jsStr == f.baseJsStr, "json from base shape is not correct")
  }

  "readBaseShape" should "generate a base shape from a json with null-property" in {
    val f = emptyFixture
    val shape = readBaseShape(Json.parse(f.baseJsStr), new ResizablePolygon(_)).get

    assert(writeBaseShape(shape).toString() == f.baseJsStr, "json with null did not generate a correct shape")
  }

  "writeShape" should "generate valid json from shapes with null values" in {
    val f = emptyFixture
    val jsStr = writeShape(f.emptyShape).toString()

    assert(jsStr == f.emptyJsStr, "shape with null values was not transformed to json correctly")
  }

  it should "generate valid json from shapes" in {
    val f = fixture
    val jsStr = writeShape(f.shape).toString()

    assert(jsStr == f.jsStr, "shape was not transformed to json correctly")
  }

  "readShape" should "generate a shape from a json with null property" in {
    val f = emptyFixture
    val shape = readShape(Json.parse(f.emptyJsStr), new ResizablePolygon(_)).get

    assert(writeShape(shape).toString() == f.emptyJsStr, "json with null did not generate a correct shape")
  }

  it should "generate a shape from a json" in {
    val f = fixture
    val shape = readShape(Json.parse(f.jsStr), new ResizablePolygon(_)).get

    assert(writeShape(shape).toString() == f.jsStr, "json with null did not generate a correct shape")
  }
}
