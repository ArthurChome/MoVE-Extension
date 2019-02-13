package de.thm.move.guitest

import de.thm.move.GuiTest
import de.thm.move.types.Point
import de.thm.move.models.house.{House, Measurement, Room}
import javafx.scene.input.{KeyCode, MouseButton}
import org.junit.Test

class SnapPointLikeTest extends GuiTest {

  /**
    * Returns true if and only if, the given list of points contains duplicates.
    */
  def containsDuplicates(points: List[Point]): Boolean = {
    var duplicates = false
    for (point <- points){
      var removed = points.filter(_ != point)
      if (points.size - 1 > removed.size){
        duplicates = true
      }
    }
    duplicates
  }

  /**
    * Retrieve points of the rooms in the current house to assert the usage of Snap to Point.
    */
  def getPoints: List[Point] = {
    var points = List[Point]()
    House.current.rooms().foreach((room: Room) => {
      points ++= room.shape.localVertexes
    })
    points
  }

  /**
    * Retrieve points of the measurements in the House
    */
  def getMeasurementPoints: List[Point] = {
    var points = List[Point]()
    House.current.measurements().foreach((measurement: Measurement) => {
      points ++= measurement.line.localVertexes
    })
    println(points)
    points
  }

  def enableSnapToPoint(): Unit = {
    clickOn("View")
    clickOn("Enable Snap-to-Point")
  }

  /**
    * Draws a polygon given a list of mouse-movements.
    */
  def makePolygon(mouseMovements: List[(Double, Double)]): Unit = {
    press(MouseButton.PRIMARY).release(MouseButton.PRIMARY)
    mouseMovements.foreach(delta => {
      moveBy(delta._1, delta._2)
      press(MouseButton.PRIMARY).release(MouseButton.PRIMARY)
    })
  }

  /**
    * Draws two rectangle and snaps its points while testing the base snapping distance.
    * Success if rectangles share a vertex.
    **/
  @Test def snapPointRectangles(): Unit = {
    enableSnapToPoint()

    clickOn("#rectangle_room_btn")
    moveBy(230, -23)
    press(MouseButton.PRIMARY).moveBy(100, 100).release(MouseButton.PRIMARY)
    moveBy(100, -100)
    press(MouseButton.PRIMARY).moveBy(100, 100).release(MouseButton.PRIMARY)

    clickOn("#line_pointer")
    moveBy(520, 83)
    press(MouseButton.PRIMARY).moveBy(-300, 130).release(MouseButton.PRIMARY)
    press(MouseButton.PRIMARY).moveBy(0, -20).release(MouseButton.PRIMARY)
    press(MouseButton.PRIMARY).moveBy(0, -2).release(MouseButton.PRIMARY)
    press(MouseButton.PRIMARY).moveBy(100, 5).release(MouseButton.PRIMARY)

    val points = getPoints
    assert(containsDuplicates(points), "Snap to Point did not work, these rectangles should have a shared vertex")
  }

  /**
    * Draws a rectangle of size 200,200 and moves it around the canvas along the grid.
    * Success if polygons share a vertex.
    **/
  @Test def snapPointPolygons(): Unit = {
    enableSnapToPoint()

    clickOn("#polygon_room_btn")
    moveBy(200, -50)
    makePolygon(List((170, 0), (0, 160), (-90, 40), (-100, -100), (20, -100)))
    moveTo("#polygon_room_btn")
    moveBy(400, -50)
    makePolygon(List((170, 0), (0, 160), (-90, 40), (-100, -100), (20, -100)))

    clickOn("#line_pointer")
    moveTo("#polygon_room_btn") // to easily move to polygon 2
    moveBy(405, -45) // move near point of polygon 2

    press(MouseButton.PRIMARY).moveBy(-30, 0).release(MouseButton.PRIMARY)
    press(MouseButton.PRIMARY).moveBy(5, 165).release(MouseButton.PRIMARY)

    val points = getPoints
    assert(containsDuplicates(points), "Snap to Point did not work, these polygons should have a shared vertex")
  }

  /**
    * Draws a rectangle and a measurement tool. Snap the two points together.
    * Success if they share a vertex.
    **/
  @Test def snapPointMeasurement(): Unit = {
    enableSnapToPoint()

    clickOn("#rectangle_room_btn")
    moveBy(230, -23)
    press(MouseButton.PRIMARY).moveBy(100, 100).release(MouseButton.PRIMARY)

    clickOn("#measurement_tool_btn")
    moveBy(175, 33)

    press(MouseButton.PRIMARY).release(MouseButton.PRIMARY)
    moveBy(50, 0)
    press(MouseButton.PRIMARY).release(MouseButton.PRIMARY)

    clickOn("#line_pointer")
    moveTo("#measurement_tool_btn")
    moveBy(200, 33)

    press(MouseButton.PRIMARY).moveBy(0, -35).release(MouseButton.PRIMARY)

    val points = getPoints ++ getMeasurementPoints
    assert(containsDuplicates(points), "Snap to Point did not work, the rectangle and measurement line should have a shared vertex")
  }

  /**
    * Enable Snap to Point.
    * Draw two rectangles of size 200,200 next to each other (no shared points).
    * Snap one rectangle to another
    * Disable Snap to Point, drag rectangle within snapping distance.
    * Success if they do NOT share a vertex.
    **/
  @Test def snapPointDisable(): Unit = {
    enableSnapToPoint()
    clickOn("#rectangle_room_btn")
    moveBy(440, -23)
    press(MouseButton.PRIMARY).moveBy(100, 100).release(MouseButton.PRIMARY)

    moveBy(-300, -100)
    press(MouseButton.PRIMARY).moveBy(100, 100).release(MouseButton.PRIMARY)

    clickOn("#line_pointer")
    moveBy(290, 85)
    press(MouseButton.PRIMARY).moveBy(93, 0).release(MouseButton.PRIMARY)
    enableSnapToPoint()
    moveTo("#line_pointer")
    moveBy(355, 85)
    press(MouseButton.PRIMARY).moveBy(-3, 0).release(MouseButton.PRIMARY)


    val points = getPoints
    assert(!containsDuplicates(points), "Snap Point was not disabled, these two rectangles should not have a shared vertex")
  }

  /**
    * Enable Snap to Point.
    * Draw two parallel, but not touching rectangles with a distance of 20 in between.
    * Try to snap on to the other, will not snap as snapping distance is by default 10.
    * Set snapping distance to 20. Snap on on the other.
    * Success if they share a vertex.
    **/
  @Test def snapPointSensitivy(): Unit = {
    enableSnapToPoint()
    clickOn("#rectangle_room_btn")
    moveBy(240, -23)
    press(MouseButton.PRIMARY).moveBy(100, 100).release(MouseButton.PRIMARY)
    moveBy(20, -100)
    press(MouseButton.PRIMARY).moveBy(100, 100).release(MouseButton.PRIMARY)

    clickOn("#line_pointer")
    moveBy(260, 80)
    press(MouseButton.PRIMARY).release(MouseButton.PRIMARY)

    clickOn("View")
    clickOn("Change sensitivity Snap-to-Point")

    moveTo("Size in px")
    moveBy(80, 0).press(MouseButton.PRIMARY).release(MouseButton.PRIMARY)
    sleep(2000)
    `type`(KeyCode.BACK_SPACE)
    `type`(KeyCode.BACK_SPACE)
    `type`(KeyCode.DIGIT3)
    sleep(1000)
    `type`(KeyCode.DIGIT0)
    sleep(3000)
    `type`(KeyCode.ENTER)

    clickOn("#line_pointer")
    moveBy(260, 80)
    press(MouseButton.PRIMARY).release(MouseButton.PRIMARY)

    val points = getPoints
    assert(containsDuplicates(points), "Note this test requires Qwerty (keyboard input) => " +
      "Snap Point sensitivity was not changed, these two rectangles should have a shared vertex")
  }
}
