package com.codingame.game.model

import com.codingame.game.Player

enum class PieFlavour {
  Strawberry,
  Blueberry
}

object Strawberries: EdibleItem()
object Blueberries: EdibleItem()
object BlueberrySlice: EdibleItem()
object StrawberrySlice: EdibleItem()
data class Pie(val pieFlavour: PieFlavour, val pieces: Int = 4): Item()
object BurntPie: Item()

sealed class OvenState(private val stateToks: List<Any>) {
  override fun toString() = stateToks.joinToString("-")

  object Empty: OvenState(listOf("EMPTY"))
  class Cooking(val flavour: PieFlavour, val timeUntilCooked: Int): OvenState(
      listOf(
          "COOKING",
          if (flavour == PieFlavour.Blueberry) "BLUEBERRY" else "STRAWBERRY",
          timeUntilCooked
      )
  )
  class Cooked(val flavour: PieFlavour, val timeUntilBurnt: Int): OvenState(
      listOf(
          "COOKED",
          if (flavour == PieFlavour.Blueberry) "BLUEBERRY" else "STRAWBERRY",
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
      is OvenState.Cooking -> {
        if (curState.timeUntilCooked == 1)
          OvenState.Cooked(curState.flavour, burnTime)
        else
          OvenState.Cooking(curState.flavour, curState.timeUntilCooked - 1)
      }
      is OvenState.Cooked -> {
        val time = curState.timeUntilBurnt
        if (time == 1) OvenState.Burnt else
          OvenState.Cooked(curState.flavour, curState.timeUntilBurnt - 1)
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
    else throw LogicException("Cannot insert pie: oven not empty!")
  }

  override fun takeFrom(player: Player): Item {
    lateinit var retVal: Item
    val curState = state
    state = when (curState) {
      OvenState.Empty -> throw LogicException("Cannot take from $this: nothing inside!")
      is OvenState.Cooking -> throw LogicException("Cannot take from $this: pie is cooking!")
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
        if (fruitsMissing == 0) throw LogicException("Cannot add more fruit: already full!")
        fruitsMissing--
      }
      else -> {
        throw LogicException("Cannot add $item to $this: wrong flavour!")
      }
    }
    player.heldItem = null
  }
}

