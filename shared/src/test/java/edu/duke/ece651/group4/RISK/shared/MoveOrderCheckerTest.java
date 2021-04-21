package edu.duke.ece651.group4.RISK.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;

import edu.duke.ece651.group4.RISK.shared.MoveOrderChecker.*;

public class MoveOrderCheckerTest {
    private final String NOT_MOVE_ORDER_MSG = "This is not a move order.";
    private final String NOT_SAME_OWNER_MSG =
         "Cannot move troop to %s, which belongs to another player who is not your ally.";
    private final String NOT_REACHABLE_MSG =
        "Cannot reach from %s to %s. " +
        "Other players' territories are blocking the way.";

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

    MoveOrderChecker moc = new MoveOrderChecker();

    /**
     * Creates a world for test. 
     * Territory layout is the same as that on Evolution 1 requirements.
     * N-----M--O--G
     * |   /  |/ \ | 
     * |  /   S ---M     
     * |/   / |  \ |
     * E------R----H
     * Can specify territory names and troops stationed on the territories.
     * @param troops is the troops on the territories.
     * @return a world object.
     */
    public World createWorld(Troop... troops) {
        World world = new World();
        for (String name: names) {
            world.addTerritory(new Territory(name));
        }
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
        world.addConnection("Roshar", "Hogwarts");

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
    public void testValid() {
        World world = createWorldAndRegister(troopsConnected);

        BasicOrder order1 = new BasicOrder("Narnia", "Midkemia", new Troop(3, green), 'M');
        assertNull(moc.checkMyOrder(order1, world));

        BasicOrder order2 = new BasicOrder("Narnia", "Oz", new Troop(3, green), 'M');
        assertNull(moc.checkMyOrder(order2, world));
    }

    @Test
    public void testNotSameOwner() {
        World world = createWorldAndRegister(troopsConnected);

        BasicOrder order1 = new BasicOrder("Narnia", "Roshar", new Troop(3, green), 'M');
        assertEquals(String.format(NOT_SAME_OWNER_MSG, "Roshar"), 
                        moc.checkMyOrder(order1, world));

        BasicOrder order2 = new BasicOrder("Oz", "Gondor", new Troop(3, green), 'M');
        assertEquals(String.format(NOT_SAME_OWNER_MSG, "Gondor"), 
                         moc.checkMyOrder(order2, world));
    }

    @Test
    public void testNotMoveOrder() {
        World world = createWorldAndRegister(troopsConnected);

        BasicOrder order1 = new BasicOrder("Narnia", "Midkemia", new Troop(3, green), 'A');
        assertEquals(NOT_MOVE_ORDER_MSG, moc.checkMyOrder(order1, world));

        BasicOrder order2 = new BasicOrder("Narnia", "Oz", new Troop(3, green), 'D');
        assertEquals(NOT_MOVE_ORDER_MSG, moc.checkMyOrder(order2, world));
    }

    @Test
    public void testNotReachable() {
        World world = createWorldAndRegister(troopsSeparated);
        
        BasicOrder order1 = new BasicOrder("Roshar", "Hogwarts", new Troop(3, green), 'M');
        assertNull(moc.checkMyOrder(order1, world));
        
        BasicOrder order2 = new BasicOrder("Roshar", "Oz", new Troop(3, green), 'M');
        assertEquals(String.format(NOT_REACHABLE_MSG, "Roshar", "Oz"), 
                    moc.checkMyOrder(order2, world));

        BasicOrder order3 = new BasicOrder("Gondor", "Mordor", new Troop(3, red), 'M');
        assertNull(moc.checkMyOrder(order3, world));

        BasicOrder order4 = new BasicOrder("Midkemia", "Mordor", new Troop(3, red), 'M');
        assertEquals(String.format(NOT_REACHABLE_MSG, "Midkemia", "Mordor"), 
                    moc.checkMyOrder(order4, world));
    }

    @Test
    public void testAlliance() {
        World world = createWorldAndRegister(troopsSeparated);
        world.registerPlayer(redInfo);
        world.registerPlayer(blueInfo);
        world.registerPlayer(greenInfo);
        world.tryFormAlliance("red","blue");
        world.tryFormAlliance("blue","red");
        world.doCheckIfAllianceSuccess();
        assertEquals(new HashSet<>(Arrays.asList("blue")), world.getAllianceNames("red"));

        MoveOrder order = new MoveOrder("Midkemia", "Mordor", new Troop(3, red), 'M');
        assertNull(moc.checkMyOrder(order, world));
    }
}
