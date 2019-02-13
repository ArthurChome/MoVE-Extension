/**
 * Copyright (C) 2016 Nicola Justus <nicola.justus@mni.thm.de>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.thm.move.util

import de.thm.move.MoveSpec
import GeometryUtils._
import de.thm.move.types.Point

object GeometryUtilsTest {
	//Some arbitrary lines:

	//vertical
	val vertical: Line = ((4, 3), (4, 8))

	//horizontal
	val horizontal: Line = ((6, 2), (3, 2))

	//bisects 1st quarter
	val bisec: Line = ((0, 0), (10, 10))

	//downward slope
	val downwards: Line = ((5, 8), (6, 4))
}

class GeometryUtilsTest extends MoveSpec {
	//Bring object's scope into this scope
	import GeometryUtilsTest._

	"GeometryUtils.`closestMultiple`" should "return the closest multiple of A to B if it exists" in {
		closestMultiple(50.0, 120.0) shouldBe Some(100.0)
		closestMultiple(50.0, 172.0) shouldBe Some(150.0)
		closestMultiple(50.0, 500.0) shouldBe Some(500.0)
		closestMultiple(50.0, 547.0) shouldBe Some(550.0)
		closestMultiple(50.0, 150.0) shouldBe Some(150.0)
		closestMultiple(50.0, 540.0) shouldBe Some(550.0)
		closestMultiple(100, 555.0) shouldBe Some(600.0)
		closestMultiple(30, 90.0) shouldBe Some(90.0)
		closestMultiple(30, 80.0) shouldBe Some(90.0)
		closestMultiple(30, 70.0) shouldBe Some(60.0)
		closestMultiple(-5.0, 10.0) shouldBe Some(10.0)
		closestMultiple(-5.0, 9.0) shouldBe Some(10.0)
		closestMultiple(-5.0, -9.0) shouldBe Some(-10.0)
	}

	it should "not return the closest multiple if it doesn't exist" in {
		closestMultiple(100.0, 150.0) shouldBe None
		closestMultiple(7.0, 10.5) shouldBe None
		closestMultiple(500, 750) shouldBe None
		closestMultiple(20, 30) shouldBe None
		closestMultiple(50.0, 125.0) shouldBe None
		closestMultiple(-50.0, 125.0) shouldBe None
	}

	"GeometryUtils.`closestMultipleForced`" should "return always return (one of) the closest multiple of A to B" in {
		closestMultipleForced(50.0, 120.0) shouldBe 100.0
		closestMultipleForced(50.0, 125.0) shouldBe 150.0
		closestMultipleForced(50.0, 172.0) shouldBe 150.0
		closestMultipleForced(100, 555.0) shouldBe 600.0
		closestMultipleForced(30, 90.0) shouldBe 90.0
		closestMultipleForced(30, 80.0) shouldBe 90.0
		closestMultipleForced(30, 70.0) shouldBe 60.0
		closestMultipleForced(-5.0, 10.0) shouldBe 10.0
		closestMultipleForced(-5.0, 9.0) shouldBe 10.0
		closestMultipleForced(-5.0, -9.0) shouldBe -10.0
	}

	//Test distance both distance procedures (overloaded to work on points-tuples)
	"distance" should "return the Euclidean distance between two points" in {
		distance((0,0), (10, 0)) shouldBe 10
		distance(0, 0, 10, 0) shouldBe 10
		distance((15, 8), (15, 8)) shouldBe 0
		distance(15, 8, 15, 8) shouldBe 0
		distance((165,156), (876, 645)) shouldBe 862.9264163299209
		distance(165, 156, 876, 645) shouldBe 862.9264163299209
	}

	//Tests closestPointToPoint
	"closestPointToPoint" should "return the point in a list closest to the given point" in {
		val points = List[Point]((8, 5), (15, -8), (546, 546), (0, 0), (-4, -5.6))
		closestPointToPoint((0, 0), points) shouldBe(0, 0)
		closestPointToPoint((350, 360), points) shouldBe(546, 546)
		closestPointToPoint((7, 4), points) shouldBe(8, 5)
	}

	//Tests closestPair
	"closestPair" should "return the closest pair of points" in {
		val destinations = List[Point]((8, 5), (15, -8), (546, 546), (0, 0), (-4, -5.6))
		closestPair(List[Point]((0, 0)), destinations) shouldBe((0,0),(0,0))
		closestPair(List[Point]((10, 3), (0, 0)), destinations) shouldBe((0,0),(0,0))
		closestPair(List[Point]((10, 3)), destinations) shouldBe((10,3),(8,5))
		closestPair(List[Point]((8, 5), (0, 0)), destinations) shouldBe((8,5),(8,5)) //if two pairs have equal distance, take first one
		closestPair(List[Point]((-1, -1), (1, 1)), destinations) shouldBe((-1,-1),(0,0)) //if two pairs have equal distance, take first one
		closestPair(List[Point]((550, 550), (10000, 0)), destinations) shouldBe((550,550),(546,546))
	}

	//Test slope
	"slope" should "return the slope of a line" in {
		slope(vertical) shouldBe Double.PositiveInfinity
		slope(horizontal) shouldBe -0
		slope(bisec) shouldBe 1
		slope(downwards) shouldBe -4
	}

	//Test inclination
	"inclination" should "return the inclination of a line" in {
		inclination(vertical) shouldBe Math.PI/2
		inclination(horizontal) shouldBe 0
		inclination(bisec) shouldBe Math.PI/4
		inclination(downwards) shouldBe -1.3258176636680326
	}

	//Test radToDeg
	"radToDeg" should "express a radial angle in degrees" in {
		radToDeg(Math.PI/2) shouldBe 90
		radToDeg(0) shouldBe 0
		radToDeg(Math.PI/4) shouldBe 45
		radToDeg(-1.3258176636680326) shouldBe -75.96375653207353
	}

	//Test pointInSegment
	"pointInSegment" should "return whether or not a point lies in a line segment" in {
		pointInSegment((4, 3), vertical) shouldBe true
		pointInSegment((4, 2.999999), vertical) shouldBe false
		pointInSegment((-1, 2), vertical) shouldBe false
		pointInSegment((4, 5), vertical) shouldBe true
		pointInSegment((8, 2), horizontal) shouldBe false
		pointInSegment((4, 2), horizontal) shouldBe true
		pointInSegment((4, 4), bisec) shouldBe true
	}

	//Test distanceToLine
	"distanceToLine" should "return the distance between a point and a line segment" in {
		distanceToLine((0,0), vertical) shouldBe 5
		distanceToLine((-7, -4), horizontal) shouldBe 11.661903789690601
		distanceToLine((0, 3), vertical) shouldBe 4
		distanceToLine((8, 5), vertical) shouldBe 4
		distanceToLine((56, 1.3), downwards) shouldBe 50.07284693324317
	}

	//Test closestPointOnLine
	"closestPointOnLine" should "returns the point on a line segment closest to the given point" in {
		closestPointOnLine((0, 0), vertical) shouldBe (4, 3)
		closestPointOnLine((5, 5), vertical) shouldBe (4, 5)
		closestPointOnLine((5, 5), horizontal) shouldBe (5, 2)
		closestPointOnLine((100, 100), downwards) shouldBe(5, 8)
		closestPointOnLine((3.1, 6), downwards) shouldBe(5.358823529411764,6.564705882352943)
		closestPointOnLine((9, 4), downwards) shouldBe(6, 4)
	}
}
