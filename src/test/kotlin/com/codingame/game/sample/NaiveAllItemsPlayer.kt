package com.codingame.game.sample

import com.codingame.game.model.*
import sample.*
import sample.Item
import java.io.InputStream
import java.io.PrintStream
import java.lang.Math.abs

class NaiveAllItemsPlayer(
    stdin: InputStream, stdout: PrintStream, stderr: PrintStream): BaseCALMPlayer(stdin, stdout, stderr) {

  var nextGoal: Item? = null
  lateinit var inputs: GameState
  lateinit var crates: Map<String, Table>

  private fun findEquipment(equipment: Constants.EQUIPMENT): Table {
    return inputs.tables.firstOrNull { it.equipment?.equipmentType == equipment.name }
      ?: throw Exception("Couldn't find equipment: $equipment")
  }

  init {
    var turn = 0
    while (true) {
      turn++
      inputs = readInputs()

//      if (turn > 5) throw Exception("ARRRGGG")

      crates = mapOf(
          Constants.FOOD.BLUEBERRIES to Constants.EQUIPMENT.BLUEBERRY_CRATE,
          Constants.FOOD.STRAWBERRIES to Constants.EQUIPMENT.STRAWBERRY_CRATE,
          Constants.FOOD.ICECREAM to Constants.EQUIPMENT.ICE_CREAM_CRATE
      )
          .mapKeys { (key, _) -> key.name }
          .mapValues { (_, crateVal) -> inputs.tables.firstOrNull { it.equipment?.equipmentType == crateVal.name } ?:
        throw Exception("Couldn't find equipment: $crateVal")
      }

      nextGoal = inputs.queue.firstOrNull()?.dish
      stdout.println(chaseGoal() ?: "WAIT")
    }
  }

  private fun chaseGoal(): String? {
    // be responsible with the cutting board
    findEquipment(Constants.EQUIPMENT.CHOPPINGBOARD).let {
      if (it.equipment?.equipmentContents()?.isNotEmpty() == true) {
        stderr.println("clearing cutting board")
        return if (inputs.myPlayer.carrying == null)
          it.use()
        else
          useEmptyTable()
      }
    }

    if (nextGoal == null) return null
    stderr.println("Current goal is: $nextGoal")
    return chaseDish(nextGoal!!)
  }

  private fun chaseDish(goal: Item): String? {
    val carrying = inputs.myPlayer.carrying

    // make all the items and leave them on tables. then grab a plate and collect them all.
    val goalItems = goal.itemContents.toSet()

    // 1. if we're holding a plate, grab missing items from tables and such.
    // If one is missing, drop the plate
    if (carrying?.itemType == Constants.ITEM.DISH.name) {
      stderr.println("we are carrying a plate")

      val dishContents = carrying.itemContents.toSet()

      // if it has anything we don't need, jarbage it
      if ((dishContents - goalItems).isNotEmpty())
        return findEquipment(Constants.EQUIPMENT.GARBAGE).use()

      // find next missing item from dish
      val missingItems = goalItems - dishContents
      val missingItem = missingItems.firstOrNull() ?:
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
            it.item?.itemType == missingItem
          }
          if (tableLoc != null) tableLoc.use()
          else useEmptyTable()  // put down the dish
        }
      }
    }

    // 2. Build missing items. If all items are built, grab an empty plate.
    goalItems.forEach { item ->
      if (isReady(item)) return@forEach
      return when(item) {
        Constants.FOOD.WAFFLE.name -> buildWaffle()
        Constants.FOOD.STRAWBERRYSLICE.name -> buildPie(true)
        Constants.FOOD.BLUEBERRYSLICE.name -> buildPie(false)
        Constants.FOOD.CHOPPEDBANANAS.name -> buildBananas()
        else -> throw Exception("Unrecognized dish: $item")
      }
    }

    // 3. if we're holding something we shouldn't, put it down.
    if (carrying != null) return useEmptyTable()

    return getEmptyPlate()
  }

  private fun buildBananas(): String? {
    val carrying = inputs.myPlayer.carrying
    return when {
      carrying == null -> findEquipment(Constants.EQUIPMENT.BANANA_CRATE).use()
      carrying.itemType == Constants.ITEM.BANANA.name -> findEquipment(Constants.EQUIPMENT.CHOPPINGBOARD).use()
      carrying.itemType == Constants.FOOD.CHOPPEDBANANAS.name -> useEmptyTable()
      else -> { stderr.println("uhhh, holding: $carrying"); return useEmptyTable() }
    }
  }

  private fun isReady(item: String): Boolean = item in crates.keys || inputs.tables.any {
    it.item?.itemType == item }

  private fun getEmptyPlate(): String = (
      inputs.tables.find { it.item?.itemType == Constants.ITEM.DISH.name }
          ?: findEquipment(Constants.EQUIPMENT.DISH_RETURN)).use()

  private fun useEmptyTable(): String {
    return inputs.myPlayer.run {
      inputs.tables.filter { it.equipment == null && it.item == null }
          .minBy { abs(it.x - x) + abs(it.y - y) }!!.use()
    }
  }

  private fun buildWaffle(): String? {
    return if (inputs.myPlayer.carrying == null)
      findEquipment(Constants.EQUIPMENT.WAFFLEIRON).use()
    else useEmptyTable()
  }

  private fun buildPie(isStrawberry: Boolean): String? {
    stderr.println("building pie")
    val x = inputs.myPlayer.x
    val y = inputs.myPlayer.y

    findEquipment(Constants.EQUIPMENT.OVEN).let {
      if (it.equipment!!.equipmentState() != "EMPTY") {
        stderr.println("waiting for pie in oven")
        return it.use()
      }
    }

    val carrying = inputs.myPlayer.carrying
    when {
      carrying == null -> // find the closest pie shell that will work, or get one from the box
      {
        stderr.println("looking for shell")
        val pie = inputs.tables.filter {
          it.item?.itemType == Constants.ITEM.RAW_PIE.name && (
              it.item.itemContents.isEmpty() ||
              it.item.itemContents[0] == Constants.FOOD.STRAWBERRIES.name && isStrawberry ||
              it.item.itemContents[0] == Constants.FOOD.BLUEBERRIES.name && !isStrawberry
              )
        }.minBy { abs(it.x - x) + abs(it.y - y) } ?:
          findEquipment(Constants.EQUIPMENT.PIECRUST_CRATE)
        return pie.use()
      }

      carrying.itemType == Constants.ITEM.RAW_PIE.name -> {
        stderr.println("doing stuff: carrying = $carrying")
        return when {
          carrying.itemContents.isEmpty() ->
            findEquipment(if (isStrawberry) Constants.EQUIPMENT.STRAWBERRY_CRATE else
              Constants.EQUIPMENT.BLUEBERRY_CRATE).use()

          carrying.itemContents.size == 3 -> {
            stderr.println("pie is complete; heading for oven")
            findEquipment(Constants.EQUIPMENT.OVEN).use()
          }

          carrying.itemContents[0] == Constants.FOOD.STRAWBERRIES.name ->
            findEquipment(Constants.EQUIPMENT.STRAWBERRY_CRATE).use()

          carrying.itemContents[0] == Constants.FOOD.BLUEBERRIES.name ->
            findEquipment(Constants.EQUIPMENT.BLUEBERRY_CRATE).use()

          else -> useEmptyTable()
        }
      }

      carrying.itemType == Constants.ITEM.WHOLE_PIE.name -> {
        stderr.println("holding whole pie; heading for chopping board")
        return findEquipment(Constants.EQUIPMENT.CHOPPINGBOARD).use()
      }

      else -> return useEmptyTable()
    }
  }

  private fun Table.use(): String = "USE $x $y"
}

