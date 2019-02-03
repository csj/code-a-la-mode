package com.codingame.game.model

import com.codingame.game.Player

class IceCreamCrate: GeneralCrate({ IceCream }) {
  override fun describe() = Constants.EQUIPMENT.ICE_CREAM_CRATE.name
}

class StrawberryCrate: GeneralCrate({ Strawberries }) {
  override fun describe() = Constants.EQUIPMENT.STRAWBERRY_CRATE.name
}

class BlueberryCrate: GeneralCrate({ Blueberries }) {
  override fun describe() = Constants.EQUIPMENT.BLUEBERRY_CRATE.name
}

class BananaCrate: GeneralCrate({ Banana }) {
  override fun describe() = Constants.EQUIPMENT.BANANA_CRATE.name
}

class PieCrustCrate: GeneralCrate({ RawPie() }) {
  override fun describe() = Constants.EQUIPMENT.PIECRUST_CRATE.name
}

abstract class AllInstancesAreConsideredEqual: Equipment() {
  override fun equals(other: Any?): Boolean = other!!.javaClass == this.javaClass
  override fun hashCode(): Int = 0
}

abstract class GeneralCrate(val newItem: () -> Item) : AllInstancesAreConsideredEqual() {
  override fun receiveItem(player: Player, item: Item) {
    item.receiveItem(player, newItem(), null)
    player.heldItem = item
  }

  override fun takeFrom(player: Player) = newItem()
}

