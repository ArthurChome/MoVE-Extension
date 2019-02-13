package de.thm.move.controllers.drawing

import javafx.scene.input.MouseEvent

import de.thm.move.controllers.ChangeDrawPanelLike
import de.thm.move.views.shapes.ResizableRectangle
import de.thm.move.models.house.House
import de.thm.move.models.house.RectangleRoom

class RectangleRoomStrategy(
    changeLike : ChangeDrawPanelLike) 
    extends RectangularRoomStrategy(changeLike, new ResizableRectangle((0,0), 0, 0)) {
}
