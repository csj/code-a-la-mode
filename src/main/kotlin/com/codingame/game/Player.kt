package com.codingame.game

import com.codingame.gameengine.core.AbstractPlayer

class Player : AbstractPlayer() {
  override fun getExpectedOutputLines() = 1
  fun use(cell: Cell) {
    val equipment = cell.equipment ?: throw Exception("Cannot use: no equipment!")
    if (cell.distanceTo(location) != 2) throw Exception("Cannot use: too far!")
    equipment.use(this)
  }

  fun drop(cell: Cell) {
    val item = heldItem ?: throw Exception("Cannot drop: not holding anything!")
    if (cell.distanceTo(location) != 2) throw Exception("Cannot drop: too far!")
    item.drop(this, cell)
  }

  fun take(cell: Cell) {
    if (heldItem != null) throw Exception("Cannot take: already holding something!")
    if (cell.distanceTo(location) != 2) throw Exception("Cannot take: too far!")
    val item = cell.item ?: throw Exception("Cannot take: nothing there!")
    item.take(this, cell)
  }

  fun moveTo(cell: Cell) {
    if (location.distanceTo(cell) ?: Int.MAX_VALUE > 7) throw Exception("Cannot move: too far!")
    location = cell
  }

  var heldItem: Item? = null
  lateinit var location: Cell
}
