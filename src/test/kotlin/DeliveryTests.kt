import com.codingame.game.*
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowAny
import io.kotlintest.specs.FreeSpec

class DeliverySpec: FreeSpec({
  "Adding delivery windows" - {
    "Adding a window to one side should NOT add a window to the other" {
      val board = buildBoard()
      board["B0"].equipment = Window()
      board["b0"].equipment shouldBe null
    }
  }

  "Interacting with the delivery window" - {
    val board = buildBoard()
    var deliveredItem: Item? = null

    board["B0"].equipment = Window { deliveredItem = it }
    val window = board["B0"]
    val player = Player()

    "Making deliveries" - {
      fun setup() {
        player.location = board["B1"]
        deliveredItem = null
      }

      "can be done with an ice cream dish (after dropping, there should be nothing there!)" {
        setup()
        player.heldItem = Dish(IceCreamBall(IceCreamFlavour.VANILLA))
        player.drop(window)
        window.item shouldBe null
        deliveredItem shouldBe Dish(IceCreamBall(IceCreamFlavour.VANILLA))
      }

      "can be done with a Milkshake (after dropping, there should be nothing there!)" {
        setup()
        player.heldItem = Milkshake(IceCreamBall(IceCreamFlavour.VANILLA))
        player.drop(window)
        window.item shouldBe null
        deliveredItem shouldBe Milkshake(IceCreamBall(IceCreamFlavour.VANILLA))
      }

      "can be done with an empty dish" {
        setup()
        player.heldItem = Dish()
        player.drop(window)
        System.err.println(deliveredItem)
        deliveredItem shouldBe Dish()
      }

      "cannot be done with a scoop" {
        setup()
        player.heldItem = Scoop()
        shouldThrowAny {
          player.drop(window)
        }
      }
    }

  }
})