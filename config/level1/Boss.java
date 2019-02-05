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
        for (int i = 0; i < numTables; i++) {
            int x = in.nextInt();
            int y = in.nextInt();
            String equipment = in.next();
        }

        // game loop
        while (true) {
            int playerX = in.nextInt();
            int playerY = in.nextInt();
            String playerItem = in.next();
            int partnerX = in.nextInt();
            int partnerY = in.nextInt();
            String partnerItem = in.next();
            for (int i = 0; i < numTables; i++) {
                int tableX = in.nextInt();
                int tableY = in.nextInt();
                String equipment = in.next();
                String item = in.next();
            }
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