package edu.duke.ece651.group4.RISK.shared;

import java.io.PrintStream;
import java.io.Reader;

public class WorldTestHelper {
    /**
     * Error messages
     */
    final String INDIVISIBLE_MSG = "Number of territories is not divisible by number of groups.";
    final String TERRITORY_NOT_FOUND_MSG = "The territory specified by the name '%s' is not found.";
    final String NOT_POSITIVE_MSG = "Number should be positive.";
    // for general order
    protected final String NOT_YOUR_TROOP_MSG =
            "Error: You tried to move troops on %s, which belongs to another player: %s";
    protected final String UNKNOWN_BASIC_ORDER_TYPE =
            "'%c' is not a valid basic order type.";
    protected final String NOT_ENOUGH_TROOP_MSG =
            "Cannot move out a troop of size larger than %d on %s, " +
                    "but you entered a troop of size %d.";
    // for move
    private final String NOT_SAME_OWNER_MSG =
            "Cannot move troop to %s, which belongs to another player.";
    private final String NOT_REACHABLE_MSG =
            "Cannot reach from %s to %s. " +
                    "Other players' territories are blocking the way.";
    // for attack
    protected final String SAME_OWNER_MSG =
            "Cannot attack %s, which belongs to you.";
    protected final String NOT_ADJACENT_MSG =
            "You tried to attack from %s to %s, which are not adjacent territories. %n" +
                    "You can only attack territories directly adjacent to your territories.";

    public World createWorldSimple() {
        World world = new World();
        Territory t1 = new Territory("1");
        Territory t2 = new Territory("2");
        world.addTerritory(t1);
        world.addTerritory(t2);
        world.addConnection(t1, t2);

        return world;
    }

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
    int areas[] = {10, 12, 8, 13, 14, 3, 5, 6, 3};
    Troop troopsConnected[] = {new Troop(10, green), new Troop(12, green), new Troop(8, green),
            new Troop(13, red), new Troop(14, red), new Troop(3, red),
            new Troop(5, blue), new Troop(6, blue), new Troop(3, blue)};
    Troop troopsSeparated[] = {new Troop(10, green), new Troop(12, red), new Troop(8, green),
            new Troop(13, red), new Troop(14, red), new Troop(3, green),
            new Troop(5, blue), new Troop(6, blue), new Troop(3, green)};
    Troop troopsSamePlayer[] = {new Troop(10, red), new Troop(12, red), new Troop(8, red),
            new Troop(13, red), new Troop(14, red), new Troop(3, red),
            new Troop(5, red), new Troop(6, red), new Troop(3, red)};

    /**
     * Creates a world for test.
     * Territory layout is the same as that on Evolution 1 requirements.
     * N-----M--O--G
     * |   /  |/ \ |
     * |  /   S ---M
     * |/   / |  \ |
     * E------R----H
     * Can specify territory names and troops stationed on the territories.
     * @param troops is the corresponding troops on these territories.
     * @return a world object.
     */
    public World createWorld(Troop... troops) {
        World world = new World();
        for (int i = 0; i < names.length; i++) {
            world.addTerritory(new Territory(names[i], areas[i]));
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

    /**
     * Helper function that creates a world and register player info of red, blue and green.
     */
    public World createWorldAndRegister(Troop... troops) {
        World world = createWorld(troops);
        world.registerPlayer(redInfo);
        world.registerPlayer(blueInfo);
        world.registerPlayer(greenInfo);
        return world;
    }
}
