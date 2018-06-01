package com.codingame.game

import com.codingame.gameengine.core.AbstractPlayer
import com.codingame.gameengine.module.entities.Rectangle

class Player : AbstractPlayer() {
  override fun getExpectedOutputLines() = 1
  lateinit var sprite:Rectangle

  fun use(cell: Cell) {
    val equipment = cell.equipment ?: throw Exception("Cannot use: no equipment!")
    if (cell.distanceTo(location) != 2) throw Exception("Cannot use: too far!")
    equipment.use(this)
  }

  fun drop(cell: Cell) {
    val item = heldItem ?: throw Exception("Cannot drop: not holding anything!")
    if (cell.distanceTo(location) != 2) throw Exception("Cannot drop: too far!")
    val equipment = cell.equipment
    if (equipment != null) return item.dropOntoEquipment(this, equipment)
    item.drop(this, cell)
  }

  fun take(cell: Cell) {
    if (heldItem != null) throw Exception("Cannot take: already holding something!")
    if (cell.distanceTo(location) != 2) throw Exception("Cannot take: too far!")
    cell.item?.let { return it.take(this, cell) }
    cell.equipment?.let { return it.takeFrom(this) }
    throw Exception("Cannot take: nothing here!")
  }

  fun moveTo(cell: Cell) {
    if (location.distanceTo(cell) ?: Int.MAX_VALUE > 7) throw Exception("Cannot move: too far!")
    if (cell.isTable) throw Exception("Cannot move: can't walk here!")
    location = cell
  }

  var heldItem: Item? = null
  lateinit var location: Cell
}
