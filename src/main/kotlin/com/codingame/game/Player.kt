package com.codingame.game

import com.codingame.game.model.Cell
import com.codingame.game.model.Item
import com.codingame.game.view.BoardView
import com.codingame.gameengine.core.AbstractMultiplayerPlayer
import com.codingame.gameengine.module.entities.Group
import com.codingame.gameengine.module.entities.Sprite

const val REACH_DISTANCE = 3
const val WALK_DISTANCE = 7
operator fun Int?.compareTo(other: Int): Int = (this ?: Int.MAX_VALUE).compareTo(other)

class Player : AbstractMultiplayerPlayer() {
  override fun toString(): String {
    return this.nicknameToken
  }

  override fun getExpectedOutputLines() = 1
  lateinit var sprite:Group
  lateinit var itemSprite: BoardView.ItemSpriteGroup
  lateinit var characterSprite: Sprite

  fun sendInputLine(toks: List<Any>) = sendInputLine(toks.joinToString(" ", transform = { it.toString() }))
  fun sendInputLine(singleTok: Int) = sendInputLine(singleTok.toString())

  // Returns true if the use was successful
  fun use(cell: Cell): Boolean {
    if (!cell.isTable) throw Exception("Cannot use $cell: not a table!")
    if (cell.distanceTo(location) > REACH_DISTANCE) { moveTo(cell); return false }
    val equipment = cell.equipment
    if (equipment != null) {
      equipment.use(this)
      return true
    }

    // try drop
    if (heldItem != null) {
      cell.item?.also { it.receiveItem(this, heldItem!!, cell); return true }
      cell.item = heldItem
      heldItem = null
      return true
    }

    // try take
    cell.item?.also { it.take(this, cell); return true }

    throw Exception("Cannot use this table, nothing to do!")
  }

  fun moveTo(cell: Cell) {
    val blockedCell = if (partner.isActive) partner.location else null

    val fromSource = location.buildDistanceMap(blockedCell)
    val target =
        if (!cell.isTable)
          cell
        else
          cell.neighbours.map { it.first }
              .filter { !it.isTable }
              .filter { it in fromSource.keys }
              .minBy { fromSource[it]!! } ?: return moveTo(blockedCell!!)

    if (target !in fromSource.keys) {
      System.err.println("Warning: cannot move! Moving to partner location instead")
      return moveTo(blockedCell!!)
    }

    if (location.distanceTo(target, blockedCell) <= WALK_DISTANCE) {
      location = target
      return
    }

    val fromTarget = target.buildDistanceMap(blockedCell)

    location = fromSource
        .filter { (cell, dist) -> dist <= WALK_DISTANCE && !cell.isTable }
        .minBy { (cell, _) -> fromTarget[cell]!! }!!
        .key
  }

  var heldItem: Item? = null
  lateinit var location: Cell
  lateinit var partner: Player
}
