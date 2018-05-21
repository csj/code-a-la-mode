import com.codingame.game.Board
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals

object BoardSpek: Spek({
  describe("an empty board") {
    val board = Board(5,5)

    on("neighbour distances") {
      it("the distance between B2 and B3 should be 2") {
        assertEquals(2, board["B2"].distanceTo(board["B3"]))
      }
      it("the distance between C2 and D3 should be 3") {
        assertEquals(3, board["C2"].distanceTo(board["D3"]))
      }
    }

    on("longer distances") {
      it("the distance between A3 and A1 should be 4") {
        assertEquals(4, board["A3"].distanceTo(board["A1"]))
      }

      it("the distance between A0 and D4 should be 11") {
        assertEquals(11, board["A0"].distanceTo(board["D4"]))
      }
    }
  }

  describe("a board with obstructions") {
    val boardLayout = listOf(
      "...X.",  // 0
      "XXXX.",  // 1
      ".X...",  // 2
      ".XX..",  // 3
      "....."   // 4
    // ABCDE    -- A is always shared with the opponent
    )
    val board = Board(5, 5, boardLayout)

    on("Some examples") {
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
        it("$c1 to $c2 should be $d") {
          assertEquals(d, board[c1].distanceTo(board[c2]))
        }
        it("-$c1 to -$c2 should be $d") {
          assertEquals(d, board[c1, true].distanceTo(board[c2, true]))
        }
      }
    }
  }
})