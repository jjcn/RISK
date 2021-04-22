package edu.duke.ece651.group4.RISK.shared;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

public class OrderCheckerTest {
    /**
     * Error Messages
     */
    protected final String UNKNOWN_BASIC_ORDER_TYPE_MSG =
            "'%c' is not a valid basic order type.";
    protected final String NO_TROOP_STATIONED_MSG =
            "Error: You do not have troop stationed on %s.";
    protected final String NOT_ENOUGH_TROOP_MSG =
            "Cannot move out a troop of size larger than %d on %s, " +
                    "but you entered a troop of size %d.";

    PrintStream out = null;
    Reader inputReader = null;
    Player green = new TextPlayer(out, inputReader, "green");
    Player red = new TextPlayer(out, inputReader, "red");
    Player blue = new TextPlayer(out, inputReader, "blue");

    PlayerInfo greenInfo = new PlayerInfo(green.getName(), 100, 100);
    PlayerInfo redInfo = new PlayerInfo(red.getName(), 100, 100);
    PlayerInfo blueInfo = new PlayerInfo(blue.getName(), 100, 100);

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
     * @param troops is the corresponding troops on the territories.
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

    public World createWorldAndRegister(Troop... troop) {
        World world = createWorld(troop);
        world.registerPlayer(redInfo);
        world.registerPlayer(blueInfo);
        world.registerPlayer(greenInfo);
        return world;
    }

    @Test
    public void testMoveOrderCheckerValid() {
        World world = createWorldAndRegister(troopsConnected);

        BasicOrder order1 = new BasicOrder("Narnia", "Midkemia", new Troop(3, green), 'M');
        assertEquals(null, oc.checkOrder(order1, "green", world));

        BasicOrder order2 = new BasicOrder("Narnia", "Oz", new Troop(3, green), 'M');
        assertEquals(null, oc.checkOrder(order2, "green", world));
    }

    @Test
    public void testAttackOrderCheckerValid() {
        World world = createWorldAndRegister(troopsConnected);

        BasicOrder order1 = new BasicOrder("Narnia", "Elantris", new Troop(3, green), 'A');
        assertEquals(null, oc.checkOrder(order1, "green", world));

        BasicOrder order2 = new BasicOrder("Scadrial", "Mordor", new Troop(3, blue), 'A');
        assertEquals(null, oc.checkOrder(order2, "blue", world));
    }


    @Test
    public void testOrderNotYourTroop() {
        World world = createWorldAndRegister(troopsConnected);

        BasicOrder order1_red = new BasicOrder("Narnia", "Midkemia", new Troop(3, red), 'M');
        BasicOrder order1_blue = new BasicOrder("Narnia", "Midkemia", new Troop(3, blue), 'M');
        assertEquals(String.format(NO_TROOP_STATIONED_MSG, "Narnia", "green"),
                        oc.checkOrder(order1_red, "red", world));
        assertEquals(String.format(NO_TROOP_STATIONED_MSG, "Narnia", "green"),
                        oc.checkOrder(order1_blue, "blue", world));

        BasicOrder order2_red = new BasicOrder("Scadrial", "Mordor", new Troop(3, red), 'A');
        BasicOrder order2_green = new BasicOrder("Scadrial", "Mordor", new Troop(3, green), 'A');
        assertEquals(String.format(NO_TROOP_STATIONED_MSG, "Scadrial", "blue"),
                        oc.checkOrder(order2_red, "red", world));
        assertEquals(String.format(NO_TROOP_STATIONED_MSG, "Scadrial", "blue"),
                        oc.checkOrder(order2_green, "green", world));
    }

    @Test
    public void testOrderNotEnoughTroop() {
        World world = createWorldAndRegister(troopsConnected);

        BasicOrder order1 = new BasicOrder("Narnia", "Midkemia", new Troop(11, green), 'M');
        assertEquals(String.format(NOT_ENOUGH_TROOP_MSG, 
                                    world.findTerritory("Narnia").checkPopulation(), 
                                    "Narnia", 11), 
                    oc.checkOrder(order1, "green", world));

        BasicOrder order2 = new BasicOrder("Scadrial", "Mordor", new Troop(6, blue), 'A');
        assertEquals(String.format(NOT_ENOUGH_TROOP_MSG, 
                                    world.findTerritory("Scadrial").checkPopulation(), 
                                    "Scadrial", 6), 
                    oc.checkOrder(order2, "blue", world));
    }

    @Test
    public void testOrderNotBasicOrderType() {
        World world = createWorldAndRegister(troopsConnected);

        BasicOrder order1 = new BasicOrder("Narnia", "Midkemia", new Troop(3, green), 'J');
        assertEquals(String.format(UNKNOWN_BASIC_ORDER_TYPE_MSG, 'J'),
                        oc.checkOrder(order1, "green", world));

        BasicOrder order2 = new BasicOrder("Narnia", "Midkemia", new Troop(3, green), 'q');
        assertEquals(String.format(UNKNOWN_BASIC_ORDER_TYPE_MSG, 'q'),
                        oc.checkOrder(order2, "green", world));
    }
}
