#Begin Imports
import sys
import math
#End Imports

#Begin Util Code
def log(x):
    print(x, file=sys.stderr)

class Player:
    def __init__(self):
        self.x = 0
        self.y = 0
        self.item = NONE

class Tile:
    def __init__(self, x, y, name):
        self.x = x
        self.y = y
        self.name = name
        self.item = None
<<<<<<< HEAD
    
=======

>>>>>>> 066833a... update python starter
    def parse_name(self):
        return self.name.split("-")

    def __repr__(self):
        return "Tile: " + str(self.x) + ", " + str(self.y)

# Cells
BLUEBERRIES_CRATE = "B"
ICE_CREAM_CRATE = "I"
WINDOW = "W"
EMPTY_TABLE = "#"
DISHWASHER = "D"
FLOOR_CELL = "."

# Items
NONE = "NONE"
DISH = "DISH"
ICE_CREAM = "ICE_CREAM"
BLUEBERRIES = "BLUEBERRIES"

class Game:
    def __init__(self):
        self.player = Player()
        self.partner = Player()
        self.tiles = []

    def addTile(self, x, y, tileChar):
        if tileChar != '.':
            self.tiles.append(Tile(x, y, tileChar))

    def getTileByName(self, name):
        for t in self.tiles:
            if t.name == name:
                return t

        #If tile not found
        log("Error: Tile not found in function getTileByName")
<<<<<<< HEAD
        
=======

>>>>>>> 066833a... update python starter
    def getTileByItem(self, item):
        for t in self.tiles:
            if t.item == item:
                return t
<<<<<<< HEAD
        
        #If tile not found
        log("Error: Tile not found in function getTileByItem")
    
=======

        #If tile not found
        log("Error: Tile not found in function getTileByItem")

>>>>>>> 066833a... update python starter
    def getTileByCoords(self, x, y):
        for t in self.tiles:
            if t.x == x and t.y == y:
                return t

        #If tile not found
        log("Error: Tile not found in function getTileByCoords")

    def updatePlayer(self, x, y, item):
        self.player.x = x
        self.player.y = y
        self.player.item = item

    def updatePartner(self, x, y, item):
        self.partner.x = x
        self.partner.y = y
        self.partner.item = item

    def use(self, tile):
<<<<<<< HEAD
        print("USE", tile.x, tile.y,"; Python Starter AI")
    
=======
        print("USE", tile.x, tile.y)

>>>>>>> 066833a... update python starter
    def move(self, tile):
        print("MOVE", tile.x, tile.y)
#End Util code

#Begin game code
game = Game()

# ALL CUSTOMERS INPUT: to ignore until bronze
num_all_customers = int(input())
for i in range(num_all_customers):
    # customer_item: the food the customer is waiting for
    # customer_award: the number of points awarded for delivering the food
    customer_item, customer_award = input().split()
    customer_award = int(customer_award)

# KITCHEN INPUT
for y in range(7):
    kitchen_line = input()
    for x, tileChar in enumerate(kitchen_line):
        game.addTile(x, y, tileChar)

# game loop
while True:
    turns_remaining = int(input())

    # PLAYERS INPUT
    #Gather and update player information
    player_x, player_y, player_item = input().split()
    player_x = int(player_x)
    player_y = int(player_y)
    game.updatePlayer(player_x, player_y, player_item)

    #Gather and update partner information
    partner_x, partner_y, partner_item = input().split()
    partner_x = int(partner_x)
    partner_y = int(partner_y)
    game.updatePartner(partner_x, partner_y, partner_item)

    #Gather and update table information
    for t in game.tiles:        
        t.item = None
    num_tables_with_items = int(input())  # the number of tables in the kitchen that currently hold an item
    for i in range(num_tables_with_items):
        table_x, table_y, item = input().split()
        table_x = int(table_x)
        table_y = int(table_y)
        game.getTileByCoords(table_x, table_y).item = item

    # oven_contents: ignore until bronze league
    oven_contents, oven_timer = input().split()
    oven_timer = int(oven_timer)
    num_customers = int(input())  # the number of customers currently waiting for food
    for i in range(num_customers):
        customer_item, customer_award = input().split()
        customer_award = int(customer_award)

    # GAME LOGIC
    #Gather plate and Icecream
    if DISH not in game.player.item:
        game.use(game.getTileByName(DISHWASHER))
    elif ICE_CREAM not in game.player.item:
        game.use(game.getTileByName(ICE_CREAM_CRATE))
    else:
        game.use(game.getTileByName(EMPTY_TABLE))
#End game code