package com.codingame.game.model

import com.codingame.game.Player
import com.codingame.game.rand
import com.codingame.game.random
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

  fun tick() {
    repeat(3 - size) { _ ->
      if (rand.nextDouble() < 0.2) {
        this += Customer.randomCustomer()
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

  companion object {
    private val possiblePlateContents = mapOf(
        IceCreamBall(IceCreamFlavour.VANILLA) to 200,
        IceCreamBall(IceCreamFlavour.CHOCOLATE) to 250,
        IceCreamBall(IceCreamFlavour.BUTTERSCOTCH) to 300,
        Strawberries to 300,
        Blueberries to 250,
        ChoppedBananas to 500,
        PieSlice(PieFlavour.Strawberry) to 800,
        PieSlice(PieFlavour.Blueberry) to 900,
        Waffle to 600
    )

    private val possibleMilkshakeContents = listOf(
        // vanilla ice cream goes without saying
        Strawberries,
        Blueberries,
        ChoppedBananas
    )

    private fun randomOrder(): DeliverableItem =
      if (rand.nextDouble() < 0.75) {
        // plate
        val itemCount = when (rand.nextDouble()) {
          in 0.0 .. 0.25 -> 4
          in 0.25 .. 0.5 -> 3
          else -> 2
        }
        Dish(possiblePlateContents.keys.shuffled(rand).take(itemCount).toMutableSet())
      } else {
        // milkshake
        Milkshake(IceCreamBall(IceCreamFlavour.VANILLA), possibleMilkshakeContents.random())
      }

    fun randomCustomer(): Customer {
      val order = randomOrder()
      val price = if (order is Milkshake) 800 else 300 + (order as Dish).contents.sumBy { possiblePlateContents[it]!! }
      return Customer(order, price)
    }
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

