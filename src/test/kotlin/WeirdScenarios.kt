import com.codingame.game.*
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowAny
import io.kotlintest.specs.FreeSpec

class WeirdScenarios: FreeSpec({

  val board = buildBoard()
  val tableLoc = board["A4"]
  val player = Player()

  fun setup() {
    player.location = board["B4"]
    player.heldItem = null
    tableLoc.item = null
    tableLoc.equipment = null
  }

  "player can insta-plate some table food while holding a dish by TAKing it" {
    setup()
    player.heldItem = Dish(Strawberries, Blueberries)
    tableLoc.item = PieSlice(PieFlavour.Blueberry)
    player.take(tableLoc)
    player.heldItem shouldBe Dish(Strawberries, Blueberries, PieSlice(PieFlavour.Blueberry))
    tableLoc.item shouldBe null
  }

  "player can insta-plate some table food by DROPping a dish onto it (squish?)" {
    setup()
    player.heldItem = Dish(Strawberries, Blueberries)
    tableLoc.item = PieSlice(PieFlavour.Blueberry)
    player.drop(tableLoc)
    player.heldItem shouldBe null
    tableLoc.item shouldBe Dish(Strawberries, Blueberries, PieSlice(PieFlavour.Blueberry))
  }

  "player can insta-shell some table berries by TAKing" {
    setup()
    player.heldItem = RawPie(PieFlavour.Strawberry, 1)
    tableLoc.item = Strawberries
    player.take(tableLoc)
    player.heldItem shouldBe RawPie(PieFlavour.Strawberry, 2)
    tableLoc.item shouldBe null
  }

  "player cannot insta-shell some table berries by DROPping a shell onto them (squish)" {
    setup()
    player.heldItem = RawPie(PieFlavour.Blueberry, 1)
    tableLoc.item = Blueberries
    shouldThrowAny { player.drop(tableLoc) }
  }

  "player can insta-plate the last piece of pie remaining on a chopping board by TAKing" {
    setup()
    tableLoc.equipment = ChoppingBoard(Pie(PieFlavour.Blueberry, 1))
    player.heldItem = Dish()
    player.take(tableLoc)
    player.heldItem shouldBe Dish(PieSlice(PieFlavour.Blueberry))
    tableLoc.equipment shouldBe ChoppingBoard()
  }

  // player cannot insta-plate ice cream. Requires interacting with a scoop.

  "player cannot chop and insta-plate pie (hands too full)" {
    setup()
    tableLoc.equipment = ChoppingBoard(Pie(PieFlavour.Strawberry, 3))
    player.heldItem = Dish()
    shouldThrowAny { player.use(tableLoc) }
  }

  "player cannot insta-plate directly from a crate (hands too full)" {
    setup()
    tableLoc.equipment = Crate(() -> Strawberries)
    player.heldItem = Dish()
    shouldThrowAny { player.use(tableLoc) }
  }

  "player cannot insta-shell directly from a crate (hands too full)" {
    setup()
    tableLoc.equipment = Crate(() -> Strawberries)
    player.heldItem = RawPie()
    shouldThrowAny { player.use(tableLoc) }
  }

  "player cannot insta-shell directly from a shell crate (hands too full)" {
    setup()
    tableLoc.equipment = Crate(() -> RawPie())
    player.heldItem = Blueberries
    shouldThrowAny { player.use(tableLoc) }
  }
})