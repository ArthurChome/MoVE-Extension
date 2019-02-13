package de.thm.move.guitest

import java.awt.{MouseInfo, Robot}

import javafx.scene.input.{KeyCode, MouseButton}
import org.junit.{Ignore, Test}
import org.junit.Assert.assertEquals
import de.thm.move.GuiTest
import de.thm.move.SaveUtil

object DrawPolygonRoomTest {
  /**
    * Draws a polygonRoom. (expects that the polygon-room-btn is not pressed)
    * @param mouseMovements A list of tuples describing how the mouse should move before creating another vertex
    *                       (hor_ofs, ver_ofs). Note that the first offset moves, starting from the button.
    * @param test The text-context.
    */
  def drawRoom(mouseMovements: List[(Double, Double)], test: GuiTest): Unit = {
    test.clickOn("#polygon_room_btn")
    mouseMovements.foreach(delta => test.moveBy(delta._1, delta._2).press(MouseButton.PRIMARY).release(MouseButton.PRIMARY))
  }
}

class DrawPolygonRoomTest extends GuiTest {
  import DrawPolygonRoomTest._

  /**
    * Creates a new file, draws a rectangle and checks its colour
    */
  @Test
  def drawPolygonRoom(): Unit = {
    clickOn("File").clickOn("New file")
    `type`(KeyCode.ENTER)

    drawRoom(List((100, 0), (20, 40), (60, 0), (20, -40), (-20, -40), (-60, 0), (-20, 40)), this)
    
    moveBy(40,0)
    val middlePR = MouseInfo.getPointerInfo.getLocation
    val pixelColor = new Robot().getPixelColor(middlePR.x, middlePR.y)
    assertEquals(255, pixelColor.getRed)
    assertEquals(0, pixelColor.getGreen)
    assertEquals(0, pixelColor.getBlue)
  }

  def savePolygonRoom(): Unit = {
    drawPolygonRoom()
    //SaveUtil.saveSnapshot(scene, "wilmaDrawPolygonRoomTest.png")
    assert(true)
    //assert(SaveUtil.currentSnapshotEqualsFile(scene, "DrawPolygonRoomTest.png"))
  }
}
