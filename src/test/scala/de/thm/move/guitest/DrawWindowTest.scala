package de.thm.move.guitest

import de.thm.move.controllers.drawing.WallSnapping.WallSnapStrategy.WallSnapType
import de.thm.move.models.house.House

import scala.collection.mutable.ListBuffer

class DrawWindowTest extends WallSnapTest {
  override val button_query: String = "#window_btn"
  override val tmpFig_query: String = "#window"
  override val firstShape_query: String = "#window0"
  //Drawing a new shape should end up here
  override def getLatestShape: WallSnapType = House.current.windows().head.shape
  //The recorded values whilst moving the mouse should equal these
  val correctPositions: ListBuffer[(Double, Double)] = ListBuffer[(Double, Double)]((43.0,402.0), (53.0,402.0), (93.0,278.0), (303.0,102.0), (353.0,102.0), (63.0,91.0), (63.0,91.0))
  val correctRotations: ListBuffer[Double] = ListBuffer[Double](-0.0, -0.0, 90.0, -0.0, -0.0, -63.43494882292201, -63.43494882292201)
  override val newPos: (Double, Double) = (543.0,431.0) //position of window after moving
}
