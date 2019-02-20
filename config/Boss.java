import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        int numAllCustomers = in.nextInt();
        for (int i=0; i<numAllCustomers; i++) {
            String customerItem = in.next(); // the food the customer is waiting for
            int customerAward = in.nextInt(); // the number of points awarded for delivering the food
        }

        int width = 11; //in.nextInt(); // the width of the kitchen
        int height = 7; //in.nextInt(); // the height of the kitchen
        for (int i = 0; i < height; i++) {
            String kitchenLine = in.next();
        }

        // game loop
        while (true) {
            int turnsRemaining = in.nextInt();

            int playerX = in.nextInt();
            int playerY = in.nextInt();
            String playerItem = in.next();
            int partnerX = in.nextInt();
            int partnerY = in.nextInt();
            String partnerItem = in.next();

            int numTablesWithItems
            for (int i = 0; i < numTablesWithItems; i++) {
                int tableX = in.nextInt();
                int tableY = in.nextInt();
                String item = in.next();
            }

            String ovenContents = in.next();
            int ovenTimer = in.nextInt();

            int numCustomers = in.nextInt(); // the number of customers currently waiting for food
            for (int i = 0; i < numCustomers; i++) {
                String customerItem = in.next(); // the food the customer is waiting for
                int customerAward = in.nextInt(); // the number of points awarded for delivering the food
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // MOVE x y
            // USE x y
            // WAIT
            System.out.println("WAIT");
        }
    }
}