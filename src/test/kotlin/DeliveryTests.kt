import com.codingame.game.*
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowAny
import io.kotlintest.specs.FreeSpec

class DeliverySpec: FreeSpec({
  "Interacting with the delivery window" - {
    val board = buildBoard()
    board["B0"].equipment = Window()
    val window = board["B0"]
    val player = Player()

    "Making deliveries" - {
      fun setup() {
        player.location = board["B1"]
      }

      "can be done with an ice cream dish (after dropping, there should be nothing there!)" {
        setup()
        player.heldItem = Dish(IceCreamBall(IceCreamFlavour.VANILLA))
        player.drop(window)
        window.item shouldBe null
      }

      "can be done with a Milkshake (after dropping, there should be nothing there!)" {
        setup()
        player.heldItem = Milkshake(IceCreamBall(IceCreamFlavour.VANILLA))
        player.drop(window)
        window.item shouldBe null
      }

      "can be done with an empty dish" {
        setup()
        player.heldItem = Dish()
        player.drop(window)
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