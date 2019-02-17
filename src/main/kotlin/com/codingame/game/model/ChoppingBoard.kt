package com.codingame.game.model

import com.codingame.game.Player

object Strawberries: EasilyDescribedItem(Constants.ITEM.STRAWBERRIES.name)
object ChoppedStrawberries: EdibleItem(Constants.FOOD.CHOPPEDSTRAWBERRIES.name)

class ChoppingBoard: Equipment() {
  override fun describe() = Constants.EQUIPMENT.CHOPPING_BOARD.name

  override fun receiveItem(player: Player, item: Item) {
    if (item === Strawberries) {
      player.heldItem = ChoppedStrawberries
      return
    }

    if (item === Dough) {
      player.heldItem = Shell()
      return
    }

    super.receiveItem(player, item)
  }
}
