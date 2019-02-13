package de.thm.move.controllers.drawing.WallSnapping

import de.thm.move.controllers.ChangeDrawPanelLike
import de.thm.move.models.house.Door.DoorShape
import de.thm.move.models.house.{Door, DoorImage, House}
import javafx.scene.paint.Paint

object DoorStrategy {
  //Doors are drawn by an image.
  val tmpFigure = new DoorImage
}

/**
  * This class represents the strategy used to draw doors on walls.
  * @param changeLike The ChangeDrawPanelLike.
  */
class DoorStrategy(changeLike: ChangeDrawPanelLike) extends WallSnapStrategy[DoorShape](changeLike, DoorStrategy.tmpFigure, "door") {
  //Change move behavior (image should flip over walls towards mouse)
  moveBehavior = new DoorSnapBehavior(DoorStrategy.tmpFigure)
}
