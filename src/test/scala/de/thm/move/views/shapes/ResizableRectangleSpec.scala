package de.thm.move.views.shapes

import de.thm.move.MoveSpec

//Test new method added to resizable rectangle
class ResizableRectangleSpec extends MoveSpec{
  "apply" should "create a new resizable rectangle from a list of points" in {
    val points = List[Double](0,0,10,0,10,10,10,10)
    //compare a rectangle made from default constructor with applied-rectangle
    val RectAp = ResizableRectangle(points)
    val Rect = new ResizableRectangle((0,0), 10, 10)

    assert(RectAp.localVertexes == Rect.localVertexes, "rectangle did not form correctly")
  }
}
