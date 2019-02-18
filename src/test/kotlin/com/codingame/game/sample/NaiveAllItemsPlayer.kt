package com.codingame.game.sample

import com.codingame.game.model.*
import sample.*
import sample.Item
import java.io.InputStream
import java.io.PrintStream
import java.lang.Math.abs

class NaiveAllItemsPlayer(
    stdin: InputStream, stdout: PrintStream, stderr: PrintStream): BaseCALMPlayer(stdin, stdout, stderr) {

  lateinit var goal: Item
  lateinit var inputs: GameState
  lateinit var crates: Map<String, Table>

  private fun findEquipment(equipment: Constants.EQUIPMENT) =
      inputs.tables.firstOrNull { it.equipment?.equipmentType == equipment.name }

  private fun findCrate(item: String) = crates[item]!!

  init {
    var turn = 0
    while (true) {
      turn++
      inputs = readInputs()

//      if (turn > 5) throw Exception("ARRRGGG")

      crates = listOf(
          Constants.FOOD.BLUEBERRIES,
          Constants.FOOD.ICECREAM,
          Constants.ITEM.DOUGH,
          Constants.ITEM.STRAWBERRIES
      ).map { item ->
        item.name to (inputs.tables.firstOrNull {
          it.equipment?.equipmentType == "CRATE" &&
          it.equipment.equipmentState() == item.name })
      }.filter { (_, v) -> v != null }.map { (k, v) -> k to v!! }.toMap()

      stdout.println(act() ?: "WAIT")
    }
  }

  private fun act(): String? {
    val carrying = inputs.myPlayer.carrying

    // 0. If the oven has something ready, go get it!
    findEquipment(Constants.EQUIPMENT.OVEN)?.let {
      if (it.equipment!!.equipmentState() in listOf("READY", "BURNT"))
        return if (carrying == null) it.use() else useEmptyTable()
    }

    goal = inputs.queue.firstOrNull()?.dish ?: return null
    stderr.println("Current goal is: $goal")


    // make all the items and leave them on tables. then grab a plate and collect them all.
    val goalItems = goal.itemContents.toSet()

    // 1. if we're holding a plate, grab missing items from tables and such.
    // If one is missing, drop the plate
    if (carrying?.itemType == Constants.ITEM.DISH.name) {
      stderr.println("we are carrying a plate")

      val dishContents = carrying.itemContents.toSet()

      // if it has anything we don't need, jarbage it
      if ((dishContents - goalItems).isNotEmpty())
        return findEquipment(Constants.EQUIPMENT.GARBAGE)!!.use()

      // find next missing item from dish
      val missingItems = goalItems - dishContents
      val missingItem = missingItems.firstOrNull() ?:
      return findEquipment(Constants.EQUIPMENT.WINDOW)!!.use()

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
        Constants.FOOD.CROISSANT.name -> buildCroissant()
        Constants.FOOD.TART.name -> buildTart()
        Constants.FOOD.CHOPPEDSTRAWBERRIES.name -> buildStrawberries()
        else -> throw Exception("Unrecognized dish: $item")
      }
    }

    // 3. if we're holding something we shouldn't, put it down.
    if (carrying != null) return useEmptyTable()

    return getEmptyPlate()
  }

  private fun buildStrawberries(): String? {
    val carrying = inputs.myPlayer.carrying
    return when {
      carrying == null -> findCrate(Constants.ITEM.STRAWBERRIES.name).use()
      carrying.itemType == Constants.ITEM.STRAWBERRIES.name -> findEquipment(Constants.EQUIPMENT.CHOPPING_BOARD)?.use()
      carrying.itemType == Constants.FOOD.CHOPPEDSTRAWBERRIES.name -> useEmptyTable()
      else -> { stderr.println("uhhh, holding: $carrying"); return useEmptyTable() }
    }
  }

  private fun isReady(item: String): Boolean =
      item in crates.keys ||
          inputs.tables.any { it.item?.itemType == item } ||
          (item == "CROISSANT" &&
              findEquipment(Constants.EQUIPMENT.OVEN)!!.equipment!!.let {
                it.equipmentState() in listOf("BAKING", "READY") &&
                    it.equipmentContents() == "CROISSANT"
              })

  private fun getEmptyPlate(): String = (
      inputs.tables.find { it.item?.itemType == Constants.ITEM.DISH.name }
          ?: findEquipment(Constants.EQUIPMENT.DISH_RETURN)!!).use()

  private fun useEmptyTable(): String {
    return inputs.myPlayer.run {
      inputs.tables.filter { it.equipment == null && it.item == null }
          .minBy { abs(it.x - x) + abs(it.y - y) }!!.use()
    }
  }

  private fun buildCroissant(): String? {
    return when(inputs.myPlayer.carrying?.itemType) {
      null -> findCrate(Constants.ITEM.DOUGH.name).use()
      Constants.ITEM.DOUGH.name -> findEquipment(Constants.EQUIPMENT.OVEN)!!.use()
      else -> useEmptyTable()
    }
  }

  private fun buildTart(): String? {
    stderr.println("building tart")
    val x = inputs.myPlayer.x
    val y = inputs.myPlayer.y

    findEquipment(Constants.EQUIPMENT.OVEN)!!.let {
      if (it.equipment!!.equipmentState() != "EMPTY") {
        stderr.println("waiting for tart in oven")
        return it.use()
      }
    }

    val carrying = inputs.myPlayer.carrying
    when {
      carrying == null -> // find the closest tart shell, or get one from the box
      {
        stderr.println("looking for shell")
        val tart = (inputs.tables.filter {
          it.item?.itemType == Constants.ITEM.SHELL.name
        } + findCrate(Constants.ITEM.DOUGH.name))
            .minBy { abs(it.x - x) + abs(it.y - y) }

        return tart!!.use()
      }

      carrying.itemType == Constants.ITEM.DOUGH.name ->
        return findEquipment(Constants.EQUIPMENT.CHOPPING_BOARD)!!.use()

      carrying.itemType == Constants.ITEM.SHELL.name -> {
        stderr.println("doing stuff: carrying = $carrying, contents = ${carrying.itemContents}")
        return if (Constants.FOOD.BLUEBERRIES.name in carrying.itemContents) {
          stderr.println("shell is complete; heading for oven")
          findEquipment(Constants.EQUIPMENT.OVEN)!!.use()
        } else findCrate(Constants.FOOD.BLUEBERRIES.name).use()
      }
    }

    return useEmptyTable()
  }

  private fun Table.use(): String = "USE $x $y"
}

