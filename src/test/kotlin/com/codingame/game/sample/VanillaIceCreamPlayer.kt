package com.codingame.game.sample

import com.codingame.game.model.Constants
import com.codingame.game.model.IceCreamFlavour
import sample.BaseCALMPlayer
import java.io.InputStream
import java.io.PrintStream

//fun main(args: Array<String>) {
//  VanillaIceCreamPlayer()
//}

class VanillaIceCreamPlayer(stdin: InputStream, stdout: PrintStream, stderr: PrintStream)
  : IceCreamPlayer(stdin, stdout, stderr, IceCreamFlavour.VANILLA)

class ChocolateIceCreamPlayer(stdin: InputStream, stdout: PrintStream, stderr: PrintStream)
  : IceCreamPlayer(stdin, stdout, stderr, IceCreamFlavour.CHOCOLATE)

open class IceCreamPlayer(
    stdin: InputStream, stdout: PrintStream, stderr: PrintStream,
    iceCreamFlavour: IceCreamFlavour)
  : BaseCALMPlayer(stdin, stdout, stderr) {

  private val crateVal = when (iceCreamFlavour) {
    IceCreamFlavour.VANILLA -> Constants.EQUIPMENT.VANILLA_CRATE.ordinal
    IceCreamFlavour.CHOCOLATE -> Constants.EQUIPMENT.CHOCOLATE_CRATE.ordinal
    IceCreamFlavour.BUTTERSCOTCH -> Constants.EQUIPMENT.BUTTERSCOTCH_CRATE.ordinal
  }

  private val ballVal = when (iceCreamFlavour) {
    IceCreamFlavour.VANILLA -> Constants.FOOD.VANILLA_BALL.value
    IceCreamFlavour.CHOCOLATE -> Constants.FOOD.CHOCOLATE_BALL.value
    IceCreamFlavour.BUTTERSCOTCH -> Constants.FOOD.BUTTERSCOTCH_BALL.value
  }

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

