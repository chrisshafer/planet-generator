package com.shafer.planetgenerator

package object delaunay {

  val EPSILON = 0.000001

  case class Point(x: Double, y: Double)
  case class Triangle(p1: Int, p2: Int, p3: Int)
  case class Edge(p1: Int, p2: Int){
    lazy val reversed = Edge(p2, p1)
  }

  case class EdgeBuffer(set: Set[Edge]) {
    def +(edge: Edge): EdgeBuffer = {
      if(set.contains(edge)) EdgeBuffer(set - edge)
      else if(set.contains(edge.reversed)) EdgeBuffer(set - edge.reversed)
      else EdgeBuffer(set + edge)
    }
  }
  object EdgeBuffer{
    def empty = EdgeBuffer(Set.empty[Edge])
  }
}
