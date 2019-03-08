package com.codingame.game.sample

import com.codingame.game.model.Constants
import sample.BaseCALMPlayer
import java.io.InputStream
import java.io.PrintStream

class InstaFoodPlayer(stdin: InputStream, stdout: PrintStream, stderr: PrintStream): BaseCALMPlayer(stdin, stdout, stderr) {
  init {
    while (true) {
      readInputs()
      val useTarget = when {
        inputs.myPlayer.carrying?.itemType == Constants.FOOD.BLUEBERRIES.name ->
          findEquipment('D')
        else ->
          findEquipment('B')
      }!!
      stdout.println("USE ${useTarget.x} ${useTarget.y}")
    }
  }
}
