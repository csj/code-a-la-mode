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

class DishReturn: Equipment() {
  override fun describe() = (listOf(Constants.EQUIPMENT.DISH_RETURN.name) + List(dishes) { Constants.ITEM.DISH.name }).joinToString("-")

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

