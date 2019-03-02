package com.codingame.game

import com.codingame.game.model.*
import java.util.*

lateinit var rand: Random
fun <E> List<E>.random(): E {
  val index = rand.nextInt(size)
  return this[index]
}

val boardLayout1 = listOf(
//        ABCDEFGHI
/* 0 */  "*********",  // 0
/* 1 */  "*.......*",  // 1
/* 2 */  "*.*****.*",  // 2
/* 3 */  "*...*...*",  // 3
/* 4 */  "*...*...*",  // 4
/* 5 */  "*...*...*",  // 5
/* 6 */  "*...*...*",  // 6
/* 7 */  "*.*****.*",  // 7
/* 8 */  "*.......*",  // 8
/* 9 */  "*********"   // 9
//        ABCDEFGHI
)

val equipment1 = listOf(
    "A0", "B0", "C0", "D0", "E0", "F0", "G0", "H0", "I0",
    "A1", "A2",       "A4", "A5", "A6", "A7", "A8",
    "I1", "I2", "I3", "I4", "I5",       "I7", "I8",
    "A9", "B9", "C9", "D9", "E9", "F9", "G9", "H9", "I9",
    "C2", "D2", "E2", "F2", "G2",
    "E3", "E4", "E5", "E6",
    "C7", "D7", "E7", "F7", "G7"
)

val boardLayout2 = listOf(
//        ABCDEFGHIJK
/* 0 */  "***********",  // 0
/* 1 */  "*.........*",  // 1
/* 2 */  "*.CODE.*A.*",  // 2
/* 3 */  "*.*..*..*.*",  // 3
/* 4 */  "*.LA.MODE.*",  // 4
/* 5 */  "*.........*",  // 5
/* 6 */  "***********"   // 6
//        ABCDEFGHIJK
)

val equipment2 = listOf(
    "A0", "B0", "C0", "D0", "E0"       ,"G0", "H0", "I0", "J0", "K0",
    "A1", "A2", "A3", "A4", "A5",
    "K1", "K2", "K3", "K4", "K5",
//    "H2", "C3", "F3", "I3",
    "A6", "B6", "C6", "D6", "E6"       ,"G6", "H6", "I6", "J6", "K6"
    )

fun buildEmptyBoard(): Board = Board(boardLayout2)

fun buildBoard(): Board {
  val board = buildEmptyBoard()

  val dishReturn1 = DishWasher().also { board["F0"].equipment = it }
  val window = Window(dishReturn1)

  board["F6"].equipment = window
  board.window = window

  val equipmentLocs = equipment2.shuffled(rand).iterator()

  val equipments = listOfNotNull(
      (league >= League.IceCreamBerries).then(IceCreamCrate()),
      (league >= League.IceCreamBerries).then(BlueberryCrate()),
      (league >= League.StrawberriesChoppingBoard).then(StrawberryCrate()),
      (league >= League.StrawberriesChoppingBoard).then(ChoppingBoard()),
      (league >= League.Croissants).then(DoughCrate()),
      (league >= League.Croissants).then(Oven(Constants.OVEN_COOKTIME, Constants.OVEN_BURNTIME))
  )

  equipments.forEach { eq -> board[equipmentLocs.next()].equipment = eq }
  return board
}