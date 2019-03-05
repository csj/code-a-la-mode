import { GraphicEntityModule } from './entity-module/GraphicEntityModule.js';
import { TooltipModule } from './modules/TooltipModule.js';
import { EndScreenModule } from './endscreen-module/EndScreenModule.js';

// List of viewer modules that you want to use in your game
export const modules = [
	GraphicEntityModule, TooltipModule , EndScreenModule
];

export const playerColors = ['#ff1d5c', '#22a1e4', '#6ac371'];