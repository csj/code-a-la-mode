package com.codingame.game.model

import com.codingame.game.Player

abstract class Item {
  open fun receiveItem(player: Player, item: Item, cell: Cell?) {
    throw Exception("Cannot drop $item onto $this!")
  }

  open fun take(player: Player, cell: Cell) {
    cell.item = null
    player.heldItem = this
  }
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
    throw Exception("$this cannot be taken directly!")
  }

  open fun receiveItem(player: Player, item: Item) {
    throw Exception("Cannot drop $item onto $this!")
  }

  open fun clone(): Equipment {
    throw Exception("$this cannot be cloned!")
  }

  abstract fun basicNumber(): Int
  open fun extras(): List<Int> = listOf(-1, -1)
}

/**
 * Marks the passage of time
 */
abstract class TimeSensitiveEquipment: Equipment() {
  abstract fun tick()
}

