package com.codingame.game.model

import com.codingame.game.view.BoardView
import com.codingame.gameengine.module.entities.*
import java.util.*

class CellView(val cell: Cell) {
  lateinit var background: Rectangle
  lateinit var content: Sprite
  lateinit var secondaryContent: Sprite
  lateinit var itemSpriteGroup: BoardView.ItemSpriteGroup
  lateinit var group: Group
}

class Cell(val x: Int, val y: Int, val isTable: Boolean = true) {
  override fun toString(): String = "($x, $y)"
  private val straightNeighbours = mutableListOf<Cell>()
  private val diagonalNeighbours = mutableListOf<Cell>()

  val neighbours by lazy { straightNeighbours.map {it to 2} + diagonalNeighbours.map {it to 3} }

  fun connect(other: Cell, isStraight: Boolean) {
    (if (isStraight) straightNeighbours else diagonalNeighbours) += other
  }

  var equipment: Equipment? = null
  var item: Item? = null

  fun buildDistanceMap(blockedCell: Cell?): Map<Cell, Int> {
    val visitedCells = mutableMapOf<Cell, Int>()
    val floodedCells = PriorityQueue<Pair<Cell, Int>> { (_,d1), (_,d2) -> d1.compareTo(d2) }
    floodedCells += this to 0
    var isFirst = true

    while (floodedCells.any()) {
      val (cell, dist) = floodedCells.remove()!!
      if (cell in visitedCells) continue
      visitedCells += cell to dist
      if ((!cell.isTable && cell != blockedCell) || isFirst) {
        floodedCells += cell.neighbours
            .filterNot { (nc, _) -> nc == blockedCell }
            .filterNot { (nc, _) -> nc in visitedCells.keys }
            .map { (nc, nd) -> nc to dist + nd }
      }
      isFirst = false
    }
    return visitedCells
  }

  fun distanceTo(target: Cell, partnerCell: Cell? = null): Int? {
    return buildDistanceMap(partnerCell)[target]
  }
}


class Board(val width: Int, val height: Int, val layout: List<String>? = null) {
  lateinit var window: Window

  constructor(layout: List<String>): this(layout[0].length, layout.size, layout)

  fun reset() { allCells.forEach { c ->
    c.equipment?.reset()
    c.item = null
  } }

  val cells = Array(width) { x ->
    Array(height) { y ->
      Cell(x, y, layout != null && layout[y][x] != '.')
    }
  }

  val allCells = cells.flatten()

  operator fun get(x: Int, y: Int): Cell = cells[x][y]
  operator fun get(cellName: String): Cell {
    val file = cellName[0]
    val x = file - 'A'
    if (x !in xRange) throw IllegalArgumentException("x: $x")
    return get(x, cellName.substring(1).toInt())
  }

  fun tick() {
    allCells.forEach { cell -> (cell.equipment as? TimeSensitiveEquipment)?.tick() }
  }

  private val xRange = 0 until width
  private val yRange = 0 until height

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

