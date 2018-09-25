package com.codingame.game

import com.codingame.game.model.*
import java.util.*

val rand = Random()
fun <E> List<E>.random(): E {
  val index = rand.nextInt(size)
  return this[index]
}

val boardLayout = listOf(
//        ABCDEFGHI
/* 0 */  "XDXXXXXXX",  // 0
/* 1 */  "X.......X",  // 1
/* 2 */  "X..X.W..I",  // 2
/* 3 */  "X..XXX..X",  // 3
/* 4 */  "X...X...X",  // 4
/* 5 */  "X...X...X",  // 5
/* 6 */  "X..CXX..X",  // 6
/* 7 */  "X.......O",  // 7
/* 8 */  "X.......X",  // 8
/* 9 */  "XXXXXXXRX"   // 9
//        ABCDEFGHI
)

fun buildEmptyBoard(): Board = Board(9, 10, boardLayout)

fun buildBoardAndQueue(scoreAwardCallback: (teamIndex: Int, points: Int) -> Unit): Pair<Board, CustomerQueue> {
  val board = buildEmptyBoard()
  val queue = CustomerQueue(scoreAwardCallback)

  // Both windows have to be added separately
  val dishReturn1 = DishReturn().also { board["H9"].equipment = it }
  val dishReturn2 = DishReturn().also { board["h9"].equipment = it }

  board["B0"].equipment = Window(dishReturn1) { queue.delivery(it, 0) }
  board["b0"].equipment = Window(dishReturn2) { queue.delivery(it, 1) }

  // For other equipment, it's sufficient to add it to one side only (a clone will go on the other side)

  val equipmentLocs = listOf(
      "C0", "D0", "E0", "F0", "G0", "H0", "I0",
      "I1", "I2", "I3", "I4", "I5", "I6", "I7", "I8",
      "B9", "C9", "D9", "E9", "F9", "G9", "I9",
      "F2", "F3", "E3", "E4", "D2", "D3", "D6", "E5", "E6", "F6"
  ).shuffled(rand).iterator()

  val equipments = listOf(
      IceCreamCrate(IceCreamFlavour.VANILLA),
      IceCreamCrate(IceCreamFlavour.CHOCOLATE),
      IceCreamCrate(IceCreamFlavour.BUTTERSCOTCH),
      PieCrustCrate(),
      StrawberryCrate(),
      BlueberryCrate(),
      BananaCrate(),
      Blender(),
      Oven(Constants.OVEN_COOKTIME, Constants.OVEN_BURNTIME),
      ChoppingBoard(),
      WaffleIron(Constants.WAFFLE_COOKTIME, Constants.WAFFLE_BURNTIME),
      Jarbage()
  )

  val items = List(4) { { Dish() } }

  equipments.forEach { eq -> board[equipmentLocs.next()].equipment = eq }
  items.forEach { item ->
    val loc = equipmentLocs.next()
    board[loc].item = item()
    board[negafyCellName(loc)].item = item()
    // Items need to be added to both sides
  }
  return Pair(board, queue)
}

fun buildPlayers(): List<Player> {
  return listOf(
      Player(true),
      Player(false),
      Player(false),
      Player(true)
  )
}