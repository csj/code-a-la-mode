package com.codingame.game

import com.codingame.game.Constants.CUSTOMER_VALUE_DECAY
import com.codingame.gameengine.module.entities.Rectangle
import java.util.*

enum class IceCreamFlavour {
  VANILLA,
  CHOCOLATE,
  BUTTERSCOTCH
}

abstract class Item {
  open fun receiveItem(player: Player, item: Item) {
    throw Exception("Cannot drop $item onto $this!")
  }

  open fun take(player: Player, cell: Cell) {
    cell.item = null
    player.heldItem = this
  }
}

abstract class EdibleItem: Item()

object Strawberries: EdibleItem()
object Blueberries: EdibleItem()
object ChoppedBananas: EdibleItem()
object Banana: Item()
object Waffle: EdibleItem()
object BurntWaffle: Item()
object BurntPie: Item()

data class IceCreamBall(val flavour: IceCreamFlavour) : EdibleItem() {
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
//      IceCreamBall(state.flavour).dropOntoDish(player, dish)
//      player.heldItem = Scoop(ScoopState.Dirty(state.flavour))
//      return
//    }
//    super.dropOntoDish(player, dish)
//  }
//
//  override fun dropOntoEquipment(player: Player, equipment: Equipment) {
//    if (state is ScoopState.IceCream) {
//      IceCreamBall(state.flavour).dropOntoEquipment(player, equipment)
//      player.heldItem = Scoop(ScoopState.Dirty(state.flavour))
//      return
//    }
//
//    super.dropOntoEquipment(player, equipment)
//  }
//}

abstract class DeliverableItem : Item()

data class Dish(override val contents: MutableSet<EdibleItem> = mutableSetOf()) : DeliverableItem(), HasContents {
  constructor(vararg initialContents: EdibleItem): this(mutableSetOf(*initialContents))

  override fun receiveItem(player: Player, item: Item) {
    if (item is EdibleItem) {
      this += item
      player.heldItem = null
      return
    }
    super.receiveItem(player, item)
  }
}

/**
 * Represents a feature of the board. Cannot be moved or picked up, but can be USEd.
 */
abstract class Equipment {
  open fun use(player: Player) {
    player.heldItem?.also { return receiveItem(player, it) }
    player.heldItem = takeFrom(player)
  }

  open fun takeFrom(player: Player): Item {
    throw Exception("$this cannot be taken directly!")
  }

  open fun receiveItem(player: Player, item: Item) {
    throw Exception("Cannot drop $item onto $this!")
  }

  open fun clone(): Equipment {
    throw Exception("$this cannot be cloned!")
  }
}

/**
 * Marks the passage of time
 */
abstract class TimeSensitiveEquipment: Equipment() {
  abstract fun tick()
}

data class IceCreamCrate(val flavour: IceCreamFlavour) : Equipment() {
  override fun clone(): Equipment = copy()

  override fun receiveItem(player: Player, item: Item) {
    if (item is Dish) {
      item.receiveItem(player, IceCreamBall(flavour))
      player.heldItem = item
      return
    }
    super.receiveItem(player, item)
  }

  override fun takeFrom(player: Player) = IceCreamBall(flavour)
}


interface HasContents {
  val contents: MutableSet<EdibleItem>
}

infix operator fun HasContents.plusAssign(item: EdibleItem) {
  if (item in contents) throw Exception("Can't add: $this already contains $item")
  contents += item
}

enum class PieFlavour {
  Strawberry,
  Blueberry
}

data class RawPie(val pieFlavour: PieFlavour): Item()

data class Pie(val pieFlavour: PieFlavour, val pieces: Int = 4): Item()

data class PieSlice(val pieFlavour: PieFlavour): EdibleItem()

data class Milkshake(override val contents: MutableSet<EdibleItem> = mutableSetOf()) : DeliverableItem(), HasContents {
  constructor(vararg initialContents: EdibleItem): this(mutableSetOf(*initialContents))
}

data class Blender(override val contents: MutableSet<EdibleItem> = mutableSetOf()) : Equipment(), HasContents {
  constructor(vararg initialContents: EdibleItem): this(mutableSetOf(*initialContents))

  override fun clone(): Equipment = copy()

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
        -> (this as HasContents) += item
      else -> throw Exception("Cannot add $item to $this")
    }
  }
}

sealed class OvenState {
  object Empty: OvenState()
  data class Cooking(val flavour: PieFlavour, val timeUntilCooked: Int): OvenState()
  data class Cooked(val flavour: PieFlavour, val timeUntilBurnt: Int): OvenState()
  object Burnt: OvenState()
}

data class Oven(private val cookTime: Int, private val burnTime: Int, private var state: OvenState = OvenState.Empty) : TimeSensitiveEquipment() {
  override fun clone(): Equipment = copy()

  override fun tick() {
    val curState = state
    state = when (curState) {
      is OvenState.Empty -> return
      is OvenState.Cooking -> {
        val (fl, time) = curState
        if (time == 1) OvenState.Cooked(fl, burnTime) else curState.copy(timeUntilCooked = time-1)
      }
      is OvenState.Cooked -> {
        val time = curState.timeUntilBurnt
        if (time == 1) OvenState.Burnt else curState.copy(timeUntilBurnt = time-1)
      }
      is OvenState.Burnt -> return
    }
  }

  override fun receiveItem(player: Player, item: Item) {
    if (item is RawPie) {
      insertRawPie(item.pieFlavour)
      player.heldItem = null
      return
    }
    super.receiveItem(player, item)
  }

  private fun insertRawPie(flavour: PieFlavour) {
    if (state === OvenState.Empty)
      state = OvenState.Cooking(flavour, cookTime)
    else throw Exception("Cannot insert pie: oven not empty!")
  }

  override fun takeFrom(player: Player): Item {
    lateinit var retVal: Item
    val curState = state
    state = when (curState) {
      OvenState.Empty -> throw Exception("Cannot take from $this: nothing inside!")
      is OvenState.Cooking -> throw Exception("Cannot take from $this: pie is cooking!")
      is OvenState.Cooked -> OvenState.Empty.also { retVal = Pie(curState.flavour) }
      OvenState.Burnt -> OvenState.Empty.also { retVal = BurntPie }
    }
    return retVal
  }
}

sealed class WaffleState {
  object Empty : WaffleState()
  data class Cooking(val timeUntilCooked: Int): WaffleState()
  data class Cooked(val timeUntilBurnt: Int): WaffleState()
  object Burnt: WaffleState()
}

data class WaffleIron(private val cookTime: Int, private val burnTime: Int, private var state: WaffleState = WaffleState.Empty) : TimeSensitiveEquipment() {
  override fun clone(): Equipment = copy()

  override fun tick() {
    val curState = state
    state = when (curState) {
      is WaffleState.Empty -> return
      is WaffleState.Cooking -> {
        val time = curState.timeUntilCooked
        if (time == 1) WaffleState.Cooked(burnTime) else curState.copy(timeUntilCooked = time-1)
      }
      is WaffleState.Cooked -> {
        val time = curState.timeUntilBurnt
        if (time == 1) WaffleState.Burnt else curState.copy(timeUntilBurnt = time-1)
      }
      is WaffleState.Burnt -> return
    }
  }

  override fun takeFrom(player: Player): Item {
    lateinit var retVal: Item
    val curState = state
    state = when (curState) {
      WaffleState.Empty -> throw Exception("Cannot take from $this: nothing inside!")
      is WaffleState.Cooking -> throw Exception("Cannot take from $this: waffle is cooking!")
      is WaffleState.Cooked -> WaffleState.Empty.also { retVal = Waffle }
      WaffleState.Burnt -> WaffleState.Empty.also { retVal = BurntWaffle }
    }
    return retVal
  }

  override fun receiveItem(player: Player, item: Item) {
    if (item is Dish) {
      item.receiveItem(player, takeFrom(player))
      player.heldItem = item
      return
    }
    super.receiveItem(player, item)
  }

  override fun use(player: Player) {
    if (state == WaffleState.Empty) {
      state = WaffleState.Cooking(cookTime)
      return
    }
    super.use(player)
  }
}

data class ChoppingBoard(var pieOnBoard: Pie? = null): Equipment() {
  override fun clone() = copy()

  private fun checkVacant() {
    if (pieOnBoard != null) throw Exception("Chopping board is not vacant")
  }

  private fun getSlice(): PieSlice {
    val pie = pieOnBoard ?: throw Exception("No pie slices to get!")
    pieOnBoard = when (pie.pieces) {
      1 -> null
      else -> pie.copy(pieces = pie.pieces - 1)
    }
    return PieSlice(pie.pieFlavour)
  }

  override fun takeFrom(player: Player) = getSlice()

  override fun receiveItem(player: Player, item: Item) {
    if (item is Pie) {
      putPie(item)
      return
    }

    if (item === Banana) {
      checkVacant()
      player.heldItem = ChoppedBananas
      return
    }

    if (item is Dish) {
      // try to insta-plate
      item.receiveItem(player, getSlice())
      player.heldItem = item
      return
    }
    super.receiveItem(player, item)
  }

  private fun putPie(pie: Pie) {
    checkVacant()
    pieOnBoard = pie
  }
}


/**
 * @param onDelivery: a callback to be called when a player makes a delivery. Typically
 * this will be a scorekeeper function of some sort.
 */
class Window(private val dishReturn: DishReturn? = null, private val onDelivery: (Item) -> Unit = { }) : Equipment() {

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

class DishReturn : TimeSensitiveEquipment() {

  private val dishQueue = LinkedList<Boolean>(List(40) { false })
  var dishes: Int = 0

  override fun takeFrom(player: Player): Item {
    if (dishes > 0) {
      dishes--
      return Dish()
    }
    return super.takeFrom(player)
  }

  fun addDishToQueue() {
    dishQueue.add(true)
  }

  override fun tick() {
    while (dishQueue.pop()!!) {
      dishes++
    }
    dishQueue.add(false)
  }

}

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
    award = award * (CUSTOMER_VALUE_DECAY - 1) / CUSTOMER_VALUE_DECAY
    return award > originalAward / 10
  }
}

fun negafyCellName(cellName: String) = ('a' + (cellName[0] - 'A')) + cellName.substring(1)

class Cell(val x: Int, val y: Int, val isTable: Boolean = true) {
  override fun toString(): String = "($x, $y)"
  private val straightNeighbours = mutableListOf<Cell>()
  private val diagonalNeighbours = mutableListOf<Cell>()
  lateinit var oppositeCell: Cell
  lateinit var visualRect: Rectangle

  val neighbours by lazy { straightNeighbours.map {it to 2} + diagonalNeighbours.map {it to 3} }

  fun connect(other: Cell, isStraight: Boolean) {
    (if (isStraight) straightNeighbours else diagonalNeighbours) += other
  }

  var equipment: Equipment? = null
  set(value) {
    field = value
    if (value !is Window && value !is DishReturn && oppositeCell.equipment != value) oppositeCell.equipment = value?.clone()
  }
  var item: Item? = null

  fun buildDistanceMap(): Map<Cell, Int> {
    val visitedCells = mutableMapOf<Cell, Int>()
    val floodedCells = PriorityQueue<Pair<Cell, Int>> { (_,d1), (_,d2) -> d1.compareTo(d2) }
    floodedCells += this to 0
    var isFirst = true

    while (floodedCells.any()) {
      val (cell, dist) = floodedCells.remove()!!
      if (cell in visitedCells) continue
      visitedCells += cell to dist
      if (!cell.isTable || isFirst) {
        floodedCells += cell.neighbours
            .filterNot { (nc, _) -> nc in visitedCells.keys }
            .map { (nc, nd) -> nc to dist + nd }
      }
      isFirst = false
    }
    return visitedCells
  }

  fun distanceTo(target: Cell): Int? {
    return buildDistanceMap()[target]
  }
}


/**
 * Width: The number of horizontal cells _per player_. If width = 5, then
 * The cells will be numbered -4,-3,-2,-1,0,1,2,3,4  -- i.e. each team works with 5 cells including cell 0 (middle counter).
 * For input/output purposes, the columns will be inverted for player 1, so that each team thinks
 * they're working with columns 0..4 while the enemy works with -4..0
 */
class Board(val width: Int, val height: Int, layout: List<String>? = null) {
  val cells = Array(width * 2 - 1) { x ->
    Array(height) { y ->
      val isTable = if (layout == null) false else {
        val layoutX = (x - (width-1)) * if (x < width) -1 else 1
        layout[y][layoutX] != '.'
      }
      Cell(x - width + 1, y, isTable)
    }
  }

  val allCells = cells.flatten()

  operator fun get(x: Int, y: Int): Cell = cells[x + width - 1][y]
  operator fun get(cellName: String): Cell {
    val file = cellName[0]
    val x = if (file in 'A'..'Z') file - 'A' else 'a' - file
    return get(x, cellName.substring(1).toInt())
  }

  fun tick() {
    allCells.forEach { cell -> (cell.equipment as? TimeSensitiveEquipment)?.tick() }
  }

  private val xRange = -(width-1)..(width-1)
  private val yRange = 0 until height

  init {
    for (x in xRange) {
      for (y in yRange) {
        get(x,y).oppositeCell = get(-x,y)
        for (dx in -1..1) for (dy in -1..1) {
          if (dx != 0 || dy != 0) {
            val x2 = x+dx; val y2 = y+dy
            if (x2 in xRange && y2 in yRange) {
              this[x,y].connect(this[x2,y2], dx*dy == 0)
            }
          }
        }
      }
    }
  }
}

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

  const val WINDOW = 0
  const val DISH_RETURN = 1
  const val VANILLA_CRATE = 2
  const val CHOCOLATE_CRATE = 3
  const val BUTTERSCOTCH_CRATE = 4
}