package edu.duke.ece651.group4.RISK.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.*;

public class MoveOrderCheckerTest {
    private final String NOT_SAME_OWNER_MSG = "Cannot move troop to a territory with different owner.";
    private final String NOT_MOVE_ORDER_MSG = "This is not a move order.";
    private final String NOT_LINKED_MSG = "There is not a path of territories that all belongs to you.";

    PrintStream out = null;
    Reader inputReader = null;
    Player green = new TextPlayer(out, inputReader, "green");
    Player red = new TextPlayer(out, inputReader, "red");
    Player blue = new TextPlayer(out, inputReader, "blue");

    String names[] = 
            "Narnia, Midkemia, Oz, Gondor, Mordor, Hogwarts, Scadrial, Elantris, Roshar".split(", ");
    Troop troopsConnected[] = {new Troop(10, green), new Troop(12, green), new Troop(8, green),
                        new Troop(13, red), new Troop(14, red), new Troop(3, red),
                        new Troop(5, blue), new Troop(6, blue),new Troop(3, blue)};
    Troop troopsSeparated[] = {new Troop(10, green), new Troop(12, red), new Troop(8, green),
                        new Troop(13, red), new Troop(14, red), new Troop(3, green),
                        new Troop(5, blue), new Troop(6, blue),new Troop(3, green)};
    /**
     * Creates a world for test. Same as the one on Evolution 1 requirements.
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
    public void testMoveOrderCheckerValid() {
        World world = createWorld(names, troopsConnected);
        MoveOrderChecker<Territory> moc = new MoveOrderChecker<>(null);

        BasicOrder order1 = new BasicOrder("Narnia", "Midkemia", new Troop(3, green), 'M');
        assertEquals(null, moc.checkMyOrder(order1, world));

        BasicOrder order2 = new BasicOrder("Narnia", "Oz", new Troop(3, green), 'M');
        assertEquals(null, moc.checkMyOrder(order2, world));
    }

    @Test
    public void testMoveOrderCheckerNotSameOwner() {
        World world = createWorld(names, troopsConnected);
        MoveOrderChecker<Territory> moc = new MoveOrderChecker<>(null);

        BasicOrder order1 = new BasicOrder("Narnia", "Roshar", new Troop(3, green), 'M');
        assertEquals(NOT_SAME_OWNER_MSG, moc.checkMyOrder(order1, world));

        BasicOrder order2 = new BasicOrder("Oz", "Gondor", new Troop(3, green), 'M');
        assertEquals(NOT_SAME_OWNER_MSG, moc.checkMyOrder(order2, world));
    }

    @Test
    public void testMoveOrderCheckerNotMove() {
        World world = createWorld(names, troopsConnected);
        MoveOrderChecker<Territory> moc = new MoveOrderChecker<>(null);

        BasicOrder order1 = new BasicOrder("Narnia", "Midkemia", new Troop(3, green), 'A');
        assertEquals(NOT_MOVE_ORDER_MSG, moc.checkMyOrder(order1, world));

        BasicOrder order2 = new BasicOrder("Narnia", "Oz", new Troop(3, green), 'D');
        assertEquals(NOT_MOVE_ORDER_MSG, moc.checkMyOrder(order2, world));
    }

    @Test
    public void testMoveOrderCheckerNotLinked() {
        World world = createWorld(names, troopsSeparated);
        MoveOrderChecker<Territory> moc = new MoveOrderChecker<>(null);

        BasicOrder order1 = new BasicOrder("Roshar", "Hogwarts", new Troop(3, green), 'M');
        assertEquals(null, moc.checkMyOrder(order1, world));

        BasicOrder order2 = new BasicOrder("Roshar", "Oz", new Troop(3, green), 'M');
        assertEquals(NOT_LINKED_MSG, moc.checkMyOrder(order2, world));
    }
}
