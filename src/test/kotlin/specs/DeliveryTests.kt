package specs

import com.codingame.game.*
import com.codingame.game.Player
import com.codingame.game.model.*
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowAny
import io.kotlintest.specs.FreeSpec

class DeliverySpec: FreeSpec({
  "Adding delivery windows" - {
    "Adding a window to one side should NOT add a window to the other" {
      val board = buildEmptyBoard()
      board["B0"].equipment = Window()
      board["b0"].equipment shouldBe null
    }
  }

  "Interacting with the delivery window" - {
    val board = buildEmptyBoard()
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
        player.heldItem = Dish(IceCream)
        player.use(window)
        window.item shouldBe null
        deliveredItem shouldBe Dish(IceCream)
      }

      "can be done with an empty dish" {
        setup()
        player.heldItem = Dish()
        player.use(window)
        System.err.println(deliveredItem)
        deliveredItem shouldBe Dish()
      }

      "cannot be done with a banana" {
        setup()
        player.heldItem = Banana
        shouldThrowAny {
          player.use(window)
        }
      }
    }

  }
})