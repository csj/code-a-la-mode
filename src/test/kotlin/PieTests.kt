import com.codingame.game.*
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowAny
import io.kotlintest.specs.FreeSpec

class PieTests: FreeSpec({

  val board = buildBoard()
  val ovenLoc = board["I7"]
  val cookTime = 5
  val burnTime = 5
  val totalBurnTime = cookTime + burnTime
  val player = Player()
  val tableLoc = board["I8"]
  val frontOfTableLoc = board["H8"]

  fun setup() {
    ovenLoc.equipment = Oven(cookTime, burnTime)
    player.location = board["H7"]
    player.heldItem = null
  }

  "player can add berries to an empty shell" {
    tableLoc.item = RawPie()
    player.heldItem = Strawberries
    player.location = frontOfTableLoc
    player.drop(tableLoc)
    player.heldItem shouldBe null
    tableLoc.item shouldBe RawPie(PieFlavour.Strawberry, 1)
  }

  "player can add more berries to a partially filled shell" {
    tableLoc.item = RawPie(PieFlavour.Blueberry, 1)
    player.heldItem = Blueberries
    player.location = frontOfTableLoc
    player.drop(tableLoc)
    player.heldItem shouldBe null
    tableLoc.item shouldBe RawPie(PieFlavour.Blueberry, 2)
  }

  "player cannot add too many berries" {
    tableLoc.item = RawPie(PieFlavour.Blueberry)
    player.heldItem = Blueberries
    player.location = frontOfTableLoc
    shouldThrowAny { player.drop(tableLoc) }
  }

  "player cannot add different kinds of berries" {
    tableLoc.item = RawPie(PieFlavour.Blueberry, 1)
    player.heldItem = Strawberries
    player.location = frontOfTableLoc
    shouldThrowAny { player.drop(tableLoc) }
  }

  "player can start cooking a pie" {
    setup()
    player.heldItem = RawPie(PieFlavour.Strawberry)
    player.drop(ovenLoc)
    player.heldItem shouldBe null
  }

  "player cannot remove a pie that is cooking" {
    setup()
    player.heldItem = RawPie(PieFlavour.Strawberry)
    player.drop(ovenLoc)
    repeat(cookTime-1) { board.tick() }
    shouldThrowAny { player.take(ovenLoc) }
  }

  "player can remove a pie that just finished cooking" {
    setup()
    player.heldItem = RawPie(PieFlavour.Strawberry)
    player.drop(ovenLoc)
    repeat(cookTime) { board.tick() }
    player.take(ovenLoc)
    player.heldItem shouldBe Pie(PieFlavour.Strawberry)
  }

  "player can remove a pie that is almost burnt" {
    setup()
    player.heldItem = RawPie(PieFlavour.Strawberry)
    player.drop(ovenLoc)
    repeat(totalBurnTime-1) { board.tick() }
    player.take(ovenLoc)
    player.heldItem shouldBe Pie(PieFlavour.Strawberry)
  }

  "player can remove a pie that has burned" {
    setup()
    player.heldItem = RawPie(PieFlavour.Strawberry)
    player.drop(ovenLoc)
    repeat(totalBurnTime) { board.tick() }
    player.take(ovenLoc)
    player.heldItem shouldBe BurntPie
  }

  "player cannot start cooking a second pie before removing the first one" {
    setup()
    player.heldItem = RawPie(PieFlavour.Strawberry)
    player.drop(ovenLoc)
    repeat(2) { board.tick() } // not that this matters

    player.heldItem = RawPie(PieFlavour.Blueberry)
    shouldThrowAny { player.drop(ovenLoc) }
  }

  "player can start cooking a second pie after removing the first one" {
    setup()
    player.heldItem = RawPie(PieFlavour.Strawberry)
    player.drop(ovenLoc)
    repeat(cookTime) { board.tick() }
    player.take(ovenLoc)

    player.heldItem = RawPie(PieFlavour.Blueberry)
    player.drop(ovenLoc)
  }

  "player cannot add cooked or burnt pie to an oven" {
    listOf(BurntPie, Pie(PieFlavour.Blueberry)).forEach {
      setup()
      player.heldItem = it
      shouldThrowAny { player.drop(ovenLoc) }
    }
  }

  "an oven is not a table" {
    setup()
    player.heldItem = Scoop()
    shouldThrowAny { player.drop(ovenLoc) }
  }

})

