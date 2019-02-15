package com.codingame.game.model

object Constants {
  const val CUSTOMER_VALUE_DECAY = 30   // higher => slower decay

  enum class ITEM {
    FOOD, DOUGH, DISH, BANANA, SHELL, BURNT_FOOD
  }
  
  enum class FOOD {
    ICECREAM,
    STRAWBERRIES,
    BLUEBERRIES,
    CHOPPEDBANANAS,
    CROISSANT,
    TART
  }
  
  enum class EQUIPMENT {
    WINDOW, DISH_RETURN, OVEN, CHOPPING_BOARD, CRATE, GARBAGE
  }

  const val OVEN_COOKTIME = 10
  const val OVEN_BURNTIME = 10
}