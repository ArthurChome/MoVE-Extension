package de.thm.move.controllers.drawing.StatePattern

import de.thm.move.controllers.ChangeDrawPanelLike
import de.thm.move.types.Point
import de.thm.move.util.GeometryUtils
import de.thm.move.views.SelectionGroup
import de.thm.move.views.shapes.{ResizableLine, ResizableShape, ResizableText}
import javafx.scene.input.MouseEvent

import scala.collection.mutable.ListBuffer


/** The measurement draw tools that get shared by both the first and second click behaviour.
 *
 * It uses the State Pattern since different behaviour objects are expected
 * to extend this abstract class with their own typical behaviour.
 * Implements all drawing functions used by the two click-behaviours
 * like resetting a resizable line figure or generating a new measurement line
 *
 * @constructor not really, the class is abstract and needs to be completed.
 * @param panel on which you can draw the measurement lines.
 * @param temporal figure that you can use to generate custom resizable lines.
 * @param pointbuffer for drawing the line.
 */
//The measurementDraw class implements all drawing functions used by the two click-behaviours.

abstract class State(changeLike:ChangeDrawPanelLike, figure: ResizableLine, pointBuffer: ListBuffer[Point]){
  /** Define the type of the temporal figure we are using. */
  type FigureType = ResizableLine

  /** Temporal figure that represents the line. */
  val tmpFigure = figure

  /** Distance used: the map's size is 500 x 800 (default). */
  var gridSize = changeLike.gridSize

/**
 * Reset the line of the object with the first click behaviour.
 *
 * This consists of clearing the pointbuffer and resetting the temporary shape.
 * The used method gets inherited all the way from the abstract class MeasurementDrawTools.
 */
  def reset(): Unit = {
    pointBuffer.clear()
    changeLike.remove(tmpFigure)
    resetLine(0,0)
  }

/**
 * Reset the line to given coordinates.
 *
 * This consists of reseting the temporary resizable line figure
 * to given coordinates.
 */
  def resetLine(x:Double, y:Double): Unit = {
    tmpFigure.setStartX(x)
    tmpFigure.setStartY(y)
    tmpFigure.setEndX(x)
    tmpFigure.setEndY(y)
  }

/**
 * Generate a measurement line with length.
 *
 * It colors the line, handles the position of the number relative to the line
 * and groups both the text shape and line shape together as to make both of them draggable in one go.
 */
  def generateMeasurementLine(x1: Double, x2: Double, y1: Double, y2: Double): SelectionGroup = {

    /** Correction numbers for the position of the distance number relative to the line. */
    val divY = y1 / y2
    val divX = x1 / x2
    var correctX = 1.5 * divX
    var correctY = 1.5 * divY

    /** Calculate the distance with Pythagoras' formula and round the distance to 3 digits. */
    var disPythagoras = GeometryUtils.roundTo(GeometryUtils.distance(x1, y1, x2, y2) / gridSize, 3)

    /** Define the new shapes including the text shape. */
    val newText = new ResizableText(disPythagoras.toString, (x1 + x2)/2  , (y1 + y2)/2 - correctY)
    tmpFigure.setStrokeWidth(2.0)
    val newShape = tmpFigure.copy

    /** Add the shapes to the list */
    val shapeList = List[ResizableShape](newShape, newText)

    /** Group the shapes as to make them draggable as one. */
    new SelectionGroup(shapeList)
  }

  /** Dispatcher: not concretisized, this will be done by specific behaviour classes. */
  def dispatchEvent(mouseEvent: MouseEvent): Unit

}