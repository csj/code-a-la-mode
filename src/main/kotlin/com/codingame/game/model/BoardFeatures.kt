package com.codingame.game.model

import com.codingame.game.rand
import com.codingame.game.view.BoardView
import com.codingame.gameengine.module.entities.*
import java.util.*
import kotlin.math.absoluteValue

class CellView(val cell: Cell) {
  lateinit var background: Rectangle
  lateinit var content: Sprite
  lateinit var secondaryContent: Sprite
  var itemSpriteGroup: BoardView.ItemSpriteGroup? = null
  lateinit var group: Group
//  lateinit var text: Text
}

class Cell(val x: Int, val y: Int, val isTable: Boolean = true, val character: Char? = null, val playerId: Int? = null) {
  constructor(): this(-1, -1)
  override fun toString(): String = "($x, $y)"
  private val straightNeighbours = mutableListOf<Cell>()
  private val diagonalNeighbours = mutableListOf<Cell>()

  val neighbours by lazy { straightNeighbours.map {it to 2} +
      diagonalNeighbours.filter { it.isTable || isTable }.map {it to 3} }

  fun connect(other: Cell, isStraight: Boolean) {
    (if (isStraight) straightNeighbours else diagonalNeighbours) += other
  }

  var equipment: Equipment? = null
  var item: Item? = null

  data class DistanceMap(val distances: Map<Cell, Int>, val traceBack: Map<Cell, Cell>)

  fun buildDistanceMap(blockedCell: Cell?): DistanceMap {
    val visitedCells = mutableMapOf<Cell, Int>()
    val traceBack = mutableMapOf<Cell, Cell>()
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
            .map { (nc, nd) ->
              traceBack[nc] = cell
              nc to dist + nd
            }
      }
      isFirst = false
    }
    return DistanceMap(visitedCells, traceBack)
  }

  fun distanceTo(target: Cell, partnerCell: Cell? = null): Int? {
    return buildDistanceMap(partnerCell).distances[target]
  }

  fun describeChar(invert: Boolean): Char = if(playerId != null) {
    '0' + if(invert) (1 - playerId) else playerId
  } else {
    if (!isTable) '.' else equipment?.describeChar ?: '#'
  }
}


class Board(val width: Int, val height: Int, allSpawns: String, val layout: List<String>? = null) {
  lateinit var window: Window
  constructor(layout: List<String>, allSpawns : String): this(layout[0].length, layout.size, allSpawns, layout)
  val spawnLocations = allSpawns.split(' ').shuffled(rand).take(2)

  fun reset() { allCells.forEach { c ->
    c.equipment?.reset()
    c.item = null
  } }

  val cells = Array(width) { x ->
    Array(height) { y ->
      Cell(x, y, layout != null && layout[y][x] != '.',
          layout?.get(y)?.get(x)?.let { if (it !in listOf('*', '.')) it else null }, getPlayerPos(x, y))
    }
  }

  val allCells = cells.flatten()

  private fun getPlayerPos(x: Int, y: Int) : Int?{
    for (i in 0..1){
      val posX = spawnLocations[i][0]-'A'
      val posY = spawnLocations[i][1]-'0'
      if(posX == x && posY == y) return i
    }

    return null
  }
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

  fun oven() = allCells.mapNotNull { it.equipment as? Oven }.firstOrNull()
}

