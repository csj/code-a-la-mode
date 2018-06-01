package com.codingame.game

import java.util.Properties

import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.GameManager
import com.codingame.gameengine.module.entities.GraphicEntityModule
import com.codingame.gameengine.module.entities.Rectangle
import com.google.inject.Inject

@Suppress("unused")  // injected by magic
class Referee : AbstractReferee() {
  @Inject
  private lateinit var gameManager: GameManager<Player>
  @Inject
  private lateinit var graphicEntityModule: GraphicEntityModule


  //  private var board = Board(boardWidth, boardWidth)
  private var board = buildBoard()


  override fun init(params: Properties): Properties {
    fun printRect(rect: Rectangle) {
      println("${rect.width} ${rect.height} ${rect.x} ${rect.y}")
    }

    gameManager.activePlayers[0].location = board["B3"]
    gameManager.activePlayers[1].location = board["b3"]


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
      player.sprite = graphicEntityModule.createRectangle().setHeight(cellWidth - 10).setWidth(cellWidth - 10).setFillColor(0x0000ff).setX(player.location.visualRect.x + 5).setY(player.location.visualRect.y + 5)
    }

//    for(i in 1..5) {
//      for (j in 1..5) {
//        graphicEntityModule.createRectangle().setWidth(100).setHeight(100).setX(100*i).setY(100*j).setLineWidth(1)
//      }
//    }


    // Params contains all the game parameters that has been to generate this game
    // For instance, it can be a seed number, the size of a grid/map, ...
    return params
  }

  override fun gameTurn(turn: Int) {
    fun sendGameState() {
      gameManager.activePlayers.forEach { player ->
//        player.sendInputLine("WORLD STATE")

        player.sendInputLine((board.cells.size * board.cells[0].size).toString())
        for(i in 0 until board.cells[0].size) {
          for(j in 0 until board.cells.size) {
            var cell = board.cells[j][i]
            val toks = listOf(cell.x, cell.y, if(cell.isTable) 1 else 0)
            player.sendInputLine(toks.joinToString(" "))
          }
        }
//        board.cells.forEach {
//          it.forEach {
//            val toks = listOf(it.x, it.y, if(it.isTable) 1 else 0)
//
//            player.sendInputLine(toks.joinToString(" "))
//          }
//        }
        player.execute()
      }
    }

    fun processPlayerActions() {
      gameManager.activePlayers.forEach { player ->
//        System.err.println("${player.nicknameToken} did: ${player.outputs[0]}")

        val line = player.outputs[0].trim()
        val toks = line.split(" ").iterator()
        val command = toks.next()

        when (command) {
          "MOVE" -> {
            var cellx = toks.next().toInt()
            var celly = toks.next().toInt()
            player.moveTo(board[cellx, celly])

            player.sprite.setX(board[cellx, celly].visualRect.x + 5).setY(board[cellx, celly].visualRect.y + 5)

            graphicEntityModule.commitEntityState(0.5, player.sprite)
          }
        }
      }
    }

    sendGameState()
    processPlayerActions()
  }
}
