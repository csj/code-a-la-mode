package com.codingame.game.model

import com.codingame.game.Player

data class Dish(override val contents: MutableSet<EdibleItem> = mutableSetOf()) : Item(), Container {
  constructor(vararg initialContents: EdibleItem): this(mutableSetOf(*initialContents))

  override fun receiveItem(player: Player, item: Item, cell: Cell?) {
    if (item is EdibleItem) {
      this += item
      player.heldItem = null
      return
    }
    super.receiveItem(player, item, cell)
  }

  override fun describeTokens(): List<String> {
    return listOf(Constants.ITEM.DISH.name) + contents.map { it.describe() }
  }
}

class DishWasher: Equipment() {
  override val toString: String
    get() = "Dish washer"

  override val describeChar = 'D'

  override fun reset() { dishes = 4 }

  var dishes: Int = 4

  override fun takeFrom(player: Player): Item {
    if (dishes <= 0) throw LogicException("Dishwasher is empty!")
    dishes--
    return Dish()
  }

  fun addDishToQueue() {
    dishes++
  }

}

