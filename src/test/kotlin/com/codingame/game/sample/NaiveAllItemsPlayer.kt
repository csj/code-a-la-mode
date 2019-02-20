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

  private fun findEquipment(equipmentChar: Char) =
      inputs.tables.firstOrNull { it.equipment == equipmentChar }

  init {
    var turn = 0
    while (true) {
      turn++
      inputs = readInputs()

//      if (turn > 5) throw Exception("ARRRGGG")

      crates = listOf(
          Constants.FOOD.BLUEBERRIES to 'B',
          Constants.FOOD.ICECREAM to 'I',
          Constants.ITEM.DOUGH to 'H',
          Constants.ITEM.STRAWBERRIES to 'S'
      ).map { (item, char) ->
        item.name to (inputs.tables.firstOrNull { it.equipment == char } )
      }.filter { (_, v) -> v != null }.map { (k, v) -> k to v!! }.toMap()

      stdout.println(act() ?: "WAIT")
    }
  }

  private fun act(): String? {
    val carrying = inputs.myPlayer.carrying

    // 0. If the oven has something ready, go get it!
    if (inputs.ovenContents in listOf(
        Constants.FOOD.CROISSANT.name,
        Constants.FOOD.TART.name,
        Constants.ITEM.BURNT_FOOD.name
    ))
      return if (carrying == null) findEquipment('O')!!.use() else useEmptyTable()

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
        return findEquipment('G')!!.use()

      // find next missing item from dish
      val missingItems = goalItems - dishContents
      val missingItem = missingItems.firstOrNull() ?:
        return findEquipment('W')!!.use()

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
      carrying == null -> crates[Constants.ITEM.STRAWBERRIES.name]!!.use()
      carrying.itemType == Constants.ITEM.STRAWBERRIES.name -> findEquipment('C')?.use()
      carrying.itemType == Constants.FOOD.CHOPPEDSTRAWBERRIES.name -> useEmptyTable()
      else -> { stderr.println("uhhh, holding: $carrying"); return useEmptyTable() }
    }
  }

  private fun isReady(item: String): Boolean =
      item in crates.keys ||
          inputs.tables.any { it.item?.itemType == item } ||
          (item == "CROISSANT" && inputs.ovenContents in listOf(
              Constants.ITEM.DOUGH.name,
              Constants.FOOD.CROISSANT.name
          ))

  private fun getEmptyPlate(): String = (
      inputs.tables.find { it.item?.itemType == Constants.ITEM.DISH.name }
          ?: findEquipment('D')!!).use()

  private fun useEmptyTable(): String {
    return inputs.myPlayer.run {
      inputs.tables.filter { it.equipment == null && it.item == null }
          .minBy { abs(it.x - x) + abs(it.y - y) }!!.use()
    }
  }

  private fun buildCroissant(): String? {
    return when(inputs.myPlayer.carrying?.itemType) {
      null -> crates[Constants.ITEM.DOUGH.name]!!.use()
      Constants.ITEM.DOUGH.name -> findEquipment('O')!!.use()
      else -> useEmptyTable()
    }
  }

  private fun buildTart(): String? {
    stderr.println("building tart")
    val x = inputs.myPlayer.x
    val y = inputs.myPlayer.y

    if (inputs.ovenContents != "NONE") {
      stderr.println("waiting for tart in oven")
      return findEquipment('O')!!.use()
    }

    val carrying = inputs.myPlayer.carrying
    when {
      carrying == null -> // find the closest tart shell, or get one from the box
      {
        stderr.println("looking for shell")
        val tart = (inputs.tables.filter {
          it.item?.itemType == Constants.ITEM.SHELL.name
        } + crates[Constants.ITEM.DOUGH.name]!!)
            .minBy { abs(it.x - x) + abs(it.y - y) }

        return tart!!.use()
      }

      carrying.itemType == Constants.ITEM.DOUGH.name ->
        return findEquipment('C')!!.use()

      carrying.itemType == Constants.ITEM.SHELL.name -> {
        stderr.println("doing stuff: carrying = $carrying, contents = ${carrying.itemContents}")
        return if (Constants.FOOD.BLUEBERRIES.name in carrying.itemContents) {
          stderr.println("shell is complete; heading for oven")
          findEquipment('O')!!.use()
        } else crates[Constants.FOOD.BLUEBERRIES.name]!!.use()
      }
    }

    return useEmptyTable()
  }

  private fun Table.use(): String = "USE $x $y"
}

