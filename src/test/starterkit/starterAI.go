package main

import (
	"bufio"
	"fmt"
	"os"
	"strings"
)

// Game parameters
const (
	WIDTH  = 11
	HEIGHT = 7
)

// Tile Types
const (
	TABLE           = "#"
	EMPTY           = "."
	ICE_CREAM_CRATE = "I"
	BLUEBERRY_CRATE = "B"
	DISH_WASHER     = "D"
	WINDOW          = "W"
)

// Item Types
const (
	NONE        = "NONE"
	DISH        = "DISH"
	BLUEBERRIES = "BLUEBERRIES"
	ICE_CREAM   = "ICE_CREAM"
)

type Position struct {
	X int
	Y int
}

type Entity struct {
	Position
	Type string
}

type Item struct {
	Entity
}

func NewItem(x, y int, item string) *Item {
	i := Item{
		Entity: Entity{
			Position: Position{
				X: x,
				Y: y,
			},
			Type: item,
		},
	}
	return &i
}

type Chef struct {
	Entity
}

func NewChef() *Chef {
	c := Chef{}
	return &c
}

type Cell struct {
	Type  string
	Index int
}

func NewCell(cellType string, index int) *Cell {
	cell := Cell{
		Type:  cellType,
		Index: index,
	}
	return &cell
}

func (c *Cell) CanWalkOnIt() bool {
	return c.Type == EMPTY
}

func (c *Cell) GetPos() Position {
	return Position{
		c.Index % WIDTH,
		c.Index / WIDTH,
	}
}

type Grid struct {
	Cells []*Cell
	Items []*Item
}

func (g *Grid) AddRow(line string) {
	for _, r := range line {
		g.Cells = append(g.Cells, NewCell(string(r), len(g.Cells)))
	}
}

func (g *Grid) GetCell(x, y int) *Cell {
	return g.Cells[x+WIDTH*y]
}

func (g *Grid) GetCellFromType(cellType string) *Cell {
	for _, c := range g.Cells {
		if c.Type == cellType {
			return c
		}
	}

	return nil
}

func (g *Grid) String() string {
	var output []string
	for _, c := range g.Cells {
		pos := c.GetPos()
		output = append(output, fmt.Sprintf("Cell X: %d // Cell Y: %d", pos.X, pos.Y))
		output = append(output, fmt.Sprintf("Cell Type: %s", c.Type))
	}
	return strings.Join(output, "\n-------------------------\n")
}

type Customer struct {
	Items []string
	Award int
}

func NewCustomer(items string, award int) *Customer {
	c := Customer{
		Items: strings.Split(items, "-"),
		Award: award,
	}
	return &c
}

type Game struct {
	Grid      Grid
	Player    Chef
	Partner   Chef
	Customers []*Customer
}

func NewGame() *Game {
	g := Game{
		Grid:    Grid{},
		Player:  Chef{},
		Partner: Chef{},
	}
	return &g
}

func main() {
	scanner := bufio.NewScanner(os.Stdin)
	scanner.Buffer(make([]byte, 1000000), 1000000)

	game := NewGame()

	// ALL CUSTOMERS INPUT: to ignore until Bronze
	var numAllCustomers int
	scanner.Scan()
	fmt.Sscan(scanner.Text(), &numAllCustomers)
	for i := 0; i < numAllCustomers; i++ {
		// customerItem: the food the customer is waiting for
		var customerItem string
		// customerAward: the number of points awarded for delivering the food
		var customerAward int
		scanner.Scan()
		fmt.Sscan(scanner.Text(), &customerItem, &customerAward)
	}

	// KITCHEN INPUT
	for i := 0; i < 7; i++ {
		scanner.Scan()
		kitchenLine := scanner.Text()
		game.Grid.AddRow(kitchenLine)
	}

	// game loop
	for {
		// Reset Items
		game.Grid.Items = nil
		// Reset customers
		game.Customers = nil

		var turnsRemaining int
		scanner.Scan()
		fmt.Sscan(scanner.Text(), &turnsRemaining)

		// PLAYERS INPUT
		scanner.Scan()
		fmt.Sscan(scanner.Text(), &game.Player.X, &game.Player.Y, &game.Player.Type)

		scanner.Scan()
		fmt.Sscan(scanner.Text(), &game.Partner.X, &game.Partner.Y, &game.Partner.Type)

		// numTablesWithItems: the number of tables in the kitchen that currently hold an item
		var numTablesWithItems int
		scanner.Scan()
		fmt.Sscan(scanner.Text(), &numTablesWithItems)

		for i := 0; i < numTablesWithItems; i++ {
			var tableX, tableY int
			var item string
			scanner.Scan()
			fmt.Sscan(scanner.Text(), &tableX, &tableY, &item)

			game.Grid.Items = append(game.Grid.Items, NewItem(tableX, tableY, item))
		}

		// ovenContents: ignore until wood 1 league
		var ovenContents string
		var ovenTimer int
		scanner.Scan()
		fmt.Sscan(scanner.Text(), &ovenContents, &ovenTimer)

		// CURRENT CUSTOMERS INPUT
		// numCustomers: the number of customers currently waiting for food
		var numCustomers int
		scanner.Scan()
		fmt.Sscan(scanner.Text(), &numCustomers)

		for i := 0; i < numCustomers; i++ {
			var customerItem string
			var customerAward int
			scanner.Scan()
			fmt.Sscan(scanner.Text(), &customerItem, &customerAward)

			game.Customers = append(game.Customers, NewCustomer(customerItem, customerAward))
		}

		// GAME LOGIC
		// fetch dish, then blueberries, then ice cream, then drop it at first empty table
		if game.Player.Type == NONE {
			dishwasher := game.Grid.GetCellFromType(DISH_WASHER).GetPos()
			fmt.Printf("USE %d %d; Go starter AI\n", dishwasher.X, dishwasher.Y)
		} else if game.Player.Type == DISH {
			blueberries := game.Grid.GetCellFromType(BLUEBERRY_CRATE).GetPos()
			fmt.Printf("USE %d %d; Go starter AI\n", blueberries.X, blueberries.Y)
		} else {
			emptyTable := game.Grid.GetCellFromType(TABLE).GetPos()
			fmt.Printf("USE %d %d; Go starter AI\n", emptyTable.X, emptyTable.Y)
		}
	}
}
