package players

import com.codingame.game.Constants
import java.io.InputStream
import java.io.PrintStream

class IceCreamPlayer(stdin: InputStream, stdout: PrintStream, stderr: PrintStream) : BaseCALMPlayer(stdin, stdout, stderr) {
  init {
    while (true) {
      val inputs = readInputs()
      val me = inputs.myPlayer
      val iceCreamLoc = inputs.tables.first { it.x >= 0 && it.equipment == Constants.VANILLA_CRATE }
      val window = inputs.tables.first { it.x >= 0 && it.equipment == Constants.WINDOW }
      val emptyDish = inputs.tables.firstOrNull { it.x >= 0 && it.item == Constants.DISH }
      val iceCreamDish = inputs.tables.firstOrNull { it.x >= 0 && it.item == Constants.DISH + Constants.VANILLA_BALL }

      // if holding a plate of ice cream, head for the window
      if (me.carrying == Constants.DISH + Constants.VANILLA_BALL) {
        stdout.println("DROP ${window.x} ${window.y}")
        continue
      }

      // if holding a ball of ice cream, head for an empty dish
      if (me.carrying == Constants.VANILLA_BALL) {
        if (emptyDish == null) {
          stdout.println("WAIT")
          continue  // no empty dishes :(
        }

        stdout.println("DROP ${emptyDish.x} ${emptyDish.y}")
        continue
      }

      // so I'm holding nothing.

      // if there's a vanilla dish around, get it
      if (iceCreamDish != null) {
        stdout.println("TAKE ${iceCreamDish.x} ${iceCreamDish.y}")
        continue
      }

      // go get some ice cream
      stdout.println("USE ${iceCreamLoc.x} ${iceCreamLoc.y}")
    }
  }
}

