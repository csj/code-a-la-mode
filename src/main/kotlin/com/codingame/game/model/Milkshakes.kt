package com.codingame.game.model

import com.codingame.game.Player

object ChoppedBananas: EdibleItem()

data class Milkshake(override val contents: MutableSet<EdibleItem> = mutableSetOf()) : DeliverableItem(), Container {
  constructor(vararg initialContents: EdibleItem): this(mutableSetOf(*initialContents))
}

class Blender(override val contents: MutableSet<EdibleItem> = mutableSetOf()) : AllInstancesAreConsideredEqual(), Container {
  constructor(vararg initialContents: EdibleItem): this(mutableSetOf(*initialContents))
  override fun basicNumber() = Constants.EQUIPMENT.BLENDER.ordinal
  override fun extras(): List<Int> {
    return listOf(contents.map { edibleEncoding[it]!! }.sum(), -1)
  }

  override fun clone(): Equipment = Blender()

  override fun takeFrom(player: Player): Milkshake {
    if (IceCreamBall(IceCreamFlavour.VANILLA) !in contents) throw Exception("Not ready for taking: no ice cream!")
    return Milkshake(contents.toMutableSet()).also { contents.clear() }
  }

  override fun receiveItem(player: Player, item: Item) {
    if (item is EdibleItem) {
      this += item
      player.heldItem = null
      return
    }
    super.receiveItem(player, item)
  }

  infix operator fun plusAssign(item: EdibleItem) {
    when (item) {
      IceCreamBall(IceCreamFlavour.VANILLA),
      is Strawberries,
      is Blueberries,
      is ChoppedBananas
      -> (this as Container) += item
      else -> throw Exception("Cannot add $item to $this")
    }
  }
}


