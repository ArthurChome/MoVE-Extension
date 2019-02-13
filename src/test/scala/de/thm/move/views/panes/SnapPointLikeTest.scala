package de.thm.move.views.panes

import de.thm.move.GuiTest
import de.thm.move.types.Point
import de.thm.move.models.house.{House, Room}
import javafx.scene.input.MouseButton
import org.junit.Test

class SnapPointLikeTest extends GuiTest {


  def containsDuplicates(points: List[Point]): Boolean = { /**
    * Returns true if and only if, the given list of points contains duplicates.
    */
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
    * Draws two rectangle and snaps its points while testing the base snapping distance
    **/
  @Test def snapPointRectangles(): Unit = {
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
    assert(containsDuplicates(points), "Snap to Point did not work, these rectangles should have points in common!")
  }

  /**
    * Draws a rectangle of size 200,200 and moves it around the canvas along the grid
    **/
  @Test def snapPointPolygons(): Unit = {

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
    assert(containsDuplicates(points), "Snap to Point did not work, these polygons should have points in common!")
  }
}
