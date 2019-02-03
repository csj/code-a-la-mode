package com.codingame.game.view

import com.codingame.game.model.*
import com.codingame.gameengine.module.entities.Sprite

class QueueView {
  lateinit var queue: CustomerQueue

  companion object {
    var x = 0
    var y = 0
  }

  private var customerViews: List<CustomerView> = List(3) {
    CustomerView()
  }

  val wholeGroup = graphicEntityModule.createGroup(*(customerViews.map { it.group }).toTypedArray()).apply {
    x = QueueView.x
    y = QueueView.y
  }

  fun updateQueue() {
    customerViews.forEachIndexed { index, custView ->
      custView.group.apply { x = 10 + index * 440; y = 10 }

      if (index >= queue.size) {
        custView.group.isVisible = false
      } else {
        queue[index].let {
          custView.group.isVisible = it.award > 0
          custView.update(it.dish.contents.toList(), it.award)
        }
      }
    }
  }

  inner class CustomerView {
    val viewWidth = 420
    val viewHeight = 210

    val customerSpritePadding = 5
    val customerSpriteWidth = viewWidth / 4 - customerSpritePadding*2

    private fun Sprite.center() {
      anchorY = 0.5
      anchorX = 0.5
      x = customerSpriteWidth / 2
      y = customerSpriteWidth / 2
    }

    val foodSprites = List(4) { i ->
      graphicEntityModule.createSprite().apply {
        center()
        baseHeight = customerSpriteWidth
        baseWidth = customerSpriteWidth
        x = ((i + 0.5) * (customerSpriteWidth + customerSpritePadding*2)).toInt()
        y = (0.5 * customerSpriteWidth + customerSpritePadding).toInt()
        zIndex = 300
        isVisible = false
      }
    }

    val awardText = graphicEntityModule.createText("0").apply {
      fillColor = 0xffffff
      fontSize = 50
      x = viewWidth / 2
      y = viewHeight * 3 / 4
      anchorX = 0.5
      anchorY = 0.5
      zIndex = 350
    }

    val backgroundBox = graphicEntityModule.createRectangle().apply {
      fillColor = 0x4286f4
      width = viewWidth
      height = viewHeight
      zIndex = 200
    }

    val group = graphicEntityModule.createGroup(*(foodSprites + awardText + backgroundBox).toTypedArray())

    fun update(edibles: List<EdibleItem>, award: Int) {
      awardText.text = award.toString()

      foodSprites.forEach { it.isVisible = false }
      edibles.zip(foodSprites).forEach { (edible, foodSprite) ->
        foodSprite.apply {
          isVisible = true
          when (edible) {
            is PieSlice -> image = "pie-slice.png"  // TODO: add flavour
            is IceCream -> image = "ice-cream.png"
            is Strawberries -> image = "strawberry.png"
            is Blueberries -> image = "blueberries.png"
            is Waffle -> image = "waffle.png"
            is ChoppedBananas -> image = "open-banana.png"
          }
        }
      }
    }
  }

}