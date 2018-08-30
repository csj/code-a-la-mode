package com.codingame.game

val boardLayout = listOf(
//        ABCDEFGHI
/* 0 */  "XDdXXXXXX",  // 0
/* 1 */  "X.......X",  // 1
/* 2 */  "X..X.W..I",  // 2
/* 3 */  "X..XXX..X",  // 3
/* 4 */  "X...X...X",  // 4
/* 5 */  "X...X...X",  // 5
/* 6 */  "X..CXX..X",  // 6
/* 7 */  "X.......O",  // 7
/* 8 */  "X.......X",  // 8
/* 9 */  "XXXXXSsXX"   // 9
//        ABCDEFGHI
)

fun buildEmptyBoard(): Board = Board(9, 10, boardLayout)

fun buildBoardAndQueue(scoreAwardCallback: (teamIndex: Int, points: Int) -> Unit): Pair<Board, CustomerQueue> {
  val board = buildEmptyBoard()
  val queue = CustomerQueue(scoreAwardCallback)

  // Both windows have to be added separately
  val dishReturn1 = DishReturn(board["G9"]).also { board["G9"].equipment = it }
  val dishReturn2 = DishReturn(board["g9"]).also { board["g9"].equipment = it }

  board["B0"].equipment = Window(dishReturn1) { queue.delivery(it, 0) }
  board["b0"].equipment = Window(dishReturn2) { queue.delivery(it, 1) }

  // For other equipment, it's sufficient to add it to one side only (a clone will go on the other side)
  // Temporary: Make this random in the future
  board["I2"].equipment = IceCreamCrate(IceCreamFlavour.VANILLA)
  board["I3"].equipment = IceCreamCrate(IceCreamFlavour.CHOCOLATE)
  board["I4"].equipment = IceCreamCrate(IceCreamFlavour.BUTTERSCOTCH)

  // Items need to be added to both sides
  listOf("F2", "F3", "E3", "E4")
      .flatMap { listOf(it, negafyCellName(it)) }
      .forEach { board[it].item = Dish() }

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