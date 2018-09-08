package com.codingame.game.sample

import com.codingame.game.Constants
import com.codingame.game.Constants.BUTTERSCOTCH_BALL
import com.codingame.game.Constants.BUTTERSCOTCH_CRATE
import com.codingame.game.Constants.CHOCOLATE_BALL
import com.codingame.game.Constants.CHOCOLATE_CRATE
import com.codingame.game.Constants.VANILLA_BALL
import com.codingame.game.Constants.VANILLA_CRATE
import com.codingame.game.Dish
import com.codingame.game.IceCreamFlavour
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
    IceCreamFlavour.VANILLA -> VANILLA_CRATE
    IceCreamFlavour.CHOCOLATE -> CHOCOLATE_CRATE
    IceCreamFlavour.BUTTERSCOTCH -> BUTTERSCOTCH_CRATE
  }

  private val ballVal = when (iceCreamFlavour) {
    IceCreamFlavour.VANILLA -> VANILLA_BALL
    IceCreamFlavour.CHOCOLATE -> CHOCOLATE_BALL
    IceCreamFlavour.BUTTERSCOTCH -> BUTTERSCOTCH_BALL
  }

  init {
    while (true) {
      val inputs = readInputs()
      val me = inputs.myPlayer

      val iceCreamLoc = inputs.tables.first { it.x >= 0 && it.equipment == crateVal }
      val window = inputs.tables.first { it.x >= 0 && it.equipment == Constants.WINDOW }
      val dishReturn = inputs.tables.first { it.x >= 0 && it.equipment == Constants.DISH_RETURN }
      val emptyDishes = inputs.tables.filter { it.x >= 0 && it.item == Constants.DISH }
      val emptyDish = emptyDishes.firstOrNull() ?: dishReturn

      when (me.carrying) {
        // if holding a plate of ice cream, head for the window
        Constants.DISH + ballVal -> {
          stderr.println("I'm holding a plate of ice cream: going to window")
          stdout.println("USE ${window.x} ${window.y}")
        }

        // if holding an empty plate, go get some ice cream
        Constants.DISH -> {
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

