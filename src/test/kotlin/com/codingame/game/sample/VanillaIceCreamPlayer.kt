package com.codingame.game.sample

import com.codingame.game.model.Constants
import sample.BaseCALMPlayer
import java.io.InputStream
import java.io.PrintStream

class IceCreamPlayer(
    stdin: InputStream, stdout: PrintStream, stderr: PrintStream)
  : BaseCALMPlayer(stdin, stdout, stderr) {

  private val ballVal = Constants.FOOD.ICE_CREAM.name

  init {
    while (true) {
      val inputs = readInputs()
      val me = inputs.myPlayer

      val iceCreamLoc = inputs.tables.firstOrNull { it.equipment == 'I' }
        ?: throw Exception("Couldn't find ice cream crate")
      val window = inputs.tables.firstOrNull { it.equipment == 'W' }
        ?: throw Exception("Couldn't find window")
      val dishReturn = inputs.tables.firstOrNull { it.equipment == 'D' }
        ?: throw Exception("Couldn't find dish return")
      val emptyDishes = inputs.tables.filter { it.item?.itemType == Constants.ITEM.DISH.name && it.item!!.itemContents.isEmpty() }
      val emptyDish = emptyDishes.firstOrNull() ?: dishReturn

      when {
        // if holding a plate of ice cream, head for the window
        me.carrying?.itemType == Constants.ITEM.DISH.name && me.carrying.itemContents == listOf(ballVal) -> {
          stderr.println("I'm holding a plate of ice cream: going to window")
          stdout.println("USE ${window.x} ${window.y}")
        }

        // if holding an empty plate, go get some ice cream
        me.carrying?.itemType == Constants.ITEM.DISH.name -> {
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

