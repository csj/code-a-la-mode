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
  override val describeChar = 'D'
  override val tooltipString = "Dish washer"

  override fun reset() { dishes = 3 }

  var dishes: Int = 3
    private set

  override fun receiveItem(player: Player, item: Item) {
    if (item is Dish) {
      player.heldItem = Dish()
      return
    }

    if (item is EdibleItem) {
      removeDish()
      player.heldItem = Dish(item)
      return
    }

    super.receiveItem(player, item)
  }

  override fun takeFrom(player: Player): Item {
    removeDish()
    return Dish()
  }

  fun addDish() { dishes++ }

  fun removeDish() {
    if (dishes <= 0) throw LogicException("Dishwasher is empty!")
    dishes--
  }

}

