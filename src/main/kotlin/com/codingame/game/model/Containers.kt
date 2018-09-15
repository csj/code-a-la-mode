package com.codingame.game.model

abstract class EdibleItem: Item()

interface Container {
  val contents: MutableSet<EdibleItem>
}

infix operator fun Container.plusAssign(item: EdibleItem) {
  if (item in contents) throw Exception("Can't add: $this already contains $item")
  contents += item
}



