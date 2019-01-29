package com.codingame.game

import com.codingame.game.model.*
import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.MultiplayerGameManager
import com.codingame.gameengine.module.entities.*
import com.google.inject.Inject
import java.util.*

@Suppress("unused")  // injected by magic
class Referee : AbstractReferee() {
  @Inject
  private lateinit var gameManager: MultiplayerGameManager<Player>
  @Inject
  private lateinit var graphicEntityModule: GraphicEntityModule

  private lateinit var board: Board
  private lateinit var queue: CustomerQueue
  private lateinit var baseQueue: CustomerQueue
  private lateinit var view: BoardView
  private lateinit var matchPlayers: MutableList<Player>

  override fun init() {
    matchPlayers = gameManager.players
    gameManager.maxTurns = 600

    fun awardTeamPoints(points: Int) {
//      println("$points points")
//      teamMap()[teamIndex]!!
//          .forEach {
//            players[it].score += points
//          }
//
//      view.scores[teamIndex]!!.text = players[teamMap()[teamIndex]!!.first()].score.toString()
    }

    val (b,q) = buildBoardAndQueue(::awardTeamPoints)
    board = b
    baseQueue = q
    view = BoardView(graphicEntityModule, board, matchPlayers)

    matchPlayers.forEach { player ->
      //      println("Sending board size to $player")
      player.sendInputLine("${board.width} ${board.height}")
      board.allCells
          .filter { it.isTable }
          .also { player.sendInputLine(it.size.toString()) }
          .also {
            it.forEach { cell ->
              player.sendInputLine("${cell.x} ${cell.y} ${cell.equipment?.basicNumber() ?: -1}")
            }
          }

    }
  }

  inner class RoundReferee(private val players: List<Player>) {
    init {
      players.forEach { player ->
        player.partner = players.find { it != player }!!
      }
    }

    fun gameTurn(turn: Int) {

      board.tick()
      queue.tick()

      fun Item?.describe(): List<Int> = when (this) {
        is Dish -> listOf(Constants.ITEM.DISH.ordinal, contents.map { edibleEncoding[it]!! }.sum())
        is Banana -> listOf(Constants.ITEM.BANANA.ordinal, 0)
        is RawPie -> listOf(Constants.ITEM.RAW_PIE.ordinal, when (this) {
          RawPie(null) -> -1
          else -> (if (pieFlavour == PieFlavour.Strawberry) 10 else 20) + this.fruitsMissing
        })
        is Pie -> listOf(Constants.ITEM.WHOLE_PIE.ordinal, if (this.pieFlavour == PieFlavour.Strawberry) 0 else 1)
        is BurntPie -> listOf(Constants.ITEM.BURNT_PIE.ordinal, 0)
        is BurntWaffle -> listOf(Constants.ITEM.BURNT_WAFFLE.ordinal, 0)
        in edibleEncoding.keys -> listOf(Constants.ITEM.FOOD.ordinal, edibleEncoding[this]!!)
        else -> listOf(-1,0)
      }

      fun sendGameState(player: Player) {
        // 1. Describe self, then partner
        players.sortedByDescending { it == player }.forEach {
          val (item1, item2) = it.heldItem.describe()

          val toks = listOf(
              it.location.x,
              it.location.y,
              item1, item2
          )
//          println("Sending player toks $toks to $player")

          player.sendInputLine(toks)
        }

        // 2. Describe all table cells
        board.allCells.filter { it.isTable }
            .forEach {
              val toks = listOf(
                  it.x,
                  it.y,
                  it.equipment?.basicNumber() ?: -1
              ) + (
                  it.equipment?.extras() ?: listOf(-1,-1)
                  ) +
                  it.item.describe()
              player.sendInputLine(toks)
//              println("Sending table toks $toks to $player")
            }

        // 3. Describe customer queue
        queue
            .also { player.sendInputLine(it.size) }
            .also {
              it.forEach {
                val toks = listOf(it.award) + it.item.describe()[1]
                player.sendInputLine(toks)
              }
            }

        player.execute()
      }

      fun processPlayerActions(player: Player) {
        val line = player.outputs[0].trim()
        val toks = line.split(" ").iterator()
        val command = toks.next()
        var useTarget: Cell? = null

        if (command != "WAIT") {
          val cellx = toks.next().toInt()
          val celly = toks.next().toInt()
          val target = board[cellx, celly]

          when (command) {
            "MOVE" -> player.moveTo(target)
            "USE" -> {
              if (player.use(target))
                useTarget = target
            }
          }
        }

        view.updateQueue()
        view.updatePlayer(player, useTarget)
      }

//      println("Current players: ${players.map { it.nicknameToken }}")
      val thePlayer = players[turn % 2]

      sendGameState(thePlayer)
      try {
        processPlayerActions(thePlayer)
      } catch (ex: Exception) {
        System.err.println("${thePlayer.nicknameToken}: $ex")
      }

      view.updateCells(board.allCells)
    }
  }

  private lateinit var currentRound: RoundReferee


  private fun nextMatch() {
    val roundPlayers = matchPlayers.take(2)
    view.removePlayer(matchPlayers[2])
    Collections.rotate(matchPlayers, 1)
    board.reset()
    queue = baseQueue.copy()

    roundPlayers[0].apply { location = board["D6"]; heldItem = null }
    roundPlayers[1].apply { location = board["F3"]; heldItem = null }
    view.board = board
    view.queue = queue
    view.players = roundPlayers

    currentRound = RoundReferee(roundPlayers)
  }

  override fun gameTurn(turn: Int) {
//    println("Turn $turn")
    if (turn % 200 == 0)
      nextMatch()
          //.also { println("Starting new match!") }
    currentRound.gameTurn(turn)
  }
}



