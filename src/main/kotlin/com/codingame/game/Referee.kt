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

  private val boardWidth = 5
  private var board = Board(boardWidth, boardWidth)
  private var boardRects: ArrayList<ArrayList<Rectangle>> = ArrayList(boardWidth)


  override fun init(params: Properties): Properties {
    fun printRect(rect: Rectangle) {
      println("${rect.width} ${rect.height} ${rect.x} ${rect.y}")
    }

    gameManager.activePlayers[0].location = board["B3"]


    var fill = 0xffffff

    val worldWidth = 1920
    val worldHeight = 1080


    val cellSpacing = 5
    val yOffset = 0
    val xOffset = 100
    val gridHeight = worldHeight - yOffset
    val gridWidth = worldWidth - xOffset
    val cellWidth = Math.min(gridHeight / boardWidth, gridWidth / (boardWidth * 2 - 1)) - cellSpacing

    for (cellCol in board.cells) {
      val indexX = cellCol[0].x + boardWidth - 1
      boardRects.add(ArrayList(boardWidth))

      for (cell in cellCol) {
        val x = (cell.x + boardWidth - 1) * (cellWidth + cellSpacing) + xOffset/2

        val y = cell.y * (cellWidth + cellSpacing) + yOffset/2
//        println("$x-$y")
//        println("$cellWidth $x $y")
        cell.visualRect = graphicEntityModule.createRectangle().setHeight(cellWidth).setWidth(cellWidth).setX(x).setY(y).setFillColor(fill)
      }
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
        player.sendInputLine("WORLD STATE")
        player.execute()
      }
    }

    fun processPlayerActions() {
      gameManager.activePlayers.forEach { player ->
        System.err.println("${player.nicknameToken} did: ${player.outputs[0]}")

        val line = player.outputs[0].trim()
        val toks = line.split(" ").iterator()
        val command = toks.next()

        when (command) {
          "MOVE" -> {
            var oldCell = player.location
            var cell = toks.next()
            player.moveTo(board[cell])

            oldCell.visualRect.setFillColor(0xffffff)
            board[cell].visualRect.setFillColor(0x000000)

            graphicEntityModule.commitEntityState(0.5, board[cell].visualRect, oldCell.visualRect)
          }
        }
      }
    }

    sendGameState()
    processPlayerActions()
  }
}
