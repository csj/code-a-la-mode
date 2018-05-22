import com.codingame.game.Board
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class BoardSpec: FreeSpec({
  "an empty board" - {
    val board = Board(5,5)

    "neighbour distances" {
      board["B2"].distanceTo(board["B3"]).shouldBe(2)
      board["C2"].distanceTo(board["D3"]).shouldBe(3)
    }

    "longer distances" {
      board["A3"].distanceTo(board["A1"]).shouldBe(4)
      board["A0"].distanceTo(board["D4"]).shouldBe(11)
    }
  }

  "a board with obstructions" - {
    val boardLayout = listOf(
      "...X.",  // 0
      "XXXX.",  // 1
      ".X...",  // 2
      ".XX..",  // 3
      "....."   // 4
    // ABCDE    -- A is always shared with the opponent
    )
    val board = Board(5, 5, boardLayout)

    "Some examples" {
      data class DistanceTest(val c1: String, val c2: String, val d: Int?)

      val tests = listOf(
        DistanceTest("B1", "B3", 6),
        DistanceTest("B1", "C3", 5),
        DistanceTest("B1", "C4", 9),
        DistanceTest("A0", "A0", 0),
        DistanceTest("B1", "B1", 0),
        DistanceTest("A0", "C2", null),
        DistanceTest("A2", "E0", 17)
      )
      tests.forEach { (c1, c2, d) ->
        "$c1 to $c2 should be $d" {
          board[c1].distanceTo(board[c2]).shouldBe(d)
        }
        "-$c1 to -$c2 should be $d" {
          fun negafyCellName(cellName: String) = ('a' + (cellName[0] - 'A')) + cellName.substring(1)
          board[negafyCellName(c1)].distanceTo(board[negafyCellName(c2)]).shouldBe(d)
        }
      }
    }
  }
})