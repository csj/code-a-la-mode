package com.codingame.game

import com.codingame.game.model.*
import com.codingame.game.view.BoardView
import com.codingame.game.view.GameView
import com.codingame.game.view.QueueView
import com.codingame.game.view.ScoresView
import com.codingame.gameengine.core.AbstractPlayer
import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.MultiplayerGameManager
import com.codingame.gameengine.module.endscreen.EndScreenModule
import com.codingame.gameengine.module.entities.GraphicEntityModule
import com.google.inject.Inject
import tooltipModule.TooltipModule
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
  @Inject private lateinit var tooltipModule: TooltipModule
  @Inject private lateinit var endScreenModule :EndScreenModule

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
    com.codingame.game.view.tooltipModule = tooltipModule

    matchPlayers = gameManager.players.toMutableList()
    scoreBoard = mapOf(
        matchPlayers[0] to ScoreEntry(arrayOf(0, 0, null)),
        matchPlayers[1] to ScoreEntry(arrayOf(0, null, 0)),
        matchPlayers[2] to ScoreEntry(arrayOf(null, 0, 0))
    )
    gameManager.maxTurns = 606

    league = when (gameManager.leagueLevel) {
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
      player.describeCustomers(originalQueue)

      board.cells.transpose { Cell() }.forEach { cellRow ->
        player.sendInputLine(cellRow.map { it.describeChar(player.index == 1) }.joinToString(""))
      }
    }

  }

  private var currentRound: RoundReferee? = null
  private var roundNumber: Int = -1

  private fun nextRound(): RoundReferee {
    val roundPlayers = matchPlayers.take(2)
    view.boardView.removePlayer(matchPlayers[2])
    Collections.rotate(matchPlayers, 1)
    board.reset()
    queue = CustomerQueue()
    queue.getNewCustomers()
    queue.onFailure = { view.queueView.failed = true }

    roundPlayers[0].apply { location = board[board.spawnLocations[0]]; heldItem = null }
    roundPlayers[1].apply { location = board[board.spawnLocations[1]]; heldItem = null }
    view.boardView.board = board
    view.boardView.players = roundPlayers
    view.queueView.queue = queue

    roundNumber++
    return RoundReferee(roundPlayers, roundNumber)
  }

  override fun gameTurn(turn: Int) {
    if (currentRound == null || currentRound!!.isOver()) {
      currentRound = nextRound()
      if (roundNumber >= 3) {
        gameManager.endGame()
        return
      }
      currentRound!!.players.forEach {
        gameManager.addTooltip(it, "Round ${roundNumber + 1} starting!")
      }
      view.boardView.resetPlayers()
    } else {
      currentRound!!.gameTurn(turn)
    }

    view.scoresView.currentRoundNumber = roundNumber
    view.scoresView.update(scoreBoard)
    view.queueView.updateQueue()
    view.boardView.updateCells(board)
  }

  override fun onEnd() {
    scoreBoard.forEach { player, entry ->
      player.score = if (player.crashed) -1 else entry.total()
    }

    endScreenModule.titleRankingsSprite = "logo.png"
    endScreenModule.setScores(gameManager.players.map { it.score }.toIntArray())
  }

  inner class RoundReferee(val players: List<Player>, roundNumber: Int) {
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

    fun isOver(): Boolean = turn > 200

    fun gameTurn(matchTurn: Int) {
      turn++
      if (isOver()) return

      board.tick()
      val thePlayer = players[nextPlayerId]
      nextPlayerId = 1-nextPlayerId
      if (!thePlayer.isActive) {
        System.err.println("(Turn $turn) WARNING: ${thePlayer.nicknameToken} is dead; skipping")
        if (thePlayer.score == 0) thePlayer.score = -1000 + matchTurn
        thePlayer.crashed = true
        view.boardView.removePlayer(thePlayer)
        return gameTurn(matchTurn)
      }

      fun sendGameState(player: Player) {

        // 0. Describe turns remaining
        player.sendInputLine(200 - turn)

        // 1. Describe self, then partner
        players.sortedByDescending { it == player }.forEach {

          val toks = if (it.isActive) listOf(
              it.location.x,
              it.location.y,
              it.heldItem?.describe() ?: "NONE"
          ) else listOf(-1, -1, "NONE")

//          println("Sending player toks $toks to $player")
          player.sendInputLine(toks)
        }

        // 2. Describe all table cells with items
        board.allCells.filter { it.isTable && it.item != null }
            .also { player.sendInputLine(it.size) }
            .forEach {
              val toks = listOf(
                  it.x,
                  it.y,
                  it.item!!.describe()
              )
              player.sendInputLine(toks)
            }

        // 3. Describe oven
        board.oven().let {
          player.sendInputLine(it?.state?.toString() ?: "NONE 0")
        }

        // 4. Describe customer queue
        player.describeCustomers(queue.activeCustomers)
      }

      fun processPlayerActions(player: Player) {
        var line = if (!player.isActive) "WAIT" else player.outputs[0].trim()
        if(line.isEmpty()) line = "WAIT"

        val semicolon = line.indexOf(';').nullIf(-1)

        val fullCommand = if (semicolon != null) {
          player.message = line.substring(semicolon + 1).replace(";", "").take(9)
          line.substring(0, semicolon)
        } else {
          player.message = ""
          line
        }

        val toks = fullCommand.split(" ").iterator()
        val command = toks.next()
        var path: List<Cell>? = null

        if (command != "WAIT") {
          if(!toks.hasNext()) throw Exception("Invalid command: $fullCommand")
          val cellx = toks.next().toInt()

          if(!toks.hasNext()) throw Exception("Invalid command: $fullCommand")
          val celly = toks.next().toInt()

          val target = board[cellx, celly]

          path = when (command) {
            "MOVE" -> player.moveTo(target)
            "USE" -> player.use(target)
            else -> throw Exception("Invalid command: $fullCommand")
          }
        }

        view.boardView.updatePlayer(player, path)
      }

      //println("Current players: ${players.map { it.nicknameToken }}")
      queue.getNewCustomers()
      sendGameState(thePlayer)
      thePlayer.execute()

      try {
        processPlayerActions(thePlayer)
      } catch (ex: LogicException) {
        gameManager.addToGameSummary("${thePlayer.nicknameToken}: ${ex.message}")
      }
      catch(ex: AbstractPlayer.TimeoutException){
        disablePlayer(thePlayer, "timeout")
      }
      catch (ex: Exception) {
        disablePlayer(thePlayer, ex.message)
      }

      queue.updateRemainingCustomers()
    }

    fun disablePlayer(player: Player, message: String?){
      player.crashed = true
      gameManager.addToGameSummary("${player.nicknameToken}: ${message} (deactivating?)")
      player.deactivate("${player.nicknameToken}: ${message}")
      if (player.heldItem is Dish) {
        board.allCells.mapNotNull { (it.equipment as? DishWasher) }
                .first().let { it.addDish() }
      }
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


