package com.codingame.game

import java.util.Properties

import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.GameManager
import com.codingame.gameengine.module.entities.GraphicEntityModule
import com.google.inject.Inject

@Suppress("unused")  // injected by magic
class Referee : AbstractReferee() {
  @Inject private lateinit var gameManager: GameManager<Player>
  @Inject private lateinit var graphicEntityModule: GraphicEntityModule

  override fun init(params: Properties): Properties {
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
      }
    }

    sendGameState()
    processPlayerActions()
  }
}
