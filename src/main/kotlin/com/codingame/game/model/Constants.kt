package com.codingame.game.model

object Constants {
  const val CUSTOMER_VALUE_DECAY = 20   // higher => slower decay

  const val VANILLA_BALL = 1
  const val CHOCOLATE_BALL = 2
  const val BUTTERSCOTCH_BALL = 4
  const val STRAWBERRIES = 8
  const val BLUEBERRIES = 16
  const val CHOPPED_BANANAS = 32
  const val STRAWBERRY_PIE = 64
  const val BLUEBERRY_PIE = 128
  const val WAFFLE = 256

  const val DISH = 1024
  const val MILKSHAKE = 2048

  enum class EQUIPMENT {
    WINDOW, DISH_RETURN, VANILLA_CRATE, CHOCOLATE_CRATE, BUTTERSCOTCH_CRATE,
    PIECRUST_CRATE, STRAWBERRY_CRATE, BLUEBERRY_CRATE, BANANA_CRATE,
    BLENDER, OVEN, CHOPPINGBOARD, WAFFLEIRON
  }

  const val OVEN_COOKTIME = 10
  const val OVEN_BURNTIME = 10
  const val PIE_FRUITS_NEEDED = 3
}