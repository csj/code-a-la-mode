package specs

import com.codingame.game.*
import com.codingame.game.model.*
import io.kotlintest.matchers.beEmpty
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.specs.FreeSpec

class CustomerTests: FreeSpec({
    val (alice, andrew) = buildPlayers()
    val board = buildEmptyBoard()

    val aliceStuff = Dish(ChoppedBananas, IceCreamBall(IceCreamFlavour.VANILLA))
    val andrewStuff = Dish(IceCreamBall(IceCreamFlavour.BUTTERSCOTCH), IceCreamBall(IceCreamFlavour.CHOCOLATE))

    var score = 0
    val queue = CustomerQueue { points -> score += points }

    val windowALoc = board["B0"]
    windowALoc.equipment = Window { queue.delivery(it) }

    fun setup() {
        alice.heldItem = aliceStuff;   alice.location = board["B1"]
        andrew.heldItem = andrewStuff; andrew.location = board["C1"]
        score = 0
    }

    "a team can score points by delivering" {
        setup()
        queue += Customer(aliceStuff, 100)
        alice.use(windowALoc)
        score shouldBe 100
        queue should beEmpty()
    }

    "a team scores no points by delivering the wrong thing" {
        setup()
        queue += Customer(aliceStuff, 100)
        andrew.use(windowALoc)
        score shouldBe 0
        queue shouldNot beEmpty()
    }
})