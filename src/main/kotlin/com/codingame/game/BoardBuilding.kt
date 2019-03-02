package com.codingame.game

import com.codingame.game.model.*
import java.util.*

lateinit var rand: Random
fun <E> List<E>.random(): E {
  val index = rand.nextInt(size)
  return this[index]
}

val boardLayout = listOf(
//        ABCDEFGHIJK
/* 0 */  "***********",  // 0
/* 1 */  "*.........*",  // 1
/* 2 */  "*.****.**.*",  // 2
/* 3 */  "*.*..*..*.*",  // 3
/* 4 */  "*.**.****.*",  // 4
/* 5 */  "*.........*",  // 5
/* 6 */  "***********"   // 6
//        ABCDEFGHIJK
)

val equipment = listOf(
    "A0", "B0", "C0", "D0", "E0"       ,"G0", "H0", "I0", "J0", "K0",
    "A1", "A2", "A3", "A4", "A5",
    "K1", "K2", "K3", "K4", "K5",
    "C2", "D2", "E2", "F2",       "H2", "I2",
    "C3",             "F3",             "I3",
    "C4", "D4",       "F4", "G4", "H4", "I4",
    "A6", "B6", "C6", "D6", "E6"       ,"G6", "H6", "I6", "J6", "K6"
    )

val ovenLocs = listOf(
    "B0", "C0", "D0", "E0"       ,"G0", "H0", "I0", "J0",
    "A1", "A2", "A3", "A4", "A5",
    "K1", "K2", "K3", "K4", "K5"
)

fun buildEmptyBoard(): Board = Board(boardLayout)

fun buildBoard(): Board {
  val board = buildEmptyBoard()

  val dishReturn = DishWasher().also { board["F0"].equipment = it }
  val window = Window(dishReturn)

  board["F6"].equipment = window
  board.window = window

  val equipmentLocs = equipment.shuffled(rand).iterator()

  val equipments = listOfNotNull(
      (league >= League.IceCreamBerries).then(IceCreamCrate()),
      (league >= League.IceCreamBerries).then(BlueberryCrate()),
      (league >= League.StrawberriesChoppingBoard).then(StrawberryCrate()),
      (league >= League.StrawberriesChoppingBoard).then(ChoppingBoard()),
      (league >= League.Croissants).then(DoughCrate()),
      (league >= League.Croissants).then(Oven())
  )

  equipments.forEach { eq ->
    val loc = equipmentLocs.findNext { eq !is Oven || it in ovenLocs }
    board[loc!!].equipment = eq
  }

  return board
}
