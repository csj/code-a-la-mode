package com.codingame.game.model

import com.codingame.game.Player

data class IceCreamCrate(val flavour: IceCreamFlavour): GeneralCrate({ IceCreamBall(flavour) })
{
  override fun basicNumber(): Int =
    when(flavour) {
      IceCreamFlavour.VANILLA -> Constants.EQUIPMENT.VANILLA_CRATE.ordinal
      IceCreamFlavour.CHOCOLATE -> Constants.EQUIPMENT.CHOCOLATE_CRATE.ordinal
      IceCreamFlavour.BUTTERSCOTCH -> Constants.EQUIPMENT.BUTTERSCOTCH_CRATE.ordinal
    }

  override fun clone(): Equipment = copy() }

class StrawberryCrate: GeneralCrate({ Strawberries }) {
  override fun basicNumber() = Constants.EQUIPMENT.STRAWBERRY_CRATE.ordinal
  override fun clone(): Equipment = StrawberryCrate()
}

class BlueberryCrate: GeneralCrate({ Blueberries }) {
  override fun clone(): Equipment = BlueberryCrate()
  override fun basicNumber() = Constants.EQUIPMENT.BLUEBERRY_CRATE.ordinal
}

class BananaCrate: GeneralCrate({ Banana }) {
  override fun clone(): Equipment = BananaCrate()
  override fun basicNumber() = Constants.EQUIPMENT.BANANA_CRATE.ordinal
}

class PieCrustCrate: GeneralCrate({ RawPie() }) {
  override fun clone(): Equipment = PieCrustCrate()
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

