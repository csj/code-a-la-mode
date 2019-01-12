package specs

import com.codingame.game.*
import com.codingame.game.model.*
import io.kotlintest.matchers.beEmpty
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.specs.FreeSpec

class CustomerTests: FreeSpec({
    val (alice, bonnie, bill, andrew) = buildPlayers()
    val board = buildEmptyBoard()

    val aliceStuff = Dish(ChoppedBananas, IceCreamBall(IceCreamFlavour.VANILLA))
    val bonnieStuff = Dish(PieSlice(PieFlavour.Blueberry))
    val billStuff = Dish(Waffle, IceCreamBall(IceCreamFlavour.BUTTERSCOTCH), Strawberries)
    val andrewStuff = Dish(IceCreamBall(IceCreamFlavour.BUTTERSCOTCH), IceCreamBall(IceCreamFlavour.CHOCOLATE))

    val scores = mutableListOf(0,0)
    val queue = CustomerQueue { teamIndex, points -> scores[teamIndex] += points }

    val windowALoc = board["B0"]
    val windowBLoc = board["b0"]
    windowALoc.equipment = Window { queue.delivery(it, 0) }
    windowBLoc.equipment = Window { queue.delivery(it, 1) }

    fun setup() {
        alice.heldItem = aliceStuff;   alice.location = board["B1"]
        bonnie.heldItem = bonnieStuff; bonnie.location = board["b1"]
        bill.heldItem = billStuff;     bill.location = board["c1"]
        andrew.heldItem = andrewStuff; andrew.location = board["C1"]
        scores[0] = 0
        scores[1] = 0
    }

    "a team can score points by delivering" {
        setup()
        queue += Customer(aliceStuff, 100)
        alice.use(windowALoc)
        scores[0] shouldBe 100
        scores[1] shouldBe 0
        queue should beEmpty()
    }

    "a team scores no points by delivering the wrong thing" {
        setup()
        queue += Customer(aliceStuff, 100)
        andrew.use(windowALoc)
        scores[0] shouldBe 0
        scores[1] shouldBe 0
        queue shouldNot beEmpty()
    }
})