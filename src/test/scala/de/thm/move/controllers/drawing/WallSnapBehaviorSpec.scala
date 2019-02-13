package de.thm.move.controllers.drawing

import de.thm.move.MoveSpec
import de.thm.move.controllers.drawing.WallSnapping.WallSnapBehavior
import de.thm.move.models.house.House
import de.thm.move.models.house.house.HouseTest
import de.thm.move.views.shapes.ResizableRectangle
import org.scalatest.{BeforeAndAfterEach, PrivateMethodTester}
import de.thm.move.util.GeometryUtils.{inclination, radToDeg}
import scala.language.reflectiveCalls

class WallSnapBehaviorSpec extends MoveSpec with BeforeAndAfterEach with PrivateMethodTester{
  //start with a new house before each test
  override def beforeEach(): Unit = House.resetCurrent(new House)

  def fixture = new {
    val fig = new ResizableRectangle((10, 10),10,10)
    val behavior = new WallSnapBehavior(fig)
  }

  "save" should "do nothing" in {
    val f = fixture
    f.behavior.save(f.fig)
    //no way of testing that no side effects happened
    assert(true)
  }

  "offset" should "return the default offset" in {
    val f = fixture
    //its a protected method
    val priv = PrivateMethod[WallSnapBehavior[ResizableRectangle]]('offset)
    val offs: (Double, Double) = (f.behavior invokePrivate priv()).asInstanceOf[(Double, Double)]
    assert(offs == (0,0), "dit not return the default offset")
  }

  "move" should "move the tmpFigure to the correct position on the closest wall near the mouse" in {
    val f = fixture
    val fig = f.fig
    val mouse: (Double, Double) = (0, 0)
    //Add a room to the house
    House.current.addRoom(HouseTest.roomA)

    //tmpFigure should move to here (offset = 0, 0)
    val (wall, point) = House.current.closestWall(mouse).get
    //its rotation should match the wall's
    val angle = radToDeg(inclination(wall))

    //move the tmpFigure
    f.behavior.move(mouse)

    assert(fig.getXY == point, "figure is moved to the wrong position")
    assert(fig.getRotate == angle, "figure did not rotate to the inclination of the wall")
  }
}
