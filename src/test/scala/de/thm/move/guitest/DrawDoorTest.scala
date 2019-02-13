package de.thm.move.guitest

import de.thm.move.GuiTest
import de.thm.move.controllers.drawing.WallSnapping.WallSnapStrategy.WallSnapType
import de.thm.move.models.house.House
import javafx.scene.layout.BorderPane
import org.junit.{Before, Test}

import scala.collection.mutable.ListBuffer

//Instantiate a test for moving/placing doors.
class DrawDoorTest extends WallSnapTest {
  override val button_query: String = "#door_btn"
  override val tmpFig_query: String = "#door"
  override val firstShape_query: String = "#door0"
  //Drawing a new shape should end up here
  override def getLatestShape: WallSnapType = House.current.doors().head.shape
  //The recorded values whilst moving the mouse should equal these
  val correctPositions: ListBuffer[(Double, Double)] = ListBuffer[(Double, Double)]((43.0,407.0), (53.0,407.0), (116.75,288.25), (303.0,107.0), (353.0,59.5), (41.757354736328125,61.62867736816406), (41.757354736328125,61.62867736816406))
  val correctRotations: ListBuffer[Double] = ListBuffer[Double](180.0, 180.0, 90.0, 180.0, 0.0, -63.43494882292201, -63.43494882292201)
  override val imgOffset: (Double, Double) = (22, 0) //select img by clicking on a non transparent part
  override val newPos: (Double, Double) = (538.2749326927228,442.5025361624871) //position of door after moving
}
