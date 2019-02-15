package com.codingame.game.model

import com.codingame.game.Player
import com.codingame.game.then

object Strawberries: EdibleItem(Constants.FOOD.STRAWBERRIES.name)
object Blueberries: EdibleItem(Constants.FOOD.BLUEBERRIES.name)
object Tart: EdibleItem(Constants.FOOD.TART.name)
object BurntFood: EasilyDescribedItem(Constants.ITEM.BURNT_FOOD.name)
object Dough: EasilyDescribedItem(Constants.ITEM.DOUGH.name)
object Croissant: EdibleItem(Constants.FOOD.CROISSANT.name)

sealed class OvenState(private val stateToks: List<Any>) {
  override fun toString() = stateToks.joinToString("-")

  object Empty: OvenState(listOf("EMPTY"))
  class Baking(val contents: EdibleItem, val timeUntilCooked: Int): OvenState(
      listOf(
          "BAKING",
          contents.describe(),
          timeUntilCooked
      )
  )
  class Ready(val contents: EdibleItem, val timeUntilBurnt: Int): OvenState(
      listOf(
          "READY",
          contents.describe(),
          timeUntilBurnt
      )
  )
  object Burnt: OvenState(listOf("BURNT"))
}

data class Oven(private val cookTime: Int, private val burnTime: Int, private var state: OvenState = OvenState.Empty) : TimeSensitiveEquipment() {
  override fun reset() { state = OvenState.Empty }
  override fun describe() = "OVEN-$state"

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
        if (time == 1) OvenState.Burnt else
          OvenState.Ready(curState.contents, curState.timeUntilBurnt - 1)
      }
      is OvenState.Burnt -> return
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

    if (item is Shell && item.hasBlueberry && item.hasStrawberry) {
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
      OvenState.Burnt -> OvenState.Empty.also { retVal = BurntFood }
    }
    return retVal
  }
}

data class Shell(var hasBlueberry: Boolean = false, var hasStrawberry: Boolean = false): Item() {
  override fun receiveItem(player: Player, item: Item, cell: Cell?) {
    when (item) {
      is Strawberries -> {
        if (hasStrawberry) throw LogicException("This already has strawberries!")
        hasStrawberry = true
      }
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
        hasBlueberry.then(Constants.FOOD.BLUEBERRIES.name),
        hasStrawberry.then(Constants.FOOD.STRAWBERRIES.name)
    )
  }
}

