package de.thm.move.views.shapes

import de.thm.move.types.Point

import scala.collection.mutable.ListBuffer

/**
  * Trait provides abstraction to list all vertexes of a figure (corner points).
  * The "value" of these vertexes is relative to the parent-shape (usually the canvas), this means that "local"
  * transformations like rotations are taking into account.
  */
trait VertexLike{
  this: ResizableShape =>

  //Implementor provides the "local" vertex points.
  def localVertexes: List[Point]

  /**
    * @return A list containing the points with parents value.
    */
  def vertexes(): List[Point] = {
    localVertexes.map(localToParentPoint)
  }

  /**
    * Lists the sides of the shape.
    * Expects that each consecutive point in localPoints corresponds to the end-point of a side between these points.
    * (the shape is closed by a side between the first and last point in this list)
    * @return A list containing the end-points of sides of the shape.
    */
  def sides(): List[(Point, Point)] = {
    if (localVertexes.length < 2) {
      //No sides possible
      return List()
    }

    //Instantiate the result with "closing" side, from first to last point
    val walls: scala.collection.mutable.ListBuffer[(Point, Point)] = ListBuffer((vertexes().head, vertexes().last))

    //If its 2, then there is only the closing side.
    if (localVertexes.length >= 3) {
      var prev: Point = vertexes().head
      vertexes().tail.foreach((point: Point) => {
        //Add a wall between adjacent points
        walls.+=((prev, point))
        prev = point
      })
    }
    walls.toList
  }
}
