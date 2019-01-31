package com.codingame.game.model

import com.codingame.game.Player
import java.util.*

val edibleEncoding: Map<EdibleItem, Int> = mapOf(
    IceCream to Constants.FOOD.ICE_CREAM.value,
    Strawberries to Constants.FOOD.STRAWBERRIES.value,
    Blueberries to Constants.FOOD.BLUEBERRIES.value,
    ChoppedBananas to Constants.FOOD.CHOPPED_BANANAS.value,
    PieSlice(PieFlavour.Strawberry) to Constants.FOOD.STRAWBERRY_PIE.value,
    PieSlice(PieFlavour.Blueberry) to Constants.FOOD.BLUEBERRY_PIE.value,
    Waffle to Constants.FOOD.WAFFLE.value
)

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

  override fun reset() {
    repeat(40) { tick() }
    dishes = 4
  }

  private val dishQueue = LinkedList<Boolean>(List(40) { false })
  var dishes: Int = 4

  override fun takeFrom(player: Player): Item {
    if (dishes <= 0) throw LogicException("Dishwasher is empty!")
    dishes--
    return Dish()
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

