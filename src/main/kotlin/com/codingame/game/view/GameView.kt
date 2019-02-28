package com.codingame.game.view

import com.codingame.gameengine.module.entities.GraphicEntityModule
import tooltipModule.TooltipModule

lateinit var graphicEntityModule: GraphicEntityModule
lateinit var tooltipModule: TooltipModule

class GameView {
  lateinit var boardView: BoardView
  lateinit var queueView: QueueView
  lateinit var scoresView: ScoresView

  init {
    BoardView.xRange = 430..1920  // = 11 * 120
    BoardView.yRange = 190..1080 // = 7 * 120
    QueueView.xRange = 10..410
    QueueView.yRange = 240..840
  }
}