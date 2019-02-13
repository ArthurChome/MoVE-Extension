package de.thm.move.controllers.drawing

import de.thm.move.MoveSpec
import de.thm.move.controllers.drawing.WallSnapping.DoorSnapBehavior
import de.thm.move.models.house.House
import de.thm.move.views.shapes.ResizableRectangle
import org.scalatest.{BeforeAndAfterEach, PrivateMethodTester}
import scala.language.reflectiveCalls

class DoorBehaviorSpec extends MoveSpec with BeforeAndAfterEach with PrivateMethodTester{
  //start with a new house before each test
  override def beforeEach(): Unit = House.resetCurrent(new House)

  def fixture = new {
    val fig = new ResizableRectangle((10, 10),10,10)
    val behavior = new DoorSnapBehavior(fig)
    val wall: ((Double, Double), (Double, Double)) = ((10, 10), (20, 10)) //we simulate that the tmpFigure was placed and rotated correctly on a wall, this is a wall bellow the tmpFigure:

  }

  "save" should "create a new door with given shape and add it to the house" in {
    val f = fixture
    f.behavior.save(f.fig)
    assert(House.current.doors().length == 1, "a single door should be added to the house")
    val door = House.current.doors().head
    assert(door.shape == f.fig, "door has an incorrect shape")
  }

  "offset" should "return the correct offset" in {
    val f = fixture
    //its a private method
    val priv = PrivateMethod[DoorSnapBehavior[ResizableRectangle]]('offset)
    val offs: (Double, Double) = (f.behavior invokePrivate priv()).asInstanceOf[(Double, Double)]
    assert(offs == (-5.0,-10.0), "offset is wrong")
  }

  "centeredTopBot" should "return the coordinates of the points at the center-top and center-bot of the figure" in {
    val f = fixture
    val priv = PrivateMethod[DoorSnapBehavior[ResizableRectangle]]('centeredTopBot)
    val pnts = (f.behavior invokePrivate priv()).asInstanceOf[((Double, Double), (Double, Double))]
    assert(pnts == ((15.0,10.0),(15.0,20.0)), "points are not correct")
  }

  //Test both cases of this proc.
  "adjustAfterMove" should "not flip tmpFigure if mouse is closer to its top" in {
    val f = fixture
    val fig = f.fig
    val priv = PrivateMethod[DoorSnapBehavior[ResizableRectangle]]('adjustAfterMove)
    //mousepos closer to the top of the figure (no flip required)
    val mouse: (Double, Double) = (0, 0)
    //apply aftermove to tmpFigure
    f.behavior invokePrivate priv(f.wall, mouse)
    assert(fig.getXY == (5.0,0.0), "tmpFigure offset should be applied without flip offset")
    assert(fig.getRotate == 0, "there should be no flip rotation")
  }

  it should "flip tmpFigure if mouse is closer to its bot" in {
    val f = fixture
    val fig = f.fig
    val priv = PrivateMethod[DoorSnapBehavior[ResizableRectangle]]('adjustAfterMove)
    //mousepos closer to the bot of the figure (a flip is required)
    val mouse: (Double, Double) = (20, 20)
    //apply aftermove to tmpFigure
    f.behavior invokePrivate priv(f.wall, mouse)
    assert(fig.getXY == (5.0,10.0), "tmpFigure offset should be applied with a flip offset")
    assert(fig.getRotate == 180, "there should be a flip rotation")
  }
}
