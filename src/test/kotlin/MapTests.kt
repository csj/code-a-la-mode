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
})