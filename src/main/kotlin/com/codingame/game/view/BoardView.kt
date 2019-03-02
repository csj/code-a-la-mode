package com.codingame.game.view

import com.codingame.game.Player
import com.codingame.game.model.*
import com.codingame.gameengine.module.entities.*
import tooltipModule.TooltipModule


class BoardView(baseBoard: Board, matchPlayers: List<Player>) {
  companion object {
    lateinit var yRange: IntRange
    lateinit var xRange: IntRange
  }

  var cellWidth: Int = 0
  var cellHeight: Int = 0
  val cellSpacing = 5

  lateinit var board: Board
  lateinit var players: List<Player>
  var ovenSprite: Sprite? = null
  var ovenFireSprite: Sprite? = null

  private var cellViews: MutableList<CellView> = mutableListOf()

  init {
    fun setTooltip(tooltipModule: TooltipModule, cell: Cell, group: Group){
      val toolTip = cell.equipment?.let { "Equipment:${it.tooltipString}" } ?: ""
      tooltipModule.registerEntity(group, toolTip)
    }

    val floorColor = 0xe0e0eb
    val tableColor = 0x756b68
//    val floorColor = 0xe0e0eb
//    val tableColor = 0xb35900

    graphicEntityModule.createSprite().apply {
      image = "background.jpg"
      baseWidth = 1920
      baseHeight = 1080
      anchorX = 0.0
      anchorY = 0.0
      x = 0
      y = 0
    }

    val gridHeight = yRange.last - yRange.first
    val gridWidth = xRange.last - xRange.first

    //cellWidth = Math.min(gridHeight / baseBoard.height, gridWidth / baseBoard.width) - cellSpacing

    var nextX = xRange.first
    var nextY = yRange.first

    baseBoard.cells.forEachIndexed { colIndex, cellCol ->
      val isEdgeCol = colIndex == 0 || colIndex == 10
      nextY = yRange.first

      cellWidth = if (isEdgeCol) 140 else 132

      cellCol.forEachIndexed { rowIndex, cell ->
        val isFirstRow = rowIndex == 0
        cellHeight = if (isFirstRow) 206 else 110
        val x = nextX
        val y = nextY

//        println("${x} - ${y}")

        nextY += cellHeight

        cellViews.add(CellView(cell).apply {

          background = graphicEntityModule
              .createRectangle()
              .setHeight(cellHeight)
              .setWidth(cellWidth)
              .setLineColor(0xffffff)
              .setFillAlpha(0.0)
              .setLineWidth(1)

          println("${cell.x} ${cell.y}")

          val equipment = cell.equipment
          content = graphicEntityModule.createSprite().apply {
            baseHeight = (110 * 0.75).toInt()
            baseWidth = (132 * 0.75).toInt()
            anchorX = 0.5
            anchorY = if(cell.x == 0 || cell.x == 10) 1.25 else 0.0
            setX(cellWidth/2)
            setY(0)
            when (equipment) {
              is ChoppingBoard -> image = "board.png"
              is GeneralCrate -> image = "bowl.png"
              is Oven -> {
                image = if (cell.y == 0) "oven_top.png" else if (cell.x == 0) "oven_left.png" else if (cell.x == 10) "oven_right.png" else "oven.png"
                baseHeight = if (cell.x == 0 || cell.x == 10) 215 else 206
                baseWidth = if (cell.x == 0 || cell.x == 10) 140 else 132
                anchorX = if(cell.x == 10) 1.0 else 0.0
                anchorY = 1.0
                if(cell.x == 0) setX(8) else if(cell.x == 10) setX(cellWidth - 8) else setX(0)
                setY(cellHeight)
              }
              is Window -> image = "window.png"
              is DishWasher -> image = "dishwasher.png"
            }
          }

          if(equipment is Oven) {
            ovenSprite = content
            secondaryContent = graphicEntityModule.createSprite().apply {
              baseHeight = cellWidth - 8
              baseWidth = cellWidth - 8
              anchorX = 0.5
              anchorY = 0.5
              setX(cellWidth / 2)
              setY(cellWidth / 2)
              image = "fire.png"
              zIndex = 4000
              isVisible = false
            }
            ovenFireSprite = secondaryContent
            tooltipModule.registerEntity(ovenSprite)
          } else {
            secondaryContent = graphicEntityModule.createSprite().apply {
              when (equipment) {
                is StrawberryCrate -> image = "strawberry.png"
                is BlueberryCrate -> image = "blueberries.png"
                is IceCreamCrate -> image = "ice-cream.png"
                is DoughCrate -> image = "dough.png"
                else -> isVisible = false
              }
            baseHeight = 132 / 2
            baseWidth = 110 / 2
              anchorX = 0.5
            anchorY = if(cell.x == 0 || cell.x == 10) 1.5 else 0.0
            alpha = 1.0
              setX(cellWidth / 2)
            setY(0)
            }
          }

          text = graphicEntityModule.createText(cell.character?.toString() ?: "").apply {
            setX(cellWidth / 2)
            setY(cellHeight / 2)
            anchorX = 0.5
            anchorY = 0.5
            fontSize = 60
            fillColor = 0
            alpha = 0.0
          }

          itemSpriteGroup = ItemSpriteGroup(cellWidth)

          if(cell.x == 0 || cell.x == 10) {
            itemSpriteGroup.mainSprite.apply {
              anchorY = 1.0
              setY(0)
            }
          }

          group = graphicEntityModule.createGroup(background, content, secondaryContent, text, itemSpriteGroup.group)
              .setX(x).setY(y)

          setTooltip(tooltipModule, cell, group)
        })
      }

      nextX += cellWidth
    }

    for (player in matchPlayers) {
      player.characterSprite = graphicEntityModule.createSprite().apply {
        image = "Player_blue_single.png"
        baseHeight = 185
        baseWidth = 126
        anchorY = 1.0
        anchorX = 0.0
        x = 5
        y = 105
        tint = player.colorToken
      }
      player.itemSprite = ItemSpriteGroup(cellWidth)

      player.sprite = graphicEntityModule.createGroup(player.characterSprite, player.itemSprite.group)
      tooltipModule.registerEntity(player.sprite, "Chef:" +player.nicknameToken)

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

  fun updateCells(board: Board) {
    board.allCells.zip(cellViews).forEach { (cell, view) ->
      view.itemSpriteGroup.update(cell.item) }

    board.oven()?.also {
      tooltipModule.updateExtraTooltipText(ovenSprite, it.toViewString())
      ovenFireSprite!!.isVisible = it.state is OvenState.Burning
    }
  }

  fun <T : Entity<*>?> Entity<T>.setLocation(cell: Cell, hardTransition: Boolean = false) {
    println(cell)
    val newX = 140 + (cell.x - 1) * (132) + xRange.first
    val newY = 206 + (cell.y - 1) * (110) + yRange.first
    println("$newX $newY")

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

  fun updatePlayer(player: Player, playerPath: List<Cell>?, hardTransition: Boolean = false) {
    player.characterSprite.isVisible = true
    player.itemSprite.isVisible = true

    player.itemSprite.update(player.heldItem)

    if (playerPath == null) {
      player.sprite.setLocation(board[player.location.x, player.location.y], hardTransition)
    } else {
      playerPath.forEachIndexed { index, cell ->
        player.sprite.setLocation(cell)
        graphicEntityModule.commitEntityState(0.2 * index + 0.1, player.sprite)
      }
    }

    graphicEntityModule.commitEntityState(0.9, player.sprite)

  }

  fun removePlayer(player: Player) {
    player.characterSprite.isVisible = false
    player.itemSprite.isVisible = false
  }

  inner class ItemSpriteGroup(width: Int = 132) {
    val mainSprite = graphicEntityModule.createSprite().apply {
      center()
      baseHeight = width - 15
      baseWidth = width - 15
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

    val group = graphicEntityModule.createGroup(*subSprites.toTypedArray()).apply {
      add(mainSprite)
    }


    init {
        tooltipModule.registerEntity(group)

    }

    var isVisible: Boolean = true
      set(value) {
        mainSprite.isVisible = value
        subSprites.forEach { it.isVisible = value }
        field = value
      }

    fun update(item: Item?) {
      subSprites.forEach { it.isVisible = false }
      mainSprite.apply {
        isVisible = true

        when (item) {
          is IceCream -> image = "ice-cream.png"
          is Blueberries -> image = "blueberries.png"
          is Dough -> image = "dough.png"
          is Strawberries -> image = "strawberry.png"
          is ChoppedStrawberries -> image = "strawberries-cut.png"
          is Croissant -> image = "croissant.png"
          is Tart -> image = "tart.png"
          is Dish -> {
            image = "plate.png"
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

      tooltipModule.updateExtraTooltipText(group,
          if(mainSprite.isVisible) "Item: " + item?.describe() else ""
      )
    }
  }

  private fun Sprite.center() {
    anchorY = 0.5
    anchorX = 0.5
    x = cellWidth / 2
    y = cellWidth / 2
  }


}

