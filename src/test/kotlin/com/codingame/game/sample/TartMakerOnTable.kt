package com.codingame.game.sample

import com.codingame.game.model.Blueberries
import com.codingame.game.model.Constants
import sample.BaseCALMPlayer
import java.io.InputStream
import java.io.PrintStream

class TartMakerOnTable(stdin: InputStream, stdout: PrintStream, stderr: PrintStream): BaseCALMPlayer(stdin, stdout, stderr) {
  init {
    while (true) {
      readInputs()

      val bluebs = inputs.tables.find { it.item?.itemType == Constants.FOOD.BLUEBERRIES.name }
      val dough = inputs.tables.find { it.item?.itemType == Constants.ITEM.CHOPPED_DOUGH.name }

      val action = when (inputs.myPlayer.carrying?.itemType) {
        Constants.FOOD.BLUEBERRIES.name ->
          dough?.use() ?: useEmptyTable()
        Constants.ITEM.DOUGH.name ->
          findEquipment('C')!!.use()
        Constants.ITEM.CHOPPED_DOUGH.name ->
          bluebs?.use() ?: useEmptyTable()
        null ->
          if (bluebs == null) findEquipment('B')!!.use()
          else findEquipment('H')!!.use()
        else ->
          useEmptyTable()
      }

      stdout.println(action)
    }
  }
}