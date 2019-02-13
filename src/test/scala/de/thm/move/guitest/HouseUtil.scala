package de.thm.move.guitest

import de.thm.move.GuiTest
import de.thm.move.models.house.House
import play.api.libs.json.Json

/**
  * Object contains functions to draw and populate a House.
  */
object HouseUtil {
  //Aux functions to draw some rooms within the given test-context.
  def drawRect1(test: GuiTest): Unit = {
    DrawRectangleRoomTest.drawRoom((50,150), (100,150), test)
  }
  def drawRect2(test: GuiTest): Unit = {
    DrawRectangleRoomTest.drawRoom((200,0), (400,100), test)
  }
  def drawPoly1(test: GuiTest): Unit = {
    DrawPolygonRoomTest.drawRoom(List((100, 0), (20, 40), (60, 0), (20, -40), (-20, -40), (-60, 0), (-20, 40)), test)
  }
  def drawPoly2(test: GuiTest): Unit = {
    DrawPolygonRoomTest.drawRoom(List((500, 200), (50, 0), (40, 100), (-90, -100)), test)
  }

  //Populates the house with rooms.
  def populateHouse(test: GuiTest): Unit = {
    drawRect1(test)
    drawPoly1(test) //note that running two of the same drawRooms in a row will break, the tool button will unselect itself
    drawRect2(test)
    drawPoly2(test)
  }
}
