package specs

import com.codingame.game.*
import com.codingame.game.Player
import com.codingame.game.model.*
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowAny
import io.kotlintest.specs.FreeSpec

class ChoppingBoardTests: FreeSpec({

  val board = buildEmptyBoard()
  val choppingBoardLoc = board["D6"]
  val player = Player()

  fun setup() {
    choppingBoardLoc.equipment = ChoppingBoard()
    player.location = board["D7"]
    player.heldItem = null
  }

  "a player can chop a banana" {
    setup()
    player.heldItem = Banana
    player.use(choppingBoardLoc)
    player.heldItem shouldBe ChoppedBananas
  }

  "a player cannot chop a banana if there's pie on the chopping board" {
    setup()
    choppingBoardLoc.equipment = ChoppingBoard(Pie(PieFlavour.Blueberry))
    player.heldItem = Banana
    shouldThrowAny { player.use(choppingBoardLoc) }
  }

  "a player cannot put most items on a chopping board" {
    listOf(Dish(), Strawberries, BurntPie, RawPie(PieFlavour.Blueberry), ChoppedBananas).forEach { item ->
      setup()
      player.heldItem = item
      shouldThrowAny { player.use(choppingBoardLoc) }
    }
  }

  "a player can put a cooked pie on a chopping board" {
    setup()
    player.heldItem = Pie(PieFlavour.Strawberry)
    player.use(choppingBoardLoc)
    player.heldItem shouldBe null
  }

  "a player cannot put a cooked pie on a chopping board if there's already pie there (no combining!)" {
    setup()
    choppingBoardLoc.equipment = ChoppingBoard(Pie(PieFlavour.Blueberry))
    player.heldItem = Pie(PieFlavour.Strawberry)
    shouldThrowAny { player.use(choppingBoardLoc) }
  }

  "chopping board with full pie" - {
    "a player can take a slice" {
      setup()
      choppingBoardLoc.equipment = ChoppingBoard(Pie(PieFlavour.Blueberry))
      player.use(choppingBoardLoc)
      player.heldItem shouldBe BlueberrySlice
      (choppingBoardLoc.equipment as ChoppingBoard).pieOnBoard shouldBe Pie(PieFlavour.Blueberry, 3)
    }
  }

  "chopping board with partial pie" - {
    "a player can take a slice" {
      setup()
      choppingBoardLoc.equipment = ChoppingBoard(Pie(PieFlavour.Strawberry, 2))
      player.use(choppingBoardLoc)
      player.heldItem shouldBe StrawberrySlice
      (choppingBoardLoc.equipment as ChoppingBoard).pieOnBoard shouldBe Pie(PieFlavour.Strawberry, 1)
    }

    "a player can insta-plate a slice" {
      setup()
      choppingBoardLoc.equipment = ChoppingBoard(Pie(PieFlavour.Strawberry, 2))
      player.heldItem = Dish()
      player.use(choppingBoardLoc)
      player.heldItem shouldBe Dish(StrawberrySlice)
    }

    "a player cannot use if his hands have something else" {
      setup()
      choppingBoardLoc.equipment = ChoppingBoard(Pie(PieFlavour.Strawberry, 2))
      player.heldItem = Blueberries
      shouldThrowAny { player.use(choppingBoardLoc) }
    }
  }

  "chopping board with a single slice pie (note: pie(1) and not pieslice yet)" - {
    "a player can take it" {
      setup()
      choppingBoardLoc.equipment = ChoppingBoard(Pie(PieFlavour.Strawberry, 1))
      player.use(choppingBoardLoc)
      player.heldItem shouldBe StrawberrySlice
      (choppingBoardLoc.equipment as ChoppingBoard).pieOnBoard shouldBe null
    }
  }

})
