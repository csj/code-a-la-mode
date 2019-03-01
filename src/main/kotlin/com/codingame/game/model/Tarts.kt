package com.codingame.game.model

import com.codingame.game.Player
import com.codingame.game.then

object Blueberries: EdibleItem(Constants.FOOD.BLUEBERRIES.name)
object Tart: EdibleItem(Constants.FOOD.TART.name)
object Croissant: EdibleItem(Constants.FOOD.CROISSANT.name)

object BurntCroissant: EasilyDescribedItem(Constants.ITEM.BURNT_CROISSANT.name)
object BurntTart: EasilyDescribedItem(Constants.ITEM.BURNT_TART.name)
object Dough: EasilyDescribedItem(Constants.ITEM.DOUGH.name)

sealed class OvenState(private val contentsStr: String, private val timer: Int) {
  override fun toString() = "$contentsStr $timer"

  object Empty: OvenState("NONE", 0)
  class Baking(val contents: EdibleItem, val timeUntilCooked: Int): OvenState(
      contents.describe(), timeUntilCooked
  )
  class Ready(val contents: EdibleItem, val timeUntilBurnt: Int): OvenState(
      contents.describe(), timeUntilBurnt
  )
  object BurnedCroissant: OvenState(BurntCroissant.describe(), 0)
  object BurnedTart: OvenState(BurntTart.describe(), 0)
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
      is OvenState.Baking -> {
        if (curState.timeUntilCooked == 1)
          OvenState.Ready(curState.contents, burnTime)
        else
          OvenState.Baking(curState.contents, curState.timeUntilCooked - 1)
      }
      is OvenState.Ready -> {
        val time = curState.timeUntilBurnt
        if (time == 1)
          (if (curState.contents is Croissant) OvenState.BurnedCroissant else OvenState.BurnedTart)
        else
          OvenState.Ready(curState.contents, curState.timeUntilBurnt - 1)
      }
      else -> return
    }
  }

  override fun receiveItem(player: Player, item: Item) {
    if (state is OvenState.Ready && item is Dish) {
      item.receiveItem(player, (state as OvenState.Ready).contents, null)
      player.heldItem = item
      state = OvenState.Empty
      return
    }

    if (state !== OvenState.Empty)
      throw LogicException("Cannot insert: oven not empty!")

    if (item is Shell && item.hasBlueberry) {
      state = OvenState.Baking(Tart, cookTime)
      player.heldItem = null
      return
    }

    if (item is Dough) {
      state = OvenState.Baking(Croissant, cookTime)
      player.heldItem = null
      return
    }

    super.receiveItem(player, item)
  }

  override fun takeFrom(player: Player): Item {
    lateinit var retVal: Item
    val curState = state
    state = when (curState) {
      OvenState.Empty -> throw LogicException("Cannot take from $this: nothing inside!")
      is OvenState.Baking -> throw LogicException("Cannot take from $this: food is baking!")
      is OvenState.Ready -> OvenState.Empty.also { retVal = curState.contents }
      OvenState.BurnedCroissant -> OvenState.Empty.also { retVal = BurntCroissant }
      OvenState.BurnedTart -> OvenState.Empty.also { retVal = BurntTart }
    }
    return retVal
  }
}

data class Shell(var hasBlueberry: Boolean = false): Item() {
  override fun receiveItem(player: Player, item: Item, cell: Cell?) {
    when (item) {
      is Blueberries -> {
        if (hasBlueberry) throw LogicException("This already has blueberries!")
        hasBlueberry = true
      }
      else -> {
        throw LogicException("Cannot add $item to $this!")
      }
    }
    player.heldItem = null
  }

  override fun describeTokens(): List<String> {
    return listOfNotNull(
        Constants.ITEM.SHELL.name,
        hasBlueberry.then(Constants.FOOD.BLUEBERRIES.name)
    )
  }
}

