package specs

import com.codingame.game.model.IceCreamCrate
import com.codingame.game.model.IceCreamFlavour
import com.codingame.game.buildEmptyBoard
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class EquipmentSpec: FreeSpec({
  "adds equipment to both sides when adding to only one side" {
    val board = buildEmptyBoard()
    board["I2"].equipment = IceCreamCrate(IceCreamFlavour.VANILLA)
    board["I2"].equipment shouldBe IceCreamCrate(IceCreamFlavour.VANILLA)
    board["i2"].equipment shouldBe IceCreamCrate(IceCreamFlavour.VANILLA)
  }
})