package com.codingame.game.model

import com.codingame.game.Player
import java.util.*

data class Dish(override val contents: MutableSet<EdibleItem> = mutableSetOf()) : DeliverableItem(), Container {
  constructor(vararg initialContents: EdibleItem): this(mutableSetOf(*initialContents))

  override fun receiveItem(player: Player, item: Item, cell: Cell?) {
    if (item is EdibleItem) {
      this += item
      player.heldItem = null
      return
    }
    super.receiveItem(player, item, cell)
  }
}

class DishReturn : TimeSensitiveEquipment() {
  override fun basicNumber() = Constants.EQUIPMENT.DISH_RETURN.ordinal

  private val dishQueue = LinkedList<Boolean>(List(40) { false })
  var dishes: Int = 0

  override fun takeFrom(player: Player): Item {
    if (dishes > 0) {
      dishes--
      return Dish()
    }
    return super.takeFrom(player)
  }

  fun addDishToQueue() {
    dishQueue.add(true)
  }

  override fun tick() {
    while (dishQueue.pop()!!) {
      dishes++
    }
    dishQueue.add(false)
  }

}

