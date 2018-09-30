package com.codingame.game.model

import com.codingame.game.Player

object Banana: Item()

data class ChoppingBoard(var pieOnBoard: Pie? = null): Equipment() {
  override fun clone() = copy()
  override fun basicNumber() = Constants.EQUIPMENT.CHOPPINGBOARD.ordinal
  override fun extras(): List<Int> {
    return when(pieOnBoard) {
      is Pie -> listOf(if (pieOnBoard!!.pieFlavour == PieFlavour.Strawberry) 0 else 1, pieOnBoard!!.pieces)
      else -> listOf(-1, -1)
    }
  }

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
      player.heldItem = null
      return
    }

    if (item === Banana) {
      checkVacant()
      player.heldItem = ChoppedBananas
      return
    }

    if (item is Dish) {
      // try to insta-plate
      item.receiveItem(player, getSlice(), null)
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
