package de.thm.move.guitest

import java.awt.MouseInfo

import de.thm.move.{GuiTest, MoveApp}
import org.junit.{Before, Test}
import de.thm.move.controllers.drawing.WallSnapping.DoorStrategy
import de.thm.move.controllers.drawing.WallSnapping.WallSnapStrategy.WallSnapType
import de.thm.move.models.house.{DoorImage, House}
import de.thm.move.views.shapes.ResizableImage
import javafx.scene.input.MouseButton
import org.junit.Before
import org.testfx.service.query.EmptyNodeQueryException

import scala.collection.mutable.ListBuffer

//Class containing utilities for testing the wall snap strategy
abstract class WallSnapTest extends GuiTest {
  //Repopulate a new House before each test
  @Before
  def resetHouse(): Unit = {
    House.resetCurrent(new House())
   HouseUtil.populateHouse(this)
  }

  /**
    * Moves the mouse by the given delta's, records the positions of the tmpFigure and its rotations along the way
    * @param deltas ListBf with mouse-delta's
    * @param tmpFigure The tmpFigure who's position and rotation gets recorded
    * @return tuple with tmpFigure positions and tmpFigure rotations
    */
  def recordPositions(deltas:ListBuffer[(Double, Double)], tmpFigure: WallSnapType): (ListBuffer[(Double, Double)], ListBuffer[Double]) = {
    //record tmpFigures positions along the way
    val recordedP = ListBuffer[(Double, Double)]()
    //record tmpFigure rotations
    val recordedR = ListBuffer[Double]()

    deltas.foreach( delta => {
      moveBy(delta._1, delta._2)
      recordedP += tmpFigure.getXY
      recordedR += tmpFigure.getRotate
    })

    (recordedP, recordedR)
  }

  /**
    * Tests if the position and rotation of TmpFigure match their expected values.
    * @param pos Measured position
    * @param rot Measured rotation
    * @param correctPos Expected position
    * @param correctRot Expected rotation
    */
  def assertTmpCorrect(pos: (Double, Double), rot: Double, correctPos: (Double, Double), correctRot: Double): Unit = {
    val (x, y) = pos
    val (p, q) = correctPos
    assert(x == p, s"x of tmpFigure is wrong: expected $p, received $x")
    assert(y == q, s"y of tmpFigure is wrong: expected $q, received $y")
    assert(rot == correctRot, s"rotation of tmpFigure is wrong: expected $correctRot, received $rot")
  }

  //Fields used for overlapping tests
  val button_query: String //which toolbar button activates the strategy
  val tmpFig_query: String //locate its tmpFigure
  //Drawing a new shape should end up here
  def getLatestShape: WallSnapType
  //Query for retrieving first shape drawn
  val firstShape_query: String

  @Test(expected = classOf[EmptyNodeQueryException])
  //In an empty house, the strategy should display no tmpFigure
  def emptyHouse(): Unit = {
    //reset house
    House.resetCurrent(new House())

    clickOn(button_query)
    moveBy(100,0) //strategy starts displaying tmpFigure when mouse enters canvas
    moveBy(-100,0)

    //Query should fail
    lookup(tmpFig_query).query()
  }

  //delta-moves used for testing
  val deltas: ListBuffer[(Double, Double)] = ListBuffer[(Double, Double)](
    (100, 200), //move alongside walls (arbitrary data)
    (10, 10),
    (100, -150), //move upwards to test jumping to other room
    (150, -200),
    (50, -50),    //test flip on wall (do nothing for windows, flips image for doors)
    (-300, 0), //test rotation on polygon rooms
    (-20, -60)) //test if tmpFig stays in corner of this room (ie end-point of closest wall)
  //TmpFigure should obtain the following positions and rotations
  val correctPositions: ListBuffer[(Double, Double)]
  val correctRotations: ListBuffer[Double]

  @Test
  //Tests if the tmpFigure follows the position of mouse correctly.
  //It should attach to the closest wall near the mouse and update if the mouse moves, at the point closest to the mouse and rotate to the rotation of this wall
  def moveMouse() {
    clickOn(button_query) //enable strategy
    moveBy(100,0) //only start displaying tmpFigure when mouse enters canvas, little cheat necessary for lookup
    moveBy(-100,0)

    //Locate tmpFigure
    val tmpFigure: WallSnapType = lookup(tmpFig_query).query().asInstanceOf[WallSnapType]

    //Move the mouse and record the position/rotation of tmpFigure along the way
    val (positions, rotations) = recordPositions(deltas, tmpFigure)

    //test if recorded positions and rotations match their expected values
    for (((pos, correctPos), (rot, correctRot)) <- (positions zip correctPositions) zip (rotations zip correctRotations)) {
      assertTmpCorrect(pos, rot, correctPos, correctRot)
    }
  }

  //Position where new figures get drawn
  //Delta to reach this position from the tool button
  val posX = 400
  val posY = 200

  //Places a new door/window (instance of tmpFigure).
  private def doPlace(): Unit = {
    clickOn(button_query)

    moveBy(posX, posY)
    press(MouseButton.PRIMARY).release(MouseButton.PRIMARY) //should create a new door/window at location of tmpFigure
  }

  @Test
  //Tests if clicking creates a new tmpFigure at the correct position and rotation
  def place(): Unit = {
    doPlace() //place it

    val tmpFigure: WallSnapType = lookup(tmpFig_query).query().asInstanceOf[WallSnapType]

    val newShape: WallSnapType = lookup(firstShape_query).query().asInstanceOf[WallSnapType]
    //sanity check, see if this shape is correct (through unit testing we know that a new Door/Window is added to the house, which should contain this shape)
    require(newShape == getLatestShape, "faulty query in test for new door image")
    //save a copy for later
    val oldPos = newShape.getXY.copy()
    val oldRot = newShape.getRotate

    //test if the positions and rotation match (the move-tests already established that tmpFigure follows the mouse correctly)
    assertTmpCorrect(newShape.getXY, newShape.getRotate, tmpFigure.getXY, tmpFigure.getRotate)

    //If the user now moves the mouse, the new image should remain in place while the tmpFigure moves
    moveBy(100, 0)
    assertTmpCorrect(newShape.getXY, newShape.getRotate, oldPos, oldRot)
  }

  //Offset for mouse to click on the image of locating it
  val imgOffset: (Double, Double) = (0, 0)
  //Move image by this much
  val moveOffset: (Double, Double) = (100, 100)
  //Should obtain this location after moving
  val newPos: (Double, Double)

  @Test
  //Tests if a drawn door/window can be moved (also tests if selecting a different tool resets tmpFigure).
  def move(): Unit = {
    doPlace() //place it

    //select move tool
    clickOn("#line_pointer")

    //selecting a different tool should remove tmpFigure
    var tmpFound: Boolean = true
    try {
      lookup(tmpFig_query).query().asInstanceOf[WallSnapType]
    } catch {
      case e: EmptyNodeQueryException => tmpFound = false
      case _ => tmpFound = true
    }
    assert(!tmpFound, "temporary figure is still drawn even if another tool is selected")

    //goto the position of the new door/window
    moveTo(firstShape_query)

    //apply some offset (door images are somewhat tricky and only move when clicked on a non transparent part)
    moveBy(imgOffset._1, imgOffset._2)

    //drag the new instance to a different location
    press(MouseButton.PRIMARY).moveBy(moveOffset._1, moveOffset._2).release(MouseButton.PRIMARY)

    //test if new figure has moved
    val newShape: WallSnapType = lookup(firstShape_query).query().asInstanceOf[WallSnapType]
    assertTmpCorrect(newShape.getXY, newShape.getRotate, newPos, newShape.getRotate)
  }
}
