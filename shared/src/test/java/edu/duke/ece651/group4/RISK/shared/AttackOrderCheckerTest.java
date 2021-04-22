package edu.duke.ece651.group4.RISK.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.*;

public class AttackOrderCheckerTest {
    protected final String NOT_ATTACK_ORDER_MSG = "This is not an attack order.";
    protected final String SAME_OWNER_MSG =
            "Cannot attack %s, which belongs to you.";
    protected final String MELEE_CANT_ATTACK_MSG =
            "Your melee units tried to attack %s from %s, which are not adjacent territories. %n" +
                    "Melee units can only attack territories directly adjacent to your territories.";
    protected final String RANGED_OUT_OF_REACH_MSG =
            "Your ranged units tried to attack %s from %s, which is out of reach. %n" +
                    "Ranged units can only attack territories within their attack range.";

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
                        new Troop(5, blue), new Troop(6, blue),new Troop(3, blue)};
    Troop troopsSeparated[] = {new Troop(10, green), new Troop(12, red), new Troop(8, green),
                        new Troop(13, red), new Troop(14, red), new Troop(3, green),
                        new Troop(5, blue), new Troop(6, blue),new Troop(3, green)};

    AttackOrderChecker aoc = new AttackOrderChecker();

    /**
     * Creates a world for test. 
     * Territory layout is the same as that on Evolution 1 requirements.
     * Can specify territory names and troops stationed on the territories.
     * @param names is an array of territory names. 
     * @param troops is the corresponding troops on these territories.
     * @return a world object.
     */
    public World createWorld(String names[], Troop troops[]) {
        World world = new World();
        for (String name: names) {
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

        for (int i = 0; i < names.length; i++) {
            world.stationTroop(names[i], troops[i]);
        }
        return world;
    }

    @Test
    public void testAttackOrderCheckerValid() {
        World world = createWorld(names, troopsConnected);

        BasicOrder order1 = new BasicOrder("Narnia", "Elantris", new Troop(3, green), 'A');
        assertEquals(null, aoc.checkMyOrder(order1, world));

        BasicOrder order2 = new BasicOrder("Scadrial", "Mordor", new Troop(3, blue), 'A');
        assertEquals(null, aoc.checkMyOrder(order2, world));
    }

    @Test
    public void testAttackOrderCheckerSameOwner() {
        World world = createWorld(names, troopsConnected);   

        BasicOrder order1 = new BasicOrder("Narnia", "Midkemia", new Troop(3, green), 'A');
        assertEquals(String.format(SAME_OWNER_MSG, "Midkemia"), 
                    aoc.checkMyOrder(order1, world));

        BasicOrder order2 = new BasicOrder("Gondor", "Mordor", new Troop(3, red), 'A');
        assertEquals(String.format(SAME_OWNER_MSG, "Mordor"), 
                    aoc.checkMyOrder(order2, world));
    }

    @Test
    public void testAttackOrderCheckerNotAttackOrder() {
        World world = createWorld(names, troopsConnected);

        BasicOrder order1 = new BasicOrder("Narnia", "Elantris", new Troop(3, green), 'M');
        assertEquals(NOT_ATTACK_ORDER_MSG, aoc.checkMyOrder(order1, world));

        BasicOrder order2 = new BasicOrder("Scadrial", "Mordor", new Troop(3, blue), 'D');
        assertEquals(NOT_ATTACK_ORDER_MSG, aoc.checkMyOrder(order2, world));
    }

    @Test
    public void testAttackOrderCheckerMelee() {
        World world = createWorld(names, troopsConnected);

        BasicOrder order1 = new BasicOrder("Narnia", "Scadrial", new Troop(3, green), 'A');

        assertEquals(String.format(MELEE_CANT_ATTACK_MSG, "Scadrial", "Narnia"),
                    aoc.checkMyOrder(order1, world));

        BasicOrder order2 = new BasicOrder("Scadrial", "Gondor", new Troop(3, blue), 'A');
        assertEquals(String.format(MELEE_CANT_ATTACK_MSG, "Gondor", "Scadrial"),
                    aoc.checkMyOrder(order2, world));
    }

    @Test
    public void testAttackOrderCheckerRanged() {
        // TODO
    }
}
