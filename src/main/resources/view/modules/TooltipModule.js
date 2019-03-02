import {
    ErrorLog
} from '../core/ErrorLog.js';
import {
    WIDTH,
    HEIGHT
} from '../core/constants.js';
import * as utils from '../core/utils.js';
import {
    api as entityModule
} from '../entity-module/GraphicEntityModule.js';

function getMouseOverFunc(id, tooltip) {
    return function() {
        tooltip.inside[id] = true;
    };
}

function getMouseOutFunc(id, tooltip) {
    return function() {
        delete tooltip.inside[id];
    };
}

function getEntityState(entity, frame, progress) {
    const subStates = entity.states[frame];
    if (subStates && subStates.length) {
        return subStates[subStates.length - 1];
    }
    return null;
}

var playerList = [];

function getMouseMoveFunc(tooltip, container, module) {
    return function(ev) {
        if (tooltip) {

            var pos = ev.data.getLocalPosition(container);
            tooltip.x = pos.x;
            tooltip.y = pos.y;
            var point = {
                x: pos.x * entityModule.toWorldUnits,
                y: pos.y * entityModule.toWorldUnits
            };
            const showing = [];
            const ids = Object.keys(tooltip.inside).map(n => +n);

            for (let id of ids) {

                if (tooltip.inside[id]) {
                    const entity = entityModule.entities.get(id);
                    const state = entity && getEntityState(entity, module.currentFrame.number);
                    if (!state || state.alpha === 0 || !state.visible) {
                        delete tooltip.inside[id];
                    } else {
                        showing.push(id);
                    }
                }
            }

            if (showing.length) {

                var width = 1910-430;
                var height = 1080-264;
                var x0 = 430;
                var y0 = 264;

                var columns = 11;
                var rows = 7;
//                var cellsize = width / columns;
                var cellsize = 132; // TODO : Variable cell size & hardcoded cellX/Y calcs
                var scale = height / (cellsize * (rows ))
                var cellX = (Math.floor((point.x - x0) / 132));
                var cellY = (Math.floor((point.y - y0) / 110));

                const tooltipBlocks = [];
                var found = false;

                for (let show of showing) {
                    const entity = entityModule.entities.get(show);

                    const state = getEntityState(entity, module.currentFrame.number);
                    if (state !== null) {
                        var tooltipBlock = '';
                        const params = module.currentFrame.registered[show];
                        if (params != null)
                            for (var key in params) {
                                // check if the property/key is defined in the object itself, not in parent
                                if (params.hasOwnProperty(key)) {
                                    var txt = key + ": " + params[key];
                                    if((tooltipBlocks.indexOf(txt) > -1)) continue;
                                    found = true;
                                    tooltipBlocks.push(txt);
                                }
                            }

                        tooltip.visible = true;
                        var extra = module.currentFrame.extraText[show];
                        if (extra && extra.length) {
                            tooltipBlock = extra;
                            found = true;
                        } else {
                            extra = module.currentFrame.extraText[show - 1];
                            if (extra && extra.length) {
                                found = true;
                                tooltipBlock = extra;
                            }
                        }

                        if((tooltipBlocks.indexOf(tooltipBlock) > -1)) continue;
                        tooltipBlocks.push(tooltipBlock);
                    }
                }
                if (cellX >= 0 && cellX < columns && cellY >= 0 && cellY < rows) {
                    tooltipBlocks.splice(0, 0, "y: " + cellY);
                    tooltipBlocks.splice(0, 0, "x: " + cellX);
                }
                else if(!found){
                    tooltip.visible = false;
                    return;
                }

                for (var i = 0; i < tooltipBlocks.length; i++) {
                    for (var p = 0; p < playerList.length; p++) {
                        tooltipBlocks[i] = tooltipBlocks[i].toString().replace("$" + p, playerList[p].name);
                    }
                }
                tooltip.label.text = tooltipBlocks.filter(t => t !== "").join('\n');
            } else {
                tooltip.visible = false;
            }

            tooltip.background.width = tooltip.label.width + 20;
            tooltip.background.height = tooltip.label.height + 20;

            tooltip.pivot.x = -80;
            tooltip.pivot.y = -50;

            if (tooltip.y - tooltip.pivot.y + tooltip.height > HEIGHT) {
                tooltip.pivot.y = 10 + tooltip.height;
                tooltip.y -= tooltip.y - tooltip.pivot.y + tooltip.height - HEIGHT
            }

            if (tooltip.x - tooltip.pivot.x + tooltip.width > WIDTH) {
                tooltip.pivot.x = tooltip.width;
            }
        }
    }
};

export class TooltipModule {
    constructor(assets) {
        this.interactive = {};
        this.previousFrame = {
            registrations: {},
            extra: {},
        };
        this.lastProgress = 1;
        this.lastFrame = 0;
        this.width = 1910 - 430;
        this.height = 1080-264;
        this.x0 = 430;
        this.y0 = 264;
    }

    static get name() {
        return 'tooltips';
    }

    updateScene(previousData, currentData, progress) {
        this.currentFrame = currentData;
        this.currentProgress = progress;
    }

    handleFrameData(frameInfo, [registrations, extra]) {
        const registered = { ...this.previousFrame.registered,
            ...registrations
        };
        const extraText = { ...this.previousFrame.extraText,
            ...extra
        };

        Object.keys(registrations).forEach(
            k => {
                this.interactive[k] = true;
            }
        );

        const frame = {
            registered,
            extraText,
            number: frameInfo.number
        };
        this.previousFrame = frame;
        return frame;
    }

    reinitScene(container, canvasData) {
        this.tooltip = this.initTooltip();
        entityModule.entities.forEach(entity => {
            if (this.interactive[entity.id]) {
                entity.container.interactive = true;
                entity.container.mouseover = getMouseOverFunc(entity.id, this.tooltip);
                entity.container.mouseout = getMouseOutFunc(entity.id, this.tooltip);
            }
        });
        this.container = container;
        container.interactive = true;
        container.mousemove = getMouseMoveFunc(this.tooltip, container, this);
        container.addChild(this.tooltip);
    }

    generateText(text, size, color, align) {
        var textEl = new PIXI.Text(text, {
            fontSize: Math.round(size / 1.2) + 'px',
            fontFamily: 'monospace',
            fontWeight: 'bold',
            fill: color
        });

        textEl.lineHeight = Math.round(size / 1.2);
        if (align === 'right') {
            textEl.anchor.x = 1;
        } else if (align === 'center') {
            textEl.anchor.x = 0.5;
        }

        return textEl;
    };

    initTooltip() {
        var tooltip = new PIXI.Container();
        var background = tooltip.background = new PIXI.Graphics();
        var label = tooltip.label = this.generateText('', 36, 0xFFFFFF, 'left');

        background.beginFill(0x0, 0.7);
        background.drawRect(0, 0, 200, 185);
        background.endFill();
        background.x = -10;
        background.y = -10;

        tooltip.visible = false;
        tooltip.inside = {};

        tooltip.addChild(background);
        tooltip.addChild(label);

        tooltip.interactiveChildren = false;
        return tooltip;
    };

    animateScene(delta) {

    }

    handleGlobalData(players, globalData) {
        playerList = players;
    }
}

class NotYetImplemented extends Error {
    constructor(feature) {
        super('Not yet implemented: "' + feature);
        this.feature = feature;
        this.name = 'NotYetImplemented';
    }
}