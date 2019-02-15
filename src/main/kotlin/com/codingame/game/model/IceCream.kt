package com.codingame.game.model

import com.codingame.game.Player

object IceCream : EdibleItem(Constants.FOOD.ICECREAM.name) {
  override fun take(player: Player, cell: Cell) {
    throw Exception("Cannot take $this directly!")
  }
}