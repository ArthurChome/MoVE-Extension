package de.thm.move.models.house

import de.thm.move.views.SelectionGroup
import de.thm.move.models.house.Measurement.{measurementLine, measurementText}
import de.thm.move.views.shapes.{ResizableLine, ResizableText}
import play.api.libs.json._

object Measurement {
  type measurementLine = ResizableLine
  type measurementText = ResizableText


  implicit val writes: Writes[Measurement] = new Writes[Measurement] {
    override def writes(o: Measurement): JsValue = Json.obj(
      "line-shape" -> writeShape(o.line),
      "text-shape" -> writeBaseShape(o.text),
      "measured-value" -> o.text.getText
    )
  }

  implicit val reads: Reads[Measurement] = new Reads[Measurement] {
    override def reads(json: JsValue): JsResult[Measurement] = {
      val line = readShape((json \ "line-shape").get, points => {
        new ResizableLine((points.head, points(1)), (points(2), points(3)), 1)
      }).get

      val text = readBaseShape((json \ "text-shape").get, points => {
        new ResizableText("ignored", points.head, points.tail.head)
      }).get

      val group = new SelectionGroup(List(line, text))

      text.setText((json \ "measured-value").validate[String].get)

      JsSuccess(new Measurement(line, text, group))
    }
  }
}

/**
  * Represents measurements.
  * @param line The line representing the measurement.
  * @param text The text-element drawing the measurements value.
  * @param group The selection group holding the line and text.
  */
class Measurement(val line: measurementLine, val text : measurementText, val group: SelectionGroup) {
}
