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
    // be responsible with the cutting board
    findEquipment(Constants.EQUIPMENT.CHOPPINGBOARD).let {
      if (it.equipmentState != -1) {
        stderr.println("clearing cutting board")
        return if (inputs.myPlayer.carrying == -1)
          it.use()
        else
          useEmptyTable()
      }
    }

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

      // if it has anything we don't need, jarbage it
      if (carryingState and goal != carryingState)
        return findEquipment(Constants.EQUIPMENT.JARBAGE).use()

      // find next missing item from dish
      val missingItem = items.firstOrNull { carryingState doesntHave it } ?:
        return findEquipment(Constants.EQUIPMENT.WINDOW).use()
      stderr.println("missing item: $missingItem")

      return when (missingItem) {
        in crates.keys -> {
          val crateLoc = crates[missingItem]!!
          stderr.println("going for crate at $crateLoc")
          crateLoc.use()
        }
        else -> {
          val tableLoc = inputs.tables.find {
            it.x > 0 && it.item == Constants.ITEM.FOOD.ordinal && it.itemState == missingItem
          }
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

    // 3. if we're holding something we shouldn't, put it down.
    if (carrying != -1) return useEmptyTable()

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
      else -> { stderr.println("uhhh, holding: $pair"); return useEmptyTable() }
    }
  }

  private fun isReady(item: Int): Boolean = item in crates.keys || inputs.tables.any {
    it.x > 0 && it.item == Constants.ITEM.FOOD.ordinal && it.itemState == item }

  private fun getEmptyPlate(): String = (
      inputs.tables.find { it.x >= 0 && it.item == Constants.ITEM.DISH.ordinal }
          ?: findEquipment(Constants.EQUIPMENT.DISH_RETURN)).use()

  private fun useEmptyTable(): String {
    return inputs.myPlayer.run {
      inputs.tables.filter { it.x > 0 && it.equipment == -1 && it.item == -1 }
          .minBy { abs(it.x - x) + abs(it.y - y) }!!.use()
    }
  }

  private fun buildWaffle(): String? {
    return if (inputs.myPlayer.carrying == -1)
      findEquipment(Constants.EQUIPMENT.WAFFLEIRON).use()
    else useEmptyTable()
  }

  private fun buildPie(isStrawberry: Boolean): String? {
    stderr.println("building pie")
    val x = inputs.myPlayer.x
    val y = inputs.myPlayer.y

    findEquipment(Constants.EQUIPMENT.OVEN).let {
      if (it.equipmentState != 0) {
        stderr.println("waiting for pie in oven")
        return it.use()
      }
    }

    when (inputs.myPlayer.carrying) {
      -1 -> // find the closest pie shell that will work, or get one from the box
      {
        stderr.println("looking for shell")
        val pie = inputs.tables.filter {
          it.x >= 0 && it.item == Constants.ITEM.RAW_PIE.ordinal && (
              it.itemState == 0 ||
                  (it.itemState in 10..19 && isStrawberry) ||
                  (it.itemState in 20..29 && !isStrawberry)
              )
        }.minBy { abs(it.x - x) + abs(it.y - y) } ?: findEquipment(Constants.EQUIPMENT.PIECRUST_CRATE)
        return pie.use()
      }

      Constants.ITEM.RAW_PIE.ordinal -> {
        stderr.println("doing stuff: carryingState = ${inputs.myPlayer.carryingState}")
        return when {
          inputs.myPlayer.carryingState in 11..12 -> findEquipment(Constants.EQUIPMENT.STRAWBERRY_CRATE).use()
          inputs.myPlayer.carryingState in 21..22 -> findEquipment(Constants.EQUIPMENT.BLUEBERRY_CRATE).use()
          inputs.myPlayer.carryingState == -1 ->
            findEquipment(if (isStrawberry) Constants.EQUIPMENT.STRAWBERRY_CRATE else
              Constants.EQUIPMENT.BLUEBERRY_CRATE).use()
          inputs.myPlayer.carryingState in listOf(20, 10) -> {
            stderr.println("pie is complete; heading for oven")
            findEquipment(Constants.EQUIPMENT.OVEN).use()
          }
          else -> useEmptyTable()
        }
      }

      Constants.ITEM.WHOLE_PIE.ordinal -> {
        stderr.println("holding whole pie; heading for chopping board")
        return findEquipment(Constants.EQUIPMENT.CHOPPINGBOARD).use()
      }

      else -> return useEmptyTable()
    }
  }

  private fun Table.use(): String = "USE $x $y"
}

