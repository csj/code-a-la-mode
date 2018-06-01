import com.codingame.game.*
import io.kotlintest.matchers.beEmpty
import io.kotlintest.matchers.collections.contain
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldHave
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

  "blending" - {
    val board = buildBoard()
    val blenderLoc = board["E4"]
    val player = Player()

    fun setup() {
      blenderLoc.equipment = Blender()
      player.location = board["F4"]
      player.heldItem = null
    }

    "you can add some items to an empty blender directly, but not others" {
      val good = listOf<Item>()  // e.g. chopped banana
      val bad = listOf(Dish(), Scoop())

      (good.map { it to true } + bad.map { it to false }).forEach { (item, isOk) ->
        setup()
        player.heldItem = item
        if (isOk) {
          player.drop(blenderLoc)
          (blenderLoc.equipment as Blender).contents shouldBe mutableSetOf(item)
        } else {
          shouldThrowAny {
            player.drop(blenderLoc)
          }
        }
      }
    }

    "you can drop a full ice cream scoop (Vanilla)" {
      setup()
      player.heldItem = Scoop(ScoopState.IceCream(IceCreamFlavour.VANILLA))
      player.drop(blenderLoc)
      player.heldItem shouldBe Scoop(ScoopState.Dirty(IceCreamFlavour.VANILLA))
      (blenderLoc.equipment as Blender).contents should contain(IceCreamBall(IceCreamFlavour.VANILLA))
    }

    "you can't use other ice cream flavours" {
      setup()
      player.heldItem = Scoop(ScoopState.IceCream(IceCreamFlavour.BUTTERSCOTCH))
      shouldThrowAny { player.drop(blenderLoc) }
    }

    "you can't use a full ice cream scoop if there's already ice cream inside" {
      setup()
      blenderLoc.equipment = Blender(IceCreamBall(IceCreamFlavour.VANILLA))
      player.heldItem = Scoop(ScoopState.IceCream(IceCreamFlavour.VANILLA))
      shouldThrowAny { player.drop(blenderLoc) }
    }

    "you can pick up a blender with vanilla ice cream and other stuff in it -- this gives you a Milkshake" {
      setup()
      val blender = blenderLoc.equipment as Blender
      blender += IceCreamBall(IceCreamFlavour.VANILLA)
      player.take(blenderLoc)
      blender.contents should beEmpty()
      player.heldItem shouldBe Milkshake(IceCreamBall(IceCreamFlavour.VANILLA))
    }

    "you cannot pick up a blender if holding something else (this includes another shake -- unlike overcooked!" {
      setup()
      val blender = blenderLoc.equipment as Blender
      blender += IceCreamBall(IceCreamFlavour.VANILLA)
      player.heldItem = Milkshake(IceCreamBall(IceCreamFlavour.VANILLA))
      shouldThrowAny { player.take(blenderLoc) }
    }

    "you cannot pick up an empty blender" {
      setup()
      shouldThrowAny { player.take(blenderLoc) }
    }
  }

  "scooping ice cream" - {
    val iceCreamLoc = "I2"
    val board = buildBoard()
    board[iceCreamLoc].equipment = IceCreamCrate(IceCreamFlavour.VANILLA)
    val player = Player()

    "a scoop on top of an ice cream crate" - {
      fun setup() {
        player.location = board["H2"]
        player.heldItem = null
        board["I2"].item = Scoop()
      }

      "can be picked up (without disturbing the crate)" {
        setup()
        player.take(board["I2"])
        board["I2"].equipment shouldBe IceCreamCrate(IceCreamFlavour.VANILLA)
        board["I2"].item shouldBe null
        player.heldItem shouldBe Scoop()
      }

      "cannot be USEd" {
        setup()
        shouldThrowAny {
          player.use(board["I2"])
        }
      }
    }

    "a player holding a full scoop near a table with a dish" - {
      fun setup() {
        player.location = board["E2"]
        player.heldItem = Scoop(ScoopState.IceCream(IceCreamFlavour.VANILLA))
        board["F2"].item = Dish()
      }

      "can DROP ice cream onto a dish out of a scoop" {
        setup()
        player.drop(board["F2"])
        board["F2"].item shouldBe Dish(IceCreamBall(IceCreamFlavour.VANILLA))
        player.heldItem shouldBe Scoop(ScoopState.Dirty(IceCreamFlavour.VANILLA))
      }

      "cannot DROP ice cream onto a dish out of a scoop, if the dish already has ice cream" {
        setup()
        (board["F2"].item as Dish) += IceCreamBall(IceCreamFlavour.VANILLA)
        shouldThrowAny {
          player.drop(board["F2"])
        }
      }

      "can DROP a full scoop onto a table (this drops the entire scoop)" {
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
        player.heldItem = Scoop(ScoopState.Dirty(IceCreamFlavour.BUTTERSCOTCH))
        shouldThrowAny {
          player.use(board[iceCreamLoc])
        }
      }

      "can't scoop if already holding ice cream" {
        setup()
        player.heldItem = Scoop(ScoopState.IceCream(IceCreamFlavour.VANILLA))
        shouldThrowAny {
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