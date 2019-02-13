package de.thm.move.controllers.drawing.WallSnapping

import de.thm.move.controllers.drawing.WallSnapping.WallSnapStrategy.WallSnapType
import de.thm.move.util.GeometryUtils.{inclination, radToDeg}
import de.thm.move.models.house.House
import de.thm.move.models.house.Room.Wall

/**
  * This class encapsulates the move behavior of tmpFigure following the mouse alongside the closest walls.
  * @param tmpFigure The tmpFigure
  * @tparam FigureType The type of the tmpFigure
  */
class WallSnapBehavior[FigureType <: WallSnapType](tmpFigure: FigureType) {
//note that this class is not an abstract interface so it can be used as a default behavior

  /**
    * Offsets for figure, applied after moving.
    * (horizontal, vertical)
    */
  protected def offset: (Double, Double) = (0, 0)

  /** Called after the tmpFigure is moved to the closest wall.
    * Rotates tmpFigure to the orientation of the wall and applies the offset.
    * @param wall The wall on which the tmpFigure is placed.
    * @param mousePos The position of the mouse.
    */
  protected def adjustAfterMove(wall: Wall, mousePos: (Double, Double)): Unit = {
    //Rotate tmpFigure to the angle of the wall
    tmpFigure.setRotate(radToDeg(inclination(wall)))
    //Apply offset
    tmpFigure.move(offset)
  }

  /**
    * When the mouse is moving, the tmpFigure should follow the closest wall.
    * This function moves the tmpFigure to the position on the closest wall to the mouse's position.
    * @param mousePos The position of the mouse.
    */
  final def move(mousePos: (Double, Double)): Unit = {
    //Move tmpFigure to the wall and position closest to the cursor on this wall.
    val closest = House.current.closestWall(mousePos).get
    tmpFigure.setXY(closest._2)
    //Adjust tmpFigure to offset and rotation
    adjustAfterMove(closest._1, mousePos)
  }

  /**
    * Called after a new copy of tmpFigure is made.
    * @param fig The new copy of tmpFigure.
    */
  def save(fig: FigureType): Unit = {
    //default = do nothing
  }
}
