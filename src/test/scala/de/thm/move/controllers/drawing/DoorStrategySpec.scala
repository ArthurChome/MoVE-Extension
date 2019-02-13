package de.thm.move.controllers.drawing

import de.thm.move.MoveSpec
import de.thm.move.controllers.ChangeDrawPanelLike
import de.thm.move.controllers.drawing.WallSnapping.DoorStrategy
import de.thm.move.models.house.DoorImage
import scala.language.reflectiveCalls
import org.scalamock.scalatest.MockFactory

class DoorStrategySpec extends MoveSpec with MockFactory{
  private def fixture = new {
    val changelike: ChangeDrawPanelLike = mock[ChangeDrawPanelLike] //mock drawpanel
    val strat = new DoorStrategy(changelike)
  }

  "tmpFigure" should "return a DoorImage" in {
    assert(DoorStrategy.tmpFigure.isInstanceOf[DoorImage], "the type of tmpFigure does not match DoorImage")
  }

  "moveBehavior" should "be an instance of DoorSnapBehavior" in {
    val f = fixture
    val behavior = f.strat.moveBehavior
    assert(behavior.isInstanceOf[de.thm.move.controllers.drawing.WallSnapping.DoorSnapBehavior[DoorImage]], "strategy has the wrong move behavior")
  }
}
