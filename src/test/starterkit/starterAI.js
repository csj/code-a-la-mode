// Game parameters
const WIDTH = 11;
const HEIGHT = 7;

// Tile Types
const TABLE = "#";
const EMPTY = ".";
const ICE_CREAM_CRATE = "I";
const DISH_WASHER = "D";
const WINDOW = "W";

// Item Types
const NONE = "NONE";
const DISH = "DISH";
const BLUEBERRIES = "BLUEBERRIES";
const ICE_CREAM = "ICE_CREAM";


class Entity{
	constructor(x, y, type){
		this.x = x;
		this.y = y;
		this.type = type;
	}
}

class Item extends Entity{
    constructor(x, y, type){
        super(x, y, type);
    }
}

class Chef extends Entity{
	constructor(x, y, type){
		super(x, y, type);
	}
}

class Cell {
	constructor(type, index) {
		this.type = type;
		this.index = index;
	}

	canWalkOnIt() {
		return this.type === EMPTY;
	}

	getPos() {
		return {
			x: this.index % WIDTH,
			y: Math.floor(this.index / WIDTH)
		};
	}
}

class Grid{
	constructor(){
		this.cells = [];
		this.items = [];
	}

	addRow(line) {
		for (let i = 0; i < line.length; i++) {
			this.cells.push(new Cell(line[i], this.cells.length));
		}
	}

	getCell(x, y) {
		return this.cells[x + WIDTH * y];
	}

	getCellFromType(type){
		return this.cells.filter(cell => {
			return cell.type === type;
		})[0];
	}

	debug(){
		this.cells.forEach(cell => {
			let pos = cell.getPos();
			console.error(`Cell X: ${pos.x} // Cell Y: ${pos.y}`);
			console.error(`Cell Type: ${cell.type}`);
			console.error(`-------------------------`);
		})
	}
}

class Customer{
	constructor(item, award){
		this.items = item.split('-');
		this.award = award;
	}
}

class Game{
	constructor(){
		this.grid = new Grid();
		this.chefs = [new Chef(), new Chef()];
		this.customers = [];
	}
}


let GAME = new Game();

// ALL CUSTOMERS INPUT: to ignore until Bronze
const numAllCustomers = parseInt(readline());
for (let i = 0; i < numAllCustomers; i++) {
	let inputs = readline().split(' ');
	const customerItem = inputs[0]; // the food the customer is waiting for
	const customerAward = parseInt(inputs[1]); // the number of points awarded for delivering the food
}

// KITCHEN INPUT
for (let i = 0; i < 7; i++) {
	const kitchenLine = readline();
	GAME.grid.addRow(kitchenLine);
}

// GAME_GRID.debug();

// game loop
while (true) {
	// Reset Items
	GAME.grid.items = [];
	// Reset customers
	GAME.customers = [];
	// Reset chefs
	GAME.chefs = [];

	const turnsRemaining = parseInt(readline());

	// PLAYERS INPUT
	let inputsPlayer = readline().split(' ');
	const playerX = parseInt(inputsPlayer[0]);
	const playerY = parseInt(inputsPlayer[1]);
	const playerItem = inputsPlayer[2];
	GAME.chefs[0] = new Chef(playerX, playerY, playerItem);

	let inputsPartner = readline().split(' ');
	const partnerX = parseInt(inputsPartner[0]);
	const partnerY = parseInt(inputsPartner[1]);
	const partnerItem = inputsPartner[2];
	GAME.chefs[1] = new Chef(partnerX, partnerY, partnerItem);


	const numTablesWithItems = parseInt(readline()); // the number of tables in the kitchen that currently hold an item
	for (let i = 0; i < numTablesWithItems; i++) {
		let inputs = readline().split(' ');
		const tableX = parseInt(inputs[0]);
		const tableY = parseInt(inputs[1]);
		const item = inputs[2];
		GAME.grid.items.push(new Item(tableX, tableY, item));
	}

	let inputs = readline().split(' ');
	const ovenContents = inputs[0]; // ignore until bronze league
	const ovenTimer = parseInt(inputs[1]);
	const numCustomers = parseInt(readline()); // the number of customers currently waiting for food

    // CURRENT CUSTOMERS INPUT
	for (let i = 0; i < numCustomers; i++) {
		let inputs = readline().split(' ');
		const customerItem = inputs[0];
		const customerAward = parseInt(inputs[1]);
		GAME.customers.push(new Customer(customerItem, customerAward))
	}

    // GAME LOGIC
    // fetch a dish
	if(playerItem === NONE){
		let dishwasher = GAME.grid.getCellFromType(DISH_WASHER).getPos();
		console.log(`USE ${dishwasher.x} ${dishwasher.y}`)
	}
	// fetch blueberries
	else if(playerItem === DISH){
		let blueberries = GAME.grid.getCellFromType(BLUEBERRY_CRATE).getPos();
		console.log(`USE ${blueberries.x} ${blueberries.y}`);
	}
	// fetch ice cream
	else if(playerItem === "DISH-BLUEBERRIES"){
		let icecream = GAME.grid.getCellFromType(ICE_CREAM_CRATE).getPos();
		console.log(`USE ${icecream.x} ${icecream.y}`);
	}
	else{
		let windowC = GAME.grid.getCellFromType(WINDOW).getPos();
		console.log(`USE ${windowC.x} ${windowC.y}`);
	}
}