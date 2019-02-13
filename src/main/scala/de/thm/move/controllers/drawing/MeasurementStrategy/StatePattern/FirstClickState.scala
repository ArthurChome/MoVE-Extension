
package de.thm.move.controllers.drawing.StatePattern

import de.thm.move.controllers.ChangeDrawPanelLike
import de.thm.move.types.Point
import de.thm.move.views.shapes.ResizableLine
import javafx.scene.input.MouseEvent

import scala.collection.mutable.ListBuffer

/** Behaviour when clicking for a first time.
  *
  * It uses the State Pattern since behaviour is encapsulated in a class instance.
  * The instance specifies where the starting point of the new measurement line is.
  *
  * @constructor create a new first click behaviour.
  * @param panel on which you can draw the measurement lines.
  * @param temporal figure that you can use to generate custom resizable lines.
  * @param pointbuffer for drawing the line.
  */
class FirstClickState(changeLike:ChangeDrawPanelLike, tmpFigure: ResizableLine, pointBuffer: ListBuffer[Point])
  extends State(changeLike, tmpFigure, pointBuffer){

/**
  * Set the beginning of the measurement line.
  *
  * This is done by specifying the start of the resizable line temporal figure.
  * Very similar to setEndLine of the SecondClickBehaviour class.
  */
  def setBeginLine(x:Double, y:Double): Unit = {
    tmpFigure.setStartX(x)
    tmpFigure.setStartY(y)
  }

/**
 * The dispatcher to handle mouse events.
 *
 * In any case, it will set the start of the line.
 */
  override def dispatchEvent(mouseEvent: MouseEvent): Unit = {
    pointBuffer += (mouseEvent.getX -> mouseEvent.getY)
    /** Reset the line to be safe. */
    resetLine(mouseEvent.getX, mouseEvent.getY)
    changeLike.addNode(tmpFigure)
    /** Set the beginning of the line. */
    setBeginLine(mouseEvent.getX, mouseEvent.getY)
  }

}