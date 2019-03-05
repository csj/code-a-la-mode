package com.codingame.game.model

import com.codingame.game.Player

sealed class OvenState(private val contentsStr: String, private val timer: Int) {
  override fun toString() = "$contentsStr $timer"

  object Empty: OvenState("NONE", 0)
  class Baking(val contents: Item, val timeUntilCooked: Int): OvenState(
      contents.describe(), timeUntilCooked
  )
  class Ready(val contents: EdibleItem, val timeUntilBurnt: Int): OvenState(
      contents.describe(), timeUntilBurnt
  )
  object Burning: OvenState("NONE", 0)
}

class Oven(
    private val cookTime: Int = Constants.OVEN_COOKTIME,
    private val burnTime: Int = Constants.OVEN_BURNTIME,
    var state: OvenState = OvenState.Empty) : TimeSensitiveEquipment() {

  override val tooltipString = "Oven"

  fun toViewString() : String {
    val curState = state
    return when (curState) {
      is OvenState.Empty -> ""
      is OvenState.Baking -> "Item:${curState.contents.describe()}\nTimer:${curState.timeUntilCooked}"
      is OvenState.Ready -> "Item:${curState.contents.describe()}\nBurn timer:${curState.timeUntilBurnt}"
      else -> "Food burnt to a crisp"
    }
  }

  override fun reset() { state = OvenState.Empty }
  override val describeChar = 'O'

  override fun tick() {
    val curState = state
    state = when (curState) {
      is OvenState.Empty -> return
      is OvenState.Burning -> OvenState.Empty
      is OvenState.Baking -> {
        if (curState.timeUntilCooked == 1)
          OvenState.Ready(
              when (curState.contents) {
                is Dough -> Croissant
                is Shell -> Tart
                else -> throw Exception("Wasn't expecting oven contents: ${curState.contents}!")
              },
              burnTime)
        else
          OvenState.Baking(curState.contents, curState.timeUntilCooked - 1)
      }
      is OvenState.Ready -> {
        val time = curState.timeUntilBurnt
        if (time == 1)
          OvenState.Burning
        else
          OvenState.Ready(curState.contents, curState.timeUntilBurnt - 1)
      }
    }
  }

  override fun receiveItem(player: Player, item: Item) {
    if (state is OvenState.Ready && item is Dish) {
      item.receiveItem(player, (state as OvenState.Ready).contents, null)
      player.heldItem = item
      state = OvenState.Empty
      return
    }

    if (state !in listOf(OvenState.Empty, OvenState.Burning))
      throw LogicException("Cannot insert: oven not empty!")

    if (item is Shell && item.hasBlueberry) {
      state = OvenState.Baking(item, cookTime)
      player.heldItem = null
      return
    }

    if (item is Dough) {
      state = OvenState.Baking(item, cookTime)
      player.heldItem = null
      return
    }

    super.receiveItem(player, item)
  }

  override fun takeFrom(player: Player): Item {
    lateinit var retVal: Item
    val curState = state
    state = when (curState) {
      OvenState.Empty, OvenState.Burning ->
        throw LogicException("Cannot take from $this: nothing inside!")
      is OvenState.Baking -> throw LogicException("Cannot take from $this: food is baking!")
      is OvenState.Ready -> OvenState.Empty.also { retVal = curState.contents }
    }
    return retVal
  }
}
