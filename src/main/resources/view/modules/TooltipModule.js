import {
  WIDTH,
  HEIGHT
} from '../core/constants.js'

import {
  api as entityModule
} from '../entity-module/GraphicEntityModule.js'

function getMouseOverFunc (id, tooltip) {
  return function () {
    tooltip.inside[id] = true
  }
}

function getMouseOutFunc (id, tooltip) {
  return function () {
    delete tooltip.inside[id]
  }
}

function getEntityState (entity, frame, progress) {
  const subStates = entity.states[frame]
  if (subStates && subStates.length) {
    return subStates[subStates.length - 1]
  }
  return null
}

function replaceAll (str, replace, find) {
  return str.replace(new RegExp(find, 'g'), replace)
}

function getMouseMoveFunc (tooltip, container, module) {
  return function (ev) {
    if (tooltip) {
      var pos = ev.data.getLocalPosition(container)
      tooltip.x = pos.x
      tooltip.y = pos.y

      var point = {
        x: pos.x * entityModule.toWorldUnits,
        y: pos.y * entityModule.toWorldUnits
      }

      var cellX = (Math.floor((point.x - module.x0) / 132))
      var cellY = (Math.floor((point.y - module.y0) / 110))

      const showing = []
      const ids = Object.keys(tooltip.inside).map(n => +n)
      const tooltipBlocks = []

      for (let id of ids) {
        if (tooltip.inside[id]) {
          const entity = entityModule.entities.get(id)
          const state = entity && getEntityState(entity, module.currentFrame.number)
          if (!state || state.alpha === 0 || !state.visible) {
            delete tooltip.inside[id]
          } else {
            showing.push(id)
          }
        }
      }

      if (showing.length) {
        for (let show of showing) {
          const entity = entityModule.entities.get(show)
          const state = getEntityState(entity, module.currentFrame.number)

          if (entity && state) {
            const params = module.currentFrame.registered[show]

            if (params != null) {
              let paramBlocks = []
              for (var key in params) {
                if (key.length > 0) {
                  const txt = key + ': ' + params[key]
                  if (paramBlocks.indexOf(txt) > -1) continue
                  paramBlocks.push(txt)
                }
              }
              if (paramBlocks.length) {
                tooltipBlocks.push(paramBlocks.join('\n'))
              }
            }

            var extra = module.currentFrame.extraText[show]
            if (extra && extra.length) {
              tooltipBlocks.push(extra)
            } else {
              extra = module.currentFrame.extraText[show - 1]
              if (extra && extra.length && tooltipBlocks.indexOf(extra) === -1) {
                tooltipBlocks.push(extra)
              }
            }
          }
        }
      }
      if (cellY >= 0 && cellY < 7 && cellX >= 0 && cellX < 11) {
        tooltipBlocks.unshift('x: ' + cellX +
        '\ny: ' + cellY)
      }

      if (tooltipBlocks.length) {
        for (var i = 0; i < tooltipBlocks.length; i++) {
          for (var p = 0; p < playerList.length; p++) {
            tooltipBlocks[i] = tooltipBlocks[i].toString().replace('$' + p, playerList[p].name)
          }
        }

        var txt = tooltipBlocks.filter(t => t !== '').join('\n')
        txt = replaceAll(txt, 'DISH', '#D')
        txt = replaceAll(txt, 'ICE_CREAM', '#I')
        txt = replaceAll(txt, 'DOUGH', '#H')
        txt = replaceAll(txt, 'STRAWBERRIES', '#S')
        txt = replaceAll(txt, 'TART', '#T')
        txt = replaceAll(txt, 'BLUEBERRIES', '#B')
        txt = replaceAll(txt, 'CROISSANT', '#C')
        txt = replaceAll(txt, 'CHOPPED', '#O')
        tooltip.label.text = txt
        tooltip.visible = true
      } else {
        tooltip.visible = false
      }

      tooltip.background.width = tooltip.label.width + 20
      tooltip.background.height = tooltip.label.height + 20

      tooltip.pivot.x = -30
      tooltip.pivot.y = -50

      if (tooltip.y - tooltip.pivot.y + tooltip.height > HEIGHT) {
        tooltip.pivot.y = 10 + tooltip.height
        tooltip.y -= tooltip.y - tooltip.pivot.y + tooltip.height - HEIGHT
      }

      if (tooltip.x - tooltip.pivot.x + tooltip.width > WIDTH) {
        tooltip.pivot.x = tooltip.width
      }
    }
  }
};

var playerList = []

export class TooltipModule {
  constructor (assets) {
    this.interactive = {}
    this.previousFrame = {
      registrations: {},
      extra: {}
    }
    this.lastProgress = 1
    this.lastFrame = 0
    this.width = 1910 - 430
    this.height = 1080 - 264
    this.x0 = 430
    this.y0 = 290
  }

  static get name () {
    return 'tooltips'
  }

  updateScene (previousData, currentData, progress) {
    this.currentFrame = currentData
    this.currentProgress = progress
  }

  handleFrameData (frameInfo, [registrations, extra]) {
    const registered = { ...this.previousFrame.registered,
      ...registrations
    }
    const extraText = { ...this.previousFrame.extraText,
      ...extra
    }

    Object.keys(registrations).forEach(
      k => {
        this.interactive[k] = true
      }
    )

    const frame = {
      registered,
      extraText,
      number: frameInfo.number
    }
    this.previousFrame = frame
    return frame
  }

  reinitScene (container, canvasData) {
    this.tooltip = this.initTooltip()
    entityModule.entities.forEach(entity => {
      if (this.interactive[entity.id]) {
        entity.container.interactive = true
        entity.container.mouseover = getMouseOverFunc(entity.id, this.tooltip)
        entity.container.mouseout = getMouseOutFunc(entity.id, this.tooltip)
      }
    })
    this.container = container
    container.interactive = true
    container.mousemove = getMouseMoveFunc(this.tooltip, container, this)
    container.addChild(this.tooltip)
  }

  generateText (text, size, color, align) {
    var textEl = new PIXI.Text(text, {
      fontSize: Math.round(size / 1.2) + 'px',
      fontFamily: 'monospace',
      fontWeight: 'bold',
      fill: color
    })

    textEl.lineHeight = Math.round(size / 1.2)
    if (align === 'right') {
      textEl.anchor.x = 1
    } else if (align === 'center') {
      textEl.anchor.x = 0.5
    }

    return textEl
  };

  initTooltip () {
    var tooltip = new PIXI.Container()
    var background = tooltip.background = new PIXI.Graphics()
    var label = tooltip.label = this.generateText('', 36, 0xFFFFFF, 'left')

    background.beginFill(0x0, 0.7)
    background.drawRect(0, 0, 200, 185)
    background.endFill()
    background.x = -10
    background.y = -10

    tooltip.visible = false
    tooltip.inside = {}

    tooltip.addChild(background)
    tooltip.addChild(label)

    tooltip.interactiveChildren = false
    return tooltip
  };

  animateScene (delta) {

  }

  handleGlobalData (players, globalData) {
    playerList = players
  }
}
