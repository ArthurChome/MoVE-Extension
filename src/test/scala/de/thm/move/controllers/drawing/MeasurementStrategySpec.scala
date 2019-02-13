package de.thm.move.controllers.drawing

import de.thm.move.MoveSpec
import de.thm.move.controllers.DrawPanelCtrl
import de.thm.move.types.Point
import de.thm.move.views.panes.DrawPanel
import de.thm.move.views.shapes.ResizableLine
import javafx.scene.Group
import javafx.scene.input.{InputEvent, MouseButton, MouseEvent}
import javafx.embed.swing.JFXPanel
import javafx.scene.input.PickResult
import scala.collection.mutable


class MeasurementStrategySpec extends MoveSpec{

  /**
   * The measurement strategy under test
   *
   * Every method in the tested class has its own unit test.
   **/
  val jfxPanel = new JFXPanel
  def dummyHandler(ev:InputEvent): Unit = {}
  val drawPanel = new DrawPanel()
  val tmpFigure = new ResizableLine((0,0), (0,0), 0)
  val emptyPointBuffer = mutable.ListBuffer[Point]()
  val drawPanelCtrl = new DrawPanelCtrl(drawPanel,dummyHandler)


  val pickResult = new PickResult(null, 0, 0)

  /**Variable with a mouse event: clicking the primary mouse button */
  var mouseClickedEventPrimary = new MouseEvent(null, null, MouseEvent.MOUSE_PRESSED, 3, 3, 0,
    0, MouseButton.PRIMARY, 1, false, false, false,
    false, true,  false, false,
    false, false, false, pickResult);

  /** Variable with a mouse event: clicking the secondary mouse button */
  var mouseClickedEventSecondary = new MouseEvent(null, null, MouseEvent.MOUSE_PRESSED, 3, 3, 0,
    0, MouseButton.SECONDARY, 1, false, false, false,
    false, false, false, true,
    false, false, true, pickResult);

  /** Variable with a mouse event: releasing a button */
  var mouseReleasedEvent = new MouseEvent(null, null, MouseEvent.MOUSE_RELEASED, 3, 3, 0,
    0, MouseButton.SECONDARY, 1, false, false, false,
    false, false, false, true,
    false, false, true, pickResult);


  /**
   * The Measurement Strategy.
   * It uses the State Pattern: this means it contains to objects (firstClickBehaviour & secondClickBehaviour)
   * that contain all behaviour and get interchanged to show different behaviour at the first and second click.
   */
  private val strategy = new MeasurementStrategy(drawPanelCtrl)

  /**
   * Measurement Draw functions
   *
   * These functionalities are universal for both the first -and second-click behaviour.
   * We will be using the first-click behaviour since we cannot instantiate an abstract class.
   * */
  it should "reset the temporal figure in the firstclick behaviour but also by consequence in the second click" in {
    strategy.firstClickBehaviour.reset() should be ()
  }

  "The first click behaviour's temporal figure" should "also be the second click's figure and vice versa" in {
    strategy.firstClickBehaviour.tmpFigure should be (strategy.secondClickBehaviour.tmpFigure)
  }

  it should "reset the line (tmpFigure)" in {
    strategy.firstClickBehaviour.resetLine(0.0, 5.0) should be ()
  }

  "Procedure to generate a measurement line" should "give back a Group (java.fx.scene)" in {
    strategy.firstClickBehaviour.generateMeasurementLine(0, 5, 0, 5) shouldBe a [Group]
  }

  /**
  First click behaviour specific functions:
  Since the first click defines the beginning of a measurement line,
  it speaks for itself that the function 'setBeginLine' is part of the first-click behaviour

  Both the second and first-click behaviour have a concrete implementation of the abstract dispatchEvent procedure.
    */

  "First click behaviour object" should "set the beginning of the line (tmpFigure)" in {
    strategy.firstClickBehaviour.setBeginLine(0.0, 5.0) should be ()
  }

  "First click behaviour object" should "show first click behaviour" in {
    strategy.firstClickBehaviour.dispatchEvent(mouseClickedEventPrimary) should be ()
  }

  /**
   * Second click behaviour specific functions
   *
   * The second click behaviour sets the end of the line and therefore has a procedure 'setEndLine'
   * Both the second and first-click behaviour have a concrete implementation
   * of the abstract dispatchEvent procedure.
  */

  it should "set the end of the line (tmpFigure)" in {
    strategy.secondClickBehaviour.setEndLine(0.0, 5.0) should be ()
  }

  "Second click behaviour object" should "show second click behaviour" in {
    strategy.secondClickBehaviour.dispatchEvent(mouseClickedEventPrimary) should be ()
  }

  "Clicking the primary button a first time" should "show first click behaviour" in {
    strategy.dispatchEvent(mouseClickedEventPrimary) should be ()
  }

  "Clicking the primary button a second time" should "show second click behaviour" in {
    strategy.dispatchEvent(mouseClickedEventPrimary) should be ()
  }

  "Clicking the primary button a third time" should "result in first click behaviour (again)" in {
    strategy.dispatchEvent(mouseClickedEventPrimary) should be ()
  }

  "Clicking the secondary button" should "do nothing" in {
    strategy.dispatchEvent(mouseClickedEventSecondary) should be ()
  }

  "Releasing a button" should "do nothing" in{
    strategy.dispatchEvent(mouseReleasedEvent) should be ()
  }

  "Calling the reset() method" should "reset the temporal figure" in {
    strategy.reset() should be ()
  }


}