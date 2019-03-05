package com.codingame.game.view

import com.codingame.game.model.*
import com.codingame.gameengine.module.entities.Curve
import com.codingame.gameengine.module.entities.Sprite
import com.codingame.gameengine.module.entities.Text
import tooltipModule.TooltipModule

class QueueView {
  lateinit var queue: CustomerQueue

  var failed = false

  companion object {
    lateinit var xRange: IntRange
    lateinit var yRange: IntRange
  }

  private var customerViews: List<CustomerView> = List(3) {
    CustomerView()
  }

  val failureBox = graphicEntityModule.createRectangle().apply {
    width = QueueView.xRange.last - QueueView.xRange.first
    height = QueueView.yRange.last - QueueView.yRange.first
    fillColor = 0xff0000
    isVisible = false
    zIndex = -1000
  }

  val wholeGroup = graphicEntityModule.createGroup(*(customerViews.map { it.group } + failureBox).toTypedArray()).apply {
    x = QueueView.xRange.first
    y = QueueView.yRange.first
  }

  fun updateQueue() {
    customerViews.forEachIndexed { index, custView ->
      custView.group.apply { y = index * (187 + 20); x = 36 }

      if (index >= queue.activeCustomers.size) {
        custView.group.isVisible = false
      } else {
        queue.activeCustomers[index].let {
          custView.group.isVisible = it.award > 0
          custView.update(it)
        }
      }
    }
    failureBox.isVisible = failed
    graphicEntityModule.commitEntityState(0.0, failureBox)
    failed = false
  }

  inner class CustomerView {
    val viewWidth = 343
    val viewHeight = 187

    val customerSpritePadding = 5
    val customerSpriteWidth = 214 / 2 - customerSpritePadding * 2

    private fun Sprite.center() {
      anchorY = 0.0
      anchorX = 0.5
      x = customerSpriteWidth / 2
      y = customerSpriteWidth / 2
    }

    val foodSprites = List(4) { i ->
      graphicEntityModule.createSprite().apply {
        baseHeight = customerSpriteWidth * 3 / 4
        baseWidth = customerSpriteWidth * 3 / 4
        anchorX = 0.5
        x = (135 + (i % 2) * (customerSpriteWidth + customerSpritePadding * 2)) + customerSpriteWidth / 2
        y = if (i < 2) 11 else 99
        zIndex = 300
        isVisible = false
      }
    }

    val awardText = graphicEntityModule.createText("0").apply {
      fillColor = 0xffffff
      strokeColor = 0x000000
      strokeThickness = 2.0
      fontSize = 35
      fontWeight = Text.FontWeight.BOLDER

      x = 127 / 2
      y = viewHeight / 2
      anchorX = 0.5
      anchorY = 0.5
      zIndex = 350
      alpha = 0.0
    }

    val waitingColour = 0x4286f4
    val dangerColour = 0xf4d507
    val angryColour = 0xc13d2c
    val happyColour = 0x37c648

    val backgroundBox = graphicEntityModule.createRectangle().apply {
      fillColor = waitingColour
      width = viewWidth
      height = viewHeight
      zIndex = 200
      alpha = 0.0
    }
    val group = graphicEntityModule.createGroup(*foodSprites.toTypedArray()).apply {
      add(awardText)
      add(backgroundBox)
    }

    init {
      tooltipModule.registerEntity(group)
    }

    fun update(customer: Customer) {
      tooltipModule.updateExtraTooltipText(group, customer.dish.describe())

      val baseAward = customer.originalAward - Constants.TIP
      val tip = customer.award - baseAward
      val edibles = customer.dish.contents

      awardText.text = "${baseAward.toString().padStart(4)}\r\n    +\r\n${tip.toString().padStart(4)}"
      awardText.alpha = 1.0

      backgroundBox.setFillColor(
          when (customer.satisfaction) {
            Satisfaction.Waiting -> waitingColour
            Satisfaction.Satisfied -> happyColour
            Satisfaction.Danger -> dangerColour
            Satisfaction.Leaving -> angryColour
          }, Curve.IMMEDIATE).setAlpha(
          when (customer.satisfaction) {
            Satisfaction.Waiting -> 0.0
            else -> 0.5
          }, Curve.IMMEDIATE)

      foodSprites.forEach { it.isVisible = false }
      edibles.zip(foodSprites).forEach { (edible, foodSprite) ->
        foodSprite.apply {
          isVisible = true
          when (edible) {
            is Tart -> image = "tart_big_bb.png"
            is IceCream -> image = "ice-cream.png"
            is Blueberries -> image = "blueberries.png"
            is Croissant -> image = "croissant.png"
            is ChoppedStrawberries -> image = "strawberries-cut.png"
          }
        }
      }
    }
  }

}