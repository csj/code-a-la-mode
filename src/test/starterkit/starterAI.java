import java.util.*;
import java.io.*;

class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // ALL CUSTOMERS INPUT: to ignore until Bronze
        int numAllCustomers = in.nextInt();
        for (int i = 0; i < numAllCustomers; i++) {
            String customerItem = in.next();
            int customerAward = in.nextInt();
        }

        // KITCHEN INPUT
        Kitchen k = new Kitchen();
        k.init(in);

        // game loop
        while (true) {
            int turnsRemaining = in.nextInt();

            // PLAYERS INPUT
            int playerX = in.nextInt();
            int playerY = in.nextInt();
            String playerItem = in.next();
            int partnerX = in.nextInt();
            int partnerY = in.nextInt();
            String partnerItem = in.next();

            int numTablesWithItems = in.nextInt();
            for (int i = 0; i < numTablesWithItems; i++) {
                int tableX = in.nextInt();
                int tableY = in.nextInt();
                String item = in.next();
            }
            // oven to ignore until bronze
            String ovenContents = in.next();
            int ovenTimer = in.nextInt();

            // CURRENT CUSTOMERS INPUT
            int numCustomers = in.nextInt();
            for (int i = 0; i < numCustomers; i++) {
                String customerItem = in.next();
                int customerAward = in.nextInt();
            }

            // GAME LOGIC
            // fectch dish, then blueberries, then ice cream
            if (!playerItem.contains("DISH")) {
                Table table = k.tables.stream().filter(t -> t.isDish()).findFirst().get();
                table.use();
            }
            else if (!playerItem.contains("BLUEBERRIES")) {
                k.tables.stream().filter(t -> t.isBlueBerries()).findFirst().get().use();
            }
            else if (!playerItem.contains("ICE_CREAM")) {
                k.tables.stream().filter(t -> t.isIceCream()).findFirst().get().use();
            }
            else {
                k.tables.stream().filter(t -> t.isWindow()).findFirst().get().use();
            }
        }
    }
}

class Kitchen {

    List<Table> tables;

    public Kitchen() {
        tables = new ArrayList<Table>();
    }

    public void init(Scanner in) {
        in.nextLine();
        for (int i = 0; i < 7; i++) {
            String kitchenLine = in.nextLine();
            for (int j = 0; j < kitchenLine.length(); j++) {
                char c = kitchenLine.charAt(j);
                TableType t = TableType.get(c);
                if (t != null) {
                    tables.add(new Table(t, j, i));
                }
            }
            System.err.println(kitchenLine);
        }
    }
}

class Position {

    int x, y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // @TODO
    public int distance(Position p) {
        return 0;
    }
}

class Table {

    TableType type;

    int x, y;

    public Table(TableType t, int x, int y) {
        type = t;
        this.x = x;
        this.y = y;
    }

    public void error() {
        System.err.println("Table of type "+type+" at "+x+" "+y);
    }

    public boolean isDish() {
        System.err.println(type);
        return isTableType(TableType.PLATES);
    }

    public boolean isBlueBerries() {
        return isTableType(TableType.BLUEBERRY);
    }

    public boolean isIceCream() {
        return isTableType(TableType.ICE_CREAM);
    }

    public boolean isWindow() {
        return isTableType(TableType.WINDOW);
    }

    private boolean isTableType(TableType t) {
        System.err.println(type.equals(t));
        return type.equals(t);
    }

    public void use() {
        System.out.println("USE "+x+" "+y+" my message");
    }
}

enum TableType {

    BLUEBERRY,
    ICE_CREAM,
    PLATES,
    WINDOW;

    static TableType get(Character c) {
        switch (c) {
            case 'D':
                return PLATES;
            case 'B':
                return BLUEBERRY;
            case 'I':
                return ICE_CREAM;
            case 'W':
                return WINDOW;

        }
        return null;
    }
}