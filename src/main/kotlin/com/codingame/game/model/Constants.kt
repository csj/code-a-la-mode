package com.codingame.game.model

object Constants {
  const val CUSTOMER_VALUE_DECAY = 30   // higher => slower decay

  enum class ITEM {
    FOOD, DISH, BANANA, RAW_PIE, WHOLE_PIE, BURNT_WAFFLE, BURNT_PIE
  }
  
  enum class FOOD(val value: Int) {
    ICECREAM(1),
    STRAWBERRIES(2),
    BLUEBERRIES(4),
    CHOPPEDBANANAS(8),
    STRAWBERRYSLICE(16),
    BLUEBERRYSLICE(32),
    WAFFLE(64)
  }
  
  enum class EQUIPMENT {
    WINDOW, DISH_RETURN, ICE_CREAM_CRATE,
    PIECRUST_CRATE, STRAWBERRY_CRATE, BLUEBERRY_CRATE, BANANA_CRATE,
    OVEN, CHOPPINGBOARD, WAFFLEIRON, GARBAGE
  }

  const val OVEN_COOKTIME = 10
  const val OVEN_BURNTIME = 10
  const val PIE_FRUITS_NEEDED = 3
  const val WAFFLE_COOKTIME = 8
  const val WAFFLE_BURNTIME = 5
}