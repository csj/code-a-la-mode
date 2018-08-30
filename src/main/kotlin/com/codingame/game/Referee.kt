package com.codingame.game

import com.codingame.game.Constants
import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.MultiplayerGameManager
import com.codingame.gameengine.module.entities.GraphicEntityModule
import com.codingame.gameengine.module.entities.Rectangle
import com.google.inject.Inject

@Suppress("unused")  // injected by magic
class Referee : AbstractReferee() {
  @Inject
  private lateinit var gameManager: MultiplayerGameManager<Player>
  @Inject
  private lateinit var graphicEntityModule: GraphicEntityModule

  private lateinit var board: Board
  private lateinit var queue: CustomerQueue

  override fun init() {
    fun teamMap() =
        when(gameManager.activePlayers.size) {
          2 -> mapOf(0 to listOf(0), 1 to listOf(1))
          4 -> mapOf(0 to listOf(0, 3), 1 to listOf(2, 3))
          else -> throw Exception("Expected 2 or 4 players!")
        }

    fun awardTeamPoints(teamIndex: Int, points: Int) {
      println("Team $teamIndex gets $points points")
      teamMap()[teamIndex]!!
          .forEach { gameManager.players[it].score += points }
    }

    val (b,q) = buildBoardAndQueue(::awardTeamPoints)
    board = b
    queue = q

    fun printRect(rect: Rectangle) {
      println("${rect.width} ${rect.height} ${rect.x} ${rect.y}")
    }

    gameManager.activePlayers[0].apply { isLeftTeam = true; location = board["b3"] }
    gameManager.activePlayers[1].apply { isLeftTeam = false; location = board["B3"] }
    gameManager.activePlayers[2].apply { isLeftTeam = false; location = board["B5"] }
    gameManager.activePlayers[3].apply { isLeftTeam = true; location = board["b5"] }

    var fill = 0xeeeeee
    var tableFill = 0x8B4513

    val worldWidth = 1920
    val worldHeight = 1080

    val cellSpacing = 5
    val yOffset = 0
    val xOffset = 100
    val gridHeight = worldHeight - yOffset
    val gridWidth = worldWidth - xOffset
    val cellWidth = Math.min(gridHeight / board.height, gridWidth / board.width) - cellSpacing

    for (cellCol in board.cells) {
      val indexX = cellCol[0].x + board.width - 1

      for (cell in cellCol) {
        val x = (cell.x + board.width - 1) * (cellWidth + cellSpacing) + xOffset / 2

        val y = cell.y * (cellWidth + cellSpacing) + yOffset / 2
//        println("$x-$y")
//        println("$cellWidth $x $y")
        cell.visualRect = graphicEntityModule.createRectangle().setHeight(cellWidth).setWidth(cellWidth).setX(x).setY(y).setFillColor(if (!cell.isTable) fill else tableFill)
      }
    }

    for (player in gameManager.activePlayers) {
      player.sprite = graphicEntityModule.createRectangle().setHeight(cellWidth - 10).setWidth(cellWidth - 10).setFillColor(player.colorToken).setX(player.location.visualRect.x + 5).setY(player.location.visualRect.y + 5)
    }

    fun describeMap(player: Player) {
      // Describe the table cells on YOUR HALF of the board (width, height, location of equipment)
      fun equipmentTypeMap(equipment: Equipment?) = when (equipment) {
        null -> -1
        is Window -> Constants.WINDOW
        is DishReturn -> Constants.DISH_RETURN
        IceCreamCrate(IceCreamFlavour.VANILLA) -> Constants.VANILLA_CRATE
        IceCreamCrate(IceCreamFlavour.CHOCOLATE) -> Constants.CHOCOLATE_CRATE
        IceCreamCrate(IceCreamFlavour.BUTTERSCOTCH) -> Constants.BUTTERSCOTCH_CRATE
        else -> throw Exception("Uncoded equipment: $equipment")
      }

      player.sendInputLine("${board.width} ${board.height}")
      board.allCells
          .filter { it.x >= 0 }
          .filter { it.isTable }
          .also { player.sendInputLine(it.size.toString()) }
          .also { it.forEach { cell ->
            player.sendInputLine("${cell.x} ${cell.y} ${equipmentTypeMap(cell.equipment)}")
          }}
    }

    gameManager.activePlayers.forEach(::describeMap)
  }

  val edibleEncoding: Map<EdibleItem, Int> = mapOf(
      IceCreamBall(IceCreamFlavour.VANILLA) to Constants.VANILLA_BALL,
      IceCreamBall(IceCreamFlavour.CHOCOLATE) to Constants.CHOCOLATE_BALL,
      IceCreamBall(IceCreamFlavour.BUTTERSCOTCH) to Constants.BUTTERSCOTCH_BALL,
      Strawberries to Constants.STRAWBERRIES,
      Blueberries to Constants.BLUEBERRIES,
      ChoppedBananas to Constants.CHOPPED_BANANAS,
      PieSlice(PieFlavour.Strawberry) to Constants.STRAWBERRY_PIE,
      PieSlice(PieFlavour.Blueberry) to Constants.BLUEBERRY_PIE,
      Waffle to Constants.WAFFLE
  )

  override fun gameTurn(turn: Int) {
    board.tick()
    queue.tick()

    fun describeItem(item: Item?): Int = when(item) {
      is Dish -> Constants.DISH + item.contents.map(::describeItem).sum()
      is Milkshake -> Constants.MILKSHAKE + item.contents.map(::describeItem).sum()
      in edibleEncoding.keys -> edibleEncoding[item]!!
      else -> -1
    }

    fun describeEquipment(equipment: Equipment?): Int = when(equipment) {
      is Window -> Constants.WINDOW
      is IceCreamCrate -> Constants.VANILLA_CRATE
      else -> -1
    }

    fun sendGameState(player: Player) {
      val xMult = if (player.isLeftTeam) -1 else 1  // make sure to invert x for left team

      // 1. Describe all players, including self
      gameManager.activePlayers.forEach {

        val toks = listOf(
            it.location.x * xMult,
            it.location.y,
            describeItem(it.heldItem),
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
            describeEquipment(it.equipment),
            describeItem(it.item)
        )
        player.sendInputLine(toks)
      }

      // 3. Describe customer queue
      queue
          .also { player.sendInputLine(it.size) }
          .also { it.forEach {
            val toks = listOf(it.award) + describeItem(it.item)
            player.sendInputLine(toks)
          }}

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
          "TAKE" -> player.take(target)
          "DROP" -> player.drop(target)
        }
      }

      player.sprite
          .setX(board[player.location.x, player.location.y].visualRect.x + 5)
          .setY(board[player.location.x, player.location.y].visualRect.y + 5)

      graphicEntityModule.commitEntityState(0.5, player.sprite)

    }

    val thePlayers =
      when (gameManager.playerCount) {
        2 -> gameManager.activePlayers
        4 -> if (turn % 2 == 0) gameManager.activePlayers.take(2) else gameManager.activePlayers.takeLast(2)
        else -> throw Exception("Expected 2 or 4 players!")
      }

    thePlayers.forEach(::sendGameState)
    thePlayers.forEach {
      try {
        processPlayerActions(it)
      } catch (ex: Exception) {
        System.err.println("${it.nicknameToken}: $ex")
      }
    }
  }
}


