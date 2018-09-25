package com.codingame.game.model

import com.codingame.game.Player

enum class PieFlavour {
  Strawberry,
  Blueberry
}

object Strawberries: EdibleItem()
object Blueberries: EdibleItem()
data class Pie(val pieFlavour: PieFlavour, val pieces: Int = 4): Item()
data class PieSlice(val pieFlavour: PieFlavour): EdibleItem()
object BurntPie: Item()

sealed class OvenState {
  object Empty: OvenState()
  data class Cooking(val flavour: PieFlavour, val timeUntilCooked: Int): OvenState()
  data class Cooked(val flavour: PieFlavour, val timeUntilBurnt: Int): OvenState()
  object Burnt: OvenState()
}

data class Oven(private val cookTime: Int, private val burnTime: Int, private var state: OvenState = OvenState.Empty) : TimeSensitiveEquipment() {
  override fun clone(): Equipment = copy()
  override fun describeAsNumber() = Constants.EQUIPMENT.OVEN.ordinal

  override fun tick() {
    val curState = state
    state = when (curState) {
      is OvenState.Empty -> return
      is OvenState.Cooking -> {
        val (fl, time) = curState
        if (time == 1) OvenState.Cooked(fl, burnTime) else curState.copy(timeUntilCooked = time-1)
      }
      is OvenState.Cooked -> {
        val time = curState.timeUntilBurnt
        if (time == 1) OvenState.Burnt else curState.copy(timeUntilBurnt = time-1)
      }
      is OvenState.Burnt -> return
    }
  }

  override fun receiveItem(player: Player, item: Item) {
    if (item is RawPie && item.pieFlavour != null && item.fruitsMissing == 0) {
      insertRawPie(item.pieFlavour!!)
      player.heldItem = null
      return
    }
    super.receiveItem(player, item)
  }

  private fun insertRawPie(flavour: PieFlavour) {
    if (state === OvenState.Empty)
      state = OvenState.Cooking(flavour, cookTime)
    else throw Exception("Cannot insert pie: oven not empty!")
  }

  override fun takeFrom(player: Player): Item {
    lateinit var retVal: Item
    val curState = state
    state = when (curState) {
      OvenState.Empty -> throw Exception("Cannot take from $this: nothing inside!")
      is OvenState.Cooking -> throw Exception("Cannot take from $this: pie is cooking!")
      is OvenState.Cooked -> OvenState.Empty.also { retVal = Pie(curState.flavour) }
      OvenState.Burnt -> OvenState.Empty.also { retVal = BurntPie }
    }
    return retVal
  }

}

data class RawPie(var pieFlavour: PieFlavour? = null, var fruitsMissing: Int = 0): Item() {
  val flavourMap = mapOf(
      Strawberries to PieFlavour.Strawberry,
      Blueberries to PieFlavour.Blueberry
  )

  override fun receiveItem(player: Player, item: Item, cell: Cell?) {
    val flavour = flavourMap[item] ?: throw Exception("Cannot add $item to $this: it's not a fruit!")
    when (pieFlavour) {
      null -> {
        pieFlavour = flavour
        fruitsMissing = Constants.PIE_FRUITS_NEEDED - 1
      }
      flavour -> {
        if (fruitsMissing == 0) throw Exception("Cannot add more fruit: already full!")
        fruitsMissing--
      }
      else -> {
        throw Exception("Cannot add $item to $this: wrong flavour!")
      }
    }
    player.heldItem = null
  }
}

