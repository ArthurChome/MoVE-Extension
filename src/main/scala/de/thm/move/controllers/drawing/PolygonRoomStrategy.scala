package de.thm.move.controllers.drawing

import javafx.scene.input.MouseEvent

import de.thm.move.controllers.ChangeDrawPanelLike
import de.thm.move.views.shapes.ResizablePolygon
import de.thm.move.models.house.House
import de.thm.move.models.house.PolygonRoom

class PolygonRoomStrategy(changeLike:ChangeDrawPanelLike) 
    extends PolygonStrategy(changeLike) {
  override def dispatchEvent(mouseEvent:MouseEvent): Unit = mouseEvent.getEventType match {
    case MouseEvent.MOUSE_CLICKED if matchesStartPoint(mouseEvent.getX -> mouseEvent.getY) =>
      //create a polygon from the tmpFigure path & copy the colors
      val polygon = ResizablePolygon(tmpFigure.getPoints)
      polygon.copyColors(tmpFigure)
      polygon.setFill(this.fill)
      House.current.addRoom(new PolygonRoom(polygon))
      changeLike.addShapeWithAnchors(polygon)
      reset()
    case MouseEvent.MOUSE_CLICKED =>
      pointBuffer += (mouseEvent.getX -> mouseEvent.getY)
      updatePath(pointBuffer.toList)
    case _ => //ignore
  }
}
