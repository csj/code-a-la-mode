package com.codingame.game.model

import com.codingame.game.Player

class Jarbage: AllInstancesAreConsideredEqual() {
  override fun clone() = Jarbage()
  override fun basicNumber(): Int = Constants.EQUIPMENT.JARBAGE.ordinal

  override fun receiveItem(player: Player, item: Item) {
    player.heldItem = if (item is Dish) Dish() else null
  }
}