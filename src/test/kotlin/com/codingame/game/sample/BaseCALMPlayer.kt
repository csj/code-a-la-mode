package sample

import java.io.InputStream
import java.io.PrintStream
import java.util.*
import kotlin.coroutines.experimental.buildSequence

infix fun Int.has(flag: Int) = (this and flag) > 0
infix fun Int.doesntHave(flag: Int) = !(this has flag)
infix fun Int.with(flags: Int) = this or flags
infix fun Int.without(flags: Int) = this and flags.inv()
fun Int.toFlags(): Sequence<Int> {
  val flags = this
  return buildSequence {
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
      Table(scanner.nextInt(), scanner.nextInt(), scanner.nextInt(), -1, -1)
    }

  }

  protected fun readInputs(): GameState {
    var myPlayer: Player? = null
    var myFriend: Player? = null
    val enemyPlayers = mutableListOf<Player>()

    repeat(4) {
      val p = Player(scanner.nextInt(), scanner.nextInt(), scanner.nextInt(), scanner.nextInt())
      when (scanner.nextInt()) {
        0 -> myPlayer = p
        1 -> myFriend = p
        else -> enemyPlayers += p
      }
    }

    val rendering = List(height) { List(width) { "." }.toMutableList() }
    val tables = List(scanner.nextInt()) {
      Table(scanner.nextInt(), scanner.nextInt(), scanner.nextInt(),
          scanner.nextInt(), scanner.nextInt(), scanner.nextInt(), scanner.nextInt()).also {
        if (it.x >= 0) {
          val desc = when (it.equipment) {
            0 -> "I"
            else -> when (it.item) {
              1024 -> "D"
              else -> "X"
            }
          }
          rendering[it.y][it.x] = desc
        }
      }
    }

    val queue = List(scanner.nextInt()) {
      Customer(scanner.nextInt(), scanner.nextInt(), scanner.nextInt())
    }

//    repeat(height) { i ->
//      repeat(width) { j ->
//        System.err.print(rendering[i][j])
//      }
//      System.err.println()
//    }

    return GameState(myPlayer!!, myFriend!!, enemyPlayers, tables, queue)
  }
}

data class GameState(
    val myPlayer: Player,
    val myFriend: Player,
    val enemyPlayers: List<Player>,
    val tables: List<Table>,
    val queue: List<Customer>)


data class Table(
    val x: Int, val y: Int,
    val equipment: Int, val equipmentState: Int, val equipmentTimer: Int,
    val item: Int = -1, val itemState: Int = -1)
data class Player(val x: Int, val y: Int, val carrying: Int = -1, val carryingState: Int = -1)
data class Customer(val award: Int, val item: Int, val itemState: Int)