package specs

import com.codingame.game.*
import com.codingame.game.Player
import com.codingame.game.model.BurntWaffle
import com.codingame.game.model.Dish
import com.codingame.game.model.Waffle
import com.codingame.game.model.WaffleIron
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowAny
import io.kotlintest.specs.FreeSpec

class WaffleTests: FreeSpec({

  val board = buildEmptyBoard()
  val waffleLoc = board["F2"]
  val player = Player()
  val cookTime = 5
  val burnTime = 3
  val totalBurnTime = cookTime + burnTime

  fun setup() {
    waffleLoc.equipment = WaffleIron(cookTime, burnTime)
    player.location = board["G2"]
    player.heldItem = null
  }

  "player can start a new waffle" {
    setup()
    player.use(waffleLoc)
  }

  "player can start a new waffle even with his hands full" {
    setup()
    player.heldItem = Dish()
    player.use(waffleLoc)
  }

  "player cannot start a new waffle if there's already one on" {
    setup()
    player.use(waffleLoc)
    board.tick()  // not necessary, but illustrates the point
    shouldThrowAny { player.use(waffleLoc) }
  }

  "player cannot take off a waffle that is cooking" {
    setup()
    player.use(waffleLoc)
    repeat(cookTime-1) { board.tick() }
    shouldThrowAny { player.use(waffleLoc) }
  }

  "player can take off a waffle that is cooked (#1)" {
    setup()
    player.use(waffleLoc)
    repeat(cookTime) { board.tick() }
    player.use(waffleLoc)
    player.heldItem shouldBe Waffle
  }

  "player can insta-plate a waffle that is cooked (#1)" {
    setup()
    player.use(waffleLoc)
    repeat(cookTime) { board.tick() }
    player.heldItem = Dish()
    player.use(waffleLoc)
    player.heldItem shouldBe Dish(Waffle)
  }

  "player can take off a waffle that is cooked (#2)" {
    setup()
    player.use(waffleLoc)
    repeat(totalBurnTime-1) { board.tick() }
    player.use(waffleLoc)
    player.heldItem shouldBe Waffle
  }

  "player can take off a burnt waffle" {
    setup()
    player.use(waffleLoc)
    repeat(totalBurnTime) { board.tick() }
    player.use(waffleLoc)
    player.heldItem shouldBe BurntWaffle
  }

  "once cleared, a waffle iron can be started again" {
    setup()
    player.use(waffleLoc)  // turn it on
    repeat(totalBurnTime-1) { board.tick() }
    player.use(waffleLoc)  // take a waffle
    player.use(waffleLoc)  // turn it on again
  }

})