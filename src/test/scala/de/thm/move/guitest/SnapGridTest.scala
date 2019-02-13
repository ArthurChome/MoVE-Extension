package de.thm.move.guitest

import de.thm.move.GuiTest
import de.thm.move.types.Point
import de.thm.move.models.house.{House, Room}
import javafx.scene.input.MouseButton
import org.junit.Test


class SnapGridTest extends GuiTest {


  // NOTE: all tests move elements by one off, if snap grid doesnt work, the results is always 1 off
  // All points of shapes should thus be divisible by 10.

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
    * Retrieve points of the rooms in the current house to assert the usage of Snap to Grid.
    */
  def getPoints: List[Point] = {
    var points = List[Point]()
    House.current.rooms().foreach((room: Room) => {
      points ++= room.shape.localVertexes
    })
    points
  }

  /**
    * Determines if the snap-to-grid moves along the grid
    */
  def pointIsOnGrid(point: Point): Boolean = {
    point._1 % 10 == 0 && (point._2 % 10 == 0)
  }

  def enableSnapToGrid(): Unit = {
    clickOn("View")
    clickOn("Enable Snap-to-Grid")
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
    * Draws a rectangle of size 200,200 and moves it around the canvas along the grid
    **/
  @Test def snapGridRectangle(): Unit = {

    enableSnapToGrid()

    clickOn("#rectangle_room_btn")
    moveBy(231, -10)
    press(MouseButton.PRIMARY).moveBy(200, 200).release(MouseButton.PRIMARY)
    clickOn("#line_pointer")
    moveBy(240, 88)

    press(MouseButton.PRIMARY).moveBy(301, 0).release(MouseButton.PRIMARY)
    press(MouseButton.PRIMARY).moveBy(0, 101).release(MouseButton.PRIMARY)
    press(MouseButton.PRIMARY).moveBy(-199, 0).release(MouseButton.PRIMARY)

    val points = getPoints
    assert(points.forall(p => pointIsOnGrid(p)), "all points of this rectangle should be on positions divisible by 10")
  }

  /**
    * Draws a polygon and moves it around the canvas along the grid.
    **/
  @Test def snapGridPolygon(): Unit = {

    enableSnapToGrid()

    clickOn("#polygon_room_btn")
    moveBy(320, -50)
    makePolygon(List((170, 0), (0, 160), (-90, 40), (-100, -100), (20, -100)))

    clickOn("#line_pointer")
    moveBy(350, 100)
    press(MouseButton.PRIMARY).moveBy(101, 0).release(MouseButton.PRIMARY)
    moveBy(140, 130)
    press(MouseButton.PRIMARY).moveBy(-169, -70).release(MouseButton.PRIMARY)
    moveBy(0, -10)
    press(MouseButton.PRIMARY).moveBy(0, 91).release(MouseButton.PRIMARY)

    val points = getPoints
    assert(points.forall(p => pointIsOnGrid(p)), "all points of this rectangle should be on positions divisible by 10")
  }

  /**
    * Draws two rectangles of size 200,200 on top of each other.
    * One is drawn, moved without SnapToGrid, then SnapToGrid is disabled.
    * Finally the other rectangle is drawn and its moved with SnapToGrid.
    * The two rectangles should not share common points.
    **/
  @Test def snapGridDisable(): Unit = {
    clickOn("#rectangle_room_btn")
    moveBy(230, -23)
    press(MouseButton.PRIMARY).moveBy(200, 200).release(MouseButton.PRIMARY)
    clickOn("#line_pointer")
    moveBy(235, 85)
    press(MouseButton.PRIMARY).moveBy(170, 0).release(MouseButton.PRIMARY)

    enableSnapToGrid()

    clickOn("#rectangle_room_btn")
    moveBy(230, -23)
    press(MouseButton.PRIMARY).moveBy(200, 200).release(MouseButton.PRIMARY)
    clickOn("#line_pointer")
    moveBy(235, 85)
    press(MouseButton.PRIMARY).moveBy(170, 0).release(MouseButton.PRIMARY)

    val points = getPoints
    assert(!containsDuplicates(points), "Snap Grid was not disabled, these two rectangles should not have the same position")

  }
}
