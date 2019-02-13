package de.thm.move.controllers.drawing

import de.thm.move.MoveSpec
import de.thm.move.controllers.drawing.WallSnapping.WindowSnapBehavior
import de.thm.move.models.house.House
import de.thm.move.views.shapes.ResizableRectangle
import org.scalatest.{BeforeAndAfterEach, PrivateMethodTester}
import scala.language.reflectiveCalls

class WindowBehaviorSpec extends MoveSpec with BeforeAndAfterEach with PrivateMethodTester{
  //start with a new house before each test
  override def beforeEach(): Unit = House.resetCurrent(new House)

  def fixture = new {
    val fig = new ResizableRectangle((10, 10),10,10)
    val behavior = new WindowSnapBehavior(fig)
    val wall: ((Double, Double), (Double, Double)) = ((10, 10), (20, 10)) //we simulate that the tmpFigure was placed and rotated correctly on a wall, this is a wall bellow the tmpFigure:

  }

  "save" should "create a new window and add it to the house" in {
    val f = fixture
    f.behavior.save(f.fig)
    assert(House.current.windows().length == 1, "a single window should be added to the house")
    val window = House.current.windows().head
    assert(window.shape == f.fig, "window has the incorrect shape")
  }

  "offset" should "return the correct offset" in {
    val f = fixture
    //its a private method
    val priv = PrivateMethod[WindowSnapBehavior[ResizableRectangle]]('offset)
    val offs: (Double, Double) = (f.behavior invokePrivate priv()).asInstanceOf[(Double, Double)]
    assert(offs == (-5.0,-5.0), "offset is wrong")
  }
}

