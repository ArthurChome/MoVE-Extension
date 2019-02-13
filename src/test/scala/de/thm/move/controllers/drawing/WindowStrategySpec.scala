package de.thm.move.controllers.drawing

import de.thm.move.MoveSpec
import de.thm.move.controllers.ChangeDrawPanelLike
import de.thm.move.controllers.drawing.WallSnapping.WindowStrategy
import de.thm.move.models.house.WindowRect
import javafx.scene.paint.Color
import org.scalamock.scalatest.MockFactory
import scala.language.reflectiveCalls

class WindowStrategySpec extends MoveSpec with MockFactory{
  private def fixture = new {
    val changelike: ChangeDrawPanelLike = mock[ChangeDrawPanelLike] //mock drawpanel
    val strat = new WindowStrategy(changelike)
  }

  "setColor" should "set the colors of the tmpFigure" in {
    val f = fixture
    f.strat.setColor(Color.BLUE, Color.BLACK, 1)
    assert(WindowStrategy.tmpFigure.getFillColor == Color.BLUE, "fill color was not set correctly")
    assert(WindowStrategy.tmpFigure.getStrokeColor == Color.BLACK, "stroke color was not set correctly")
    assert(WindowStrategy.tmpFigure.getStrokeWidth == 1, "stroke width was not set correctly")
  }

  "tmpFigure" should "return a WindowRect" in {
    assert(WindowStrategy.tmpFigure.isInstanceOf[WindowRect], "the type of tmpFigure does not match the expected WindowRect")
  }

  "moveBehavior" should "be an instance of WindowSnapBehavior" in {
    val f = fixture
    val behavior = f.strat.moveBehavior
    assert(behavior.isInstanceOf[de.thm.move.controllers.drawing.WallSnapping.WindowSnapBehavior[WindowRect]], "strategy has the wrong move behavior")
  }
}
