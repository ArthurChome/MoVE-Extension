package de.thm.move.models.house.house

import de.thm.move.MoveSpec
import de.thm.move.views.SelectionGroup
import de.thm.move.models.house._
import de.thm.move.views.shapes._
import org.scalatest.BeforeAndAfterEach

object HouseTest {
  //Initialise test rooms
  val roomA = new RectangleRoom(new ResizableRectangle((7,11), 13, 17))
  val roomB = new PolygonRoom(new ResizablePolygon(List(19,23, 29,31, 37,41)))

  //Test Door
  val door = new Door(new ResizableRectangle((0,0), 50, 50))

  //Test Window
  val window = new Window(new ResizableRectangle((80, 80), 10, 20))

  //Test Measurement
  private val m_line = new ResizableLine((20,20),(100,100), 4)
  private val m_text = new ResizableText("512", 20, 20)
  private val m_group = new SelectionGroup(List(m_line, m_text))
  val measurement = new Measurement(m_line, m_text, m_group)

  //The correct coordinates of roomA's walls.
  val ARoomCoordinates = List(((7.0,11.0),(7.0,28.0)), ((7.0,11.0),(20.0,11.0)), ((20.0,11.0),(20.0,28.0)), ((20.0,28.0),(7.0,28.0)))
  //The correct coordinates of roomB's walls.
  val BRoomCoordinates = List(((19, 23), (37, 41)), ((19, 23), (29, 31)), ((29, 31), (37, 41)))

  //House used for testing
  var House = new House

  //Reset the House
  def reset(): Unit = {
    House = new House
  }

  //Clears and repopulates the House with test objects.
  def repopulate(): Unit = {
    reset()

    //Supply House
    House.addRoom(roomA)
    House.addRoom(roomB)
    House.addDoor(door)
    House.addWindow(window)
    House.addMeasurement(measurement)
  }
}

//Tests for the House class, each tests uses a new "clean" House.
class HouseTest extends MoveSpec with BeforeAndAfterEach {
  import HouseTest._

  override def beforeEach(): Unit = reset()

  "House" should "correctly add, return and remove multiple Rooms" in {
    assert(House.rooms().isEmpty, "House.rooms is not empty at the start of the test")
    House.addRoom(roomA)
    assert(House.rooms sameElements Array(roomA), "roomA was not added to House.rooms")
    House.addRoom(roomB)
    assert((House.rooms sameElements Array(roomA, roomB)) || (House.rooms sameElements Array(roomB, roomA)),
      "House.rooms does not return both rooms that were added to it")
    House.remRoom(roomA)
    assert(House.rooms sameElements Array(roomB), "roomB was not removed from House.rooms")
    House.remRoom(roomB)
    assert(House.rooms().isEmpty, "House.rooms is not empty after its contents are removed")
  }

  it should "correctly list all walls of its Rooms" in {
    assert(House.rooms().isEmpty, "House.rooms is not empty at the start of the test")
    House.addRoom(HouseTest.roomA)
    assert(House.walls == HouseTest.ARoomCoordinates, "House.walls does not correctly list rRoom.listWalls")
    House.addRoom(HouseTest.roomB)
    assert(House.walls == HouseTest.ARoomCoordinates ++ HouseTest.BRoomCoordinates ||
      House.walls == HouseTest.BRoomCoordinates ++ HouseTest.ARoomCoordinates,
      "House.walls does not return all walls of all rooms that were added to it")
    House.remRoom(HouseTest.roomA)
    assert(House.walls == HouseTest.BRoomCoordinates,
      "House.walls did not update correctly after the removal of a room")
    House.remRoom(HouseTest.roomB)
    assert(House.walls().isEmpty, "House.rooms is not empty after all rooms were removed")
  }

  "closestWall" should "return None if there are no walls" in {
    assert(House.closestWall(0, 0).isEmpty, "expected None, there are no walls")
  }

  "addDoor" should "add a Door to the House" in {
    House.addDoor(HouseTest.door)
    assert(House.doors() sameElements Array(HouseTest.door), "door is not correctly added to House")
  }

  "addWindow" should "add a Window to the House" in {
    House.addWindow(HouseTest.window)
    assert(House.windows() sameElements Array(HouseTest.window), "window is not correctly added to House")
  }

  "addMeasurement" should "add a Measurement to the House" in {
    House.addMeasurement(HouseTest.measurement)
    assert(House.measurements() sameElements Array(HouseTest.measurement))
  }
}