using System;
using System.Linq;
using System.IO;
using System.Text;
using System.Collections;
using System.Collections.Generic;

public class Game
{
    public Player[] Players = new Player[2];
    public Table Dishwasher;
    public Table Window;
    public Table Blueberry;
    public Table IceCream;
    public List<Table> Tables = new List<Table>();
}

public class Table
{
    public Position Position;
    public bool HasFunction;
    public Item Item;
}

public class Item
{
    public string Content;
    public bool HasPlate;
    public Item(string content){
        Content = content;
        HasPlate = Content.Contains(MainClass.Dish);
    }
}

public class Player
{
    public Position Position;
    public Item Item;
    public Player(Position position, Item item){
        Position = position;
        Item = item;
    }
    public void Update(Position position, Item item){
        Position = position;
        Item = item;
    }
}

public class Position
{
    public int X, Y;
    public Position(int x, int y){
        X = x;
        Y = y;
    }

    public int Manhattan(Position p2) => Math.Abs(X - p2.X) + Math.Abs(Y - p2.Y);

    public override string ToString()
    {
        return X + " " + Y;
    }
}



public class MainClass
{
    public static bool Debug = true;
    public const string Dish = "DISH";

    public static Game ReadGame(){
        var game = new Game();
        game.Players[0] = new Player(null, null);
        game.Players[1] = new Player(null, null);

        for (int i = 0; i < 7; i++)
        {
            string kitchenLine = ReadLine();
            for (var x = 0; x < kitchenLine.Length; x++){
                if (kitchenLine[x] == 'W') game.Window = new Table { Position = new Position(x, i), HasFunction = true };
                if (kitchenLine[x] == 'D') game.Dishwasher = new Table { Position = new Position(x, i), HasFunction = true };
                if (kitchenLine[x] == 'I') game.IceCream = new Table { Position = new Position(x, i), HasFunction = true };
                if (kitchenLine[x] == 'B') game.Blueberry = new Table { Position = new Position(x, i), HasFunction = true };
                if (kitchenLine[x] == '#') game.Tables.Add(new Table { Position = new Position(x, i) });
            }
        }

        return game;
    }

    private static void Move(Position p) => Console.WriteLine("MOVE " + p);

    private static void Use(Position p){
        Console.WriteLine("USE " + p + "; C# Starter AI");
    }

    private static string ReadLine(){
        var s = Console.ReadLine();
        if (Debug)
            Console.Error.WriteLine(s);
        return s;
    }


    static void Main()
    {
        string[] inputs;

        // ALL CUSTOMERS INPUT: to ignore until Bronze
        int numAllCustomers = int.Parse(ReadLine());
        for (int i = 0; i < numAllCustomers; i++)
        {
            inputs = ReadLine().Split(' ');
            string customerItem = inputs[0]; // the food the customer is waiting for
            int customerAward = int.Parse(inputs[1]); // the number of points awarded for delivering the food
        }

        // KITCHEN INPUT
        var game = ReadGame();

        while (true)
        {
            int turnsRemaining = int.Parse(ReadLine());

            // PLAYERS INPUT
            inputs = ReadLine().Split(' ');
            game.Players[0].Update(new Position(int.Parse(inputs[0]), int.Parse(inputs[1])), new Item(inputs[2]));
            inputs = ReadLine().Split(' ');
            game.Players[1].Update(new Position(int.Parse(inputs[0]), int.Parse(inputs[1])), new Item(inputs[2]));

            //Clean other tables
            foreach(var t in game.Tables){
                t.Item = null;
            }
            int numTablesWithItems = int.Parse(ReadLine()); // the number of tables in the kitchen that currently hold an item
            for (int i = 0; i < numTablesWithItems; i++)
            {
                inputs = ReadLine().Split(' ');
                var table = game.Tables.First(t => t.Position.X == int.Parse(inputs[0]) && t.Position.Y == int.Parse(inputs[1]));
                table.Item = new Item(inputs[2]);
            }

            inputs = ReadLine().Split(' ');
            string ovenContents = inputs[0]; // ignore until bronze league
            int ovenTimer = int.Parse(inputs[1]);
            int numCustomers = int.Parse(ReadLine()); // the number of customers currently waiting for food
            for (int i = 0; i < numCustomers; i++)
            {
                inputs = ReadLine().Split(' ');
                string customerItem = inputs[0];
                int customerAward = int.Parse(inputs[1]);
            }

            // GAME LOGIC
            // fetch a dish, pick ice cream and drop the dish on an empty table
            var myChef = game.Players[0];
            if (!myChef.Item?.HasPlate ?? false)
                Use(game.Dishwasher.Position);
            else if(!myChef.Item.Content.Contains("ICE_CREAM"))
                Use(game.IceCream.Position);
            // once ready, put it on the first empty table for now
            else
                Use(game.Tables.First(t => t.Item == null).Position);
        }
    }
}