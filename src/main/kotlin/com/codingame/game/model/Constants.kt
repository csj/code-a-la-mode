package com.codingame.game.model

object Constants {
  const val CUSTOMER_VALUE_DECAY = 30   // higher => slower decay

  enum class ITEM {
    FOOD, DOUGH, DISH, STRAWBERRIES, SHELL, BURNT_CROISSANT, BURNT_TART
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
}