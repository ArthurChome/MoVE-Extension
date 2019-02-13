/**
 * Copyright (C) 2016 Nicola Justus <nicola.justus@mni.thm.de>
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.thm.move.util


import javafx.geometry.Point2D
import javafx.scene.transform.Rotate
import de.thm.move.types._

import scala.math._


object GeometryUtils {

  type Vector2D = Point
  type Line = (Point, Point)

  /** Calculates the middle-point M of the line represented by the given 2 points */
  def middleOfLine(start:Point, end:Point): Point = {
    val (startX, startY) = start
    val (endX,endY) = end
    ( (startX+endX)/2, (startY+endY)/2 )
  }

  /** Calculates the middle-point M of the line represented by the given 2 points */
  def middleOfLine(startX:Double, startY:Double, endX:Double, endY:Double): Point =
    middleOfLine((startX,startY), (endX,endY))

  /** Converts the given double into a radius number */
  def asRadius(x:Double):Double = x/2

  /** Returns the closest multiple of x to v */
  def closestMultiple(x:Double, v:Double): Option[Double] = {
    val rem = v / x
    val multiple = BigDecimal(rem).setScale(0, BigDecimal.RoundingMode.HALF_UP).toDouble
    if(v % x == 0) Some(v) //v = multiple of x
    else if(rem % 0.5 == 0) None //remainder is a.5 => no nearest value
    else Some(multiple*x)
  }

  /** Returns the closest multiple of x to v. If the value is in the middle, chooses as result the next multiple of x. */
  def closestMultipleForced(x:Double, v:Double): Double = {
    val rem = v / x
    val multiple = BigDecimal(rem).setScale(0, BigDecimal.RoundingMode.HALF_UP).toDouble
    if(v % x == 0) v //v = multiple of x
    else multiple*x
  }

  /** Returns the euclidean distance between two points*/
  def distance(x1: Double, y1: Double, x2: Double, y2: Double): Double =
    Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2))

  /**
    * Calculates the Euclidean distance between two points.
    * @param point1 (x1, y1)
    * @param point2 (x2, y2)
    * @return The distance.
    */
  def distance(point1: Point, point2: Point): Double = {
    val (x1, y1) = point1
    val (x2, y2) = point2

    sqrt(pow(x2 - x1, 2) + pow(y2 - y1, 2))
  }

  /** Return for a given point the closest point in a given list of points.*/
  def closestPointToPoint(origin: Point, points: List[Point]): Point = {
    points.minBy(destination => distance(origin, destination))
  }

  /**
    * Calculates a pair of the two closest points (a, b).
    * Each point a and b is retrieved from two given separate lists. Every distance between all points from the first list
    *   is compared to all points of the second list. The pair of points with the smallest distance is returned.
    * @param origins A list of points, where the first point a of the closest pair (a,b) s found.
    * @param destinations A list of points, where the second point b of the closest pair (a,b) is found.
    * @return A tuple of two points of the two closest points combination of the two given lists.
    */
  def closestPair(origins: List[Point], destinations: List[Point]): (Point, Point) = {
    val closestDestinations = origins.map(o => closestPointToPoint(o, destinations))
    val index = (origins, closestDestinations).zipped.map((o, b) => GeometryUtils.distance(o,b)).zipWithIndex.minBy(_._1)._2
    (origins(index), closestDestinations(index))
  }

  /** Calculates the offset which needs to get added to a (rotated) RectangleLike
    * after a RectangleLike got resized.
    *
    * @note All given Point2D's have to be in untransformed
    *       coordinates.
    *       This function is an adaption of Christopher Schölzel's equivalent in Java, Processing.
    * @param cOld the middle-point of the rectangle BEFORE the resize
    * @param cNew the middle-point of the rectangle AFTER the resize
    * @param deg the rotation-degree
    * @param isoCorner the corner which shouldn't change;
    *                  in general the opposite corner of the resized corner
    */
  def calculateRotationOffset(cOld: Point2D, cNew: Point2D, deg: Double, isoCorner: Point2D): Point2D =  {
    val r = new Rotate(deg, cOld.getX, cOld.getY)
    val rotCorner = r.transform(isoCorner)
    val relCorner = isoCorner.subtract(cNew)
    // find new center for new shape
    val c = Math.cos(Math.toRadians(deg))
    val s = Math.sin(Math.toRadians(deg))
    val cNewTrans = new Point2D(
            rotCorner.getX - c * relCorner.getX + s * relCorner.getY,
            rotCorner.getY - c * relCorner.getY - s * relCorner.getX
    )
    // find delta vector between old and new center
    val delta = cNewTrans.subtract(cNew)
    delta
  }

  /** Calculates the middle-point between the given 4 points
    * if the 4 points represent a rectangular-bounds.
    */
  def rectangleMiddlePoint(p1:Point, p2:Point, p3:Point, p4:Point): Point = {
    List(p1,p2,p3,p4).foldLeft((0.0,0.0)) {
      case (acc, elem) => acc + elem
    } / (4.0,4.0)
  }

  /* Calculates the vector between the given 2 points */
  def vectorOf(startPoint:Point, endPoint:Point): Vector2D =
    endPoint - startPoint

  /* Calculates the length of the given vector */
  def vectorLength(v:Vector2D): Double =
    sqrt(pow(v.x, 2) + pow(v.y, 2))

  /* Calculates the scalar-product from the given 2 vectors */
  def scalarProduct(v1:Vector2D, v2:Vector2D): Double =
    v1.x * v2.x + v1.y * v2.y

  /**
    * Calculates the slope of a line.
    * @param line (p, q) line defined by two points.
    * @return The slope of the line.
    */
  def slope(line: Line): Double = {
    val ((x1, y1), (x2, y2)) = line

    (y2 - y1) / (x2 - x1)
  }

  /**
    * Calculates the inclination of a line.
    * @param line The line.
    * @return Its inclination (in rad).
    */
  def inclination(line: Line): Double = {
    //Slope
    val m = slope(line)
    atan(m) //Note atan will catch Nan and Infinite on vertical and horizontal lines and still give the correct angle
  }

  /**
    * Transforms a radial angle to one in degrees.
    * @param rad The radial angle.
    * @return Angle in expressed in degrees.
    */
  def radToDeg(rad: Double): Double ={
    rad*180/math.Pi
  }

  /**
    * Checks whether or not a point falls within a line segment.
    * @param point (x0, y0)
    * @param line The line segment, defined by two points in a tuple.
    * @return Boolean indicating whether or not the point falls within the line-segment.
    */
  def pointInSegment(point: Point, line: Line): Boolean = {
    val (x0, y0) = point
    val ((x1, y1), (x2, y2)) = line

    //This happens when the following value is between 0 and 1.
    //see: https://math.stackexchange.com/questions/2248617/shortest-distance-between-a-point-and-a-line-segment
    val t = - (((x1 - x0)*(x2 - x1) + (y1 - y0)*(y2 - y1))/(pow(x2-x1, 2) + pow(y2-y1, 2)))

    0 <= t && t <= 1
  }

  /**
    * Calculates the distance from a point to a line segment.
    * @param point (x0, y0)
    * @param line Line segment, defined by two points in a tuple.
    * @return The distance between the point and the line segment.
    */
  def distanceToLine(point: Point, line: Line): Double = {
    val (x0, y0) = point
    val ((x1, y1), (x2, y2)) = line

    //First check whether or not the shortest distance will fall within the segment of line.
    if (pointInSegment(point, line)) {
      //The distance to a point within the segment.
      abs((y2 - y1)*x0 - (x2 - x1)*y0 + x2*y1 - y2*x1 ) / distance((x1, y1),(x2, y2))
    } else {
      //The smallest distance to the end-points.
      min(distance(point, (x1, y1)), distance(point, (x2, y2)))
    }
  }

  /**
    * Calculates the coordinates of the point on a line segment closest to the given point.
    * @param point (x0, y0)
    * @param line Line segment, defined by two points in a tuple.
    * @return A point on the line-segment, closest to the given point.
    */
  def closestPointOnLine(point: Point, line: Line): Point = {
    val (x0, y0) = point
    val (p1, p2) = line //Referred to as "x"
    val (x1, y1) = p1
    val (x2, y2) = p2

    //First check whether or not the point falls within the line segment.
    //If it doesn't, return the closest end-point of the segment.

    if (pointInSegment(point, line)) {
      //Edge cases with perfect vertical/horizontal lines.
      //We case use the y/x of the point combined with the cte of the line.
      if (y1 == y2) {
        //Horizontal line
        return (x0, y1)
      } else if(x1 == x2) {
        //Vertical line
        return (x1, y0)
      }

      //Slope of x
      val m = slope(line)

      //There is a line "xp" which goes through x and point with minimal distance.
      //xp is perpendicular to x, meaning we can calculate its slope using m (-1/m).
      //We are looking for the intersection of x and xp.
      //Using the slopes, we can construct a linear system expressing their intersection.
      //Transforming this system using substitution gives us:
      val x = (m * x1 - y1 + y0 + x0 / m) / (m + 1 / m)
      val y = m * (x - x1) + y1

      (x, y)
    } else {
      //Find the closest end-point.
      val dist1 = distance(p1, point)
      val dist2 = distance(p2, point)

      if (dist1 < dist2){
        p1
      } else {
        p2
      }
    }
  }


  /* Round up a given Double number to a given total number of digits.*/

  /* Round up a given Double number to a given total number of digits.*/
  def roundTo(value: Double, places: Int): Double = {
    import java.math.BigDecimal
    var bigDecimal = new BigDecimal(value)
    bigDecimal = bigDecimal.setScale(places, BigDecimal.ROUND_HALF_UP)
    bigDecimal.doubleValue
  }


}
