package com.codingame.game

val boardLayout = listOf(
//        ABCDEFGHI
/* 0 */  "XDdXXXXXX",  // 0
/* 1 */  "X.......X",  // 1
/* 2 */  "X..X.X..X",  // 2
/* 3 */  "X..XXX..X",  // 3
/* 4 */  "X...X...X",  // 4
/* 5 */  "X...X...X",  // 5
/* 6 */  "X..CXX..X",  // 6
/* 7 */  "X.......O",  // 7
/* 8 */  "X.......X",  // 8
/* 9 */  "XXXXXSsXX"   // 9
//        ABCDEFGHI
)

fun buildBoard(): Board {
  val board = Board(9, 10, boardLayout)
  return board
}