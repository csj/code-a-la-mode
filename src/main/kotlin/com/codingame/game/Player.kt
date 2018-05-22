package com.codingame.game

import com.codingame.gameengine.core.AbstractPlayer

class Player : AbstractPlayer() {
  override fun getExpectedOutputLines() = 1

  var heldItem: Item? = null
}
