package com.codingame.game.model

import com.codingame.game.Player

class ChoppingBoard: Equipment() {
  override val describeChar = 'C'
  override val tooltipString = "Chopping board"

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
