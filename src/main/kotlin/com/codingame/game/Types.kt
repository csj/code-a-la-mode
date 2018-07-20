package com.codingame.game

import com.codingame.gameengine.module.entities.Rectangle
import java.util.*

enum class IceCreamFlavour {
  VANILLA,
  CHOCOLATE,
  BUTTERSCOTCH
}

abstract class Item {
  open fun drop(player: Player, cell: Cell) {
    val item = cell.item
    if (item is Dish) return dropOntoDish(player, item)
    if (!cell.isTable) throw Exception("Cannot drop: not a table!")
    cell.item = this
    player.heldItem = null
  }

  open fun dropOntoDish(player: Player, dish: Dish) {
    throw Exception("Cannot drop $this onto a dish")
  }

  open fun dropOntoEquipment(player: Player, equipment: Equipment) {
    throw Exception("Cannot drop $this onto $equipment!")
  }

  open fun take(player: Player, cell: Cell) {
    cell.item = null
    player.heldItem = this
  }
}

abstract class EdibleItem: Item() {
  override fun dropOntoDish(player: Player, dish: Dish) { dish += this }
  override fun dropOntoEquipment(player: Player, equipment: Equipment) {
    if (equipment is Blender) {
      equipment += this
      player.heldItem = null
      return
    }
    super.dropOntoEquipment(player, equipment)
  }
}

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

abstract class DeliverableItem : Item() {
  override fun dropOntoEquipment(player: Player, equipment: Equipment) {
    when (equipment) {
      is Window -> { equipment.deliver(this); player.heldItem = null }
      else -> super.dropOntoEquipment(player, equipment)
    }
  }
}

data class Dish(override val contents: MutableSet<EdibleItem> = mutableSetOf()) : DeliverableItem(), HasContents {
  constructor(vararg initialContents: EdibleItem): this(mutableSetOf(*initialContents))
}

/**
 * Represents a feature of the board. Cannot be moved or picked up, but can be USEd.
 */
abstract class Equipment {
  open fun use(player: Player) {
    throw Exception("Cannot use $this at this time!")
  }

  open fun takeFrom(player: Player) {
    throw Exception("$this cannot be taken directly!")
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

//  override fun use(player: Player) {
//    val item = player.heldItem
//    if (item is Scoop && (item.state == ScoopState.Clean || item.state == ScoopState.Dirty(flavour)))
//      player.heldItem = Scoop(ScoopState.IceCream(flavour))
//    else throw Exception("Cannot use this now")
//  }
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

data class RawPie(private val pieFlavour: PieFlavour): Item() {
  override fun dropOntoEquipment(player: Player, equipment: Equipment) {
    if (equipment is Oven) {
      equipment.insertRawPie(pieFlavour)
      player.heldItem = null
      return
    }
    super.dropOntoEquipment(player, equipment)
  }
}

data class Pie(val pieFlavour: PieFlavour, val pieces: Int = 4): Item() {
  override fun dropOntoEquipment(player: Player, equipment: Equipment) {
    if (equipment is ChoppingBoard) {
      equipment.putPie(this)
      return
    }
    super.dropOntoEquipment(player, equipment)
  }
}

data class PieSlice(val pieFlavour: PieFlavour): EdibleItem()

data class Milkshake(override val contents: MutableSet<EdibleItem> = mutableSetOf()) : DeliverableItem(), HasContents {
  constructor(vararg initialContents: EdibleItem): this(mutableSetOf(*initialContents))
}

data class Blender(override val contents: MutableSet<EdibleItem> = mutableSetOf()) : Equipment(), HasContents {
  constructor(vararg initialContents: EdibleItem): this(mutableSetOf(*initialContents))

  override fun clone(): Equipment = copy()

  override fun takeFrom(player: Player) {
    if (IceCreamBall(IceCreamFlavour.VANILLA) !in contents) throw Exception("Not ready for taking: no ice cream!")
    player.heldItem = Milkshake(contents.toMutableSet())
    contents.clear()
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

  fun insertRawPie(flavour: PieFlavour) {
    if (state === OvenState.Empty)
      state = OvenState.Cooking(flavour, cookTime)
    else throw Exception("Cannot insert pie: oven not empty!")
  }

  override fun takeFrom(player: Player) {
    val curState = state
    state = when (curState) {
      OvenState.Empty -> throw Exception("Cannot take from $this: nothing inside!")
      is OvenState.Cooking -> throw Exception("Cannot take from $this: pie is cooking!")
      is OvenState.Cooked -> { player.heldItem = Pie(curState.flavour); OvenState.Empty }
      OvenState.Burnt -> { player.heldItem = BurntPie; OvenState.Empty }
    }
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

  override fun takeFrom(player: Player) {
    val curState = state
    state = when (curState) {
      WaffleState.Empty -> throw Exception("Cannot take from $this: nothing inside!")
      is WaffleState.Cooking -> throw Exception("Cannot take from $this: waffle is cooking!")
      is WaffleState.Cooked -> { player.heldItem = Waffle; WaffleState.Empty }
      WaffleState.Burnt -> { player.heldItem = BurntWaffle; WaffleState.Empty }
    }
  }

  override fun use(player: Player) {
    if (state != WaffleState.Empty) throw Exception("Cannot use $this now: not empty!")
    state = WaffleState.Cooking(cookTime)
  }
}

data class ChoppingBoard(var pieOnBoard: Pie? = null): Equipment() {
  override fun clone() = copy()

  override fun use(player: Player) {
    if (player.heldItem === Banana) {
      checkVacant()
      player.heldItem = ChoppedBananas
      return
    }

    val pie = pieOnBoard
    if (player.heldItem == null && pie != null) {
      player.heldItem = PieSlice(pie.pieFlavour)
      pieOnBoard = when (pie.pieces) {
        1 -> throw Exception("Cannot chop: only one piece left!")
        else -> pie.copy(pieces = pie.pieces - 1)
      }
      return
    }

    super.use(player)
  }

  private fun checkVacant() {
    if (pieOnBoard != null) throw Exception("Chopping board is not vacant")
  }

  override fun takeFrom(player: Player) {
    val pie = pieOnBoard
    if (player.heldItem == null && pie != null) {
      player.heldItem = when(pie.pieces) {
        1 -> PieSlice(pie.pieFlavour)
        else -> pie
      }
      pieOnBoard = null
      return
    }

    super.takeFrom(player)
  }

  fun putPie(pie: Pie) {
    checkVacant()
    pieOnBoard = pie
  }
}


/**
 * @param onDelivery: a callback to be called when a player makes a delivery. Typically
 * this will be a scorekeeper function of some sort.
 */
class Window(private val onDelivery: (Item) -> Unit = { }) : Equipment() {

  override fun use(player: Player) {
    throw Exception("Cannot use a delivery window")
  }

  fun deliver(food: DeliverableItem) = onDelivery(food)
}

class CustomerQueue(private val onPointsAwarded: (Int, Int) -> Unit): ArrayList<Customer>() {
  fun delivery(item: Item, teamIndex: Int) =
    this.find { it.item == item }?.let {
      onPointsAwarded(teamIndex, it.award)
      remove(it)
    }

}

data class Customer(val item: Item, var award: Int) {
  fun age() { award = award*9/10 }
}

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
    if (value !is Window && oppositeCell.equipment != value) oppositeCell.equipment = value?.clone()
  }
  var item: Item? = null

  fun distanceTo(target: Cell): Int? {
    val visitedCells = mutableSetOf<Cell>()
    val floodedCells = PriorityQueue<Pair<Cell, Int>> { (_,d1), (_,d2) -> d1.compareTo(d2) }
    floodedCells += this to 0
    var isFirst = true

    while (floodedCells.any()) {
      val (cell, dist) = floodedCells.remove()!!
      if (cell in visitedCells) continue
      visitedCells += cell
      if (cell == target) return dist
      if (!cell.isTable || isFirst) {
        floodedCells += cell.neighbours
          .filterNot { (nc, _) -> nc in visitedCells }
          .map { (nc, nd) -> nc to dist + nd }
      }
      isFirst = false
    }
    return null
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

  val xRange = -(width-1)..(width-1)
  val yRange = 0 until height

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