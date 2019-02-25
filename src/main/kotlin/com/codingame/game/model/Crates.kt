package com.codingame.game.model

import com.codingame.game.Player

class IceCreamCrate: GeneralCrate(IceCream, 'I', "Ice cream crate")
class BlueberryCrate: GeneralCrate(Blueberries, 'B', "Blueberry crate")
class StrawberryCrate: GeneralCrate(Strawberries, 'S', "Strawberry crate")
class DoughCrate: GeneralCrate(Dough, 'H', "Dough crate")

abstract class AllInstancesAreConsideredEqual: Equipment() {
  override fun equals(other: Any?): Boolean = other!!.javaClass == this.javaClass
  override fun hashCode(): Int = 0
}

abstract class GeneralCrate(
    private val newItem: Item,
    override val describeChar: Char,
    override val tooltipString: String
) : AllInstancesAreConsideredEqual() {

  override fun receiveItem(player: Player, item: Item) {
    item.receiveItem(player, newItem, null)
    player.heldItem = item
  }

  override fun takeFrom(player: Player) = newItem
}

