import com.codingame.game.*
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowAny
import io.kotlintest.specs.FreeSpec

class ChoppingBoardTests: FreeSpec({

  val board = buildBoard()
  val choppingBoardLoc = board["C7"]
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
    listOf(Scoop(), Dish(), Strawberries, BurntPie, RawPie(PieFlavour.Blueberry), ChoppedBananas).forEach { item ->
      setup()
      player.heldItem = item
      shouldThrowAny { player.drop(choppingBoardLoc) }
    }
  }

  "a player can put a cooked pie on a chopping board" {
    setup()
    player.heldItem = Pie(PieFlavour.Strawberry)
    player.drop(choppingBoardLoc)
    player.heldItem shouldBe null
    choppingBoardLoc.equipment shouldBe ChoppingBoard(Pie(PieFlavour.Strawberry))
  }

  "a player can put a partial pie on a chopping board" {
    setup()
    player.heldItem = Pie(PieFlavour.Strawberry, 2)
    player.drop(choppingBoardLoc)
    player.heldItem shouldBe null
    choppingBoardLoc.equipment shouldBe ChoppingBoard(Pie(PieFlavour.Strawberry, 2))
  }

  "a player cannot put a partial pie on a chopping board if there's already pie there (no combining!)" {
    setup()
    choppingBoardLoc.equipment = ChoppingBoard(Pie(PieFlavour.Blueberry, 1))
    player.heldItem = Pie(PieFlavour.Blueberry, 2)
    shouldThrowAny { player.drop(choppingBoardLoc) }
  }

  "a player cannot put a pie slice on a chopping board" {
    setup()
    player.heldItem = PieSlice(PieFlavour.Strawberry)
    shouldThrowAny { player.drop(choppingBoardLoc) }
  }


  "chopping board with full pie" - {
    "a player can take it" {
      setup()
      choppingBoardLoc.equipment = ChoppingBoard(Pie(PieFlavour.Blueberry))
      player.take(choppingBoardLoc)
      player.heldItem shouldBe Pie(PieFlavour.Blueberry)
      (choppingBoardLoc.equipment as ChoppingBoard).pieOnBoard shouldBe null
    }

    "a player can use it" {
      setup()
      choppingBoardLoc.equipment = ChoppingBoard(Pie(PieFlavour.Blueberry))
      player.use(choppingBoardLoc)
      player.heldItem shouldBe PieSlice(PieFlavour.Blueberry)
      (choppingBoardLoc.equipment as ChoppingBoard).pieOnBoard shouldBe Pie(PieFlavour.Blueberry, 3)
    }
  }

  "chopping board with partial pie" - {
    "a player can take it" {
      setup()
      choppingBoardLoc.equipment = ChoppingBoard(Pie(PieFlavour.Strawberry, 2))
      player.take(choppingBoardLoc)
      player.heldItem shouldBe Pie(PieFlavour.Strawberry, 2)
      (choppingBoardLoc.equipment as ChoppingBoard).pieOnBoard shouldBe null
    }

    "a player can use it" {
      setup()
      choppingBoardLoc.equipment = ChoppingBoard(Pie(PieFlavour.Strawberry, 2))
      player.use(choppingBoardLoc)
      player.heldItem shouldBe PieSlice(PieFlavour.Strawberry)
      (choppingBoardLoc.equipment as ChoppingBoard).pieOnBoard shouldBe Pie(PieFlavour.Strawberry, 1)
    }

    "a player cannot use it with his hands full" {
      setup()
      choppingBoardLoc.equipment = ChoppingBoard(Pie(PieFlavour.Strawberry, 2))
      player.heldItem = Dish()
      shouldThrowAny { player.use(choppingBoardLoc) }
    }
  }

  "chopping board with a single slice pie (note: pie(1) and not pieslice yet)" - {
    "a player can take it" {
      setup()
      choppingBoardLoc.equipment = ChoppingBoard(Pie(PieFlavour.Strawberry, 1))
      player.take(choppingBoardLoc)
      player.heldItem shouldBe PieSlice(PieFlavour.Strawberry)
      (choppingBoardLoc.equipment as ChoppingBoard).pieOnBoard shouldBe null
    }

    "a player cannot use it (nothing to chop!)" {
      setup()
      choppingBoardLoc.equipment = ChoppingBoard(Pie(PieFlavour.Strawberry, 1))
      shouldThrowAny { player.use(choppingBoardLoc) }
    }
  }
})
