package com.codingame.game.model

object Constants {
  enum class ITEM {
    FOOD, DOUGH, DISH, STRAWBERRIES, SHELL, RAW_TART
  }
  
  enum class FOOD {
    ICE_CREAM,
    BLUEBERRIES,
    CHOPPED_STRAWBERRIES,
    CROISSANT,
    TART
  }
  
  const val OVEN_COOKTIME = 10
  const val OVEN_BURNTIME = 10
  val TIP = 200
}