package com.codingame.game.model

import com.codingame.game.Player

object IceCream : EdibleItem(Constants.FOOD.ICE_CREAM.name)
object Strawberries: EasilyDescribedItem(Constants.ITEM.STRAWBERRIES.name)
object ChoppedStrawberries: EdibleItem(Constants.FOOD.CHOPPED_STRAWBERRIES.name)
object Dough: EasilyDescribedItem(Constants.ITEM.DOUGH.name)
object Croissant: EdibleItem(Constants.FOOD.CROISSANT.name)
object Tart: EdibleItem(Constants.FOOD.TART.name)

data class Shell(var hasBlueberry: Boolean = false): Item() {
  override fun receiveItem(player: Player, item: Item, cell: Cell?) {
    if (item !is Blueberries) throw LogicException("Cannot add $item to $this!")
    if (hasBlueberry) throw LogicException("This already has blueberries!")

    player.heldItem = this.also { it.hasBlueberry = true }
    cell?.item = null
  }

  override fun describeTokens() =
    listOf(if (hasBlueberry) Constants.ITEM.RAW_TART.name else Constants.ITEM.CHOPPED_DOUGH.name)
}

object Blueberries: EdibleItem(Constants.FOOD.BLUEBERRIES.name) {
  override fun receiveItem(player: Player, item: Item, cell: Cell?) {
    if (item !is Shell) return super.receiveItem(player, item, cell)
    if (item.hasBlueberry) throw LogicException("This already has blueberries!")

    player.heldItem = item.also { it.hasBlueberry = true }
    cell?.item = null
  }
}