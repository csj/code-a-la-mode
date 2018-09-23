package com.codingame.game

import com.codingame.game.model.*
import com.codingame.gameengine.module.entities.GraphicEntityModule
import com.codingame.gameengine.module.entities.Text

class BoardView(graphicEntityModule: GraphicEntityModule, val board: Board, players: List<Player>) {
  private var cellWidth: Int = 0
  private var scores = mutableMapOf<Int, Text>()

  init {
    val floorColor = 0xe0e0eb
    val tableColor = 0xb35900

    val worldWidth = 1920
    val worldHeight = 1080

    val cellSpacing = 5
    val yOffset = 100
    val xOffset = 200
    val gridHeight = worldHeight - yOffset
    val gridWidth = worldWidth - xOffset
    cellWidth = Math.min(gridHeight / board.height, gridWidth / board.width) - cellSpacing

    for (cellCol in board.cells) {

      for (cell in cellCol) {
        val x = (cell.x + board.width - 1) * (cellWidth + cellSpacing) + xOffset / 2
        val y = cell.y * (cellWidth + cellSpacing) + yOffset

        cell.view = CellView(cell).apply {
          background = graphicEntityModule
              .createRectangle()
              .setHeight(cellWidth)
              .setWidth(cellWidth)
              .setFillColor(if (cell.isTable) tableColor else floorColor)

          val equipment = cell.equipment
          content = graphicEntityModule.createSprite().apply {
            when (equipment) {
              is ChoppingBoard -> image = "board.png"
              is GeneralCrate -> image = "crate.png"
              is Blender -> image = "mixer.png"
              is Oven -> image = "oven.png"
              is Window -> image = "window.png"
              is WaffleIron -> image = "waffle-iron.png"
              is DishReturn -> image = "dishwasher.png"
            }
            baseHeight = cellWidth - 8
            baseWidth = cellWidth - 8
            anchorX = 0.5
            anchorY = 0.5
            setX(cellWidth / 2)
            setY(cellWidth / 2)
          }
          secondaryContent = graphicEntityModule.createSprite().apply {
            when (equipment) {
              is BananaCrate -> image = "banana.png"
              is BlueberryCrate -> image = "blueberries.png"
              is StrawberryCrate -> image = "strawberry.png"
              is IceCreamCrate -> image = "ice-cream.png"
              is PieCrustCrate -> image = "pie.png"
              else -> isVisible = false
            }
            baseHeight = cellWidth / 2
            baseWidth = cellWidth / 2
            anchorX = 0.5
            anchorY = 0.5
            setX(cellWidth / 2)
            setY(cellWidth / 2)
          }

          group = graphicEntityModule.createGroup(background, content, secondaryContent)
              .setX(x).setY(y)

        }

      }
    }

    for (player in players) {
      player.charaterSprite = graphicEntityModule.createRectangle()
          .setHeight(cellWidth - 10)
          .setWidth(cellWidth - 10)
          .setFillColor(player.colorToken)

      player.itemSprite = graphicEntityModule.createText("0").setAlpha(0.0)

      player.sprite = graphicEntityModule.createGroup(player.charaterSprite, player.itemSprite)
//          .setX(player.location.view.group.x + 5)
//          .setY(player.location.view.group.y + 5)
    }

    // TODO: Replace this
    scores[players[0].colorToken] = graphicEntityModule.createText("0").setX(0).setY(10).setFillColor(players[0].colorToken)
    scores[players[3].colorToken] = graphicEntityModule.createText("0").setX(200).setY(10).setFillColor(players[3].colorToken)
    scores[players[1].colorToken] = graphicEntityModule.createText("0").setX(400).setY(10).setFillColor(players[1].colorToken)
    scores[players[2].colorToken] = graphicEntityModule.createText("0").setX(600).setY(10).setFillColor(players[2].colorToken)
  }
}