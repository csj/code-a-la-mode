import java.util.*
import kotlin.math.abs

data class XY(val x: Int, val y: Int) {
    fun use() { println("USE $x $y") }
    fun dist(myPos: XY) = abs(myPos.x - x) + abs(myPos.y - y)
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
    val blueberries = findEquipment('B')
    val iceCream = findEquipment('I')
    val dishWasher = findEquipment('D')
    val bell = findEquipment('W')
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

        fun prepareStrawberries(): XY? {
            System.err.println("STRAWABEEIES")
            if (findItem(Things.CHOPPED_STRAWBERRIES) != null) return null
            if (playerThing == Things.STRAWBERRIES) return choppingBoard
            if (playerItem == "NONE") return strawberries
            return useClosestEmptyTable()!!
        }

        fun prepareCroissant(): XY? {
            System.err.println("CROIESIEAT")

            if (findItem(Things.CROISSANT) != null) return null
            if (ovenContents != "NONE") return oven
            if (playerThing == Things.DOUGH) return oven
            if (playerItem == "NONE") return dough
            return useClosestEmptyTable()!!
        }

        fun prepareTart(): XY? {
            System.err.println("TANTA")

            if (findItem(Things.TART) != null) return null
            if (ovenContents != "NONE") return oven
            if (playerThing == Things.DOUGH) return choppingBoard
            if (playerThing == Things.CHOPPED_DOUGH) return blueberries
            if (playerThing == Things.RAW_TART) return oven
            if (playerItem == "NONE") return dough
            return useClosestEmptyTable()!!
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
                if (prepareAction != null) {
                    System.err.println("Doing a thing to prepare")
                    return println("USE ${prepareAction.x} ${prepareAction.y}")
                }
                System.err.println("Everything is ready")
            }

            // 2. If not holding a dish, get a dish
            System.err.println("2")
            if (playerDish == null) return dishWasher!!.use()

            System.err.println("3")
            // 3. We are holding a dish. Figure out what to get next
            val nextItem = (itemsTheyWant - playerDish).firstOrNull() ?: return bell!!.use()

            // 4. Get the next item we need
            System.err.println("4")
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

