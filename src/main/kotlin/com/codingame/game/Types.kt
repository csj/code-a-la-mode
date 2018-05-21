package com.codingame.game

import java.util.*

class Cell(val x: Int, val y: Int) {
  private val straightNeighbours = mutableListOf<Cell>()
  private val diagonalNeighbours = mutableListOf<Cell>()

  val neighbours by lazy { straightNeighbours.map {it to 2} + diagonalNeighbours.map {it to 3} }

  fun connect(other: Cell, isStraight: Boolean) {
    (if (isStraight) straightNeighbours else diagonalNeighbours) += other
  }

  fun distanceTo(target: Cell): Int? {
    val floodedCells = PriorityQueue<Pair<Cell, Int>> { (_,d1), (_,d2) -> d1.compareTo(d2) }
    floodedCells += this to 0
    while (floodedCells.any()) {
      val (cell, dist) = floodedCells.remove()!!
      if (cell == target) return dist
      floodedCells += cell.neighbours.map { (nc, nd) -> nc to dist + nd }
    }
    return neighbours.find { (cell, _) -> cell == target }?.let { (_, dist) -> dist }
  }
}

/**
 * Width: The number of horizontal cells _per player_. If width = 5, then
 * The cells will be numbered -4,-3,-2,-1,0,1,2,3,4  -- i.e. each team works with 5 cells including cell 0 (middle counter).
 * For input/output purposes, the columns will be inverted for player 1, so that each team thinks
 * they're working with columns 0..4 while the enemy works with -4..0
 */
class Board(val width: Int, val height: Int) {
  val cells = Array(width * 2 - 1, { x ->
    Array(height, { y ->
      Cell(x - width + 1, y)
    })
  })

  operator fun get(x: Int, y: Int): Cell = cells[x + width - 1][y]
  operator fun get(cellName: String): Cell = get(cellName[0] - 'A', cellName.substring(1).toInt())

  val xRange = -(width-1)..(width-1)
  val yRange = 0 until height

  init {
    for (x in xRange) {
      for (y in yRange) {
        for (dx in -1..1) for (dy in -1..1) {
          if (dx != 0 || dy != 0) {
            val x2 = x+dx; val y2 = y+dy
            if (x2 in xRange && y2 in yRange) {
              this[x,y].connect(this[x2,y2], dx*dy == 0)
            }
          }
        }
      }
    }
  }
}