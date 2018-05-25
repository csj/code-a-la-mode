import com.codingame.game.*
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class DeliverySpec: FreeSpec({
  "Interacting with the delivery window" - {
    val board = buildBoard()
    val player = Player()

    "Making deliveries" - {
      fun setup() {
        player.location = board["B1"]
      }

      "can be done with an ice cream dish" {
        setup()
        player.heldItem = Dish(IceCreamBall(IceCreamFlavour.VANILLA))
        player.drop(board["B0"])
      }
    }

  }
})