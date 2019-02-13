/**
 * Copyright (C) 2016 Nicola Justus <nicola.justus@mni.thm.de>
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.thm.move.views.panes

import javafx.scene.Node
import de.thm.move.types._
import de.thm.move.views.shapes.{MovableShape, VertexLike}
import de.thm.move.util.GeometryUtils

/** Represents elements that can calculate a distance for a snapping-mechanism to a specific element.
  *
  * E.G.: A grid with lines on which elements can get snapped (a.k.a. snap-to-grid).
  * */
trait SnapLike {

  /** Returns the point for the closest element to p. */
  def getClosestPosition(p:Point):Option[Point]
}

object SnapLike {

  //The type of "Snapable" nodes/shapes
  type Snapable = Node with VertexLike with MovableShape

  /**
    * This variable option index Point contains an index of a point in a list
    * This is the index to the point that will move in a snapGrid operation.
    * A point is chosen and updated in this variable.
    * We keep the index to a point in the list of points of the shape that is being moved,
    *      because points can move and multiple points can have the same values.
    * */
  var indexPoint: Option[Int] = None

  /** Prepares snap-to-grid. Calculates closest point to the mouse. This point is the one being moved on the grid. */
  def beforeSnap(snaplike:SnapLike, node:Snapable, mouse: Point): Unit = {
    val closestPoint = GeometryUtils.closestPointToPoint(mouse, node.vertexes())
    indexPoint = Some(node.vertexes().indexOf(closestPoint))
  }

  /** Complete snap-to-grid operation by resetting the indexPoint. */
  def afterSnap(): Unit = {
    indexPoint = None
  }

  /**
    * Checks if the mouse is within the bounds of the grid.
    * @param mouse The position of the mouse.
    * @return Returns true if and only if the mouse is within the bounds, or returns false.
    */
  def withinBounds(mouse: Point): Boolean = {
    mouse._1 > 0 && mouse._2 > 0
  }

  /** Applies snap-to-grid to the given node using the points of the node and the last position of the mouse. */
  def applySnapToGrid(snaplike:SnapLike, node:Snapable, mouse: Point): Unit = {
    if (withinBounds(mouse)){
      val delta = getSnapToGridDistance(snaplike, node.vertexes(), mouse)
      node.move(delta)
    }
  }

  /**
    * Returns the delta to move a point in function of the grid.
    * Calculates the closest grid intersection to the mouse (mouseDest).
    * If the mouse is close enough to the intersection, it will return a destination point.
    * Calculates the delta change in x and y to move the indexPoint to the found intersection.
    * If no intersection destination was found, return delta (0,0) -> no change.
    */
  def getSnapToGridDistance(snaplike:SnapLike, points: List[Point], mouse: Point):Point = {
    val mouseDest = snaplike.getClosestPosition(mouse)
    mouseDest match{
      case Some(dest) =>
        val origin = points(indexPoint.get)
        val xChange = dest._1 - origin._1
        val yChange = dest._2 - origin._2
        (xChange, yChange)
      case None =>
        (0, 0)
    }
  }
}