package de.thm.move.models

import de.thm.move.models.{FillPattern, LinePattern}
import de.thm.move.views.shapes.{ColorizableShape, ResizableShape, VertexLike}
import javafx.scene.paint.Color
import play.api.libs.json._

//Contains some house util functions.
package object house {
  type BaseShape = ResizableShape with VertexLike
  type ColorShape = BaseShape with ColorizableShape


  //Like JsString(x.toString) with check for null values to avoid exceptions
  private def toJsString[T](x: T): JsValue = {
    x match {
      case null => JsNull
      case _ => JsString(x.toString)
    }
  }

  /**
    * Form json from the base of a shape
    * @param shape Resizableshape with vertexlike
    * @return Json object
    */
  def writeBaseShape(shape: BaseShape): JsObject = {
    Json.obj(
      "rotation" -> shape.getRotate,
      "scale-x" -> shape.getScaleX,
      "scale-y" -> shape.getScaleY,
      "points" -> shape.localVertexes.flatten {case (a,b) => List(a,b)}
    )
  }

  /**
    * Generate a shape from json.
    * @param js jsonobject representing the shape
    * @param createFromPoints function to create the shape from its list of vertexes
    * @tparam T type of the shape
    * @return Resizable VertexLike shape, as created by createFromPoints, adjusted with values from the json
    */
  def readBaseShape[T <: BaseShape](js: JsValue, createFromPoints: List[Double] => T): JsResult[T] = {
    val points: List[Double] = (js \ "points").validate[List[Double]].get
    val shape: T = createFromPoints(points)
    shape.rotate((js \ "rotation").validate[Double].get)
    shape.setScaleX((js \ "scale-x").validate[Double].get)
    shape.setScaleY((js \ "scale-y").validate[Double].get)

    JsSuccess(shape)
  }

  /**
    * Creates a json object from a shape.
    * @param shape resizable, colorizable and vertexlike shape
    * @return Json object
    */
  def writeShape(shape: ColorShape): JsObject = {
    Json.obj (
    "fill-color" ->  toJsString(shape.getFillColor),
    "fill-type" -> JsString(shape.fillPatternProperty.get().toString),
    "border-color" -> toJsString(shape.getStrokeColor),
    "border-width" -> toJsString(shape.getStrokeWidth),
    "border-type" -> JsString(shape.linePattern.get().toString),
    "base" -> writeBaseShape(shape)
  )}

  /**
    * Creates a shape from its json representation
    * @param js jsonobject representing the shape
    * @param createFromPoints function to create the shape from its list of vertexes
    * @tparam T type of the shape
    * @return Resizable VertexLike Colorizable shape, as created by createFromPoints, adjusted with values from the json
    */
  def readShape[T <: ColorShape](js: JsValue, createFromPoints: List[Double] => T): JsResult[T] = {
    val shape = readBaseShape((js \ "base").get, createFromPoints).get

    //the following values may be invalid (null)
    (js \ "fill-color").validate[String] match {
      case x: JsSuccess[String] => shape.setFillColor(Color.web(x.get))
      case _ => //ignored
    }
    shape.fillPatternProperty.set(FillPattern.withName((js \ "fill-type").validate[String].get))
    (js \ "border-color").validate[String] match {
      case x: JsSuccess[String] => shape.setStrokeColor(Color.web(x.get))
      case _ => //ignored
    }
    (js \ "border-width").validate[Double] match {
      case x: JsSuccess[Double] => shape.setStrokeWidth(x.get)
      case _ => //ignored
    }
    shape.linePattern.set(LinePattern.withName((js \ "border-type").validate[String].get))
    JsSuccess(shape)
  }
}