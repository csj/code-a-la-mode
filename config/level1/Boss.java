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
        int width = in.nextInt(); // the width of OUR SIDE of the restaurant
        int height = in.nextInt(); // the height of the restaurant
        int numOurTables = in.nextInt(); // the number of tables accessible to us
        for (int i = 0; i < numOurTables; i++) {
            int x = in.nextInt();
            int y = in.nextInt();
            int equipment = in.nextInt();
        }

        // game loop
        while (true) {
            for (int i = 0; i < 4; i++) {
                int playerX = in.nextInt(); // when x>0, our side of the restaurant. x<0, enemy side
                int playerY = in.nextInt();
                int playerCarry = in.nextInt();
                int playerType = in.nextInt();
            }
            int numAllTables = in.nextInt(); // the total number of tables in the restaurant
            for (int i = 0; i < numAllTables; i++) {
                int tableX = in.nextInt(); // when x>0, our side of the restaurant. x<0, enemy side
                int tableY = in.nextInt();
                int equipment = in.nextInt();
                int item = in.nextInt(); // the food the customer is waiting for
            }
            int numCustomers = in.nextInt(); // the number of customers currently waiting for food
            for (int i = 0; i < numCustomers; i++) {
                int award = in.nextInt(); // the number of points awarded for delivering the food
                int item = in.nextInt();
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // MOVE x y
            // USE x y
            // TAKE x y
            // DROP x y
            // WAIT
            System.out.println("WAIT");
        }
    }
}