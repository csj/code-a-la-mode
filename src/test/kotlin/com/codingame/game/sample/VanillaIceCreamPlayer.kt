package com.codingame.game.sample

import com.codingame.game.Constants
import com.codingame.game.Constants.BUTTERSCOTCH_BALL
import com.codingame.game.Constants.BUTTERSCOTCH_CRATE
import com.codingame.game.Constants.CHOCOLATE_BALL
import com.codingame.game.Constants.CHOCOLATE_CRATE
import com.codingame.game.Constants.VANILLA_BALL
import com.codingame.game.Constants.VANILLA_CRATE
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
      val emptyDish = inputs.tables.firstOrNull { it.x >= 0 && it.item == Constants.DISH }
      val iceCreamDish = inputs.tables.firstOrNull { it.x >= 0 && it.item == Constants.DISH + ballVal }

      // if holding a plate of ice cream, head for the window
      if (me.carrying == Constants.DISH + ballVal) {
        stdout.println("DROP ${window.x} ${window.y}")
        continue
      }

      // if holding a ball of ice cream, head for an empty dish
      if (me.carrying == ballVal) {
        if (emptyDish == null) {
          stdout.println("WAIT")
          continue  // no empty dishes :(
        }

        stdout.println("DROP ${emptyDish.x} ${emptyDish.y}")
        continue
      }

      // so I'm holding nothing.

      // if there's a dish with ice cream around, get it
      if (iceCreamDish != null) {
        stdout.println("TAKE ${iceCreamDish.x} ${iceCreamDish.y}")
        continue
      }

      // go get some ice cream
      stdout.println("USE ${iceCreamLoc.x} ${iceCreamLoc.y}")
    }
  }
}

