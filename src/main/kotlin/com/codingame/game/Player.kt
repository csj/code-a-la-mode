package com.codingame.game

import com.codingame.game.model.Cell
import com.codingame.game.model.Item
import com.codingame.game.model.LogicException
import com.codingame.game.view.BoardView
import com.codingame.gameengine.core.AbstractMultiplayerPlayer
import com.codingame.gameengine.module.entities.Group
import com.codingame.gameengine.module.entities.Sprite

const val REACH_DISTANCE = 3
const val WALK_DISTANCE = 9
operator fun Int?.compareTo(other: Int): Int = (this ?: Int.MAX_VALUE).compareTo(other)

class Player : AbstractMultiplayerPlayer() {
  var message : String = ""
  fun toViewString()  : String
  {
    return "CHEF:${this.nicknameToken}"
  }
  override fun toString() = this.nicknameToken

  override fun getExpectedOutputLines() = 1
  lateinit var sprite:Group
  lateinit var itemSprite: BoardView.ItemSpriteGroup
  lateinit var characterSprite: Sprite

  fun sendInputLine(toks: List<Any>) = sendInputLine(toks.joinToString(" ", transform = { it.toString() }))
  fun sendInputLine(singleTok: Int) = sendInputLine(singleTok.toString())

  fun use(cell: Cell): List<Cell> {
    val useCellPath = listOf(location, cell, cell, location)

    if (!cell.isTable) throw Exception("Cannot use $cell: not a table!")
    if (cell.distanceTo(location) > REACH_DISTANCE) return moveTo(cell)
    val equipment = cell.equipment
    if (equipment != null) {
      equipment.use(this)
      return useCellPath
    }

    // try drop
    if (heldItem != null) {
      cell.item?.also { it.receiveItem(this, heldItem!!, cell); return useCellPath }
      cell.item = heldItem
      heldItem = null
      return useCellPath
    }

    // try take
    cell.item?.also { it.take(this, cell); return useCellPath }

    throw LogicException("Cannot USE this table now, nothing to do!")
  }

  fun moveTo(cell: Cell): List<Cell> {
    val blockedCell = if (partner.isActive) partner.location else null

    val (fromSource, traceBack) = location.buildDistanceMap(blockedCell)
    val target =
        if (!cell.isTable && cell != blockedCell)
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
      return trace(cell, location, traceBack)
    }

    val fromTarget = target.buildDistanceMap(blockedCell).distances

    location = fromSource
        .filter { (cell, dist) -> dist <= WALK_DISTANCE && !cell.isTable }
        .minBy { (cell, _) -> fromTarget[cell]!! }!!
        .key

    return trace(cell, location, traceBack)
  }

  private fun trace(source: Cell, target: Cell, traceBack: Map<Cell, Cell>): List<Cell> {
    return sequence {
      var cur = target
      yield(target)
      while (cur != source) {
        cur = traceBack[cur] ?: break
        yield(cur)
      }
    }.toList().reversed()

  }

  var heldItem: Item? = null
  lateinit var location: Cell
  lateinit var partner: Player
}
