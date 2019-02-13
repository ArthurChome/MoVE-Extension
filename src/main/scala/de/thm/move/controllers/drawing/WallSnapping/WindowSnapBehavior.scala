package de.thm.move.controllers.drawing.WallSnapping

import de.thm.move.models.house.Window.WindowShape
import de.thm.move.models.house.{House, Window}

class WindowSnapBehavior[FigureType  <: WindowShape](tmpFigure: FigureType) extends WallSnapBehavior(tmpFigure) {
  //Keep rectangle centered on the wall.
  override protected def offset: (Double, Double) =
    (-(tmpFigure.getWidth/2), -(tmpFigure.getHeight/2))

  //Save new windows in the house
  override def save(fig: FigureType): Unit = {
    House.current.addWindow(new Window(fig))
  }
}
