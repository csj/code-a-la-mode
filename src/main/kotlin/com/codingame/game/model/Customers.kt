package com.codingame.game.model

import com.codingame.game.Player
import com.codingame.game.rand
import java.util.ArrayList

var nextQueueId: Int = 0

class CustomerQueue(): ArrayList<Customer>() {
  val queueId: Int = nextQueueId++

  constructor(eagerPointsAwarded: (Int) -> Unit): this() {
    onPointsAwarded = eagerPointsAwarded
  }

  override fun toString(): String {
    return "CustomerQueue@$queueId"
  }

  lateinit var onPointsAwarded: (Int) -> Unit

  init {
    System.err.println("It's a new queue: $this")
  }

  fun delivery(item: Item) {
    println("Delivery: $item; current queue: $this")
    this.find { it.dish == item }?.also {
      onPointsAwarded(it.award)
      remove(it)
    } ?: onPointsAwarded(0)
  }

  fun tick() {
    removeIf { !it.stillWaiting() }
    while(size < 3) { this += Customer.randomCustomer() }
  }

  fun copy() = CustomerQueue().also {
    it.clear()
    forEach { customer -> it.add(customer.copy()) }
  }
}


data class Customer(val dish: Dish, var award: Int) {
  val originalAward = award

  fun copy() = Customer(dish, originalAward)

  fun stillWaiting(): Boolean {
    award = award * (Constants.CUSTOMER_VALUE_DECAY - 1) / Constants.CUSTOMER_VALUE_DECAY
    return award > 10
  }

  companion object {
    private val possiblePlateContents = mapOf(
        IceCream to 200,
        Strawberries to 300,
        Blueberries to 250,
        ChoppedBananas to 500,
        StrawberrySlice to 800,
        BlueberrySlice to 900,
        Waffle to 600
    )

    private fun randomOrder(): Dish =
        Dish(possiblePlateContents.keys.shuffled(rand)
            .take(
            when (rand.nextDouble()) {
              in 0.0 .. 0.25 -> 4
              in 0.25 .. 0.5 -> 3
              else -> 2
            }
        ).toMutableSet())

    fun randomCustomer(): Customer {
      val order = randomOrder()
      val price = 300 + order.contents.sumBy { possiblePlateContents[it]!! }
      return Customer(order, price)
    }
  }
}

class Window(private val dishReturn: DishReturn? = null) : Equipment() {
  var onDelivery: (Item) -> Unit = { }

  override fun describe() = Constants.EQUIPMENT.WINDOW.name

  private fun deliver(dish: Dish) {
    onDelivery(dish)
    dishReturn?.addDishToQueue()
  }

  override fun receiveItem(player: Player, item: Item) {
    if (item is Dish) {
      deliver(item)
      player.heldItem = null
      return
    }
    super.receiveItem(player, item)
  }
}

