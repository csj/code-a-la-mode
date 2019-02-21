package com.codingame.game.model

import com.codingame.game.League
import com.codingame.game.Player
import com.codingame.game.league
import com.codingame.game.rand

val originalQueue = List(50) { Customer.randomCustomer() }

class CustomerQueue() {
  constructor(eagerPointsAwarded: (Int) -> Unit): this() {
    onPointsAwarded = eagerPointsAwarded
  }

  val queueIterator = originalQueue.iterator()

  val activeCustomers: MutableList<Customer> = mutableListOf()

  lateinit var onPointsAwarded: (Int) -> Unit
  lateinit var onFailure: () -> Unit

  fun delivery(item: Item) {
    activeCustomers.filter { it.dish == item }.maxBy { it.award }?.also {
      onPointsAwarded(it.award)
      it.satisfaction = Satisfaction.Satisfied
    } ?: onFailure()
  }

  fun getNewCustomers() {
    activeCustomers.removeIf {
      it.satisfaction in listOf(Satisfaction.Satisfied, Satisfaction.Leaving)
    }
    while(activeCustomers.size < 3) {
      activeCustomers += queueIterator.next().also {
        it.award = it.originalAward
        it.satisfaction = Satisfaction.Waiting
      }}
  }

  fun updateRemainingCustomers() {
    activeCustomers
        .filter { it.satisfaction != Satisfaction.Satisfied }
        .forEach { it.updateSatisfaction() }
  }
}

enum class Satisfaction {
  Waiting,
  Satisfied,
  Danger,
  Leaving
}

data class Customer(val dish: Dish, var award: Int) {
  val originalAward = award
  var satisfaction: Satisfaction = Satisfaction.Waiting

  fun updateSatisfaction() {
    award = award * (Constants.CUSTOMER_VALUE_DECAY - 1) / Constants.CUSTOMER_VALUE_DECAY
    satisfaction = when {
      award > 25 -> Satisfaction.Waiting
      award > 10 -> Satisfaction.Danger
      else -> Satisfaction.Leaving
    }
  }

  companion object {
    private val possiblePlateContents =
        mapOf(IceCream to 200, Blueberries to 250) +
        (if (league >= League.StrawberriesChoppingBoard) mapOf(ChoppedStrawberries to 400) else mapOf()) +
        (if (league >= League.Croissants) mapOf(Croissant to 600) else mapOf()) +
        (if (league >= League.All) mapOf(Tart to 800) else mapOf())

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

class Window(private val dishWasher: DishWasher? = null) : Equipment() {
  override val toString: String
    get() = "Window"
  var onDelivery: (Item) -> Unit = { }

  override val describeChar = 'W'

  private fun deliver(dish: Dish) {
    onDelivery(dish)
    dishWasher?.addDishToQueue()
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

