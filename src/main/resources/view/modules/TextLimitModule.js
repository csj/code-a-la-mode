import { api as entityModule } from '../entity-module/GraphicEntityModule.js'
import { fitAspectRatio } from '../core/utils.js'
export const api = {
  showDamage: true,
  showCurve: false
}

export class TextLimitModule {
  constructor (assets) {
    this.mustShrinkNickname = true
    this.nicknameIds = []
  }

  static get name () {
    return 'limits'
  }

  updateScene (previousData, currentData, progress) {
    this.limits.forEach(limit => {
      const entityId = limit.textId
      const width = limit.width
      const height = limit.height

      let entity = entityModule.entities.get(entityId)
      if (entity.currentState.text) {
        entity.graphics.scale.set(1)
        if (entity.graphics.width > width) {
          let aspectRatio = fitAspectRatio(entity.graphics.width, entity.graphics.height, width, height)
          entity.graphics.scale.set(aspectRatio)
        }
      }
    })
  }

  handleFrameData (frameInfo, nothing) {
    return { ...frameInfo }
  }

  reinitScene (container, canvasData) {
    this.mustShrinkNickname = true
  }

  animateScene (delta) {
  }

  handleGlobalData (players, limits) {
    this.limits = limits || []
  }
}
