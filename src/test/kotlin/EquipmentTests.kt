import com.codingame.game.*
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowAny
import io.kotlintest.specs.FreeSpec

class EquipmentSpec: FreeSpec({
  "the game board" - {
    "adding equipment" - {
      "adds equipment to both sides when adding to only one side" {
        val board = buildBoard()
        board["I2"].equipment = IceCreamCrate(IceCreamFlavour.VANILLA)
        board["I2"].equipment shouldBe IceCreamCrate(IceCreamFlavour.VANILLA)
        board["i2"].equipment shouldBe IceCreamCrate(IceCreamFlavour.VANILLA)
      }
    }
  }

  "scooping ice cream" - {
    val iceCreamLoc = "I2"
    val board = buildBoard()
    board[iceCreamLoc].equipment = IceCreamCrate(IceCreamFlavour.VANILLA)
    val player = Player()

    "a player holding a full scoop near a table with a dish" - {
      fun setup() {
        player.location = board["E2"]
        player.heldItem = Scoop(ScoopState.IceCream(IceCreamFlavour.VANILLA))
        board["F2"].item = Dish()
      }

      "can DROP ice cream onto a dish out of a scoop" {
        setup()
        player.drop(board["F2"])
        board["F2"].item shouldBe Dish(mutableListOf(IceCreamBall(IceCreamFlavour.VANILLA)))
        player.heldItem shouldBe Scoop(ScoopState.Dirty(IceCreamFlavour.VANILLA))
      }

      "can DROP a full scoop onto a table" {
        setup()
        player.drop(board["D2"])
        board["D2"].item shouldBe Scoop(ScoopState.IceCream(IceCreamFlavour.VANILLA))
        player.heldItem shouldBe null
      }

      "cannot DROP a scoop onto the floor" {
        setup()
        shouldThrowAny {
          player.drop(board["E1"])
        }
      }

    }

    "a player holding a scoop near the ice cream crate" - {
      fun setup() {
        player.location = board["H2"]
        player.heldItem = Scoop()
      }

      "can't scoop if he's too far away" {
        setup()
        shouldThrowAny {
          player.location = board["I4"]
          player.use(board[iceCreamLoc])
        }
      }

      "can scoop under normal circumstances" {
        setup()
        player.use(board[iceCreamLoc])
        player.heldItem.shouldBe(Scoop(ScoopState.IceCream(IceCreamFlavour.VANILLA)))
      }

      "can scoop using a dirty vanilla scoop" {
        setup()
        player.heldItem = Scoop(ScoopState.Dirty(IceCreamFlavour.VANILLA))
        player.use(board[iceCreamLoc])
        player.heldItem.shouldBe(Scoop(ScoopState.IceCream(IceCreamFlavour.VANILLA)))
      }

      "can't scoop if the scoop was used with a different flavour" {
        setup()
        shouldThrowAny {
          player.heldItem = Scoop(ScoopState.Dirty(IceCreamFlavour.BUTTERSCOTCH))
          player.use(board[iceCreamLoc])
        }
      }

      "can't scoop if already holding ice cream" {
        setup()
        shouldThrowAny {
          player.heldItem = Scoop(ScoopState.IceCream(IceCreamFlavour.VANILLA))
          player.use(board[iceCreamLoc])
        }
      }

      "can't scoop if he USEs the wrong location" {
        setup()
        shouldThrowAny {
          player.use(board["H3"])
        }
      }
    }
  }
})