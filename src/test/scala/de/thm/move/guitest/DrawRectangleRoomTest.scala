/**
  * Original copied from
  * https://gist.github.com/timmolderez/5af5adfa60e62989846750ccc01cb7a7
  */

package de.thm.move.guitest

import java.awt.{MouseInfo, Robot}

import javafx.scene.input.{KeyCode, MouseButton}
import org.junit.{Ignore, Test}
import org.junit.Assert._
import de.thm.move.GuiTest
import de.thm.move.SaveUtil
import de.thm.move.models.house.House

object DrawRectangleRoomTest {
  /**
    * Draws a rectangularRoom. (expects that the rectangle-room-btn is not pressed)
    * @param moveTopLeft The mouse is moved by this offset before creating the top-left point of the rectangle.
    * @param moveBotRight The mouse is dragged by this offset to create the bot-rigth point.
    * @param test The test context.
    */
  def drawRoom(moveTopLeft: (Double, Double), moveBotRight: (Double, Double), test: GuiTest): Unit = {
    test.clickOn("#rectangle_room_btn")
    test.moveBy(moveTopLeft._1, moveTopLeft._2)
    test.press(MouseButton.PRIMARY).moveBy(moveBotRight._1, moveBotRight._2).release(MouseButton.PRIMARY)
  }
}

class DrawRectangleRoomTest extends GuiTest {
  import DrawRectangleRoomTest._

  /**
    * Creates a new file, and draws a rectangle
    */
  @Test def drawRectangleRoom(): Unit = {
    clickOn("File")
    clickOn("New file")
    `type`(KeyCode.ENTER)
//    clickOn("OK")

    drawRoom((200, 0), (200, 200), this)

    moveBy(-100, -100)
    val loc = MouseInfo.getPointerInfo.getLocation
    val pixelColor = new Robot().getPixelColor(loc.x, loc.y)
    assertEquals(255, pixelColor.getRed)
    assertEquals(0, pixelColor.getGreen)
    assertEquals(0, pixelColor.getBlue)
  }

  @Test
  def rotateRectangleRoom(): Unit = {
    clickOn("File")
    clickOn("New file")
    `type`(KeyCode.ENTER)

    drawRoom((200, 0), (200, 200), this)
    // Mouse is at bottom right

    //remember location
    val botRight = MouseInfo.getPointerInfo.getLocation

    //select selection tool
    clickOn("#line_pointer")

    //goto middle of rectangle
    moveTo(botRight.x, botRight.y)
    moveBy(-100,-100)

    //doubleclick
    press(MouseButton.PRIMARY).release(MouseButton.PRIMARY)
    press(MouseButton.PRIMARY).release(MouseButton.PRIMARY)

    //return to remembered location
    moveTo(botRight.x, botRight.y)

    //drag point some way
    moveBy(0,-100)

    //make snapshot, remember
    val snapshotManualRotation = SaveUtil.makeSnapshot(this.scene)

    //new file
    clickOn("File")
    clickOn("New file")
    `type`(KeyCode.ENTER)

    //draw same rectangle
    drawRoom((200,0), (200, 200), this)

    //rotate by rectangle.rotate
    val rooms = House.current.rooms()
    rooms(1).shape.rotate(45)

    //make snapshot, compare with remembered snapshot (though how
    //we'll get the rectangle to rotate the right amount is a bit of a
    //mistery)
    val snapshotAutomaticRotation = SaveUtil.makeSnapshot(this.scene)
    assertTrue(SaveUtil.bufferedImagesEqual(snapshotManualRotation, snapshotAutomaticRotation))
  }
}
