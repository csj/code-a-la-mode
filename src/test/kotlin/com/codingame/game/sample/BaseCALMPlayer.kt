package sample

import java.io.InputStream
import java.io.PrintStream
import java.util.*

@Suppress("unused")
abstract class BaseCALMPlayer(stdin: InputStream, val stdout: PrintStream, val stderr: PrintStream) {
  val scanner = Scanner(stdin)
  val width: Int
  val height: Int
  var numTables: Int

  init {
    width = scanner.nextInt()
    height = scanner.nextInt()
    numTables = scanner.nextInt()
    List(numTables) {
      Table(scanner.nextInt(), scanner.nextInt(), scanner.nextInt())
    }

  }

  protected fun readInputs(): GameState {
    var myPlayer: Player? = null
    var myFriend: Player? = null
    val enemyPlayers = mutableListOf<Player>()

    repeat(4) {
      val p = Player(scanner.nextInt(), scanner.nextInt(), scanner.nextInt())
      when (scanner.nextInt()) {
        0 -> myPlayer = p
        1 -> myFriend = p
        else -> enemyPlayers += p
      }
    }

    val rendering = List(height) { List(width) { "." }.toMutableList() }
    val tables = List(scanner.nextInt()) {
      Table(scanner.nextInt(), scanner.nextInt(), scanner.nextInt(), scanner.nextInt()).also {
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
      Customer(scanner.nextInt(), scanner.nextInt())
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


data class Table(val x: Int, val y: Int, val equipment: Int, val item: Int = -1)
data class Player(val x: Int, val y: Int, val carrying: Int = -1)
data class Customer(val award: Int, val item: Int)