import java.util.*
import kotlin.math.abs

// ideas:
// - get from closest table, not just any, or crate
// - when holding plate, go clockwise, not in plate order
// - when building food, build easiest first

data class XY(val x: Int, val y: Int) {
    fun use() { println("USE $x $y") }
    fun dist(other: XY) = abs(other.x - x) + abs(other.y - y)
}

data class Table(val loc: XY, val item: String)
data class Customer(val item: String, val award: Int)
enum class Things {
    BLUEBERRIES,
    ICE_CREAM,
    STRAWBERRIES,
    CHOPPED_STRAWBERRIES,
    DOUGH,
    CROISSANT,
    CHOPPED_DOUGH,
    RAW_TART,
    TART
}
/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fun main(args : Array<String>) {
    val input = Scanner(System.`in`)
    val numAllCustomers = input.nextInt()
    for (i in 0 until numAllCustomers) {
        val customerItem = input.next() // the food the customer is waiting for
        val customerAward = input.nextInt() // the number of points awarded for delivering the food
    }
    input.nextLine()
    val kitchen = List(7) { input.nextLine() }
    fun findEquipment(ch: Char): XY? {
        kitchen.forEachIndexed { y, line ->
            val x = line.indexOf(ch)
            if (x != -1) return XY(x, y)
        }
        return null
    }

    val oven = findEquipment('O')
    val blueberries = findEquipment('B')!!
    val iceCream = findEquipment('I')!!
    val dishWasher = findEquipment('D')!!
    val bell = findEquipment('W')!!
    val choppingBoard = findEquipment('C')
    val strawberries = findEquipment('S')
    val dough = findEquipment('H')

    val tables = mutableListOf<XY>()
    kitchen.forEachIndexed { y, line ->
        line.forEachIndexed { x, ch ->
            if (ch == '#') tables.add(XY(x,y))
        }
    }
    
    // game loop
    while (true) {
        val turnsRemaining = input.nextInt()
        val playerX = input.nextInt()
        val playerY = input.nextInt()
        val myPos = XY(playerX, playerY)
        val playerItem = input.next()
        val playerThing = try {
            Things.valueOf(playerItem)
        } catch (ex: Exception) {
            null
        }

        System.err.println("Player item: $playerItem")
        val playerDish = if (playerItem.startsWith("DISH")) {
            playerItem.split('-').drop(1)
                .map { Things.valueOf(it) }
        } else {
            null
        }
        System.err.println("Player dish: $playerDish")

        val partnerX = input.nextInt()
        val partnerY = input.nextInt()
        val partnerItem = input.next()

        val numTablesWithItems = input.nextInt() // the number of tables in the kitchen that currently hold an item
        val tablesWithItems = List(numTablesWithItems) {
            val tableX = input.nextInt()
            val tableY = input.nextInt()
            val item = input.next()
            Table(XY(tableX, tableY), item)
        }

        fun findItem(itemToFind: Things): Table? =
            tablesWithItems.find { it.item == itemToFind.name }

        val blankTables = tables - tablesWithItems.map { it.loc }
        fun useClosestEmptyTable() = blankTables.minBy { it.dist(myPos) }

        val ovenContents = input.next() // ignore until wood 1 league
        val ovenTimer = input.nextInt()

        val numCustomers = input.nextInt() // the number of customers currently waiting for food
        val customers = List(numCustomers) {
            val customerItem = input.next()
            val customerAward = input.nextInt()
            Customer(customerItem, customerAward)
        }

        val customer = customers.first()

        // DISH-BLUEBERRIES-TART
        val itemsTheyWant = customer.item.split('-').drop(1)
            .map { Things.valueOf(it) }

        fun prepareStrawberries(): XY? = when {
            findItem(Things.CHOPPED_STRAWBERRIES) != null -> null
            playerThing == Things.STRAWBERRIES -> choppingBoard
            playerItem == "NONE" -> strawberries
            else -> useClosestEmptyTable()!!
        }

        fun prepareCroissant(): XY? = when {
            findItem(Things.CROISSANT) != null -> null
            ovenContents != "NONE" -> oven
            playerThing == Things.DOUGH -> oven
            playerItem == "NONE" -> dough
            else -> useClosestEmptyTable()!!
        }

        fun prepareTart(): XY? = when {
            findItem(Things.TART) != null -> null
            ovenContents != "NONE" -> oven
            playerThing == Things.DOUGH -> choppingBoard
            playerThing == Things.CHOPPED_DOUGH -> blueberries
            playerThing == Things.RAW_TART -> oven
            playerItem == "NONE" -> dough
            else -> useClosestEmptyTable()!!
        }

        fun action() {
            // 1. make sure all the things are ready
            if (playerDish == null) itemsTheyWant.forEach { thing ->
                val prepareAction = when(thing) {
                    Things.BLUEBERRIES -> { null }
                    Things.ICE_CREAM -> { null }
                    Things.CHOPPED_STRAWBERRIES -> prepareStrawberries()
                    Things.CROISSANT -> prepareCroissant()
                    Things.TART -> prepareTart()
                    else -> throw Exception("They want unusual things!")
                }
                if (prepareAction != null) return prepareAction.use()
            }
            System.err.println("Everything is ready")

            // 2. If not holding a dish, get a dish
            if (playerDish == null) return dishWasher.use()

            // 3. We are holding a dish. Figure out what to get next
            val nextItem = (itemsTheyWant - playerDish).firstOrNull() ?: return bell.use()

            // 4. Get the next item we need
            val useLoc = when (nextItem) {
                Things.BLUEBERRIES -> blueberries
                Things.ICE_CREAM -> iceCream
                else -> findItem(nextItem)?.loc
            }
            if (useLoc != null) return useLoc.use()
            return useClosestEmptyTable()!!.use()
        }

        action()

    }
}

