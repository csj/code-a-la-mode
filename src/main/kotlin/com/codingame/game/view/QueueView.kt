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

  val wholeGroup = graphicEntityModule.createGroup().apply {
    for(c in customerViews)
      add(c.group)
    add(failureBox)
    x = QueueView.xRange.first
    y = QueueView.yRange.first
  }

  fun updateQueue() {
    customerViews.forEachIndexed { index, custView ->
        queue.activeCustomers[index].let {
          custView.update(it, index)
      }
    }

    if(failed != failureBox.isVisible){
      failureBox.isVisible = failed
      graphicEntityModule.commitEntityState(0.0, failureBox)
      failed = false
    }
  }

  inner class CustomerView {
    var model : Customer? = null
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
      y = (viewHeight / 6 * 1.5).toInt()
      anchorX = 0.5
      anchorY = 0.5
      zIndex = 350
      alpha = 1.0
    }


    val plusText = graphicEntityModule.createText("+").apply {
      fillColor = 0xffffff
      strokeColor = 0x000000
      strokeThickness = 2.0
      fontSize = 25
      fontWeight = Text.FontWeight.BOLDER
      x = 127 / 2
      y = viewHeight / 2
      anchorX = 0.5
      anchorY = 0.5
      zIndex = 350
      alpha = 1.0
    }

    val tiptext = graphicEntityModule.createText("0").apply {
      fillColor = 0xffffff
      strokeColor = 0x000000
      strokeThickness = 2.0
      fontSize = 35
      fontWeight = Text.FontWeight.BOLDER
      x = 127 / 2
      y = (viewHeight / 6 * 4.5).toInt()
      anchorX = 0.5
      anchorY = 0.5
      zIndex = 350
      alpha = 1.0
    }

    val waitingColour = 0x4286f4
    val happyColour = 0x37c648

    val backgroundBox = graphicEntityModule.createRectangle().apply {
      fillColor = waitingColour
      width = viewWidth
      height = viewHeight
      zIndex = 200
      alpha = 0.0
    }
    val group = graphicEntityModule.createGroup().apply {
      for (f in foodSprites)
        add(f)
      add(plusText)
      add(tiptext)
      add(awardText)
      add(backgroundBox)
    }

    init {
      tooltipModule.registerEntity(group)
    }

    fun update(customer: Customer, index: Int) {
      val baseAward = customer.originalAward - Constants.TIP
      val tip = customer.award - baseAward

      tiptext.text = tip.toString()
      if(customer.satisfaction == Satisfaction.Satisfied){
        backgroundBox.setFillColor(happyColour).setAlpha(0.5, Curve.IMMEDIATE)
        graphicEntityModule.commitEntityState(0.0, backgroundBox)
      }

      if(customer == model)
        return

      model = customer
      tooltipModule.updateExtraTooltipText(group, customer.dish.describe())
      backgroundBox.setFillColor(waitingColour, Curve.IMMEDIATE).setAlpha(0.0, Curve.IMMEDIATE)

      group.apply { y = index * (187 + 20); x = 36 }

      awardText.text = baseAward.toString()

      val edibles = customer.dish.contents
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

      graphicEntityModule.commitEntityState(0.0, awardText)
      graphicEntityModule.commitEntityState(0.0, tiptext)
      for(f in foodSprites)
        graphicEntityModule.commitEntityState(0.0, f)
    }
  }
}