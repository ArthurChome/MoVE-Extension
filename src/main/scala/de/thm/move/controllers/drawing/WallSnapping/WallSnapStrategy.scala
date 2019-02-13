package de.thm.move.controllers.drawing.WallSnapping

import de.thm.move.controllers.ChangeDrawPanelLike
import de.thm.move.controllers.drawing.DrawStrategy
import de.thm.move.controllers.drawing.WallSnapping.WallSnapStrategy.WallSnapType
import de.thm.move.models.house.House
import de.thm.move.views.shapes.{RectangleLike, ResizableShape}
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Paint

object WallSnapStrategy {
  //Type of tmpFigure
  type WallSnapType = ResizableShape with RectangleLike
}

/**
  * The WallSnapStrategy moves a temporary figure along the walls closest to the mouse.
  * After clicking, a copy of tmpFigure is placed at it current position.
  * @param changeLike The ChangeDrawPanelLike.
  * @param tmpFigure The temporary figure.
  * @tparam NewFigureType The type of the temporary figure.
  * @param idString idString given to new copies of tmpFigure; idString+int combinations should be unique for each strategy (idString itself identifies tmpFigure)
  */
class WallSnapStrategy[NewFigureType <: WallSnapType](changeLike: ChangeDrawPanelLike, protected val tmpFigure: NewFigureType, idString: String) extends DrawStrategy {
  //extended DrawStrategy
  override type FigureType = NewFigureType
  tmpFigure.setId(idString)

  override def setColor(fill: Paint, stroke: Paint, strokeThickness: Int): Unit = {
    //Can't colour images.
  }

  //Whether or not tmpShape is drawn.
  private var is_Drawn: Boolean = false

  //Reset is called when another tool is selected; reset draw-boolean and remove the tmpFigure from drawPanel.
  final override def reset(): Unit = {
    if (is_Drawn) {
      is_Drawn = false
      changeLike.removeShape(tmpFigure)
    }
  }

  //Assigns a new id to each copy of tmpFigure
  private val idMaker: ()=>String = {
    var c = -1
    () => {
      c += 1
      idString + c.toString
    }
  }

  /**
    * The behavior changing how the tmpFigure gets moved.
    */
  var moveBehavior: WallSnapBehavior[NewFigureType] = new WallSnapBehavior(tmpFigure)

  //Similar to update() from observer pattern, called whenever a mouse event fires
  final def dispatchEvent(mouseEvent: MouseEvent): Unit = {

    mouseEvent.getEventType match {
      //When the mouse is moving, the tmpFigure should follow the closest wall.
      case MouseEvent.MOUSE_MOVED =>
        //If there are no rooms, do nothing
        val rooms: Boolean = House.current.rooms().nonEmpty

        //Check if tmpShape needs to be drawn.
        //Relevant if the tool was just selected, the tmpFigure needs to be added to the canvas.
        if (rooms) {
          if (!is_Drawn) {
            is_Drawn = true
            changeLike.addShape(tmpFigure)
          }

          //Call the move behavior
          moveBehavior.move(mouseEvent.getX, mouseEvent.getY)
        }

      //When the user clicks, draw a copy of tmpFigure.
      case MouseEvent.MOUSE_CLICKED => {
        if(is_Drawn) {
          //Make a persistent clone of tmpFigure.
          val copy = tmpFigure.copy.asInstanceOf[FigureType] //copy method on certain shapes may be bugged
          //Draw the clone
          changeLike.addShapeWithAnchors(copy)
          copy.setId(idMaker())
          //Call the save method.
          moveBehavior.save(copy)
        }
      }
      case _ => //Ignored
    }
  }
}
