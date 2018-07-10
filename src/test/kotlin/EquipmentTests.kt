import com.codingame.game.IceCreamCrate
import com.codingame.game.IceCreamFlavour
import com.codingame.game.buildBoard
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class EquipmentSpec: FreeSpec({
  "adds equipment to both sides when adding to only one side" {
    val board = buildBoard()
    board["I2"].equipment = IceCreamCrate(IceCreamFlavour.VANILLA)
    board["I2"].equipment shouldBe IceCreamCrate(IceCreamFlavour.VANILLA)
    board["i2"].equipment shouldBe IceCreamCrate(IceCreamFlavour.VANILLA)
  }
})