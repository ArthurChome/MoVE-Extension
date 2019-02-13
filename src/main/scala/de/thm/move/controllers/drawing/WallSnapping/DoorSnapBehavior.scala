package de.thm.move.controllers.drawing.WallSnapping

import de.thm.move.controllers.drawing.WallSnapping.WallSnapStrategy.WallSnapType
import de.thm.move.types.Point
import de.thm.move.util.GeometryUtils.closestPointOnLine
import de.thm.move.models.house.Door.DoorShape
import de.thm.move.models.house.{Door, House}
import de.thm.move.models.house.Room.Wall

class DoorSnapBehavior[FigureType <: DoorShape](tmpFigure: FigureType) extends WallSnapBehavior(tmpFigure) {
  //Center image on "foot" of door.
  override protected def offset: (Double, Double) = {
    val (x, y) = tmpFigure.getXY
    //The center of the "foot", after rotation
    val (cx, cy) = tmpFigure.localToParentPoint(x + (tmpFigure.getWidth/2), y + tmpFigure.getHeight)
    (-(cx-x), -(cy-y))
  }

  /**
    * Calculates the coordinates of the point at the center-top and center-bot of the door.
    * @return The coordinates of both points in a tuple (top, bot).
    */
  final protected def centeredTopBot(): (Point, Point) = {
    val (x, y) = tmpFigure.getXY
    val top = tmpFigure.localToParentPoint(x + tmpFigure.getWidth/2, y)
    val bot = tmpFigure.localToParentPoint(x + tmpFigure.getWidth/2, y + tmpFigure.getHeight)
    (top, bot)
  }

  /**
    * Rotate door inwards/outwards depending on the position of the mouse vs the wall, after its moved.
    * @param wall The wall on which the tmpFigure is placed.
    * @param mouse The position of the mouse.
    */
  override protected def adjustAfterMove(wall: Wall, mouse: (Double, Double)): Unit = {
    super.adjustAfterMove(wall, mouse)

    val centerLine = centeredTopBot()
    val (_, bot) = centerLine

    //The closest point on the line (between the centered top and bottom points on the door)
    //and the mouse.
    val closest = closestPointOnLine(mouse, centerLine)

    //If this point is at the bottom of the figure, it should be rotated towards the mouse.
    if (closest == bot) {
      tmpFigure.rotate(180)
      val ((tx, ty), (bx, by)) = centeredTopBot() //Calculate rotated top and bot & move to other side of wall
      tmpFigure.move(-(bx - tx), -(by - ty))
    }
  }

  //Save new Doors in House
  override def save(fig: FigureType): Unit = {
    House.current.addDoor(new Door(fig))
  }
}
