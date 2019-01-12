package specs

import com.codingame.game.Player
import com.codingame.game.buildEmptyBoard
import com.codingame.game.model.*
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class JarbageTests: FreeSpec({

  val board = buildEmptyBoard()
  val jarbageLoc = board["I2"]
  val player = Player()

  fun setup() {
    jarbageLoc.equipment = Jarbage()
    player.location = board["H2"]
    player.heldItem = null
  }

  "a player can throw out most items" {
    setup()
    listOf(
        IceCreamBall(IceCreamFlavour.VANILLA), Banana, ChoppedBananas, Blueberries,
        Pie(PieFlavour.Strawberry), RawPie(PieFlavour.Strawberry, 1), Waffle, BurntPie, BurntWaffle
    ).forEach {
      player.heldItem = it
      player.use(jarbageLoc)
      player.heldItem shouldBe null
      jarbageLoc.item shouldBe null
    }
  }

  "a player can jarbage a plate; this makes it clean" {
    setup()
    player.heldItem = Dish(Strawberries, PieSlice(PieFlavour.Blueberry), IceCreamBall(IceCreamFlavour.BUTTERSCOTCH))
    player.use(jarbageLoc)
    player.heldItem shouldBe Dish()
    jarbageLoc.item shouldBe null
  }
})
