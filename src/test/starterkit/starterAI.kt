import java.util.*
import kotlin.math.abs


// Strategy:
// 1. Grab a blueberry, put it down
// 2. Grab some ice cream, put it down
// 3. Grab a plate, put it down
// 4. Repeat



data class XY(val x: Int, val y: Int)

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
    val map = List(7) {
        input.nextLine()
    }

    val tables = mutableListOf<XY>()
    map.forEachIndexed { y, line ->
        line.forEachIndexed { x, ch ->
            if (ch == '#') tables += XY(x,y)
        }
    }

    fun findEquipment(searchChar: Char): XY? {
        map.forEachIndexed { y, line ->
            line.forEachIndexed { x, ch ->
                if (ch == searchChar) return XY(x, y)
            }
        }
        return null
    }

    val blueberryXY = findEquipment('B')!!
    val iceCreamXY = findEquipment('I')!!
    val dishWasherXY = findEquipment('D')!!

    val equipmentLocations = listOf(blueberryXY, iceCreamXY, dishWasherXY)
    var nextEquipmentIndex = 0
    var useEmptyTable = false

    // game loop
    while (true) {
        val turnsRemaining = input.nextInt()
        val playerX = input.nextInt()
        val playerY = input.nextInt()
        val playerItem = input.next()
        val partnerX = input.nextInt()
        val partnerY = input.nextInt()
        val partnerItem = input.next()
        val numTablesWithItems = input.nextInt() // the number of tables in the kitchen that currently hold an item

        val filledTables = mutableListOf<XY>()
        for (i in 0 until numTablesWithItems) {
            val tableX = input.nextInt()
            val tableY = input.nextInt()
            val item = input.next()
            filledTables += XY(tableX, tableY)
        }

        val ovenContents = input.next() // ignore until bronze league
        val ovenTimer = input.nextInt()
        val numCustomers = input.nextInt() // the number of customers currently waiting for food
        for (i in 0 until numCustomers) {
            val customerItem = input.next()
            val customerAward = input.nextInt()
        }

        // Write an action using println()
        // To debug: System.err.println("Debug messages...");

        val useTarget = if (useEmptyTable) {
            (tables - filledTables)[0]   // the first empty table
        } else {
            equipmentLocations[nextEquipmentIndex]     // the next needed equipment
        }

        // if we're adjacent to it, it should work.
        val dx = playerX - useTarget.x
        val dy = playerY - useTarget.y
        if (abs(dx) <= 1 && abs(dy) <= 1) {
            if (useEmptyTable) {
                nextEquipmentIndex++
                if (nextEquipmentIndex >= equipmentLocations.size)
                    nextEquipmentIndex = 0
            }
            useEmptyTable = !useEmptyTable
        }

        println("USE ${useTarget.x} ${useTarget.y}")
    }
}