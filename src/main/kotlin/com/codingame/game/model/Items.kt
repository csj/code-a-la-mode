package com.codingame.game.model

import com.codingame.game.Player

object IceCream : EdibleItem(Constants.FOOD.ICE_CREAM.name)
object Blueberries: EdibleItem(Constants.FOOD.BLUEBERRIES.name)
object Strawberries: EasilyDescribedItem(Constants.ITEM.STRAWBERRIES.name)
object ChoppedStrawberries: EdibleItem(Constants.FOOD.CHOPPED_STRAWBERRIES.name)
object Dough: EasilyDescribedItem(Constants.ITEM.DOUGH.name)
object Croissant: EdibleItem(Constants.FOOD.CROISSANT.name)
object Tart: EdibleItem(Constants.FOOD.TART.name)

data class Shell(var hasBlueberry: Boolean = false): Item() {
  override fun receiveItem(player: Player, item: Item, cell: Cell?) {
    when (item) {
      is Blueberries -> {
        if (hasBlueberry) throw LogicException("This already has blueberries!")
          hasBlueberry = true
        }
      else -> throw LogicException("Cannot add $item to $this!")
    }
    player.heldItem = null
  }

  override fun describeTokens() =
    listOf(if (hasBlueberry) Constants.ITEM.RAW_TART.name else Constants.ITEM.SHELL.name)
}
