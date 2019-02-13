package com.codingame.game.model

import com.codingame.game.Player
import java.util.*

val edibleEncoding: Map<EdibleItem, String> = mapOf(
    IceCream to Constants.FOOD.ICECREAM.name,
    Strawberries to Constants.FOOD.STRAWBERRIES.name,
    Blueberries to Constants.FOOD.BLUEBERRIES.name,
    ChoppedBananas to Constants.FOOD.CHOPPEDBANANAS.name,
    StrawberrySlice to Constants.FOOD.STRAWBERRYSLICE.name,
    BlueberrySlice to Constants.FOOD.BLUEBERRYSLICE.name,
    Waffle to Constants.FOOD.WAFFLE.name
)

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
}

class DishReturn: Equipment() {
  override fun describe() = (listOf(Constants.EQUIPMENT.DISH_RETURN.name) + List(dishes) { Constants.ITEM.DISH.name }).joinToString("-")

  override fun reset() {
    dishes = 4
  }

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

