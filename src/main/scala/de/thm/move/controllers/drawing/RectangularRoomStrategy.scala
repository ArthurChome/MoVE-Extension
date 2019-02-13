package de.thm.move.controllers.drawing

import javafx.scene.input.MouseEvent

import de.thm.move.controllers.ChangeDrawPanelLike
import de.thm.move.views.shapes.ResizableRectangle
import de.thm.move.models.house.House
import de.thm.move.models.house.RectangleRoom

class RectangularRoomStrategy(
    changeLike : ChangeDrawPanelLike,
    override protected val tmpFigure:ResizableRectangle) 
    extends RectangularStrategy(changeLike, tmpFigure) {
  override def dispatchEvent(mouseEvent: MouseEvent): Unit = {
    mouseEvent.getEventType match {
      case MouseEvent.MOUSE_PRESSED =>
        changeLike.addNode(tmpFigure)
        setStartXY(mouseEvent.getX, mouseEvent.getY)
      case MouseEvent.MOUSE_DRAGGED =>
        setBounds(mouseEvent.getX, mouseEvent.getY)
      case MouseEvent.MOUSE_RELEASED =>
        setBounds(mouseEvent.getX, mouseEvent.getY)
        val copyOfTmpFigure = tmpFigure.copy
        House.current.addRoom(new RectangleRoom(copyOfTmpFigure))
        changeLike.addShapeWithAnchors(copyOfTmpFigure)
        reset()
      case _ => //ignore
    }
  }
}
