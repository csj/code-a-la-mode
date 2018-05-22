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
        board["I2"].equipment.shouldBe(IceCreamCrate(IceCreamFlavour.VANILLA))
        board["i2"].equipment.shouldBe(IceCreamCrate(IceCreamFlavour.VANILLA))
      }
    }
  }

  "scooping ice cream" - {
    "a player holding a scoop" - {
      val board = buildBoard()
      board["I2"].equipment = IceCreamCrate(IceCreamFlavour.VANILLA)
      val player = Player()

      fun setup() {
        player.location = board["I3"]
        player.heldItem = Scoop()
      }

      "can't scoop if he's too far away" {
        setup()
        shouldThrowAny {
          player.location = board["I4"]
          player.use(board["I2"])
        }
      }

      "can scoop under normal circumstances" {
        setup()
        player.use(board["I2"])
        player.heldItem.shouldBe(Scoop(ScoopState.IceCream(IceCreamFlavour.VANILLA)))
      }

      "can scoop using a dirty vanilla scoop" {
        setup()
        player.heldItem = Scoop(ScoopState.Dirty(IceCreamFlavour.VANILLA))
        player.use(board["I2"])
        player.heldItem.shouldBe(Scoop(ScoopState.IceCream(IceCreamFlavour.VANILLA)))
      }

      "can't scoop if the scoop was used with a different flavour" {
        setup()
        shouldThrowAny {
          player.heldItem = Scoop(ScoopState.Dirty(IceCreamFlavour.BUTTERSCOTCH))
          player.use(board["I2"])
        }
      }

      "can't scoop if already holding ice cream" {
        setup()
        shouldThrowAny {
          player.heldItem = Scoop(ScoopState.IceCream(IceCreamFlavour.VANILLA))
          player.use(board["I2"])
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