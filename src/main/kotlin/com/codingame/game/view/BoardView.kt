package com.codingame.game.view

import com.codingame.game.Player
import com.codingame.game.model.*
import com.codingame.gameengine.module.entities.*


class BoardView(baseBoard: Board, matchPlayers: List<Player>) {
  companion object {
    lateinit var yRange: IntRange
    lateinit var xRange: IntRange
  }

  var cellWidth: Int = 0
  val cellSpacing = 5

  lateinit var board: Board
  lateinit var players: List<Player>

  private var cellViews: MutableList<CellView> = mutableListOf()

  init {
    val floorColor = 0xe0e0eb
    val tableColor = 0x756b68
//    val floorColor = 0xe0e0eb
//    val tableColor = 0xb35900


    val gridHeight = yRange.last - yRange.first
    val gridWidth = xRange.last - xRange.first
    cellWidth = Math.min(gridHeight / baseBoard.height, gridWidth / baseBoard.width) - cellSpacing

    for (cellCol in baseBoard.cells) {

      for (cell in cellCol) {
        val x = cell.x * (cellWidth + cellSpacing) + xRange.first
        val y = cell.y * (cellWidth + cellSpacing) + yRange.first

        cellViews.add(CellView(cell).apply {
          background = graphicEntityModule
              .createRectangle()
              .setHeight(cellWidth)
              .setWidth(cellWidth)
              .setFillColor(if (cell.isTable) tableColor else floorColor)

          val equipment = cell.equipment
          content = graphicEntityModule.createSprite().apply {
            baseHeight = cellWidth - 8
            baseWidth = cellWidth - 8
            anchorX = 0.5
            anchorY = 0.5
            setX(cellWidth / 2)
            setY(cellWidth / 2)
            when (equipment) {
              is ChoppingBoard -> image = "board.png"
              is GeneralCrate -> image = "crate.png"
              is Oven -> image = "oven.png"
              is Window -> image = "window.png"
              is DishReturn -> image = "dishwasher.png"
              is Jarbage -> image = "trash.png"
            }
          }
          secondaryContent = graphicEntityModule.createSprite().apply {
            when (equipment) {
              is StrawberryCrate -> image = "strawberry.png"
              is BlueberryCrate -> image = "blueberries.png"
              is IceCreamCrate -> image = "ice-cream.png"
              is DoughCrate -> image = "dough.png"
              else -> isVisible = false
            }
            baseHeight = cellWidth / 2
            baseWidth = cellWidth / 2
            anchorX = 0.5
            anchorY = 0.5
            setX(cellWidth / 2)
            setY(cellWidth / 2)
          }

          text = graphicEntityModule.createText(cell.character?.toString() ?: "").apply {
            setX(cellWidth / 2)
            setY(cellWidth / 2)
            anchorX = 0.5
            anchorY = 0.5
            fontSize = 60
            fillColor = 0
          }

          itemSpriteGroup = ItemSpriteGroup()

          group = graphicEntityModule.createGroup(background, content, secondaryContent, text, itemSpriteGroup.group)
              .setX(x).setY(y)

        })
      }
    }

    for (player in matchPlayers) {
      player.characterSprite = graphicEntityModule.createSprite().apply {
        image = "chef.png"
        baseHeight = cellWidth
        baseWidth = cellWidth
        center()
        tint = player.colorToken
      }

      player.itemSprite = ItemSpriteGroup()

      player.sprite = graphicEntityModule.createGroup(player.characterSprite, player.itemSprite.group)
//          .setX(player.location.view.group.x + 5)
//          .setY(player.location.view.group.y + 5)
    }

    // TODO: Replace this
//    graphicEntityModule.createRectangle().setX(0).setY(10).setFillColor(players[0].colorToken).setHeight(15).setWidth(15)
//    graphicEntityModule.createRectangle().setX(0).setY(30).setFillColor(players[3].colorToken).setHeight(15).setWidth(15)
//    scores[0] = graphicEntityModule.createText("0").setX(20).setY(20).setFillColor(0xffffff)
//
//    graphicEntityModule.createRectangle().setX(400).setY(10).setFillColor(players[1].colorToken).setHeight(15).setWidth(15)
//    graphicEntityModule.createRectangle().setX(400).setY(30).setFillColor(players[2].colorToken).setHeight(15).setWidth(15)
//    scores[1] = graphicEntityModule.createText("0").setX(420).setY(20).setFillColor(0xffffff)
  }

  fun updateCells(boardCells: List<Cell>) {
    boardCells.zip(cellViews).forEach { (cell, view) ->
      view.itemSpriteGroup.update(cell.item) }
  }

  fun <T : Entity<*>?> Entity<T>.setLocation(cell: Cell, hardTransition: Boolean = false) {
    val newX = cell.x * (cellWidth + cellSpacing) + xRange.first + 5
    val newY = cell.y * (cellWidth + cellSpacing) + yRange.first + 5

    if (hardTransition) {
      setX(newX, Curve.NONE)
      setY(newY, Curve.NONE)
    } else {
      x = newX
      y = newY
    }
  }

  fun resetPlayers() {
    players.forEach { updatePlayer(it, null, true) }
  }

  fun updatePlayer(player: Player, useTarget: Cell?, hardTransition: Boolean = false) {
    player.characterSprite.isVisible = true
    player.itemSprite.isVisible = true

    player.itemSprite.update(player.heldItem)

    if (useTarget == null) {
      player.sprite.setLocation(board[player.location.x, player.location.y], hardTransition)
    } else {
      player.sprite.setLocation(useTarget)
      graphicEntityModule.commitEntityState(0.3, player.sprite)
      player.sprite.setLocation(board[player.location.x, player.location.y])
      graphicEntityModule.commitEntityState(0.6, player.sprite)
    }

    graphicEntityModule.commitEntityState(0.5, player.sprite)

  }

  fun removePlayer(player: Player) {
    player.characterSprite.isVisible = false
    player.itemSprite.isVisible = false
  }

  inner class ItemSpriteGroup(width: Int = cellWidth) {
    val mainSprite = graphicEntityModule.createSprite().apply {
      center()
      baseHeight = width + 4
      baseWidth = width + 4
      zIndex = 50
      isVisible = false
    }

    val subSprites = List(4) { i ->
      graphicEntityModule.createSprite().apply {
        center()
        baseHeight = width / 2
        baseWidth = width / 2
        zIndex = 50 + i
        x = ((i - 1.5) * 8 + width / 2).toInt()
        y = ((i - 1.5) * 8 + width / 2).toInt()
        isVisible = false
      }
    }

    val group = graphicEntityModule.createGroup(*(subSprites + mainSprite).toTypedArray())
    var isVisible: Boolean = true
      set(value) {
        mainSprite.isVisible = value
        subSprites.forEach { it.isVisible = value }
      }

    fun update(item: Item?) {
      subSprites.forEach { it.isVisible = false }
      mainSprite.apply {
        isVisible = true

        when(item) {
          is IceCream -> image = "ice-cream.png"
          is Blueberries -> image = "blueberries.png"
          is Dough -> image = "dough.png"
          is Strawberries -> image = "strawberry.png"
          is ChoppedStrawberries -> image = "strawberries-cut.png"
          is Croissant -> image = "croissant.png"
          is Tart -> image = "tart.png"
          is BurntFood -> image = "coal.png"
          is Dish -> {
            image = "dish.png"
            item.contents.zip(subSprites).forEach { (edible, subSprite) ->
              subSprite.apply {
                isVisible = true
                when (edible) {
                  is IceCream -> image = "ice-cream.png"
                  is Blueberries -> image = "blueberries.png"
                  is ChoppedStrawberries -> image = "strawberries-cut.png"
                  is Croissant -> image = "croissant.png"
                  is Tart -> image = "tart.png"
                }
              }
            }
          }
          is Shell -> {
            image = "empty-tart.png"
            if (item.hasBlueberry) subSprites[1].apply {
              isVisible = true
              image = "blueberries.png"
            }
          }

          else -> mainSprite.isVisible = false
        }
      }
    }
  }

  private fun Sprite.center() {
    anchorY = 0.5
    anchorX = 0.5
    x = cellWidth / 2
    y = cellWidth / 2
  }




}

