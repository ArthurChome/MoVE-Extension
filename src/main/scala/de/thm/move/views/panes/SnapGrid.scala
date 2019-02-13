/**
 * Copyright (C) 2016 Nicola Justus <nicola.justus@mni.thm.de>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.thm.move.views.panes

import de.thm.move.Global.config
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.shape.Line
import de.thm.move.types._
import de.thm.move.implicits.FxHandlerImplicits._
import de.thm.move.util.GeometryUtils

/**
  * Creates a new grid with the size of the given topPane.
  * @param cellSize Size of each cell
  * @param snapDistance How close must the shape be so that the snapping mode is activate
  * */
class SnapGrid(topPane:Pane, val cellSize:Int) extends Pane with SnapLike {

  val verticalLineId = "vertical-grid-line"
  val horizontalLineId = "horizontal-grid-line"

  val gridVisibleProperty = new SimpleBooleanProperty(true)
  val snappingProperty = new SimpleBooleanProperty(false)

  /** Minimum distance to the intersection of the grid to enable snapping. */
  var snapDistance:Int = config.getInt("grid-snap-grid").getOrElse(100)

  setPickOnBounds(false)
  getStyleClass.add("snap-grid-pane")

    //make this pane the same size as topPane
  prefHeightProperty.bind(topPane.prefHeightProperty)
  prefWidthProperty.bind(topPane.prefWidthProperty)
  minHeightProperty.bind(topPane.minHeightProperty)
  minWidthProperty.bind(topPane.minWidthProperty)
  maxHeightProperty.bind(topPane.maxHeightProperty)
  maxWidthProperty.bind(topPane.maxWidthProperty)

  gridVisibleProperty.addListener { (oldB:java.lang.Boolean,newB:java.lang.Boolean) =>
    getChildren.clear()
    if(newB) {
      val horizontalLines = recalculateHorizontalLines(getHeight)
      val verticalLines = recalculateVerticalLines(getWidth)
      getChildren.addAll(horizontalLines:_*)
      getChildren.addAll(verticalLines:_*)
    }
  }

  heightProperty().addListener { (_:Number, newH:Number) =>
    if(gridVisibleProperty.get) {
      val newLines = recalculateHorizontalLines(newH.doubleValue())
      getChildren.removeIf { node:Node => node.getId == horizontalLineId }
      getChildren.addAll(newLines: _*)
    }
  }
  widthProperty().addListener { (_:Number, newW:Number) =>
    if(gridVisibleProperty.get) {
      val newLines = recalculateVerticalLines(newW.doubleValue())
      getChildren.removeIf { node:Node => node.getId == verticalLineId }
      getChildren.addAll(newLines:_*)
    }
  }

  def recalculateHorizontalLines(height:Double): Seq[Line] =
    for(i <- 1 to (height/cellSize).toInt) yield {
      val line = newGridLine
      line.setId(horizontalLineId)
      line.setStartX(0)
      line.endXProperty().bind(widthProperty())
      val y = i*cellSize
      line.setStartY(y)
      line.setEndY(y)
      line
    }

  def recalculateVerticalLines(width:Double): Seq[Line] =
    for(i <- 1 to (width/cellSize).toInt) yield {
      val line = newGridLine
      line.setId(verticalLineId)
      line.setStartY(0)
      line.endYProperty().bind(heightProperty())
      val x = i*cellSize
      line.setStartX(x)
      line.setEndX(x)
      line
    }

  def newGridLine: Line = {
    val line = new Line()
    line.getStyleClass.add("grid-line")
    line
  }

  private def withSnappingMode[A](fn: => Option[A]): Option[A] = {
    if(gridVisibleProperty.get && snappingProperty.get) fn
    else None
  }

  /**
    * Calculates for a given position the closest intersection to the grid.
    * If the distance between the position and the intersection is lesser than the snapDistance, return the intersection.
    * Else return None.
    * This is however calculated, if and only if, the snappingMode is activated.
    * @param origin The given point where the closest intersection of the grid must be found for.
    * @return An option: either the closest intersection to a given point, either None.
    */
  override def getClosestPosition(origin: Point): Option[Point] = withSnappingMode {
    val x = GeometryUtils.closestMultipleForced(cellSize, origin._1)
    val y = GeometryUtils.closestMultipleForced(cellSize, origin._2)
    val destination: Point = (x, y)
    val distance = GeometryUtils.distance(origin, destination)
    if (distance < snapDistance)
      Some(destination)
    else
      None
  }

  def setCellSize(size:Int):SnapGrid =
    new SnapGrid(topPane, size)

  def setSnapDistance(distance:Int): Unit =
    snapDistance = distance
}
