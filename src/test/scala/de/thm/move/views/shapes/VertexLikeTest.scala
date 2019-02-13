package de.thm.move.views.shapes

import de.thm.move.MoveSpec

object VertexLikeTest {
  //Arbitrary shapes with VertexLike trait for testing
  private val rect = new ResizableRectangle((0, 0), 50, 50)
  //Apply arbitrary rotation.
  rect.rotate(90)
  private val poly0 = new ResizablePolygon(List())
  private val poly1 = new ResizablePolygon(List(0, 0))
  private val poly2 = new ResizablePolygon(List(0, 0, 50, 50))
  private val poly3 = new ResizablePolygon(List(0, 0, 50, 50, 0, 50))
  poly3.rotate(90)
  private val line = new ResizableLine((0, 0), (50, 50), 5)
  line.rotate(90)
  private val text = new ResizableText("text", 0, 0)
}

//Test class for shapes that implement the VertexLike shape
class VertexLikeTest extends MoveSpec{
  import VertexLikeTest._

  "ResizableRectangle.vertexes" should "return a list of all (parent) vertexes of a resizable rectangle" in {
    //Corner points should be rotated by 90 degrees.
    assert(rect.vertexes() == List((50.0,0.0), (50.0,50.0), (0.0,50.0), (0.0,0.0)), "expected rotated corner points")
  }

  "ResizableRectangle.sides" should "return a list of all (parent) sides of a resizable rectangle" in {
    //Corner points should be rotated by 90 degrees.
    assert(rect.sides() == List(((50.0,0.0),(0.0,0.0)), ((50.0,0.0),(50.0,50.0)), ((50.0,50.0),(0.0,50.0)), ((0.0,50.0),(0.0,0.0))))
  }

  "ResizableLine.vertexes" should "return a list of 2 (parent) end-points of a resizable line" in {
    //Line should be rotated by 90 degrees.
    assert(line.vertexes() == List((50.0,0.0), (0.0,50.0)))
  }

  "ResizableLine.sides" should "return a list containing the edge of a resizable line" in {
    //Line should be rotated by 90 degrees.
    assert(line.sides() == List(((50.0,0.0), (0.0,50.0))))
  }

  "Polygon.vertexes" should "return a list containing the vertexes of a resizable polygon" in {
    poly0.vertexes() shouldBe List()
    poly1.vertexes() shouldBe List((0,0))
    poly2.vertexes() shouldBe List((0, 0), (50, 50))
    //Rotated 90 degrees
    poly3.vertexes() shouldBe List((50.0,0.0), (0.0,50.0), (0.0,0.0))
  }

  "Polygon.sides" should "return a list containing the sides of a resizable polygon" in {
    poly0.sides() shouldBe List()
    poly1.sides() shouldBe List() //No side possible with < 2 points
    poly2.sides() shouldBe List(((0, 0), (50, 50)))
    //Rotated 90 degrees
    poly3.sides() shouldBe List(((50.0,0.0),(0.0,0.0)), ((50.0,0.0),(0.0,50.0)), ((0.0,50.0),(0.0,0.0)))
  }

  "Text.vertexes" should "return a list containing one point" in {
    text.vertexes() shouldBe List((0.0, 0.0))
  }

  "Test.sides" should "return an empty list" in {
    text.sides() shouldBe List()
  }
}
