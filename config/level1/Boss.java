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
        int width = in.nextInt(); // the width of the kitchen
        int height = in.nextInt(); // the height of the kitchen
        int numTables = in.nextInt(); // the number of tables in the kitchen
        System.err.println("There are " + numTables + " tables");
        for (int i = 0; i < numTables; i++) {
            int x = in.nextInt();
            int y = in.nextInt();
            int equipmentType = in.nextInt();
        }

        // game loop
        while (true) {
            int playerX = in.nextInt();
            int playerY = in.nextInt();
            int playerItemType = in.nextInt();
            int playerItemDetail = in.nextInt();
            System.err.println("I am at " + playerX + ", " + playerY);
            int partnerX = in.nextInt();
            int partnerY = in.nextInt();
            int partnerItemType = in.nextInt();
            int partnerItemDetail = in.nextInt();
            System.err.println("Partner is at " + partnerX + ", " + partnerY);

            for (int i = 0; i < numTables; i++) {
                int tableX = in.nextInt();
                int tableY = in.nextInt();
//                System.err.println("Table is at " + tableX + ", " + tableY);
                int equipmentType = in.nextInt();
                int equipmentState = in.nextInt();
                int equipmentTimer = in.nextInt();
                int itemType = in.nextInt();
                int itemDetail = in.nextInt();
            }
            int numCustomers = in.nextInt(); // the number of customers currently waiting for food
//            System.err.println("There are " + numCustomers + " customers");

            for (int i = 0; i < numCustomers; i++) {
                int customerAward = in.nextInt(); // the number of points awarded for delivering the food
                int customerItem = in.nextInt(); // the food the customer is waiting for
//                System.err.println("Customer: " + customerAward + ", " + customerItem);

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