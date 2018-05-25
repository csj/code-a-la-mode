package com.codingame.game

import java.util.Properties

import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.GameManager
import com.codingame.gameengine.module.entities.GraphicEntityModule
import com.codingame.gameengine.module.entities.Rectangle
import com.google.inject.Inject

@Suppress("unused")  // injected by magic
class Referee : AbstractReferee() {
  @Inject private lateinit var gameManager: GameManager<Player>
  @Inject private lateinit var graphicEntityModule: GraphicEntityModule

  private var board = Board(5,5)
  private lateinit var boardRects:Array<Array<Rectangle>>


  override fun init(params: Properties): Properties {
    gameManager.activePlayers[0].location = board["B3"]

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

        when(command) {
          "MOVE" -> {
            player.moveTo(board[toks.next()])
          }
        }
      }
    }

    sendGameState()
    processPlayerActions()
  }
}
