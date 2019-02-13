/*
This trait implements all essential functions
for snap-to-point.
 */


package de.thm.move.views.panes

import de.thm.move.Global._
import de.thm.move.controllers.ChangeDrawPanelLike
import de.thm.move.types._
import de.thm.move.util.GeometryUtils
import de.thm.move.views.SelectionGroup
import de.thm.move.views.panes.SnapLike.Snapable
import de.thm.move.views.shapes.{ResizableShape, VertexLike}
import javafx.scene.Node

trait SnapPointLike{

  /** This variable is overridden with the current drawpanel. */
  var Like:ChangeDrawPanelLike

  /** Minimum distance to the shape to snap to the point. */
  var snapDistance: Int = config.getInt("grid-snap-point").getOrElse(10)

  /** Sets a new sensitivity value for the snap distance. */
  def setSensitivity(sensitivity:Int): Unit =
    snapDistance = sensitivity

  /**
    * Iterates over all shapes in the panel and retrieves all points that are not part of the given shape.
    * @param selected The shape which points are not to be included.
    * @return A list of all points in the panel, except for the points of the given shape.
    */
  def getRestPoints(selected: ResizableShape): List[Point] = {
    var restPoints = List[Point]()
    Like.getElements.foreach(f = {
      case shape: ResizableShape with VertexLike =>
        val shapePoints = shape.vertexes()
        if (shape != selected)
          restPoints = restPoints ::: shapePoints
      case shape: SelectionGroup =>
        shape.childrens.head match {
          case line: ResizableShape with VertexLike =>
            val linePoints = line.vertexes()
            if (line != selected)
              restPoints = restPoints ::: linePoints
        }
      case _ =>
    })
    restPoints
  }

  /**
    * Returns x/y-change in function of a pair of points.
    *   This pair is first determined by finding a combination of two points in two lists.
    *   This combination is closer to any other combination of points.
    * @param origins A list of points, where the first point a of the closest pair (a,b) s found.
    * @param destinations A list of points, where the second point b of the closest pair (a,b) is found.
    * @return The x/y change to go from the first element of the pair to the other.
    */
  def snapPoint(origins: List[Point], destinations: List[Point]): Point =  {
    var xChange: Double = 0
    var yChange: Double = 0
    val pair: (Point, Point) = GeometryUtils.closestPair(origins, destinations)
    val origin = pair._1
    val destination = pair._2
    if (GeometryUtils.distance(origin, destination) < snapDistance) {
      xChange = destination._1 - origin._1 // destination x - origin x
      yChange = destination._2 - origin._2 // destination y - origin y
    }
    (xChange, yChange)
  }

  /**
    * Moves a shape to a point of another shape, if it's within the snapping distance of another shape.
    * @param group Group to move if a point is found within the snapping distance
    */
  def snapPointGroup(group: SelectionGroup): Unit = {
    val lineShape: ResizableShape = group.childrens.head
    var change: Point = (0, 0)
    lineShape match {
      case line: ResizableShape with VertexLike =>
        val linePoints: List[Point] = line.vertexes()
        val restPoints = getRestPoints(lineShape) // retrieve all points that are not points of the node
        if (restPoints.nonEmpty) {
          change = snapPoint(linePoints, restPoints)
        }
    }
    group.move(change)
  }

  /**
    * Moves a shape to a point of another shape, if it's within the snapping distance of another shape.
    * @param node Shape to move if a point is found within the snapping distance
    */
  def snapPointShape(node: Node with Snapable): Unit = {
    var change: Point = (0, 0)
    node match {
      case shape: ResizableShape with VertexLike =>
        val shapePoints = node.vertexes()
        val restPoints = getRestPoints(shape) // retrieve all points that are not points of the node
        if (restPoints.nonEmpty) {
          change = snapPoint(shapePoints, restPoints)
        }
    }
    node.move(change)
  }
}