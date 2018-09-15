package com.codingame.game.model

import com.codingame.game.Player
import com.codingame.game.rand
import java.util.ArrayList

abstract class DeliverableItem : Item()

class CustomerQueue(private val onPointsAwarded: (Int, Int) -> Unit): ArrayList<Customer>() {
  fun delivery(item: Item, teamIndex: Int) {
    println("Delivery: $item; current queue: $this")
    this.find { it.item == item }?.also {
      onPointsAwarded(teamIndex, it.award)
      remove(it)
    } ?: onPointsAwarded(teamIndex, 0)
  }

  private val possibleOrders = listOf(
      Dish(IceCreamBall(IceCreamFlavour.VANILLA)),
      Dish(IceCreamBall(IceCreamFlavour.CHOCOLATE)),
      Dish(IceCreamBall(IceCreamFlavour.BUTTERSCOTCH))
  )

  private fun <E> List<E>.random(): E {
    val index = rand.nextInt(size)
    return this[index]
  }

  fun tick() {
    repeat(3 - size) { _ ->
      if (rand.nextDouble() < 0.2) {
        val newOrder = (possibleOrders - this.map { it.item }).random()
        this += Customer(newOrder, 1000)
      }
    }
    removeIf { !it.stillWaiting() }
  }
}


data class Customer(val item: DeliverableItem, var award: Int) {
  val originalAward = award
  fun stillWaiting(): Boolean {
    award = award * (Constants.CUSTOMER_VALUE_DECAY - 1) / Constants.CUSTOMER_VALUE_DECAY
    return award > originalAward / 10
  }
}

/**
 * @param onDelivery: a callback to be called when a player makes a delivery. Typically
 * this will be a scorekeeper function of some sort.
 */
class Window(private val dishReturn: DishReturn? = null, private val onDelivery: (Item) -> Unit = { }) : Equipment() {
  override fun describeAsNumber() = Constants.EQUIPMENT.WINDOW.ordinal

  private fun deliver(food: DeliverableItem) {
    onDelivery(food)
    dishReturn?.addDishToQueue()
  }

  override fun receiveItem(player: Player, item: Item) {
    if (item is DeliverableItem) {
      deliver(item)
      player.heldItem = null
      return
    }
    super.receiveItem(player, item)
  }
}

