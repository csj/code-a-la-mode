package com.codingame.game.model

import com.codingame.game.Player

object IceCream : EdibleItem() {
  override fun take(player: Player, cell: Cell) {
    throw Exception("Cannot take $this directly!")
  }
}

//sealed class ScoopState {
//  object Clean : ScoopState()
//  data class Dirty(val flavour: IceCreamFlavour) : ScoopState()
//  data class IceCream(val flavour: IceCreamFlavour) : ScoopState()
//}
//
//data class Scoop(val state: ScoopState = ScoopState.Clean) : Item() {
//  override fun dropOntoDish(player: Player, dish: Dish) {
//    if (state is ScoopState.IceCream) {
//      IceCream(state.flavour).dropOntoDish(player, dish)
//      player.heldItem = Scoop(ScoopState.Dirty(state.flavour))
//      return
//    }
//    super.dropOntoDish(player, dish)
//  }
//
//  override fun dropOntoEquipment(player: Player, equipment: Equipment) {
//    if (state is ScoopState.IceCream) {
//      IceCream(state.flavour).dropOntoEquipment(player, equipment)
//      player.heldItem = Scoop(ScoopState.Dirty(state.flavour))
//      return
//    }
//
//    super.dropOntoEquipment(player, equipment)
//  }
//}

