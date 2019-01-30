package com.codingame.game.model

import com.codingame.game.Player

class IceCreamCrate: GeneralCrate({ IceCream }) {
  override fun basicNumber(): Int = Constants.EQUIPMENT.ICE_CREAM_CRATE.ordinal
}

class StrawberryCrate: GeneralCrate({ Strawberries }) {
  override fun basicNumber() = Constants.EQUIPMENT.STRAWBERRY_CRATE.ordinal
}

class BlueberryCrate: GeneralCrate({ Blueberries }) {
  override fun basicNumber() = Constants.EQUIPMENT.BLUEBERRY_CRATE.ordinal
}

class BananaCrate: GeneralCrate({ Banana }) {
  override fun basicNumber() = Constants.EQUIPMENT.BANANA_CRATE.ordinal
}

class PieCrustCrate: GeneralCrate({ RawPie() }) {
  override fun basicNumber() = Constants.EQUIPMENT.PIECRUST_CRATE.ordinal
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

