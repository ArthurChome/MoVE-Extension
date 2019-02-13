package de.thm.move.controllers.drawing

import de.thm.move.GuiTest
import javafx.scene.input.MouseButton
import org.junit.Test

class MeasurementStrategyTest extends GuiTest {


  //First interface test: will activate the measurement tool feature by pressing the button on the side menu
  // and deactivate it afterwards.
  @Test def clickMeasurementTool(): Unit = {
    clickOn("#measurement_tool_btn")
    //Wait for 5 seconds (expressed in milliseconds).
    sleep(5000)
    clickOn("#measurement_tool_btn")
  }

  //Select the measurement tool and draw a measurement line.
  //Wait a while before you unselect the measurement tool.
  @Test def drawMeasurementLine(): Unit = {
    clickOn("#measurement_tool_btn")
    moveBy(200, 0)
    press(MouseButton.PRIMARY)
    //You also have to specify that a button must be released.
    release(MouseButton.PRIMARY)
    moveBy(300, 100)
    press(MouseButton.PRIMARY)
    release(MouseButton.PRIMARY)
    //Wait for 5 seconds
    sleep(5000)
    clickOn("#measurement_tool_btn")
  }

  //Test the change appearance feature:
  @Test def changeAppearance(): Unit = {

    //Draw a rectangle room
    clickOn("#rectangle_room_btn")
    moveBy(50, 50)
    press(MouseButton.PRIMARY)
    moveBy(200, 200)
    release(MouseButton.PRIMARY)
    sleep(2000)

    //Draw a measurement line
    clickOn("#measurement_tool_btn")
    moveBy(500, -50)
    press(MouseButton.PRIMARY)
    release(MouseButton.PRIMARY)
    moveBy(100, -50)
    press(MouseButton.PRIMARY)
    release(MouseButton.PRIMARY)
    sleep(2000)

    //Change the color for the measurement line
    clickOn("#strokeColorPicker")
    moveBy(20, 150)
    press(MouseButton.PRIMARY)
    release(MouseButton.PRIMARY)
    sleep(2000)

    //Draw another line
    press(MouseButton.PRIMARY)
    release(MouseButton.PRIMARY)
    moveBy(100, 200)
    press(MouseButton.PRIMARY)
    release(MouseButton.PRIMARY)
    sleep(2000)

    //Draw a line on the shape
    moveBy(-300, -130)
    sleep(2000)
    press(MouseButton.PRIMARY)
    release(MouseButton.PRIMARY)
    moveBy(150, 150)
    press(MouseButton.PRIMARY)
    release(MouseButton.PRIMARY)
    sleep(2000)
  }
}