package com.codingame.game.view

import com.codingame.game.Player
import com.codingame.game.model.*
import com.codingame.gameengine.module.entities.*
import tooltipModule.TooltipModule
import java.awt.geom.Point2D


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
  var ovenGlowSprite: Sprite? = null
  var ovenContentSprite: Sprite? = null
  lateinit var dishesSprites: List<Sprite>

  private var cellViews: MutableList<CellView> = mutableListOf()

  init {
    fun setTooltip(tooltipModule: TooltipModule, cell: Cell, group: Group) {
      val toolTip = cell.equipment?.let { "Equipment:${it.tooltipString}" } ?: ""
      tooltipModule.registerEntity(group, toolTip)
    }

    val floorColor = 0xe0e0eb
    val tableColor = 0x756b68
//    val floorColor = 0xe0e0eb
//    val tableColor = 0xb35900

    graphicEntityModule.createSprite().apply {
      image = "background03.jpg"
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
        cellHeight = if (isFirstRow) 136 else 110
        val x = nextX
        val y = nextY

        nextY += cellHeight

        cellViews.add(CellView(cell).apply {

          val equipment = cell.equipment
          if (equipment != null) {
            content = graphicEntityModule.createSprite().apply {
              baseHeight = (110 * 0.75).toInt()
              baseWidth = (132 * 0.75).toInt()
              anchorX = 0.5
              anchorY = 0.0
              setX(cellWidth / 2)
              setY(10)
              when (equipment) {
                is ChoppingBoard -> image = "board02.png"
                is GeneralCrate -> image = "bowl.png"
                is Oven -> {
                  image = if (cell.x == 0) "oven_left.png" else if (cell.x == 10) "oven_right.png" else "oven_top.png"
//                baseHeight = if (cell.x == 0 || cell.x == 10) 131 else 99
//                baseWidth = if (cell.x == 0 || cell.x == 10) 140 else 132
                  baseHeight = cellHeight
                  baseWidth = cellWidth
                  anchorX = if (cell.x == 10) 1.0 else 0.0
                  anchorY = 0.0
                  if (cell.x == 0) setX(8) else if (cell.x == 10) setX(cellWidth - 8) else setX(0)
                  setY(0)
                }
                is Window -> {
                  image = "window.png"
                  baseWidth = cellWidth / 2
                  baseHeight = cellHeight / 2
                }
//              is DishWasher -> image = "dishwasher.png"
              }
            }

            when (equipment) {
              is Oven -> {
                ovenSprite = content
                secondaryContent = graphicEntityModule.createSprite().apply {
                  baseWidth = cellWidth * 3 / 4
                  baseHeight = cellHeight * 3 / 4
                  anchorX = 0.5
                  anchorY = 0.5
                  setX(cellWidth / 2)
                  setY(cellHeight / 2)
                  zIndex = 4000
                  isVisible = false
                  alpha = 0.5
                }
                ovenGlowSprite = graphicEntityModule.createSprite().apply {
                  baseWidth = cellWidth
                  baseHeight = cellHeight
                  anchorX = if (cell.x == 0) -0.2 else if (cell.x == 10) 0.2 else 0.0
                  anchorY = 0.0
                  zIndex = 3999
                  isVisible = false
                  alpha = 1.0
                  image = if (cell.x == 0) "hot_right.png" else if (cell.x == 10) "hot_left.png" else if (cell.y == 0) "hot_top.png" else ""
                }
                (ovenGlowSprite as Sprite).setLocation(cell)
                ovenContentSprite = secondaryContent
                tooltipModule.registerEntity(ovenSprite)
              }
              is DishWasher -> {
                secondaryContent = graphicEntityModule.createSprite().apply { isVisible = false }
                dishesSprites = List(3) { i ->
                  graphicEntityModule.createSprite().apply {
                    anchorX = 0.5
                    anchorY = 0.0
                    baseHeight = 78 + (7 * i)
                    baseWidth = 78 + (7 * i)
                    zIndex = 50 + i
                    image = "plate.png"
                    setX((132 + (-10..10).random()) / 2)
                    setY(15 - (20 * i))
                    isVisible = true
                  }
                }
              }
              else -> secondaryContent = graphicEntityModule.createSprite().apply {
                when (equipment) {
                  is StrawberryCrate -> image = "strawberry.png"
                  is BlueberryCrate -> image = "blueberries.png"
                  is IceCreamCrate -> image = "ice-cream.png"
                  is DoughCrate -> image = "dough.png"
                  else -> isVisible = false
                }
                baseHeight = cellHeight / 2
                baseWidth = cellWidth / 2
                anchorX = 0.5
                anchorY = 0.0
                alpha = 1.0
                setX(cellWidth / 2)
                setY(if (cell.y == 0) 10 else 20)
              }
            }

            itemSpriteGroup = null

//            itemSpriteGroup = ItemSpriteGroup()
//            itemSpriteGroup.group.setY(-20).setX(10)

            group = graphicEntityModule.createGroup(content, secondaryContent)
                .setX(x).setY(y)

            if (equipment is DishWasher) {
              graphicEntityModule.createGroup().apply {
                add(dishesSprites[0])
                add(dishesSprites[1])
                add(dishesSprites[2])
              }.setX(x).setY(y)
            }


          } else {
            itemSpriteGroup = null

//            itemSpriteGroup = ItemSpriteGroup()
//            itemSpriteGroup!!.group.setY(-20).setX(10)
            group = graphicEntityModule.createGroup().setX(x).setY(y)
          }

          setTooltip(tooltipModule, cell, group)
        })
      }

      nextX += cellWidth
    }

    for (player in matchPlayers) {
      player.characterSprite = graphicEntityModule.createSprite().apply {
//      image = "player00"
        scaleX = 0.92
        scaleY = 0.92
        x = 132 / 2
        y = 100
        anchorX = 0.5
        anchorY = 1.0
        isVisible = false
      }
      player.itemSprite = ItemSpriteGroup(cellWidth)

      player.itemSprite.group.apply {
        y = -165 + (132 / 2)
      }

      player.sprite = graphicEntityModule.createGroup(player.characterSprite, player.itemSprite.group)
      tooltipModule.registerEntity(player.sprite, player.toViewString())
    }
  }

  fun updateCells(board: Board) {
    board.allCells.zip(cellViews).forEach { (cell, view) ->
      //      view.itemSpriteGroup.update(cell.item)
      if (cell.item !== null && view.itemSpriteGroup === null) {
        view.itemSpriteGroup = ItemSpriteGroup()
        view.group.add(view.itemSpriteGroup!!.group)
        view.itemSpriteGroup!!.update(cell.item)
      }
      if (view.itemSpriteGroup !== null) {
        view.itemSpriteGroup!!.update(cell.item)
      }
      if (cell.equipment is DishWasher) {
        var dishCount = (cell.equipment as DishWasher).dishes
        for ((index, dishSprite) in dishesSprites.withIndex()) {
          dishSprite.isVisible = index <= dishCount - 1
        }
      }
    }

    board.oven()?.also {
      tooltipModule.updateExtraTooltipText(ovenSprite, it.toViewString())
      var showOvenOverlay = it.state is OvenState.Burning || it.state is OvenState.Baking || it.state is OvenState.Ready
      var ovenImage = when (it.state) {
        is OvenState.Baking -> when ((it.state as OvenState.Baking).contents) {
          is Dough -> "dough.png"
          is Shell -> "paton_bb.png"
          else -> ""
        }
        is OvenState.Ready -> when ((it.state as OvenState.Ready).contents) {
          is Croissant -> "croissant.png"
          is Tart -> "tart_big_bb.png"
          else -> ""
        }
        else -> "smoke.png"
      }
      ovenContentSprite!!.apply {
        isVisible = showOvenOverlay
        image = ovenImage
      }
      ovenGlowSprite!!.isVisible = it.state is OvenState.Baking
    }


  }

  fun <T : Entity<*>?> Entity<T>.setLocation(cell: Cell, hardTransition: Boolean = false) {
    val newX = 140 + (cell.x - 1) * (132) + xRange.first
    val newY = 136 + (cell.y - 1) * (110) + yRange.first

    if (hardTransition) {
      setX(newX, Curve.NONE)
      setY(newY, Curve.NONE)
    } else {
      x = newX
      y = newY
      zIndex = 200 + newY
    }
  }

  fun resetPlayers() {
    players.forEach { updatePlayer(it, null, true) }
  }

  fun updatePlayer(player: Player, playerPath: List<Cell>?, hardTransition: Boolean = false) {
    if (!player.crashed) {
      player.characterSprite.isVisible = true
      player.itemSprite.isVisible = true

      player.itemSprite.update(player.heldItem)

      if(player.characterSprite.image === null) {
        player.characterSprite.image = playerSprites[player.index][0]
        player.characterSprite.anchorX = playerSpriteAnchors[0].getX()
        player.characterSprite.anchorY = playerSpriteAnchors[0].getY()
      }

      if (playerPath == null) {
        player.sprite.setLocation(board[player.location.x, player.location.y], hardTransition)
      } else {
        println(playerPath)
        playerPath.forEachIndexed { index, cell ->
          if(playerPath.size > 1 && playerPath.first() !== playerPath.last()) {
            if (index + 1 < playerPath.size) {
              var nextCell = playerPath[index + 1]
              var spriteNum = 0
              when {
                nextCell.x < cell.x -> spriteNum = 1
                nextCell.x > cell.x -> spriteNum = 2
                nextCell.y > cell.y -> spriteNum = 3
                nextCell.y < cell.y -> spriteNum = 4
              }
              player.characterSprite.image = playerSprites[player.index][spriteNum]
              player.characterSprite.anchorX = playerSpriteAnchors[spriteNum].getX()
              player.characterSprite.anchorY = playerSpriteAnchors[spriteNum].getY()
              graphicEntityModule.commitEntityState(0.2 * index + 0.1, player.characterSprite)
            }
          }
          player.sprite.setLocation(cell)
          graphicEntityModule.commitEntityState(0.2 * index + 0.1, player.sprite)
        }
      }

      graphicEntityModule.commitEntityState(0.9, player.sprite)
    }
  }

  fun removePlayer(player: Player) {
    player.characterSprite.isVisible = false
    player.itemSprite.isVisible = false
  }

  inner class ItemSpriteGroup(width: Int = 132) {
    val mainSprite = graphicEntityModule.createSprite().apply {
      anchorY = 0.5
      anchorX = 0.5
      x = width / 2
      y = width / 2
      baseHeight = width * 3 / 4
      baseWidth = width * 3 / 4
      zIndex = 50
      isVisible = false
    }

    val subSprites = List(4) { i ->
      graphicEntityModule.createSprite().apply {
        anchorX = 0.0
        anchorY = 0.0
        baseHeight = 40
        baseWidth = 50
        zIndex = 50 + i
        x = 20 + (i % 2) * 52
        y = if (i < 2) 30 else 70
        isVisible = false
      }
    }


    val group = graphicEntityModule.createGroup().apply {
      add(subSprites[0])
      add(subSprites[1])
      add(subSprites[2])
      add(subSprites[3])
      add(mainSprite)
      x = 10
      y = -20
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
          is Tart -> image = "tart_big_bb.png"
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
                  is Tart -> image = "tart_big_bb.png"
                }
              }
            }
          }
          is Shell -> {
            if (item.hasBlueberry) {
              image = "paton_bb.png"
            } else {
              image = "paton_cut_big.png"
            }
          }

          else -> mainSprite.isVisible = false
        }
      }

      tooltipModule.updateExtraTooltipText(group,
          if (mainSprite.isVisible) "Item: " + item?.describe() else ""
      )
    }
  }
}

  private fun Sprite.center() {
    anchorY = 0.5
    anchorX = 0.5
    x = 132 / 2
    y = 110 / 2
  }

  val playerSpriteAnchors = arrayOf(
          Point2D.Double(73/135.toDouble(), 1.toDouble()),
          Point2D.Double(59/117.toDouble(), 1.toDouble()),
          Point2D.Double(57/116.toDouble(), 169/156.toDouble()),
          Point2D.Double(83/141.toDouble(), 168/160.toDouble()),
          Point2D.Double(59/143.toDouble(), 180/158.toDouble())
  )

  val playerSprites = arrayOf(0,1,2).map { index ->
    Array(5) { "player$index$it" }
  }

