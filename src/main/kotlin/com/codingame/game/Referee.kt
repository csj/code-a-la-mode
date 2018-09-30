package com.codingame.game

import com.codingame.game.model.*
import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.MultiplayerGameManager
import com.codingame.gameengine.module.entities.GraphicEntityModule
import com.google.inject.Inject

@Suppress("unused")  // injected by magic
class Referee : AbstractReferee() {
  @Inject
  private lateinit var gameManager: MultiplayerGameManager<Player>
  @Inject
  private lateinit var graphicEntityModule: GraphicEntityModule

  private lateinit var board: Board
  private lateinit var queue: CustomerQueue
  private lateinit var view: BoardView
  private lateinit var players: List<Player>

  private val edibleEncoding: Map<EdibleItem, Int> = mapOf(
      IceCreamBall(IceCreamFlavour.VANILLA) to Constants.FOOD.VANILLA_BALL.value,
      IceCreamBall(IceCreamFlavour.CHOCOLATE) to Constants.FOOD.CHOCOLATE_BALL.value,
      IceCreamBall(IceCreamFlavour.BUTTERSCOTCH) to Constants.FOOD.BUTTERSCOTCH_BALL.value,
      Strawberries to Constants.FOOD.STRAWBERRIES.value,
      Blueberries to Constants.FOOD.BLUEBERRIES.value,
      ChoppedBananas to Constants.FOOD.CHOPPED_BANANAS.value,
      PieSlice(PieFlavour.Strawberry) to Constants.FOOD.STRAWBERRY_PIE.value,
      PieSlice(PieFlavour.Blueberry) to Constants.FOOD.BLUEBERRY_PIE.value,
      Waffle to Constants.FOOD.WAFFLE.value
  )

  private fun Item?.describe(): List<Int> = when (this) {
    is Dish -> listOf(Constants.ITEM.DISH.ordinal, contents.map { edibleEncoding[it]!! }.sum())
    is Milkshake -> listOf(Constants.ITEM.MILKSHAKE.ordinal, contents.map { edibleEncoding[it]!! }.sum())
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


  override fun init() {
    players = gameManager.players

    fun teamMap() =
        when (players.size) {
          2 -> mapOf(0 to listOf(0), 1 to listOf(1))
          4 -> mapOf(1 to listOf(0, 3), 0 to listOf(1, 2))
          else -> throw Exception("Expected 2 or 4 players!")
        }

    fun awardTeamPoints(teamIndex: Int, points: Int) {
      println("Team $teamIndex gets $points points")
      teamMap()[teamIndex]!!
          .forEach {
            players[it].score += points

          }
    }

    val (b, q) = buildBoardAndQueue(::awardTeamPoints)
    board = b
    queue = q
    view = BoardView(graphicEntityModule, board, players)

    players[0].apply { isLeftTeam = true; location = board["b3"] }
    players[1].apply { isLeftTeam = false; location = board["B3"] }
    players[2].apply { isLeftTeam = false; location = board["B5"] }
    players[3].apply { isLeftTeam = true; location = board["b5"] }

    fun describeMap(player: Player) {
      // Describe the table cells on YOUR HALF of the board (width, height, location of equipment)

      player.sendInputLine("${board.width} ${board.height}")
      board.allCells
          .filter { it.x >= 0 }
          .filter { it.isTable }
          .also { player.sendInputLine(it.size.toString()) }
          .also {
            it.forEach { cell ->
              player.sendInputLine("${cell.x} ${cell.y} ${cell.equipment?.basicNumber() ?: -1}")
            }
          }
    }

    gameManager.activePlayers.forEach(::describeMap)
  }

  override fun gameTurn(turn: Int) {
    board.tick()
    queue.tick()

    fun sendGameState(player: Player) {
      val xMult = if (player.isLeftTeam) -1 else 1  // make sure to invert x for left team

      // 1. Describe all players, including self
      gameManager.activePlayers.forEach {

        val (item1, item2) = it.heldItem.describe()

        val toks = listOf(
            it.location.x * xMult,
            it.location.y,
            item1, item2,
            when {
              it == player -> 0    // self
              it.isLeftTeam == player.isLeftTeam -> 1   // friend
              else -> 2    // enemy
            }
        )
        player.sendInputLine(toks)
      }

      // 2. Describe all table cells
      board.allCells.filter { it.isTable }
          .also { player.sendInputLine(it.size) }
          .forEach {
            val toks = listOf(
                it.x * xMult,
                it.y,
                it.equipment?.basicNumber() ?: -1
            ) + (
                it.equipment?.extras() ?: listOf(-1,-1)
            ) +
                it.item.describe()
            player.sendInputLine(toks)
          }

      // 3. Describe customer queue
      queue
          .also { player.sendInputLine(it.size) }
          .also {
            it.forEach {
              val toks = listOf(it.award) + it.item.describe()
              player.sendInputLine(toks)
            }
          }

      player.execute()
    }

    fun processPlayerActions(player: Player) {
      val xMult = if (player.isLeftTeam) -1 else 1  // make sure to invert x for left team

      val line = player.outputs[0].trim()
      val toks = line.split(" ").iterator()
      val command = toks.next()

      if (command != "WAIT") {
        val cellx = toks.next().toInt() * xMult
        val celly = toks.next().toInt()
        val target = board[cellx, celly]

        when (command) {
          "MOVE" -> player.moveTo(target)
          "USE" -> player.use(target)
        }
      }

      player.itemSprite.update(player.heldItem)

      player.sprite
          .setX(board[player.location.x, player.location.y].view.group.x + 5)
          .setY(board[player.location.x, player.location.y].view.group.y + 5)

      graphicEntityModule.commitEntityState(0.5, player.sprite)
    }

    val thePlayers =
        when (gameManager.playerCount) {
          2 -> gameManager.activePlayers
          4 -> if (turn % 2 == 0) gameManager.activePlayers.take(2) else gameManager.activePlayers.takeLast(2)
          else -> throw Exception("Expected 2 or 4 players!")
        }

    thePlayers.forEach(::sendGameState)
    thePlayers.forEachIndexed { index, it ->
      try {
        processPlayerActions(it)
      } catch (ex: Exception) {
        System.err.println("${it.nicknameToken}: $ex")
      }
    }

    board.allCells.forEach { it.view.itemSpriteGroup.update(it.item) }
  }
}



