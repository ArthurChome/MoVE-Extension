package de.thm.move.controllers.drawing

import de.thm.move.MoveSpec
import de.thm.move.controllers.ChangeDrawPanelLike
import de.thm.move.controllers.drawing.WallSnapping.WallSnapStrategy
import de.thm.move.views.shapes.ResizableRectangle
import javafx.scene.paint.Color
import org.scalamock.scalatest.MockFactory
import org.scalatest.PrivateMethodTester
import scala.language.reflectiveCalls

class WallSnapStrategySpec extends MoveSpec with MockFactory with PrivateMethodTester{
  private def fixture = new {
    val changelike: ChangeDrawPanelLike = mock[ChangeDrawPanelLike] //mock drawpanel
    val fig = new ResizableRectangle((10, 10), 10, 10)
    val strat = new WallSnapStrategy(changelike, fig, "")
  }

  "setColor" should "do nothing" in {
    val f = fixture
    f.strat.setColor(Color.BLUE, Color.BLACK, 1)
    assert(true)
  }

  "moveBehavior" should "be an instance of WallSnapBehavior" in {
    val f = fixture
    val behavior = f.strat.moveBehavior
    assert(behavior.isInstanceOf[de.thm.move.controllers.drawing.WallSnapping.WallSnapBehavior[ResizableRectangle]], "strategy has the wrong move behavior")
  }

  //the other procedures are too complex to test (require actual ChangeDrawPanelLike and mouse events) (no time for this)
}

