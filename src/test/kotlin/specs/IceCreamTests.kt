package specs

import com.codingame.game.*
import com.codingame.game.Player
import com.codingame.game.model.*
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowAny
import io.kotlintest.specs.FreeSpec

class IceCreamTests: FreeSpec({

  val board = buildEmptyBoard()
  val crateLoc = board["I2"]
  val tableLoc = board["I3"]
  val player = Player()

  fun setup() {
    crateLoc.equipment = IceCreamCrate(IceCreamFlavour.VANILLA)
    player.location = board["H2"]
    tableLoc.item = null
    player.heldItem = null
  }

  "a player can take ice cream" {
    setup()
    player.use(crateLoc)
    player.heldItem shouldBe IceCreamBall(IceCreamFlavour.VANILLA)
  }

  "a player can insta-plate ice cream" {
    setup()
    player.heldItem = Dish()
    player.use(crateLoc)
    player.heldItem shouldBe Dish(IceCreamBall(IceCreamFlavour.VANILLA))
  }

  "a player cannot put things onto a crate" {
    setup()
    player.heldItem = Milkshake()
    shouldThrowAny { player.use(crateLoc) }
  }

  "a player can add an ice cream ball to a plate on a table" {
    setup()
    player.heldItem = IceCreamBall(IceCreamFlavour.VANILLA)
    tableLoc.item = Dish()
    player.use(tableLoc)
    player.heldItem shouldBe null
    tableLoc.item shouldBe Dish(IceCreamBall(IceCreamFlavour.VANILLA))
  }
})
