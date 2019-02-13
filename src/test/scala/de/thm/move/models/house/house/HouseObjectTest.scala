package de.thm.move.models.house.house

import de.thm.move.MoveSpec
import de.thm.move.models.house.{PolygonRoom, RectangleRoom}
import de.thm.move.models.house.House
import de.thm.move.views.shapes.{ResizablePolygon, ResizableRectangle}
import play.api.libs.json.Json

//Tests the House object, containing the currentHouse.
class HouseObjectTest extends MoveSpec{
  //Resets the currentHouse
  "resetCurrent" should "reset the current House to a new House" in {
    //Add some rooms to current House.
    House.current.addRoom(new PolygonRoom(new ResizablePolygon(List(5, 8, 7, 3))))
    House.current.addRoom(new RectangleRoom(new ResizableRectangle((58, 165), 48, 89)))
    House.current.addMeasurement(HouseTest.measurement)
    House.current.addWindow(HouseTest.window)
    House.current.addDoor(HouseTest.door)

    val newHouse = new House
    House.resetCurrent(newHouse)

    //New House should be empty
    assert(House.current == newHouse, "new house is not the new current")
    assert(House.current.rooms().isEmpty, "empty house contains rooms")
    assert(House.current.measurements().isEmpty, "empty house contains measurements")
    assert(House.current.windows().isEmpty, "empty house contains windows")
    assert(House.current.doors().isEmpty, "empty house contains doors")
  }

  "House fromJson" should "parse Json to a House" in {
    val jStr = """{"rooms":[{"room-type":"rectangle-room","shape":{"fill-color":"0x000000ff","fill-type":"Solid","border-color":null,"border-width":"1.0","border-type":"Solid","base":{"rotation":0,"scale-x":1,"scale-y":1,"points":[7,11,20,11,20,28,7,28]}}},{"room-type":"polygon-room","shape":{"fill-color":"0x000000ff","fill-type":"Solid","border-color":null,"border-width":"1.0","border-type":"Solid","base":{"rotation":0,"scale-x":1,"scale-y":1,"points":[19,23,29,31,37,41]}}}],"windows":[{"shape":{"fill-color":"0x000000ff","fill-type":"Solid","border-color":null,"border-width":"1.0","border-type":"Solid","base":{"rotation":0,"scale-x":1,"scale-y":1,"points":[80,80,90,80,90,100,80,100]}}}],"doors":[{"shape":{"rotation":0,"scale-x":1,"scale-y":1,"points":[0,0,50,0,50,47.5,0,47.5]}}],"measurements":[{"line-shape":{"fill-color":null,"fill-type":"Solid","border-color":"0x000000ff","border-width":"1.0","border-type":"Solid","base":{"rotation":0,"scale-x":1,"scale-y":1,"points":[20,20,100,100]}},"text-shape":{"rotation":0,"scale-x":1,"scale-y":1,"points":[20,20]},"measured-value":"512"}]}"""
    val house: House = Json.fromJson[House](Json.parse(jStr)).get

    //If the house can be transformed back into json, it is loaded-in correctly
    assert(Json.toJson(house).toString() == jStr, "house was not parsed correctly")
  }
}
