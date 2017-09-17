package com.shafer.planetgenerator.delaunay

// Bourke Delauny Triangulation
// http://paulbourke.net/papers/triangulate/

object Delaunay {

  def centroidOfCircumcircle(pointOne: Point, pointTwo: Point, pointThree: Point): Point = {
    val mid1 = Point((pointOne.x + pointTwo.x)/2, (pointOne.y + pointTwo.y)/2 )
    val mid2 = Point((pointTwo.x + pointThree.x)/2, (pointTwo.y + pointThree.y)/2 )

    if (Math.abs(pointTwo.y - pointOne.y) < EPSILON ) {
      val d2 = -(pointThree.x - pointTwo.x) / (pointThree.y - pointTwo.y)
      val xc =  mid1.x
      val yc =  d2 * (xc - mid2.x) + mid2.y
      Point(xc, yc)

    } else if (Math.abs(pointThree.y - pointTwo.y) < EPSILON ) {
      val d1 = -(pointTwo.x - pointOne.x) / (pointTwo.y - pointOne.y)
      val xc =  mid2.x
      val yc =  d1 * (xc - mid1.x) + mid1.y
      Point(xc, yc)

    } else {
      val d1 = -(pointTwo.x - pointOne.x) / (pointTwo.y - pointOne.y)
      val d2 = -(pointThree.x - pointTwo.x) / (pointThree.y - pointTwo.y)
      val xc =  ((d1 * mid1.x - mid1.y) - (d2 * mid2.x - mid2.y)) / (d1 - d2)
      val yc =  d1 * (xc - mid1.x) + mid1.y
      Point(xc, yc)
    }
  }

  def circumcircle(queryPoint: Point, pointOne: Point, pointTwo: Point, pointThree: Point): (Boolean, Point, Double) = {
    if (Math.abs(pointOne.y - pointTwo.y) < EPSILON && Math.abs(pointTwo.y - pointThree.y) < EPSILON ) {
      (false, Point(0,0), 0)
    } else {

      val centroid = centroidOfCircumcircle(pointOne, pointTwo, pointThree)
      val radius = {
        val (dx, dy) = (pointTwo.x - centroid.x, pointTwo.y - centroid.y)
        dx*dx + dy*dy
      }

      val queryDistanceToCenter = {
        val (dx, dy) = (queryPoint.x - centroid.x, queryPoint.y - centroid.y)
        dx*dx + dy*dy
      }

      (queryDistanceToCenter - radius <= EPSILON, centroid, Math.sqrt(radius) )
    }
  }

  def convertTrianglesToEdges(completedTriangles: List[Triangle],
                              currentTriangles: List[Triangle],
                              point: Point)(pointList: List[Point]) =
    currentTriangles.foldLeft((completedTriangles, List.empty[Triangle], EdgeBuffer.empty) ) {
      case ((completed, current, edges), triangle) =>

        val (inside, centroid, radius) = circumcircle(point, pointList(triangle.p1),
                                                             pointList(triangle.p2),
                                                             pointList(triangle.p3))
        if (centroid.x + radius < point.x) { // optimize left to right scan
          (triangle::completed, current, edges)
        } else {
          if(inside) {
            val updatedEdges = edges
                .+(Edge(triangle.p1, triangle.p2))
                .+(Edge(triangle.p2, triangle.p3))
                .+(Edge(triangle.p3, triangle.p1))
            (completed, current, updatedEdges)
          } else {
            (completed, triangle::current, edges)
          }
        }
    }

  def addPointToMesh(completeTriangles: List[Triangle], currentTriangles: List[Triangle], pointIdx: Int)(pointList: List[Point]) = {
    val (completedTrianglesUpdated: List[Triangle], currentTrianglesUpdated: List[Triangle], edgesCreated: EdgeBuffer) =
      convertTrianglesToEdges(completeTriangles, currentTriangles, pointList(pointIdx))(pointList)

    val newlyCompletedTriangles = edgesCreated.set.map(e => Triangle( e.p1, e.p2, pointIdx ))
    (completedTrianglesUpdated, newlyCompletedTriangles.toList ::: currentTrianglesUpdated)
  }

  def boundingTriangle(pointList: List[Point]): List[Point] = {
    val minPoint = Point(pointList.map(_.x).min, pointList.map(_.y).min)
    val maxPoint = Point(pointList.map(_.x).max, pointList.map(_.y).max)
    val midpoint = Point((minPoint.x + maxPoint.x)/2, (minPoint.y + maxPoint.y)/2)
    val diameter = (maxPoint.x - minPoint.x).max(maxPoint.y - minPoint.y)

    List(
      Point(midpoint.x - 2*diameter, midpoint.y - 1*diameter),
      Point(midpoint.x - 0*diameter, midpoint.y + 2*diameter),
      Point(midpoint.x + 2*diameter, midpoint.y - 1*diameter)
    )
  }
  
  def triangulate(seedPoints : List[Point]) : Seq[(Point, Point, Point)] = {
    if(seedPoints.nonEmpty) {
      val numPoints = seedPoints.length
      val pointList = seedPoints ::: boundingTriangle(seedPoints)
      val initialTriangle = List(Triangle(numPoints + 0, numPoints + 1, numPoints + 2))
      val pointsSorted = pointList.take(numPoints).zipWithIndex.sortBy(_._1.x).map { case (Point(x, y), i) => i}.distinct

      val (finallyCompleted, triangles) = pointsSorted.foldLeft((List.empty[Triangle], initialTriangle)) {
        case ((completedTriangles, currentTriangle), pointIdx) =>
          addPointToMesh(completedTriangles, currentTriangle, pointIdx)(pointList)
      }

      (finallyCompleted ::: triangles).filterNot(t => t.p1 >= numPoints || t.p2 >= numPoints || t.p3 >= numPoints)
        .map { t => (seedPoints(t.p1), seedPoints(t.p2), seedPoints(t.p3))}
    } else Nil
  }

}