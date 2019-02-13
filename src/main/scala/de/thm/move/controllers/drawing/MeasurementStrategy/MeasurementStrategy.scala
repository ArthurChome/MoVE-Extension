package de.thm.move.controllers.drawing

import de.thm.move.controllers.ChangeDrawPanelLike
import de.thm.move.controllers.drawing.StatePattern.{FirstClickState, SecondClickState, State}
import de.thm.move.views.shapes.ResizableLine
import javafx.scene.input.MouseEvent

 /** The measurement line's strategy.
  *
  * It uses the State Pattern since the behaviour of an instance
  * of MeasurementStrategy is incapsulated in Behaviour Objects.
  * These get used interchangeably depending on the user's click input.
  *
  * @constructor create a new measurement strategy
  * @param the panel on which you can draw the measurement lines.
  *
  */

class MeasurementStrategy(changeLike:ChangeDrawPanelLike) extends PathLikeStrategy(changeLike) {

  /** Define the type of the temporal figure we are using. */
  override type FigureType = ResizableLine

  /** Temporal figure that represents the line. */
  override protected val tmpFigure = new ResizableLine((0,0), (0,0), 0)
  tmpFigure.setId(tmpShapeId)

  /**
   * These variables contain the two kinds of behaviours we can have.
   *
   * The first object (firstClick) gets used when the user clicks for a first time
   * to specify the beginning of the measurement line.
   * The second object (secondClick) gets used at the second mouse click
   * to specify the end of the line and draw it on the panel.
   * These two objects get interchanged at every mouse click.
   */
  var firstClickBehaviour = new FirstClickState(changeLike, tmpFigure, pointBuffer)
  var secondClickBehaviour = new SecondClickState(changeLike, tmpFigure, pointBuffer)

  /**
   * Variables for the current behaviour object used and the other behaviour object.
   *
   * They get changed every time a user provides mouse input.
   * More information can be found about this in the de.thm.move.controllers.drawing.StatePattern folder.
   */
  var currentBehaviour: State = firstClickBehaviour
  var notCurrentBehaviour: State = secondClickBehaviour

  /**
   * Reset the line of the object with the first click behaviour.
   *
   * This consists of clearing the pointbuffer and resetting the temporary shape.
   * The used method gets inherited all the way from the abstract class MeasurementDrawTools.
   */
  override def reset(): Unit = {
    firstClickBehaviour.reset()
  }

  /**
   * The dispatch of the class that reacts on the mouse click input.
   *
   * The dispatch procedure will get the dispatcher of our current behaviour
   * and switch the two behaviours.
   */
  def dispatchEvent(mouseEvent: MouseEvent): Unit = mouseEvent.getEventType match {
    /** Mouse got pressed (click input) */
    case MouseEvent.MOUSE_PRESSED =>
      /** Check if the user clicked on the primary button */
      if (mouseEvent.isPrimaryButtonDown){
        currentBehaviour.dispatchEvent(mouseEvent)
        val switcharoo = notCurrentBehaviour
        notCurrentBehaviour = currentBehaviour
        currentBehaviour = switcharoo
      }
    /** Elstak: nothing happens */
    case _ => //ignore
  }
}
