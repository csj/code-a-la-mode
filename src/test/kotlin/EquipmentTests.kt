import com.codingame.game.Board
import com.codingame.game.IceCreamCrate
import com.codingame.game.IceCreamFlavour
import com.codingame.game.buildBoard
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import kotlin.test.assertEquals
import kotlin.test.assertTrue

object EquipmentSpek: SubjectSpek<Board>({
  subject { buildBoard() }

  describe("the game board") {
    on("adding equipment") {
      it("adds equipment to both sides when adding to only one side") {
        subject["I2"].equipment = IceCreamCrate(IceCreamFlavour.VANILLA)
        assertTrue { subject["I2"].equipment is IceCreamCrate }
        assertTrue { subject["i2"].equipment is IceCreamCrate }
      }
    }
  }
})