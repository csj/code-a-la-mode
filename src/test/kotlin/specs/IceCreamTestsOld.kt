package specs

//import com.codingame.game.*
//import io.kotlintest.shouldBe
//import io.kotlintest.shouldThrowAny
//import io.kotlintest.specs.FreeSpec
//
//class IceCreamTestsOld: FreeSpec({
//  "scooping ice cream" - {
//    val iceCreamLoc = "I2"
//    val board = buildEmptyBoard()
//    board[iceCreamLoc].equipment = IceCreamCrate(IceCreamFlavour.VANILLA)
//    val player = Player()
//
//    "a scoop on top of an ice cream crate" - {
//      fun setup() {
//        player.location = board["H2"]
//        player.heldItem = null
//        board["I2"].dish = Scoop()
//      }
//
//      "can be picked up (without disturbing the crate)" {
//        setup()
//        player.take(board["I2"])
//        board["I2"].equipment shouldBe IceCreamCrate(IceCreamFlavour.VANILLA)
//        board["I2"].dish shouldBe null
//        player.heldItem shouldBe Scoop()
//      }
//
//      "cannot be USEd" {
//        setup()
//        shouldThrowAny {
//          player.use(board["I2"])
//        }
//      }
//    }
//
//    "a player holding a full scoop near a table with a dish" - {
//      fun setup() {
//        player.location = board["E2"]
//        player.heldItem = Scoop(ScoopState.IceCream(IceCreamFlavour.VANILLA))
//        board["F2"].dish = Dish()
//      }
//
//      "can DROP ice cream onto a dish out of a scoop" {
//        setup()
//        player.drop(board["F2"])
//        board["F2"].dish shouldBe Dish(IceCream(IceCreamFlavour.VANILLA))
//        player.heldItem shouldBe Scoop(ScoopState.Dirty(IceCreamFlavour.VANILLA))
//      }
//
//      "cannot DROP ice cream onto a dish out of a scoop, if the dish already has ice cream" {
//        setup()
//        (board["F2"].dish as Dish) += IceCream(IceCreamFlavour.VANILLA)
//        shouldThrowAny {
//          player.drop(board["F2"])
//        }
//      }
//
//      "can DROP a full scoop onto a table (this drops the entire scoop)" {
//        setup()
//        player.drop(board["D2"])
//        board["D2"].dish shouldBe Scoop(ScoopState.IceCream(IceCreamFlavour.VANILLA))
//        player.heldItem shouldBe null
//      }
//
//      "cannot DROP a scoop onto the floor" {
//        setup()
//        shouldThrowAny {
//          player.drop(board["E1"])
//        }
//      }
//
//    }
//
//    "a player holding a scoop near the ice cream crate" - {
//      fun setup() {
//        player.location = board["H2"]
//        player.heldItem = Scoop()
//      }
//
//      "can't scoop if he's too far away" {
//        setup()
//        shouldThrowAny {
//          player.location = board["I4"]
//          player.use(board[iceCreamLoc])
//        }
//      }
//
//      "can scoop under normal circumstances" {
//        setup()
//        player.use(board[iceCreamLoc])
//        player.heldItem.shouldBe(Scoop(ScoopState.IceCream(IceCreamFlavour.VANILLA)))
//      }
//
//      "can scoop using a dirty vanilla scoop" {
//        setup()
//        player.heldItem = Scoop(ScoopState.Dirty(IceCreamFlavour.VANILLA))
//        player.use(board[iceCreamLoc])
//        player.heldItem.shouldBe(Scoop(ScoopState.IceCream(IceCreamFlavour.VANILLA)))
//      }
//
//      "can't scoop if the scoop was used with a different flavour" {
//        setup()
//        player.heldItem = Scoop(ScoopState.Dirty(IceCreamFlavour.BUTTERSCOTCH))
//        shouldThrowAny {
//          player.use(board[iceCreamLoc])
//        }
//      }
//
//      "can't scoop if already holding ice cream" {
//        setup()
//        player.heldItem = Scoop(ScoopState.IceCream(IceCreamFlavour.VANILLA))
//        shouldThrowAny {
//          player.use(board[iceCreamLoc])
//        }
//      }
//
//      "can't scoop if he USEs the wrong location" {
//        setup()
//        shouldThrowAny {
//          player.use(board["H3"])
//        }
//      }
//    }
//  }
//})