package com.codingame.game.model

import com.codingame.game.Player

object Banana: Item()
object ChoppedBananas: EdibleItem()

data class ChoppingBoard(var pieOnBoard: Pie? = null): Equipment() {
  override fun reset() { pieOnBoard = null }

  override fun describe() = (listOf("CHOPPINGBOARD") + (pieOnBoard?.let { pie ->
    List(pie.pieces) {
      when (pie.pieFlavour) {
        PieFlavour.Strawberry -> "STRAWBERRYSLICE"
        PieFlavour.Blueberry -> "BLUEBERRYSLICE"
      }
    }
  } ?: emptyList())).joinToString("-")

  private fun checkVacant() {
    if (pieOnBoard != null) throw LogicException("Chopping board is not vacant!")
  }

  private fun getSlice(): EdibleItem {
    val pie = pieOnBoard ?: throw LogicException("No pie slices to get!")
    pieOnBoard = when (pie.pieces) {
      1 -> null
      else -> pie.copy(pieces = pie.pieces - 1)
    }
    return when (pie.pieFlavour) {
      PieFlavour.Strawberry -> StrawberrySlice
      PieFlavour.Blueberry -> BlueberrySlice
    }
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
