/**
 * Copyright (C) 2016 Nicola Justus <nicola.justus@mni.thm.de>
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.thm.move.views.shapes

import javafx.scene.shape.Rectangle
import de.thm.move.types._

object ResizableRectangle {
  /**
    * Creates a resizable rectangle from a list of points.
    * @param points List of 8 doubles (4 unzipped vertex-points of the rectangle), topLeft, topRight, botRight, botLeft
    * @return A new resizable rectangle.
    */
  def apply(points: List[Double]): ResizableRectangle = {
    //Point coordinates are assumed to match
    val topLeft: Point = (points.head, points(1))
    val botRight: Point = (points(4), points(5))
    val width: Double = botRight._1 - topLeft._1
    val height: Double = botRight._2 - topLeft._2

    new ResizableRectangle(topLeft, width, height)
  }
}

class ResizableRectangle(
            startPoint:Point,
            width:Double,
            height:Double)
    extends Rectangle(startPoint._1, startPoint._2, width, height)
    with ResizableShape
    with RectangleLike
    with ColorizableShape
    with VertexLike {

  //These are LOCAL points
  override def getTopLeft:Point = (getX, getY)
  override def getTopRight:Point = (getX + getWidth, getY)
  override def getBottomLeft:Point = (getX, getY + getHeight)
  override def getBottomRight:Point = (getX + getWidth,getY + getHeight)
  override def copy: ResizableRectangle = {
    val duplicate = new ResizableRectangle(startPoint, width, height)
    duplicate.copyColors(this)
    duplicate.copyPosition(this)
    duplicate
  }

  //Local vertex-points of the rectangle.
  override def localVertexes: List[Point] = List(getTopLeft, getTopRight, getBottomRight, getBottomLeft)
}
