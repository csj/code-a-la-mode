package com.codingame.game.model

import com.codingame.game.Player

object Banana: Item()

data class ChoppingBoard(var pieOnBoard: Pie? = null): Equipment() {
  override fun clone() = copy()
  override fun describeAsNumber() = Constants.EQUIPMENT.CHOPPINGBOARD.ordinal

  private fun checkVacant() {
    if (pieOnBoard != null) throw Exception("Chopping board is not vacant")
  }

  private fun getSlice(): PieSlice {
    val pie = pieOnBoard ?: throw Exception("No pie slices to get!")
    pieOnBoard = when (pie.pieces) {
      1 -> null
      else -> pie.copy(pieces = pie.pieces - 1)
    }
    return PieSlice(pie.pieFlavour)
  }

  override fun takeFrom(player: Player) = getSlice()

  override fun receiveItem(player: Player, item: Item) {
    if (item is Pie) {
      putPie(item)
      return
    }

    if (item === Banana) {
      checkVacant()
      player.heldItem = ChoppedBananas
      return
    }

    if (item is Dish) {
      // try to insta-plate
      item.receiveItem(player, getSlice())
      player.heldItem = item
      return
    }
    super.receiveItem(player, item)
  }

  private fun putPie(pie: Pie) {
    checkVacant()
    pieOnBoard = pie
  }
}
