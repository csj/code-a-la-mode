package com.codingame.game.view

import com.codingame.game.Player
import com.codingame.game.Referee
import com.codingame.game.ScoreBoard
import com.codingame.gameengine.module.entities.Text

class ScoresView(matchPlayers: List<Player>) {

  var currentRoundNumber = 0    /* 0, 1, 2 */

  val playerScoreViews = matchPlayers.mapIndexed { i, player ->
    player to PlayerScoreView(player).apply {
      group.x = 36
      group.y = 34 + (i * (1080 - 36 - 36 - 185))
    }
  }.toMap()

  fun update(scores: ScoreBoard) {
    var players = ArrayList(playerScoreViews.keys)

    when (currentRoundNumber) {
      0 -> {
        playerScoreViews[players[0]]!!.group.apply {
          y = 34
          isVisible = true
        }
        playerScoreViews[players[1]]!!.group.apply {
          y = 857
          isVisible = true
        }
        playerScoreViews[players[2]]!!.group.apply {
          isVisible = false
        }
      }
      1 -> {
        playerScoreViews[players[0]]!!.group.apply {
          y = 34
          isVisible = true
        }
        playerScoreViews[players[1]]!!.group.apply {
          isVisible = false
        }
        playerScoreViews[players[2]]!!.group.apply {
          y = 857
          isVisible = true
        }
      }
      2 -> {
        playerScoreViews[players[0]]!!.group.apply {
          isVisible = false
        }
        playerScoreViews[players[1]]!!.group.apply {
          y = 34
          isVisible = true
        }
        playerScoreViews[players[2]]!!.group.apply {
          y = 857
          isVisible = true
        }
      }
    }
    scores.forEach { player, entry ->
      playerScoreViews[player]!!.update(entry)

      playerScoreViews[player]!!.messageText.text = (player.message)
    }
  }

  inner class PlayerScoreView(player: Player) {
    val viewWidth = 343
    val viewHeight = 186

    private val playerAvatar = graphicEntityModule.createSprite().apply {
      image = player.avatarToken
      x = 22
      y = 72
      anchorX = 0.0
      anchorY = 0.0
      baseWidth = 93
      baseHeight = 93
      zIndex = 350
    }

    private val playerNameText = graphicEntityModule.createText(player.nicknameToken).apply {
      x = viewWidth / 2
      y = 20
      anchorX = 0.5
      anchorY = 0.0
      fontSize = 40
      fontWeight = Text.FontWeight.BOLDER
      fillColor = 0xFFFFFF
      strokeThickness = 3.0
      strokeColor = 0
      zIndex = 350

    }

    private val scoreTexts = // List(3) { i ->
        graphicEntityModule.createText("--").apply {
          fillColor = 0xffffff
          fontSize = 35
          x = 120
          y = 87
          anchorX = 0.0
          anchorY = 0.0
          zIndex = 350
        }
    //}

    val messageText =
        graphicEntityModule.createText("").apply {
          fillColor = 0xffffff
          fontSize = 25
          x = 120
          y = viewHeight - 20
          anchorX = 0.0
          anchorY = 1.0
          zIndex = 350
        }


    val backgroundBox = graphicEntityModule.createRectangle().apply {
      fillColor = player.colorToken
      width = viewWidth
      height = viewHeight
      zIndex = 200
      alpha = 0.5
    }!!

    val group = graphicEntityModule.createGroup().apply {
      add(scoreTexts)
      add(backgroundBox)
      add(playerAvatar)
      add(playerNameText)
      add(messageText)
    }

    fun update(entry: Referee.ScoreEntry) {
      if(playerNameText.text.length > 8 && !playerNameText.text.endsWith("..."))
        playerNameText.text = playerNameText.text.substring(0,8) + "..."

      if (currentRoundNumber >= 3) return
      entry.roundScores[currentRoundNumber]?.let { scoreTexts.text = it.toString() }
    }

  }
}