package de.thm.move.models.house

import de.thm.move.MoveSpec
import de.thm.move.views.SelectionGroup
import de.thm.move.views.shapes.{ResizableLine, ResizableText}
import play.api.libs.json.Json
import scala.language.reflectiveCalls

class MeasurementTest extends MoveSpec{
  private def fixture = new Object {
    val line = new ResizableLine((0, 0), (10, 10), 1)
    val text = new ResizableText("I am a test", 5, 5)
    val group = new SelectionGroup(List(line, text))
    val measurement = new Measurement(line, text, group)
    val jsString = """{"line-shape":{"fill-color":null,"fill-type":"Solid","border-color":"0x000000ff","border-width":"1.0","border-type":"Solid","base":{"rotation":0,"scale-x":1,"scale-y":1,"points":[0,0,10,10]}},"text-shape":{"rotation":0,"scale-x":1,"scale-y":1,"points":[5,5]},"measured-value":"I am a test"}"""
  }

  "line" should "return the line shape of a measurement" in {
    val f = fixture
    f.measurement.line shouldBe f.line
  }

  "text" should "return the text shape of a measurement" in {
    val f = fixture
    f.measurement.text shouldBe f.text
  }

  "group" should "return the group shape of a measurement" in {
    val f = fixture
    f.measurement.group shouldBe f.group
  }

  "write json" should "return a json representing the measurement" in {
    val f = fixture
    val jsString = Json.toJson(f.measurement)

    jsString.toString() shouldBe f.jsString
  }

  "read json" should "return a measurement made from its json representation" in {
    val f = fixture
    val measurement = Json.fromJson[Measurement](Json.parse(f.jsString)).get

    assert(Json.toJson(measurement).toString() == f.jsString, "measurement was not parsed correctly")
  }
}
