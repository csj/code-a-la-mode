package com.codingame.game

import com.codingame.game.model.*
import com.codingame.game.view.BoardView
import com.codingame.game.view.GameView
import com.codingame.game.view.QueueView
import com.codingame.game.view.ScoresView
import com.codingame.gameengine.core.AbstractPlayer
import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.MultiplayerGameManager
import com.codingame.gameengine.module.entities.*
import com.google.inject.Inject
import java.util.*

typealias ScoreBoard = Map<Player, Referee.ScoreEntry>

enum class League {
  IceCreamBerries,
  StrawberriesChoppingBoard,
  Croissants,
  All
}

lateinit var league: League

@Suppress("unused")  // injected by magic
class Referee : AbstractReferee() {
  @Inject
  private lateinit var gameManager: MultiplayerGameManager<Player>
  @Inject
  private lateinit var graphicEntityModule: GraphicEntityModule

  private lateinit var board: Board
  private lateinit var queue: CustomerQueue
  val view = GameView()
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
    com.codingame.game.view.graphicEntityModule = graphicEntityModule

    matchPlayers = gameManager.players.toMutableList()
    scoreBoard = mapOf(
        matchPlayers[0] to ScoreEntry(arrayOf(0, 0, null)),
        matchPlayers[1] to ScoreEntry(arrayOf(0, null, 0)),
        matchPlayers[2] to ScoreEntry(arrayOf(null, 0, 0))
    )
    gameManager.maxTurns = 600

    league = when (4) {  // when (gameManager.leagueLevel) {
      1 -> League.IceCreamBerries
      2 -> League.StrawberriesChoppingBoard
      3 -> League.Croissants
      else -> League.All
    }

    board = buildBoard()
    view.boardView = BoardView(board, matchPlayers)

    view.queueView = QueueView()

    view.scoresView = ScoresView(matchPlayers)

    matchPlayers.forEach { player ->
      //      println("Sending board size to $player")
      //      player.sendInputLine("${board.width} ${board.height}")

      player.describeCustomers(originalQueue)

      board.allCells
          .filter { it.isTable }
          .also { player.sendInputLine(it.size.toString()) }
          .also {
            it.forEach { cell ->
              player.sendInputLine("${cell.x} ${cell.y} ${cell.equipment?.describe() ?: "NONE"}")
            }
          }
    }

    nextRound()

  }

  private lateinit var currentRound: RoundReferee
  private var roundNumber: Int = 0

  private fun nextRound() {
    val roundPlayers = matchPlayers.take(2)
    view.boardView.removePlayer(matchPlayers[2])
    Collections.rotate(matchPlayers, 1)
    board.reset()
    queue = CustomerQueue()
    queue.onFailure = { view.queueView.failed = true }

    roundPlayers[0].apply { location = board["D3"]; heldItem = null }
    roundPlayers[1].apply { location = board["H3"]; heldItem = null }
    view.boardView.board = board
    view.boardView.players = roundPlayers
    view.queueView.queue = queue

    currentRound = RoundReferee(roundPlayers, roundNumber++)
  }

  override fun gameTurn(turn: Int) {
    if (currentRound.isOver()) {
      if (roundNumber >= 3) gameManager.endGame()
      else {
        nextRound()
        view.boardView.resetPlayers()
      }
    } else {
      currentRound.gameTurn(turn)
    }

    view.scoresView.update(scoreBoard)
    view.queueView.updateQueue()
    view.boardView.updateCells(board.allCells)
  }

  override fun onEnd() {
    scoreBoard.forEach { player, entry ->
      player.score = entry.total()  // TODO not if they're dead ..
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
      queue.onPointsAwarded = {
        score += it
        scoreBoard.setScore(roundNumber, score)
      }
      board.window.onDelivery = queue::delivery

      players.forEach { player ->

        player.partner = players.find { it != player }!!
      }
    }

    var turn = 0
    var nextPlayerId = 0

    fun isOver(): Boolean = turn >= 200

    fun gameTurn(matchTurn: Int) {
      turn++
      if (isOver()) return

      board.tick()
      val thePlayer = players[nextPlayerId]
      nextPlayerId = 1-nextPlayerId
      if (!thePlayer.isActive) {
        System.err.println("(Turn $turn) WARNING: ${thePlayer.nicknameToken} is dead; skipping")
        if (thePlayer.score == 0) thePlayer.score = -1000 + matchTurn
        view.boardView.removePlayer(thePlayer)
        return gameTurn(matchTurn)
      }

      fun sendGameState(player: Player) {
        // 1. Describe self, then partner
        players.sortedByDescending { it == player }.forEach {
          val item = it.heldItem?.describe() ?: "NONE"

          val toks = if (it.isActive) listOf(
              it.location.x,
              it.location.y,
              item
          ) else listOf(-1, -1, "NONE")

//          println("Sending player toks $toks to $player")
          player.sendInputLine(toks)
        }

        // 2. Describe all table cells
        board.allCells.filter { it.isTable }
            .forEach {
              val toks = listOf(
                  it.x,
                  it.y,
                  it.equipment?.describe() ?: "NONE",
                  it.item?.describe() ?: "NONE"
              )
              player.sendInputLine(toks)
//              println("Sending table toks $toks to $player")
            }

        // 3. Describe customer queue
        player.describeCustomers(queue.activeCustomers)
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
        view.boardView.updatePlayer(player, useTarget)
      }

//      println("Current players: ${players.map { it.nicknameToken }}")

      queue.getNewCustomers()
      sendGameState(thePlayer)
      thePlayer.execute()

      try {
        processPlayerActions(thePlayer)
      } catch (ex: LogicException) {
        System.err.println("${thePlayer.nicknameToken}: $ex")
      } catch (ex: Exception) {
        System.err.println("${thePlayer.nicknameToken}: $ex (deactivating!)")
        ex.printStackTrace()
        thePlayer.deactivate("${thePlayer.nicknameToken}: $ex")
      }

      queue.updateRemainingCustomers()
    }
  }


}

private fun Player.describeCustomers(customers: List<Customer>) {
  customers
      .also { sendInputLine(it.size) }
      .also {
        it.forEach {
          val toks = listOf(it.dish.describe(), it.award.toString())
          sendInputLine(toks)
        }
      }
}


