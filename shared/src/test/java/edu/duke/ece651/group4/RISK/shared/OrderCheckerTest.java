package edu.duke.ece651.group4.RISK.shared;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

public class OrderCheckerTest {
    /**
     * Error Messages
     */
    protected final String NOT_YOUR_TROOP_MSG = 
        "Error: You tried to move troops on %s, which belongs to another player: %s";
    protected final String UNKNOWN_BASIC_ORDER_TYPE = 
        "'%c' is not a valid basic order type.";
    protected final String NOT_ENOUGH_TROOP_MSG = 
        "Cannot move out a troop of size larger than %d on %s, " +
        "but you entered a troop of size %d.";

    PrintStream out = null;
    Reader inputReader = null;
    Player green = new TextPlayer(out, inputReader, "green");
    Player red = new TextPlayer(out, inputReader, "red");
    Player blue = new TextPlayer(out, inputReader, "blue");

    String names[] = 
            "Narnia, Midkemia, Oz, Gondor, Mordor, Hogwarts, Scadrial, Elantris, Roshar".split(", ");
    Troop troopsConnected[] = {new Troop(10, green), new Troop(12, green), new Troop(8, green),
                        new Troop(13, red), new Troop(14, red), new Troop(3, red),
                        new Troop(5, blue), new Troop(6, blue), new Troop(3, blue)};
    Troop troopsSeparated[] = {new Troop(10, green), new Troop(12, red), new Troop(8, green),
                        new Troop(13, red), new Troop(14, red), new Troop(3, green),
                        new Troop(5, blue), new Troop(6, blue), new Troop(3, green)};

    OrderChecker oc = new OrderChecker();

    /**
     * Creates a world for test. 
     * Territory layout is the same as that on Evolution 1 requirements.
     * N-----M--O--G
     * |   /  |/ \ | 
     * |  /   S ---M     
     * |/   / |  \ |
     * E------R----H
     * Can specify territory names and troops stationed on the territories.
     * @param names is an array of territory names. 
     * @param troops is the corresponding troops on these territories.
     * @return a world object.
     */
    public World createWorld(Troop... troops) {
        World world = new World();
        for (String name : names) {
            world.addTerritory(new Territory(name));
        }
        world.addConnection("Narnia", "Midkemia");
        world.addConnection("Narnia", "Midkemia");
        world.addConnection("Narnia", "Elantris");
        world.addConnection("Midkemia", "Elantris");
        world.addConnection("Midkemia", "Scadrial");
        world.addConnection("Midkemia", "Oz");
        world.addConnection("Oz", "Scadrial");
        world.addConnection("Oz", "Mordor");
        world.addConnection("Oz", "Gondor");
        world.addConnection("Gondor", "Mordor");
        world.addConnection("Elantris", "Scadrial");
        world.addConnection("Elantris", "Roshar");
        world.addConnection("Scadrial", "Roshar");
        world.addConnection("Scadrial", "Hogwarts");
        world.addConnection("Scadrial", "Mordor");
        world.addConnection("Mordor", "Hogwarts");

        for (int i = 0; i < troops.length; i++) {
            world.stationTroop(names[i], troops[i]);
        }
        return world;
    }

    @Test
    public void testMoveOrderCheckerValid() {
        World world = createWorld(troopsConnected);

        BasicOrder order1 = new BasicOrder("Narnia", "Midkemia", new Troop(3, green), 'M');
        assertEquals(null, oc.checkOrder(order1, world));

        BasicOrder order2 = new BasicOrder("Narnia", "Oz", new Troop(3, green), 'M');
        assertEquals(null, oc.checkOrder(order2, world));
    }

    @Test
    public void testAttackOrderCheckerValid() {
        World world = createWorld(troopsConnected);

        BasicOrder order1 = new BasicOrder("Narnia", "Elantris", new Troop(3, green), 'A');
        assertEquals(null, oc.checkOrder(order1, world));

        BasicOrder order2 = new BasicOrder("Scadrial", "Mordor", new Troop(3, blue), 'A');
        assertEquals(null, oc.checkOrder(order2, world));
    }


    @Test
    public void testOrderNotYourTroop() {
        World world = createWorld(troopsConnected);

        BasicOrder order1_red = new BasicOrder("Narnia", "Midkemia", new Troop(3, red), 'M');
        BasicOrder order1_blue = new BasicOrder("Narnia", "Midkemia", new Troop(3, blue), 'M');
        assertEquals(String.format(NOT_YOUR_TROOP_MSG, "Narnia", "green"),
                        oc.checkOrder(order1_red, world));
        assertEquals(String.format(NOT_YOUR_TROOP_MSG, "Narnia", "green"), 
                        oc.checkOrder(order1_blue, world));

        BasicOrder order2_red = new BasicOrder("Scadrial", "Mordor", new Troop(3, red), 'A');
        BasicOrder order2_green = new BasicOrder("Scadrial", "Mordor", new Troop(3, green), 'A');
        assertEquals(String.format(NOT_YOUR_TROOP_MSG, "Scadrial", "blue"), 
                        oc.checkOrder(order2_red, world));
        assertEquals(String.format(NOT_YOUR_TROOP_MSG, "Scadrial", "blue"), 
                        oc.checkOrder(order2_green, world));
    }

    @Test
    public void testOrderNotEnoughTroop() {
        World world = createWorld(troopsConnected);

        BasicOrder order1 = new BasicOrder("Narnia", "Midkemia", new Troop(11, green), 'M');
        assertEquals(String.format(NOT_ENOUGH_TROOP_MSG, 
                                    world.findTerritory("Narnia").checkPopulation(), 
                                    "Narnia", 11), 
                    oc.checkOrder(order1, world));

        BasicOrder order2 = new BasicOrder("Scadrial", "Mordor", new Troop(6, blue), 'A');
        assertEquals(String.format(NOT_ENOUGH_TROOP_MSG, 
                                    world.findTerritory("Scadrial").checkPopulation(), 
                                    "Scadrial", 6), 
                    oc.checkOrder(order2, world));
    }

    @Test
    public void testOrderNotBasicOrderType() {
        World world = createWorld(troopsConnected);

        BasicOrder order1 = new BasicOrder("Narnia", "Midkemia", new Troop(3, green), 'J');
        assertEquals(String.format(UNKNOWN_BASIC_ORDER_TYPE, 'J'), 
                        oc.checkOrder(order1, world));

        BasicOrder order2 = new BasicOrder("Narnia", "Midkemia", new Troop(3, green), 'q');
        assertEquals(String.format(UNKNOWN_BASIC_ORDER_TYPE, 'q'), 
                        oc.checkOrder(order2, world));
    }
}
