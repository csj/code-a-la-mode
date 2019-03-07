package com.codingame.game.sample

import com.codingame.game.model.*
import com.codingame.game.splitAccumulate
import com.codingame.game.tryNext
import sample.*
import sample.Item
import java.io.InputStream
import java.io.PrintStream
import java.lang.Math.abs

class NaiveAllItemsPlayer(
    stdin: InputStream, stdout: PrintStream, stderr: PrintStream): BaseCALMPlayer(stdin, stdout, stderr) {

  val lyrics = """
How come you're
always such a
fussy young man?
Don't want no
Captain Crunch,
don't want no
Raisin Bran?!
Well, don't you
know that other kids
are starving in
Japan? So eat it
Just eat it
Don't wanna argue
I don't wanna debate
Don't wanna hear
about what kind of
food you hate
You won't get no
dessert 'till you
clean off your plate
So eat it. Don't you
tell me you're full
Just eat it, eat it,
eat it, eat it
Get yourself an egg
and beat it
Have some more
chicken, have some
more pie, it doesn't
matter if it's
broiled or fried
Just eat it, eat it,
just eat it, eat it
Your table manners are
a cryin' shame
You're playin' with
your food, this
ain't some kind of
game! Now, if you
starve to death,
you'll just have
yourself to blame
So eat it
just eat it
You better listen
better do what
you're told, you
haven't even
touched your tuna
casserole
You better chow
down or it's gonna
get cold So eat it
I don't care if
you're full
Just eat it, eat it
Eat it, eat it
Open up your mouth
and feed it
Have some more
yogurt, have some
more Spam
It doesn't matter
if it's fresh or
canned Just eat it
Eat it, eat it
Don't you make me
Repeat it
Have a banana
Have a whole bunch
It doesn't matter
what you had for
lunch, just eat it!"""
      .split("\n", " ")
      .asSequence()
      .splitAccumulate()
      .iterator()

  lateinit var goal: Item
  lateinit var crates: Map<String, Table>

  init {
    var turn = 0
    while (true) {
      turn++
      readInputs()

      crates = listOf(
          Constants.FOOD.BLUEBERRIES to 'B',
          Constants.FOOD.ICE_CREAM to 'I',
          Constants.ITEM.DOUGH to 'H',
          Constants.ITEM.STRAWBERRIES to 'S'
      ).map { (item, char) ->
        item.name to (inputs.tables.firstOrNull { it.equipment == char } )
      }.filter { (_, v) -> v != null }.map { (k, v) -> k to v!! }.toMap()

      stdout.println((act() ?: "WAIT") + ";" + (lyrics.tryNext() ?: ""))
    }
  }

  private fun act(): String? {
    val carrying = inputs.myPlayer.carrying

    // 0. If the oven has something ready, go get it!
    if (inputs.ovenContents in listOf(
        Constants.FOOD.CROISSANT.name,
        Constants.FOOD.TART.name
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

      // if it has anything we don't need, dishwasher it
      if ((dishContents - goalItems).isNotEmpty())
        return findEquipment('D')!!.use()

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
        Constants.FOOD.CHOPPED_STRAWBERRIES.name -> buildStrawberries()
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
      carrying.itemType == Constants.FOOD.CHOPPED_STRAWBERRIES.name -> useEmptyTable()
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
          it.item?.itemType == Constants.ITEM.CHOPPED_DOUGH.name
        } + crates[Constants.ITEM.DOUGH.name]!!)
            .minBy { abs(it.x - x) + abs(it.y - y) }

        return tart!!.use()
      }

      carrying.itemType == Constants.ITEM.DOUGH.name ->
        return findEquipment('C')!!.use()

      carrying.itemType == Constants.ITEM.CHOPPED_DOUGH.name ->
        return crates[Constants.FOOD.BLUEBERRIES.name]!!.use()

      carrying.itemType == Constants.ITEM.RAW_TART.name -> {
        stderr.println("shell is complete; heading for oven")
        return findEquipment('O')!!.use()
      }
    }

    return useEmptyTable()
  }

  private fun Table.use(): String = "USE $x $y"
}
