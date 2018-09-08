package com.codingame.game

import com.codingame.gameengine.core.AbstractMultiplayerPlayer
import com.codingame.gameengine.core.AbstractPlayer
import com.codingame.gameengine.module.entities.Rectangle

const val REACH_DISTANCE = 3
const val WALK_DISTANCE = 7
operator fun Int?.compareTo(other: Int): Int = (this ?: Int.MAX_VALUE).compareTo(other)

class Player(var isLeftTeam: Boolean = true) : AbstractMultiplayerPlayer() {
  override fun getExpectedOutputLines() = 1
  lateinit var sprite:Rectangle

  fun sendInputLine(toks: List<Int>) = sendInputLine(toks.joinToString(" "))
  fun sendInputLine(singleTok: Int) = sendInputLine(singleTok.toString())

  fun use(cell: Cell) {
    if (!cell.isTable) throw Exception("Cannot use $cell: not a table!")
    if (cell.distanceTo(location) > REACH_DISTANCE) { moveTo(cell); return }
    val equipment = cell.equipment
    if (equipment != null) {
      equipment.use(this)
      return
    }

    // try drop
    if (heldItem != null) {
      cell.item?.also { return it.receiveItem(this, heldItem!!) }
      cell.item = heldItem
      heldItem = null
      return
    }

    // try take
    cell.item?.also { return it.take(this, cell) }

    throw Exception("Cannot use this table, nothing to do!")
  }

//  fun drop(cell: Cell) {
//    val item = heldItem ?: throw Exception("Cannot drop: not holding anything!")
//    if (cell.distanceTo(location) > REACH_DISTANCE) { moveTo(cell); return }
//    val equipment = cell.equipment
//    if (equipment != null) return item.dropOntoEquipment(this, equipment)
//    item.drop(this, cell)
//  }
//
//  fun take(cell: Cell) {
//    if (heldItem != null) throw Exception("Cannot take: already holding something!")
//    if (cell.distanceTo(location) > REACH_DISTANCE) { moveTo(cell); return }
//    cell.item?.let { return it.take(this, cell) }
//    cell.equipment?.let { return it.takeFrom(this) }
//    throw Exception("Cannot take: nothing here!")
//  }

  fun moveTo(cell: Cell) {
    val fromSource = location.buildDistanceMap()
    val target =
        if (!cell.isTable)
          cell
        else
          cell.neighbours.map { it.first }
              .filter { !it.isTable }
              .filter { it in fromSource.keys }
              .minBy { fromSource[it]!! } ?: throw Exception("Cannot move to table; no available neighbour!")

    if (target !in fromSource.keys) throw Exception("Cannot move: no path!")

    if (location.distanceTo(target) <= WALK_DISTANCE) {
      location = target
      return
    }

    val fromTarget = target.buildDistanceMap()

    location = fromSource
        .filter { (cell, dist) -> dist <= WALK_DISTANCE && !cell.isTable }
        .minBy { (cell, _) -> fromTarget[cell]!! }!!
        .key
  }

  var heldItem: Item? = null
  lateinit var location: Cell
}
