package de.thm.move.controllers.drawing.WallSnapping

import de.thm.move.controllers.ChangeDrawPanelLike
import de.thm.move.models.house.Window.WindowShape
import de.thm.move.models.house.WindowRect
import javafx.scene.paint.Paint

object WindowStrategy {
  //Windows are represented by rectangles.
  val tmpFigure = new WindowRect
}

/**
  * This class is used to draw Windows on walls.
  * @param changeLike The ChangeDrawPanelLike.
  */
class WindowStrategy(changeLike: ChangeDrawPanelLike) extends WallSnapStrategy[WindowShape](changeLike, WindowStrategy.tmpFigure, "window") {

  //Defines behaviour of colour-bar on tmpFigure.
  //Apply the colouring to the rectangle
  override def setColor(fill:Paint, stroke:Paint, strokeThickness:Int): Unit = {
    WindowStrategy.tmpFigure.setFillColor(fill)
    WindowStrategy.tmpFigure.setStrokeColor(stroke)
    WindowStrategy.tmpFigure.setStrokeWidth(strokeThickness)
  }

  //Set the moveBehavior
  moveBehavior = new WindowSnapBehavior(tmpFigure)
}
