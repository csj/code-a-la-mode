package com.codingame.game.model

import com.codingame.game.ItemSpriteGroup
import com.codingame.gameengine.module.entities.*
import java.util.*

fun negafyCellName(cellName: String) = ('a' + (cellName[0] - 'A')) + cellName.substring(1)

class CellView(val cell: Cell) {
  lateinit var background: Rectangle
  lateinit var content: Sprite
  lateinit var secondaryContent: Sprite
  lateinit var itemSpriteGroup: ItemSpriteGroup
  lateinit var group: Group
}

class Cell(val x: Int, val y: Int, val isTable: Boolean = true) {
  override fun toString(): String = "($x, $y)"
  private val straightNeighbours = mutableListOf<Cell>()
  private val diagonalNeighbours = mutableListOf<Cell>()
  lateinit var oppositeCell: Cell
  lateinit var view: CellView

  val neighbours by lazy { straightNeighbours.map {it to 2} + diagonalNeighbours.map {it to 3} }

  fun connect(other: Cell, isStraight: Boolean) {
    (if (isStraight) straightNeighbours else diagonalNeighbours) += other
  }

  var equipment: Equipment? = null
    set(value) {
      field = value
      if (value !is Window && value !is DishReturn && oppositeCell.equipment != value) oppositeCell.equipment = value?.clone()
    }
  var item: Item? = null

  fun buildDistanceMap(): Map<Cell, Int> {
    val visitedCells = mutableMapOf<Cell, Int>()
    val floodedCells = PriorityQueue<Pair<Cell, Int>> { (_,d1), (_,d2) -> d1.compareTo(d2) }
    floodedCells += this to 0
    var isFirst = true

    while (floodedCells.any()) {
      val (cell, dist) = floodedCells.remove()!!
      if (cell in visitedCells) continue
      visitedCells += cell to dist
      if (!cell.isTable || isFirst) {
        floodedCells += cell.neighbours
            .filterNot { (nc, _) -> nc in visitedCells.keys }
            .map { (nc, nd) -> nc to dist + nd }
      }
      isFirst = false
    }
    return visitedCells
  }

  fun distanceTo(target: Cell): Int? {
    return buildDistanceMap()[target]
  }
}


/**
 * Width: The number of horizontal cells _per player_. If width = 5, then
 * The cells will be numbered -4,-3,-2,-1,0,1,2,3,4  -- i.e. each team works with 5 cells including cell 0 (middle counter).
 * For input/output purposes, the columns will be inverted for player 1, so that each team thinks
 * they're working with columns 0..4 while the enemy works with -4..0
 */
class Board(val width: Int, val height: Int, layout: List<String>? = null) {
  val cells = Array(width * 2 - 1) { x ->
    Array(height) { y ->
      val isTable = if (layout == null) false else {
        val layoutX = (x - (width-1)) * if (x < width) -1 else 1
        layout[y][layoutX] != '.'
      }
      Cell(x - width + 1, y, isTable)
    }
  }

  val allCells = cells.flatten()

  operator fun get(x: Int, y: Int): Cell = cells[x + width - 1][y]
  operator fun get(cellName: String): Cell {
    val file = cellName[0]
    val x = if (file in 'A'..'Z') file - 'A' else 'a' - file
    return get(x, cellName.substring(1).toInt())
  }

  fun tick() {
    allCells.forEach { cell -> (cell.equipment as? TimeSensitiveEquipment)?.tick() }
  }

  private val xRange = -(width-1)..(width-1)
  private val yRange = 0 until height

  init {
    for (x in xRange) {
      for (y in yRange) {
        get(x,y).oppositeCell = get(-x,y)
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

