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
/* 0 */  "XXXXXXXXX",  // 0
/* 1 */  "X.......X",  // 1
/* 2 */  "X.XXXXX.X",  // 2
/* 3 */  "D...X...X",  // 3
/* 4 */  "X...X...X",  // 4
/* 5 */  "X...X...X",  // 5
/* 6 */  "X...X...W",  // 6
/* 7 */  "X.XXXXX.X",  // 7
/* 8 */  "X.......X",  // 8
/* 9 */  "XXXXXXXXX"   // 9
//        ABCDEFGHI
)

fun buildEmptyBoard(): Board = Board(9, 10, boardLayout)

fun buildBoardAndQueue(scoreAwardCallback: (points: Int) -> Unit): Pair<Board, CustomerQueue> {
  val board = buildEmptyBoard()
  val queue = CustomerQueue(scoreAwardCallback)

  val dishReturn1 = DishReturn().also { board["A3"].equipment = it }
  board["I6"].equipment = Window(dishReturn1, queue::delivery)

  val equipmentLocs = listOf(
      "A0", "B0", "C0", "D0", "E0", "F0", "G0", "H0", "I0",
      "A1", "A2",       "A4", "A5", "A6", "A7", "A8",
      "I1", "I2", "I3", "I4", "I5",       "I7", "I8",
      "A9", "B9", "C9", "D9", "E9", "F9", "G9", "H9", "I9",
      "C2", "D2", "E2", "F2", "G2",
      "E3", "E4", "E5", "E6",
      "C7", "D7", "E7", "F7", "G7"
  ).shuffled(rand).iterator()

  val equipments = listOf(
      IceCreamCrate(IceCreamFlavour.VANILLA),
      IceCreamCrate(IceCreamFlavour.CHOCOLATE),
      IceCreamCrate(IceCreamFlavour.BUTTERSCOTCH),
      PieCrustCrate(),
      StrawberryCrate(),
      BlueberryCrate(),
      BananaCrate(),
      Oven(Constants.OVEN_COOKTIME, Constants.OVEN_BURNTIME),
      ChoppingBoard(),
      WaffleIron(Constants.WAFFLE_COOKTIME, Constants.WAFFLE_BURNTIME),
      Jarbage()
  )

  equipments.forEach { eq -> board[equipmentLocs.next()].equipment = eq }
  return Pair(board, queue)
}

fun buildPlayers(): List<Player> {
  return listOf(
      Player(),
      Player()
  )
}