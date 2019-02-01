package com.codingame.game

import com.codingame.game.model.*
import com.codingame.gameengine.core.AbstractPlayer
import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.MultiplayerGameManager
import com.codingame.gameengine.module.entities.*
import com.google.inject.Inject
import java.util.*

typealias ScoreBoard = Map<Player, Referee.ScoreEntry>

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

  class ScoreEntry(var roundScores: Array<Int?>) {
    fun total() = roundScores.filterNotNull().sum()
    override fun toString() = roundScores.let {
      "[${it[0]}, ${it[1]}, ${it[2]}]"
    }
  }
  private lateinit var scoreBoard: ScoreBoard

  override fun init() {
    rand = Random(gameManager.seed)

    matchPlayers = gameManager.players
    scoreBoard = mapOf(
        matchPlayers[0] to ScoreEntry(arrayOf(0, 0, null)),
        matchPlayers[1] to ScoreEntry(arrayOf(0, null, 0)),
        matchPlayers[2] to ScoreEntry(arrayOf(null, 0, 0))
    )
    gameManager.maxTurns = 600

    board = buildBoard()
    baseQueue = CustomerQueue()
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

  inner class RoundReferee(private val players: List<Player>, roundNumber: Int) {
    var score = 0

    private fun ScoreBoard.setScore(roundNumber: Int, score: Int) {
      forEach { _, entry ->
        if (entry.roundScores[roundNumber] != null) entry.roundScores[roundNumber] = score
      }
    }

    init {
      System.err.println("Setting callback to queue $queue")
      queue.onPointsAwarded = {
        score += it
        scoreBoard.setScore(roundNumber, score)
        System.err.println("Score is now $scoreBoard")
      }
      board.window.onDelivery = queue::delivery

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

      }

      fun processPlayerActions(player: Player) {
        val line = if (!player.isActive) "WAIT" else
        try {
          player.outputs[0].trim()
        } catch (ex: AbstractPlayer.TimeoutException) {
          player.deactivate("Player $player timed out!")
          "WAIT"
        }

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

      if (thePlayer.isActive) {
        sendGameState(thePlayer)
        thePlayer.execute()
      }

      try {
        processPlayerActions(thePlayer)
      } catch (ex: LogicException) {
        System.err.println("${thePlayer.nicknameToken}: $ex")
      } catch (ex: Exception) {
        System.err.println("${thePlayer.nicknameToken}: $ex (deactivating!)")
        thePlayer.deactivate("${thePlayer.nicknameToken}: $ex")
      }

      view.updateCells(board.allCells)
    }
  }

  private lateinit var currentRound: RoundReferee
  private var roundNumber: Int = 0

  private fun nextMatch() {
    val roundPlayers = matchPlayers.take(2)
    view.removePlayer(matchPlayers[2])
    Collections.rotate(matchPlayers, 1)
    board.reset()
    queue = baseQueue.copy()

    roundPlayers[0].apply { location = board["D3"]; heldItem = null }
    roundPlayers[1].apply { location = board["H3"]; heldItem = null }
    view.board = board
    view.queue = queue
    view.players = roundPlayers

    currentRound = RoundReferee(roundPlayers, roundNumber++)
  }

  override fun gameTurn(turn: Int) {
    println("Turn $turn")
    if (turn % 200 == 0)
      nextMatch()
          .also { println("Starting new match!") }
    currentRound.gameTurn(turn)
  }

  override fun onEnd() {
    scoreBoard.forEach { player, entry ->
      player.score = entry.total()
    }
  }
}


