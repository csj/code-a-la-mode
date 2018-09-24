package com.codingame.game.sample

import com.codingame.game.model.*
import sample.*
import java.io.InputStream
import java.io.PrintStream
import java.lang.Math.abs

class NaiveAllItemsPlayer(
    stdin: InputStream, stdout: PrintStream, stderr: PrintStream): BaseCALMPlayer(stdin, stdout, stderr) {

  var nextGoal: Pair<Int, Int>? = null
  lateinit var inputs: GameState
  lateinit var crates: Map<Int, Table>

  fun findEquipment(equipment: Constants.EQUIPMENT): Table {
    return inputs.tables.firstOrNull { it.x >= 0 && it.equipment == equipment.ordinal }
      ?: throw Exception("Couldn't find equipment: $equipment")
  }

  init {
    while (true) {
      inputs = readInputs()

      crates = mapOf(
          Constants.FOOD.BLUEBERRIES to Constants.EQUIPMENT.BLUEBERRY_CRATE,
          Constants.FOOD.STRAWBERRIES to Constants.EQUIPMENT.STRAWBERRY_CRATE,
          Constants.FOOD.VANILLA_BALL to Constants.EQUIPMENT.VANILLA_CRATE,
          Constants.FOOD.CHOCOLATE_BALL to Constants.EQUIPMENT.CHOCOLATE_CRATE,
          Constants.FOOD.BUTTERSCOTCH_BALL to Constants.EQUIPMENT.BUTTERSCOTCH_CRATE
      )
          .mapKeys { (key, _) -> key.value }
          .mapValues { (_, crateVal) -> inputs.tables.firstOrNull { it.x > 0 && it.equipment == crateVal.ordinal } ?:
        throw Exception("Couldn't find equipment: $crateVal")
      }

      nextGoal = inputs.queue.firstOrNull()?.let { Pair(it.item, it.itemState)}
      stdout.println(chaseGoal() ?: "WAIT")
    }
  }

  private fun chaseGoal(): String? {
    val (item, itemState) = nextGoal ?: return null
    stderr.println("Current goal is: $item/$itemState")
    return when (item) {
      Constants.ITEM.DISH.ordinal -> chaseDish(itemState)
      Constants.ITEM.MILKSHAKE.ordinal -> chaseShake(itemState)
      else -> null   // pick nose
    }
  }

  private fun chaseShake(goal: Int): String? {
    return null
  }

  private fun chaseDish(goal: Int): String? {
    stderr.println("chasing dish")

    val carrying = inputs.myPlayer.carrying
    val carryingState = inputs.myPlayer.carryingState

    // make all the items and leave them on tables. then grab a plate and collect them all.
    val items = goal.toFlags()

    // 1. if we're holding a plate, grab missing items from tables and such.
    // If one is missing, drop the plate
    if (carrying == Constants.ITEM.DISH.ordinal) {
      stderr.println("we are carrying a plate")

      // find next missing item from dish
      val missingItem = items.firstOrNull { carryingState doesntHave it } ?:
        return findEquipment(Constants.EQUIPMENT.WINDOW).use()
      stderr.println("missing item: $missingItem")

      return when (missingItem) {
        in crates.keys -> {
          val crateLoc = crates[missingItem]!!
          crateLoc.use()
        }
        else -> {
          val tableLoc = inputs.tables.find { it.item == missingItem }
          if (tableLoc != null) tableLoc.use()
          else useEmptyTable()  // put down the dish
        }
      }
    }

    // 2. Build missing items. If all items are built, grab an empty plate.
    items.forEach { item ->
      if (isReady(item)) return@forEach
      return when(item) {
        Constants.FOOD.WAFFLE.value -> buildWaffle()
        Constants.FOOD.STRAWBERRY_PIE.value -> buildPie(true)
        Constants.FOOD.BLUEBERRY_PIE.value -> buildPie(false)
        Constants.FOOD.CHOPPED_BANANAS.value -> buildBananas()
        else -> throw Exception("Unrecognized item: $item")
      }
    }
    return getEmptyPlate()
  }

  private fun buildBananas(): String? {
    val me = inputs.myPlayer
    val pair = Pair(inputs.myPlayer.carrying, inputs.myPlayer.carryingState)
    return when {
      me.carrying == -1 -> findEquipment(Constants.EQUIPMENT.BANANA_CRATE).use()
      me.carrying == Constants.ITEM.BANANA.ordinal -> findEquipment(Constants.EQUIPMENT.CHOPPINGBOARD).use()
      me.carrying == Constants.ITEM.FOOD.ordinal &&
          me.carryingState == Constants.FOOD.CHOPPED_BANANAS.value ->
        useEmptyTable()
      else -> { stderr.println("uhhh, holding: $pair"); return null }
    }
  }

  private fun isReady(item: Int): Boolean = item in crates.keys || inputs.tables.any {
    it.item == Constants.ITEM.FOOD.ordinal && it.itemState == item }

  private fun getEmptyPlate(): String {
    return (inputs.tables.find { it.x >= 0 && it.item == Constants.ITEM.DISH.ordinal && it.itemState == 0 }
        ?: findEquipment(Constants.EQUIPMENT.DISH_RETURN)).use()
  }

  private fun useEmptyTable(): String {
    return inputs.myPlayer.run {
      inputs.tables.filter { it.x > 0 && it.equipment == -1 && it.item == -1 }
          .minBy { abs(it.x - x) + abs(it.y - y) }!!.use()
    }
  }

  private fun buildWaffle(): String? {
    stderr.println("building waffle, obv")
    return null
  }

  private fun buildPie(isStrawberry: Boolean): String? {
    stderr.println("building pie, obv")
    return null
  }

  private fun Table.use(): String = "USE $x $y"
}

