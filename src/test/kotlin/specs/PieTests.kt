package specs

import com.codingame.game.*
import com.codingame.game.Player
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowAny
import io.kotlintest.specs.FreeSpec

class PieTests: FreeSpec({

  val board = buildEmptyBoard()
  val ovenLoc = board["I7"]
  val cookTime = 5
  val burnTime = 5
  val totalBurnTime = cookTime + burnTime
  val player = Player()

  fun setup() {
    ovenLoc.equipment = Oven(cookTime, burnTime)
    player.location = board["H7"]
    player.heldItem = null
  }

  "player can start cooking a pie" {
    setup()
    player.heldItem = RawPie(PieFlavour.Strawberry)
    player.use(ovenLoc)
    player.heldItem shouldBe null
  }

  "player cannot remove a pie that is cooking" {
    setup()
    player.heldItem = RawPie(PieFlavour.Strawberry)
    player.use(ovenLoc)
    repeat(cookTime-1) { board.tick() }
    shouldThrowAny { player.use(ovenLoc) }
  }

  "player can remove a pie that just finished cooking" {
    setup()
    player.heldItem = RawPie(PieFlavour.Strawberry)
    player.use(ovenLoc)
    repeat(cookTime) { board.tick() }
    player.use(ovenLoc)
    player.heldItem shouldBe Pie(PieFlavour.Strawberry)
  }

  "player can remove a pie that is almost burnt" {
    setup()
    player.heldItem = RawPie(PieFlavour.Strawberry)
    player.use(ovenLoc)
    repeat(totalBurnTime-1) { board.tick() }
    player.use(ovenLoc)
    player.heldItem shouldBe Pie(PieFlavour.Strawberry)
  }

  "player can remove a pie that has burned" {
    setup()
    player.heldItem = RawPie(PieFlavour.Strawberry)
    player.use(ovenLoc)
    repeat(totalBurnTime) { board.tick() }
    player.use(ovenLoc)
    player.heldItem shouldBe BurntPie
  }

  "burnt pie stays burnt" {
    setup()
    player.heldItem = RawPie(PieFlavour.Strawberry)
    player.use(ovenLoc)
    repeat(totalBurnTime + 3) { board.tick() }
    player.use(ovenLoc)
    player.heldItem shouldBe BurntPie
  }

  "player cannot start cooking a second pie before removing the first one" {
    setup()
    player.heldItem = RawPie(PieFlavour.Strawberry)
    player.use(ovenLoc)
    repeat(2) { board.tick() } // not that this matters

    player.heldItem = RawPie(PieFlavour.Blueberry)
    shouldThrowAny { player.use(ovenLoc) }
  }

  "player can start cooking a second pie after removing the first one" {
    setup()
    player.heldItem = RawPie(PieFlavour.Strawberry)
    player.use(ovenLoc)
    repeat(cookTime) { board.tick() }
    player.use(ovenLoc)

    player.heldItem = RawPie(PieFlavour.Blueberry)
    player.use(ovenLoc)
  }

  "player cannot add cooked or burnt pie to an oven" {
    listOf(BurntPie, Pie(PieFlavour.Blueberry)).forEach {
      setup()
      player.heldItem = it
      shouldThrowAny { player.use(ovenLoc) }
    }
  }

  "an oven is not a table" {
    setup()
    player.heldItem = Banana
    shouldThrowAny { player.use(ovenLoc) }
  }

})

