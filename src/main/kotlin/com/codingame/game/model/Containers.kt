package com.codingame.game.model

import com.codingame.game.Player

abstract class EdibleItem: Item() {
  override fun receiveItem(player: Player, item: Item, cell: Cell?) {
    if (item is Dish) {
      item += this
      cell!!.item = null
      return
    }
    return super.receiveItem(player, item, cell)
  }

}

interface Container {
  val contents: MutableSet<EdibleItem>
}

infix operator fun Container.plusAssign(item: EdibleItem) {
  if (item in contents) throw Exception("Can't add: $this already contains $item")
  contents += item
}



