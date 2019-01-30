package com.codingame.game.sample

import com.codingame.game.model.Constants
import sample.BaseCALMPlayer
import java.io.InputStream
import java.io.PrintStream

class IceCreamPlayer(
    stdin: InputStream, stdout: PrintStream, stderr: PrintStream)
  : BaseCALMPlayer(stdin, stdout, stderr) {

  private val crateVal = Constants.EQUIPMENT.ICE_CREAM_CRATE.ordinal
  private val ballVal = Constants.FOOD.ICE_CREAM.value

  init {
    while (true) {
      val inputs = readInputs()
      val me = inputs.myPlayer

      val iceCreamLoc = inputs.tables.firstOrNull { it.x >= 0 && it.equipment == crateVal }
        ?: throw Exception("Couldn't find ice cream crate")
      val window = inputs.tables.firstOrNull { it.x >= 0 && it.equipment == Constants.EQUIPMENT.WINDOW.ordinal }
        ?: throw Exception("Couldn't find window")
      val dishReturn = inputs.tables.firstOrNull { it.x >= 0 && it.equipment == Constants.EQUIPMENT.DISH_RETURN.ordinal }
        ?: throw Exception("Couldn't find dish return")
      val emptyDishes = inputs.tables.filter { it.x >= 0 && it.item == Constants.ITEM.DISH.ordinal && it.itemState == 0 }
      val emptyDish = emptyDishes.firstOrNull() ?: dishReturn

      when {
        // if holding a plate of ice cream, head for the window
        me.carrying == Constants.ITEM.DISH.ordinal && me.carryingState == ballVal -> {
          stderr.println("I'm holding a plate of ice cream: going to window")
          stdout.println("USE ${window.x} ${window.y}")
        }

        // if holding an empty plate, go get some ice cream
        me.carrying == Constants.ITEM.DISH.ordinal -> {
          stderr.println("I'm holding an empty dish, going for ice cream")
          stdout.println("USE ${iceCreamLoc.x} ${iceCreamLoc.y}")
        }

        // go get an empty plate
        else -> {
          stderr.println("I'm going to get a plate")
          stdout.println("USE ${emptyDish.x} ${emptyDish.y}")
        }
      }
    }
  }
}

