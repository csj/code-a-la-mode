import com.codingame.game.*
import io.kotlintest.matchers.beEmpty
import io.kotlintest.matchers.collections.contain
import io.kotlintest.matchers.containAll
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowAny
import io.kotlintest.specs.FreeSpec

class BlenderTests: FreeSpec({

  val board = buildBoard()
  val blenderLoc = board["E4"]
  val player = Player()

  fun setup() {
    blenderLoc.equipment = Blender()
    player.location = board["F4"]
    player.heldItem = null
  }

  "you can add some items to an empty blender directly, but not others" {
    val good = listOf<Item>(ChoppedBananas, Strawberries)
    val bad = listOf(Dish(), Banana, BurntPie, Waffle)

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
    player.heldItem = IceCreamBall(IceCreamFlavour.VANILLA)
    player.drop(blenderLoc)
    player.heldItem shouldBe null
    (blenderLoc.equipment as Blender).contents should contain(IceCreamBall(IceCreamFlavour.VANILLA))
  }

  "you can't use other ice cream flavours" {
    setup()
    player.heldItem = IceCreamBall(IceCreamFlavour.BUTTERSCOTCH)
    shouldThrowAny { player.drop(blenderLoc) }
  }

  "you can't use a full ice cream scoop if there's already ice cream inside" {
    setup()
    blenderLoc.equipment = Blender(IceCreamBall(IceCreamFlavour.VANILLA))
    player.heldItem = IceCreamBall(IceCreamFlavour.VANILLA)
    shouldThrowAny { player.drop(blenderLoc) }
  }

  "you can drop berries in directly" {
    setup()
    blenderLoc.equipment = Blender(IceCreamBall(IceCreamFlavour.VANILLA))
    player.heldItem = Strawberries
    player.drop(blenderLoc)
    (blenderLoc.equipment as Blender).contents should containAll(IceCreamBall(IceCreamFlavour.VANILLA), Strawberries)
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

  "a milkshake is not a milkshake unless it contains ice cream" {
    setup()
    val blender = blenderLoc.equipment as Blender
    blender += Strawberries
    blender += ChoppedBananas
    shouldThrowAny { player.take(blenderLoc) }
  }

  "blenders cannot blend whole bananas" {
    setup()
    player.heldItem = Banana
    shouldThrowAny { player.drop(blenderLoc) }
  }
})