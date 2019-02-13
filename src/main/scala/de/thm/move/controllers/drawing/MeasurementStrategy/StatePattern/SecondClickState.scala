package de.thm.move.controllers.drawing.StatePattern

import de.thm.move.controllers.ChangeDrawPanelLike
import de.thm.move.types.Point
import de.thm.move.views.shapes.ResizableLine
import javafx.scene.input.MouseEvent

import scala.collection.mutable.ListBuffer

/** Second click state.
 *
 * It uses the State Pattern since behaviour is encapsulated in a class instance.
 * The instance specifies where the ending point of the measurement line is,
 * draws the generated measurement line and resets everything including the temporal shape.
 *
 * @constructor create a new first click behaviour.
 * @param panel on which you can draw the measurement lines.
 * @param temporal figure that you can use to generate custom resizable lines.
 * @param pointbuffer for drawing the line.
 */
class SecondClickState(changeLike:ChangeDrawPanelLike, tmpFigure: ResizableLine, pointBuffer: ListBuffer[Point])
  extends State(changeLike, tmpFigure, pointBuffer){

 /**
  * Set the end of the measurement line.
  *
  * This is done by specifying the end of the resizable line temporal figure.
  * Very similar to setBeginLine of the FirstClickBehaviour class.
  */
  def setEndLine(x:Double, y:Double): Unit = {
    tmpFigure.setEndX(x)
    tmpFigure.setEndY(y)
  }

 /**
  * The dispatcher to handle mouse events.
  *
  * In any case, it will make a new measurement line,
  * add it to the draw panel and then reset the temporal figure.
  */
  override def dispatchEvent(mouseEvent: MouseEvent): Unit = {

    /** Get updates about the grid size. */
    gridSize = changeLike.gridSize

    /** Specify where the line ends. */
    setEndLine(mouseEvent.getX, mouseEvent.getY)

    /** Generate a new measurement line. */
    val measurementLine = generateMeasurementLine(tmpFigure.getStartX, tmpFigure.getEndX, tmpFigure.getStartY, tmpFigure.getEndY)
    changeLike.addShapeWithAnchors(measurementLine)
    reset()
  }
}