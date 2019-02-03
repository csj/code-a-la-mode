package sample

import java.io.InputStream
import java.io.PrintStream
import java.util.*

infix fun Int.has(flag: Int) = (this and flag) > 0
infix fun Int.doesntHave(flag: Int) = !(this has flag)
infix fun Int.with(flags: Int) = this or flags
infix fun Int.without(flags: Int) = this and flags.inv()
fun Int.toFlags(): Sequence<Int> {
  val flags = this
  return sequence {
    repeat(16) { i ->
      (1 shl i).let { if (flags has it) yield (it) }
    }
  }
}

@Suppress("unused")
abstract class BaseCALMPlayer(val stdin: InputStream, val stdout: PrintStream, val stderr: PrintStream) {
  val scanner = Scanner(stdin)
  val width: Int
  val height: Int
  var numTables: Int

  init {
    width = scanner.nextInt()
    height = scanner.nextInt()
    numTables = scanner.nextInt()
    List(numTables) {
      Table(scanner.nextInt(), scanner.nextInt(),
          Equipment.parse(scanner.next()))
    }

  }

  protected fun readInputs(): GameState {
    val myPlayer = Player(scanner.nextInt(), scanner.nextInt(), Item.parse(scanner.next()))
    val myFriend = Player(scanner.nextInt(), scanner.nextInt(), Item.parse(scanner.next()))

    val tables = List(numTables) {
      Table(scanner.nextInt(), scanner.nextInt(),
          Equipment.parse(scanner.next()),
          Item.parse(scanner.next()))
//          .also { System.err.println("Received table: $it") }
    }

    val queue = List(scanner.nextInt()) {
      Customer(Item.parse(scanner.next())!!, scanner.nextInt())
    }

    return GameState(myPlayer, myFriend, tables, queue)
  }
}

data class GameState(
    val myPlayer: Player,
    val myFriend: Player,
    val tables: List<Table>,
    val queue: List<Customer>)

data class Table(
    val x: Int, val y: Int,
    val equipment: Equipment? = null,
    val item: Item? = null)

data class Equipment(val description: String) {
  val toks = description.split("-")
  val equipmentType = toks[0]

  fun equipmentState() = toks[1]
  fun equipmentTimer() = toks[2].toInt()
  fun equipmentContents() = toks.drop(1)

  companion object {
    fun parse(description: String) =
        if (description == "NONE") null
        else Equipment(description)
  }

}

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