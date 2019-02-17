package com.codingame.game.model

import com.codingame.game.Player

class IceCreamCrate: GeneralCrate(IceCream)
class BlueberryCrate: GeneralCrate(Blueberries)
class StrawberryCrate: GeneralCrate(Strawberries)
class DoughCrate: GeneralCrate(Dough)

abstract class AllInstancesAreConsideredEqual: Equipment() {
  override fun equals(other: Any?): Boolean = other!!.javaClass == this.javaClass
  override fun hashCode(): Int = 0
}

abstract class GeneralCrate(private val newItem: Item) : AllInstancesAreConsideredEqual() {
  override fun receiveItem(player: Player, item: Item) {
    item.receiveItem(player, newItem, null)
    player.heldItem = item
  }

  override fun takeFrom(player: Player) = newItem

  override fun describe(): String {
    return "${Constants.EQUIPMENT.CRATE.name}-${newItem.describe()}"
  }
}

