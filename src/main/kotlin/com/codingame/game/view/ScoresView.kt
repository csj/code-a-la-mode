package com.codingame.game.view

import com.codingame.game.Player
import com.codingame.game.Referee
import com.codingame.game.ScoreBoard
import com.codingame.gameengine.module.entities.Text

class ScoresView(matchPlayers: List<Player>) {

  val playerScoreViews = matchPlayers.mapIndexed { i, player ->
    player to PlayerScoreView(player).apply {
      group.x = 36
      group.y = 34 + (i * (1080 - 36 - 36 - 186))
    }
  }.toMap()

  fun update(scores: ScoreBoard) {
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

    private val scoreTexts = List(3) { i ->
      graphicEntityModule.createText("--").apply {
        fillColor = 0xffffff
        fontSize = 35
        x = ((i + 1.5) * viewWidth / 4).toInt()
        y = 72
        anchorX = 0.0
        anchorY = 0.0
        zIndex = 350
      }
    }

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

    val group = graphicEntityModule.createGroup(*scoreTexts.toTypedArray()).apply {
      add(backgroundBox)
      add(playerAvatar)
      add(playerNameText)
      add(messageText)
    }

    fun update(entry: Referee.ScoreEntry) {
      entry.roundScores.zip(scoreTexts).forEach { (score, text) ->
        if (score != null) text.text = score.toString()
      }
    }

  }
}