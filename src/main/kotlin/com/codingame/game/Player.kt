package com.codingame.game

import com.codingame.gameengine.core.AbstractPlayer

class Player : AbstractPlayer() {
  override fun getExpectedOutputLines() = 1
  fun use(cell: Cell) {
    val equipment = cell.equipment ?: throw Exception("Cannot use: no equipment!")
    if (cell.distanceTo(location) != 2) throw Exception("Cannot use: too far!")
    equipment.use(this)
  }

  var heldItem: Item? = null
  lateinit var location: Cell
}
