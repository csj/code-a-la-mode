package com.codingame.game.model

import com.codingame.game.Player

object Banana: EasilyDescribedItem(Constants.ITEM.BANANA.name)
object ChoppedBananas: EdibleItem(Constants.FOOD.CHOPPEDBANANAS.name)

class ChoppingBoard: Equipment() {
  override fun describe() = Constants.EQUIPMENT.CHOPPING_BOARD.name

  override fun receiveItem(player: Player, item: Item) {
    if (item === Banana) {
      player.heldItem = ChoppedBananas
      return
    }

    if (item === Dough) {
      player.heldItem = Shell()
      return
    }

    super.receiveItem(player, item)
  }
}
