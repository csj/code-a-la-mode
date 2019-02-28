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
    BoardView.xRange = 590..1910  // = 11 * 120
    BoardView.yRange = 10..850 // = 7 * 120
    QueueView.xRange = 590..1910
    QueueView.yRange = 850..1080
  }
}