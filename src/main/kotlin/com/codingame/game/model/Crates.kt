package com.codingame.game.model

import com.codingame.game.Player

class IceCreamCrate: GeneralCrate(IceCream, 'I') {
  override val toString: String
    get() = "Ice cream crate"
}

class BlueberryCrate: GeneralCrate(Blueberries, 'B'){
  override val toString: String
    get() = "Blueberry crate"
}
class StrawberryCrate: GeneralCrate(Strawberries, 'S'){
  override val toString: String
    get() = "Strawberry crate"
}
class DoughCrate: GeneralCrate(Dough, 'H'){
  override val toString: String
    get() = "Dough crate"
}

abstract class AllInstancesAreConsideredEqual: Equipment() {
  override fun equals(other: Any?): Boolean = other!!.javaClass == this.javaClass
  override fun hashCode(): Int = 0
}

abstract class GeneralCrate(private val newItem: Item, override val describeChar: Char) : AllInstancesAreConsideredEqual() {
  override fun receiveItem(player: Player, item: Item) {
    item.receiveItem(player, newItem, null)
    player.heldItem = item
  }

  override fun takeFrom(player: Player) = newItem

}

