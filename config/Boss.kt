import java.io.InputStream
import java.io.PrintStream
import java.util.*
import java.lang.Math.abs

fun <T> Boolean.then(thenExpr: T) = if (this) thenExpr else null

fun <T> Iterator<T>.tryNext() = if (hasNext()) next() else null

fun <T> Iterator<T>.findNext(pred: (T) -> Boolean): T? {
    while (hasNext()) {
        next().also { if (pred(it)) return it }
    }
    return null
}

inline fun <reified T> Array<Array<T>>.transpose(default: () -> T): Array<Array<T>> {
    val rows = this.size
    val cols = this[0].size
    val trans = Array(cols) { Array(rows) { default() } }
    for (i in 0 until cols) {
        for (j in 0 until rows) trans[i][j] = this[j][i]
    }
    return trans
}

fun Sequence<String>.splitAccumulate() = sequence {
    var currentLine = ""

    forEach { tok ->
        if (currentLine.length + tok.length > 9) {
            yield(currentLine.substring(1))
            currentLine = ""
        }
        currentLine += " $tok"
    }

    yield(currentLine.substring(1))
}

@Suppress("unused")
abstract class BaseCALMPlayer(val stdin: InputStream = System.`in`, val stdout: PrintStream = System.out, val stderr: PrintStream = System.err) {
    val scanner = Scanner(stdin)
    val width: Int = 11
    val height: Int = 7
    val layout: List<String>

    init {
        val longQueue = List(scanner.nextInt()) {
            Customer(Item.parse(scanner.next())!!, scanner.nextInt())
        }

        layout = List(height) { scanner.next() }
            .also { stderr.println("Table layout:\n$it")}
    }

    protected fun readInputs(): GameState {
        val turnsRemaining = scanner.nextInt()
            .also { stderr.println("Turns remaining: $it") }
        val myPlayer = Player(scanner.nextInt(), scanner.nextInt(), Item.parse(scanner.next()))
        val myFriend = Player(scanner.nextInt(), scanner.nextInt(), Item.parse(scanner.next()))

        val tables = (0 until height).flatMap { row -> (0 until width)
            .filter { col -> layout[row][col] != '.'}
            .map { col -> Table(col, row, layout[row][col].nullIf('#')) }
        }

        repeat(scanner.nextInt()) {
            val x = scanner.nextInt()
            val y = scanner.nextInt()
            tables.find { it.x == x && it.y == y }!!.item = Item.parse(scanner.next())
        }

        val ovenContents = scanner.next()
        val ovenTimer = scanner.nextInt()

        val queue = List(scanner.nextInt()) {
            Customer(Item.parse(scanner.next())!!, scanner.nextInt())
        }

        return GameState(myPlayer, myFriend, tables, queue, ovenContents, ovenTimer)
    }
}

private fun Char.nullIf(char: Char) = if (this == char) null else this

data class GameState(
    val myPlayer: Player,
    val myFriend: Player,
    val tables: List<Table>,
    val queue: List<Customer>,
    val ovenContents: String,
    val ovenTimer: Int)

data class Table(
    val x: Int, val y: Int,
    var equipment: Char? = null,
    var item: Item? = null)

data class Item(val description: String) {
    val toks = description.split("-")
    val itemType = toks[0]
    val itemContents = toks.drop(1)

    companion object {
        fun parse(description: String) =
            if (description == "NONE") null
            else Item(description)

    }
}

data class Player(val x: Int, val y: Int, val carrying: Item?)
data class Customer(val dish: Item, val award: Int)

object Constants {
    enum class ITEM {
        FOOD, DOUGH, DISH, STRAWBERRIES, CHOPPED_DOUGH, RAW_TART
    }

    enum class FOOD {
        ICE_CREAM,
        BLUEBERRIES,
        CHOPPED_STRAWBERRIES,
        CROISSANT,
        TART
    }

    const val OVEN_COOKTIME = 10
    const val OVEN_BURNTIME = 10
    val TIP = 200
}


class NaiveAllItemsPlayer(
    stdin: InputStream = System.`in`, stdout: PrintStream = System.out, stderr: PrintStream = System.err): BaseCALMPlayer(stdin, stdout, stderr) {

    lateinit var goal: Item
    lateinit var inputs: GameState
    lateinit var crates: Map<String, Table>

    private fun findEquipment(equipmentChar: Char) =
        inputs.tables.firstOrNull { it.equipment == equipmentChar }

    init {
        var turn = 0
        while (true) {
            turn++
            inputs = readInputs()

            crates = listOf(
                Constants.FOOD.BLUEBERRIES to 'B',
                Constants.FOOD.ICE_CREAM to 'I',
                Constants.ITEM.DOUGH to 'H',
                Constants.ITEM.STRAWBERRIES to 'S'
            ).map { (item, char) ->
                item.name to (inputs.tables.firstOrNull { it.equipment == char } )
            }.filter { (_, v) -> v != null }.map { (k, v) -> k to v!! }.toMap()

            if (turn % 3 == 0) {
                stdout.println("WAIT")
            } else {
                stdout.println(act() ?: "WAIT")
            }

        }
    }

    private fun act(): String? {
        val carrying = inputs.myPlayer.carrying

        // 0. If the oven has something ready, go get it!
        if (inputs.ovenContents in listOf(
                Constants.FOOD.CROISSANT.name,
                Constants.FOOD.TART.name
            ))
            return if (carrying == null) findEquipment('O')!!.use() else useEmptyTable()

        goal = inputs.queue.firstOrNull()?.dish ?: return null
        stderr.println("Current goal is: $goal")

        // make all the items and leave them on tables. then grab a plate and collect them all.
        val goalItems = goal.itemContents.toSet()

        // 1. if we're holding a plate, grab missing items from tables and such.
        // If one is missing, drop the plate
        if (carrying?.itemType == Constants.ITEM.DISH.name) {
            stderr.println("we are carrying a plate")

            val dishContents = carrying.itemContents.toSet()

            // if it has anything we don't need, dishwasher it
            if ((dishContents - goalItems).isNotEmpty())
                return findEquipment('D')!!.use()

            // find next missing item from dish
            val missingItems = goalItems - dishContents
            val missingItem = missingItems.firstOrNull() ?:
            return findEquipment('W')!!.use()

            stderr.println("missing item: $missingItem")

            return when (missingItem) {
                in crates.keys -> {
                    val crateLoc = crates[missingItem]!!
                    stderr.println("going for crate at $crateLoc")
                    crateLoc.use()
                }
                else -> {
                    val tableLoc = inputs.tables.find {
                        it.item?.itemType == missingItem
                    }
                    if (tableLoc != null) tableLoc.use()
                    else useEmptyTable()  // put down the dish
                }
            }
        }

        // 2. Build missing items. If all items are built, grab an empty plate.
        goalItems.forEach { item ->
            if (isReady(item)) return@forEach
            return when(item) {
                Constants.FOOD.CROISSANT.name -> buildCroissant()
                Constants.FOOD.TART.name -> buildTart()
                Constants.FOOD.CHOPPED_STRAWBERRIES.name -> buildStrawberries()
                else -> throw Exception("Unrecognized dish: $item")
            }
        }

        // 3. if we're holding something we shouldn't, put it down.
        if (carrying != null) return useEmptyTable()

        return getEmptyPlate()
    }

    private fun buildStrawberries(): String? {
        val carrying = inputs.myPlayer.carrying
        return when {
            carrying == null -> crates[Constants.ITEM.STRAWBERRIES.name]!!.use()
            carrying.itemType == Constants.ITEM.STRAWBERRIES.name -> findEquipment('C')?.use()
            carrying.itemType == Constants.FOOD.CHOPPED_STRAWBERRIES.name -> useEmptyTable()
            else -> { stderr.println("uhhh, holding: $carrying"); return useEmptyTable() }
        }
    }

    private fun isReady(item: String): Boolean =
        item in crates.keys ||
            inputs.tables.any { it.item?.itemType == item } ||
            (item == "CROISSANT" && inputs.ovenContents in listOf(
                Constants.ITEM.DOUGH.name,
                Constants.FOOD.CROISSANT.name
            ))

    private fun getEmptyPlate(): String = (
        inputs.tables.find { it.item?.itemType == Constants.ITEM.DISH.name }
            ?: findEquipment('D')!!).use()

    private fun useEmptyTable(): String {
        return inputs.myPlayer.run {
            inputs.tables.filter { it.equipment == null && it.item == null }
                .minBy { abs(it.x - x) + abs(it.y - y) }!!.use()
        }
    }

    private fun buildCroissant(): String? {
        return when(inputs.myPlayer.carrying?.itemType) {
            null -> crates[Constants.ITEM.DOUGH.name]!!.use()
            Constants.ITEM.DOUGH.name -> findEquipment('O')!!.use()
            else -> useEmptyTable()
        }
    }

    private fun buildTart(): String? {
        stderr.println("building tart")
        val x = inputs.myPlayer.x
        val y = inputs.myPlayer.y

        if (inputs.ovenContents != "NONE") {
            stderr.println("waiting for tart in oven")
            return findEquipment('O')!!.use()
        }

        val carrying = inputs.myPlayer.carrying
        when {
            carrying == null -> // find the closest tart CHOPPED_DOUGH, or get one from the box
            {
                stderr.println("looking for CHOPPED_DOUGH")
                val tart = (inputs.tables.filter {
                    it.item?.itemType == Constants.ITEM.CHOPPED_DOUGH.name
                } + crates[Constants.ITEM.DOUGH.name]!!)
                    .minBy { abs(it.x - x) + abs(it.y - y) }

                return tart!!.use()
            }

            carrying.itemType == Constants.ITEM.DOUGH.name ->
                return findEquipment('C')!!.use()

            carrying.itemType == Constants.ITEM.CHOPPED_DOUGH.name ->
                return crates[Constants.FOOD.BLUEBERRIES.name]!!.use()

            carrying.itemType == Constants.ITEM.RAW_TART.name -> {
                stderr.println("CHOPPED_DOUGH is complete; heading for oven")
                return findEquipment('O')!!.use()
            }
        }

        return useEmptyTable()
    }

    private fun Table.use(): String = "USE $x $y"
}

fun main() {
    NaiveAllItemsPlayer()
}