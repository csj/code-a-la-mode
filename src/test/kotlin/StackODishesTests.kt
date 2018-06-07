import com.codingame.game.Dish
import com.codingame.game.Player
import com.codingame.game.buildBoard
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowAny
import io.kotlintest.specs.FreeSpec

class StackODishesTests: FreeSpec({

  val board = buildBoard()
  val sinkLoc = board["F9"]
  val frontOfSinkLoc = board["F8"]
  val dirtyLoc = board["C0"]
  val frontOfDirtyLoc = board["C1"]
  val player = Player()

  fun setup() {
    sinkLoc.equipment = Sink()
    dirtyLoc.equipment = DirtyDishesReturn()
    player.heldItem = null
  }

  "player can pick up a stack-o-dirties from the return window" {
    setup()
    dirtyLoc.equipment = DirtyDishesReturn(2)
    player.location = frontOfDirtyLoc
    player.take(dirtyLoc)
    player.heldItem shouldBe Dishes(DishState.Dirty, 2)
  }

  "player can pick up MORE dirties when already holding dirties" {
    setup()
    dirtyLoc.equipment = DirtyDishesReturn(2)
    player.location = frontOfDirtyLoc
    player.heldItem = Dishes(DishState.Dirty, 3)
    player.take(dirtyLoc)
    player.heldItem shouldBe Dishes(DishState.Dirty, 5)
  }

  "player cannot pick up more dirties when holding cleans" {
    setup()
    dirtyLoc.equipment = DirtyDishesReturn(2)
    player.location = frontOfDirtyLoc
    player.heldItem = Dishes(DishState.Clean, 3)
    shouldThrowAny { player.take(dirtyLoc) }
  }

  "player can wash a single dirty dish" {
    setup()
    player.location = frontOfSinkLoc
    player.heldItem = Dishes(DishState.Dirty, 1)
    player.use(sinkLoc)
    player.heldItem shouldBe Dish()
  }

  "player can instantly wash a stack-o-dirties, and emerge with a stack-o-cleans" {
    // N.B. this means we don't need a second cell for a sink to hold clean dishes
    setup()
    player.location = frontOfSinkLoc
    player.heldItem = Dishes(DishState.Dirty, 3)
    player.use(sinkLoc)
    player.heldItem shouldBe Dishes(DishState.Clean, 3)
  }

  "player can distribute a stack-o-cleans via successive DROPs" {
    // ... but can't just drop the whole stack
    player.location = frontOfSinkLoc
    player.heldItem = Dishes(DishState.Clean, 3)
    player.moveTo(board["E8"]); player.drop(board["E9"])
    player.moveTo(board["D8"]); player.drop(board["D9"])
    player.moveTo(board["C8"]); player.drop(board["C9"])
    listOf("C9", "D9", "E9").forEach { cell ->
      board[cell].item shouldBe Dish()
    }
    player.heldItem shouldBe null
  }
})