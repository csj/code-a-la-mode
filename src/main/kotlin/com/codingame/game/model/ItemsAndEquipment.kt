package com.codingame.game.model

import com.codingame.game.Player

abstract class Item {
  open fun receiveItem(player: Player, item: Item, cell: Cell?) {
    throw LogicException("Cannot take $item while holding $this!")
  }

  open fun take(player: Player, cell: Cell) {
    cell.item = null
    player.heldItem = this
  }

  abstract fun describeTokens(): List<String>
  fun describe() = describeTokens().joinToString("-")
  override fun toString() = describe()
}

abstract class EasilyDescribedItem(private val singleItemToken: String): Item() {
  override fun describeTokens() = listOf(singleItemToken)
}

/**
 * Represents a feature of the board. Cannot be moved or picked up, but can be USEd.
 */
abstract class Equipment {
  open fun use(player: Player) {
    player.heldItem?.also { return receiveItem(player, it) }
    player.heldItem = takeFrom(player)
  }

  open fun takeFrom(player: Player): Item {
    throw LogicException("Cannot USE $this now, not holding anything!")
  }

  open fun receiveItem(player: Player, item: Item) {
    throw LogicException("Cannot drop $item onto $this!")
  }

  open fun reset() { }

  abstract val describeChar: Char?
  abstract val tooltipString: String
  override fun toString() = tooltipString
}

/**
 * Marks the passage of time
 */
abstract class TimeSensitiveEquipment: Equipment() {
  abstract fun tick()
}