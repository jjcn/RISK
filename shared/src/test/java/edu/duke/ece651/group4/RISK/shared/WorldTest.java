package edu.duke.ece651.group4.RISK.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

public class WorldTest {
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

    String names[] = 
            "Narnia, Midkemia, Oz, Gondor, Mordor, Hogwarts, Scadrial, Elantris, Roshar".split(", ");
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
     * Creates a test world. Troop not specified. Same as the one on Evolution 1 requirements.
     */
    public World createWorld(Troop... troops) {
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

        for (int i = 0; i < troops.length; i++) {
            world.stationTroop(names[i], troops[i]);
        }
        
        return world;
    }

    @Test
    public void testCreation() {
        World randomWorldWithSeed = new World(9, new Random(0));
        World randomWorld = new World(9);
    }

    @Test
    public void testClone() {
        World world = createWorldSimple();
        World worldClone = world.clone();

        assertEquals(world, worldClone);
        assertEquals(worldClone.getAllTerritories(), 
            new ArrayList<Territory>(Arrays.asList(new Territory("1"), new Territory("2"))));
        assertEquals(worldClone.getAdjacents("1"), 
            new ArrayList<Territory>(Arrays.asList(new Territory("2"))));
    }

    @Test
    public void testSetRandom() {
        World world = createWorldSimple();
        Random seed = new Random(0);

        world.setRandom("1", seed);
        world.setRandom(new Territory("2"), seed);
    }

    @Test
    public void testAddTerritory() {
        World world = new World();
        List<Territory> expected = new ArrayList<>();
        Territory t1 = new Territory("1");
        Territory t2 = new Territory("2");
        Territory t3 = new Territory("3");

        world.addTerritory(t1);
        expected.add(new Territory("1"));
        assertTrue(world.getAllTerritories().containsAll(expected));

        world.addTerritory(t2);
        expected.add(new Territory("2"));
        assertTrue(world.getAllTerritories().containsAll(expected));
        
        world.addTerritory(t3);
        expected.add(new Territory("4"));
        assertFalse(world.getAllTerritories().containsAll(expected));
    }

    @Test
    public void testAddConnection() {
        World world = new World();
        
        for (int i = 1; i <= 4; i++) {
            Territory t = new Territory(String.format("%s", i));
            world.addTerritory(t);
        }
        assertFalse(world.checkIfAdjacent("1", "2"));
        
        world.addConnection("1", "2");
        world.addConnection("1", "3");
        assertTrue(world.checkIfAdjacent("1", "2"));
        assertTrue(world.checkIfAdjacent("1", "3"));
        assertFalse(world.checkIfAdjacent("1", "4"));
        assertFalse(world.checkIfAdjacent("2", "3"));
        assertFalse(world.checkIfAdjacent("2", "4"));
        assertFalse(world.checkIfAdjacent("3", "4"));

        world.addConnection("3", "4");
        assertTrue(world.checkIfAdjacent("1", "2"));
        assertTrue(world.checkIfAdjacent("1", "3"));
        assertFalse(world.checkIfAdjacent("1", "4"));
        assertFalse(world.checkIfAdjacent("2", "3"));
        assertFalse(world.checkIfAdjacent("2", "4"));
        assertTrue(world.checkIfAdjacent("3", "4"));
    }

    @Test
    public void testStationTroop() {
        World world = createWorldSimple();
        world.stationTroop("1", new Troop(8, green));
        world.stationTroop("2", new Troop(10, red));
        world.stationTroop("2", 4); // this will initialize the territory again

        assertEquals(8, world.findTerritory("1").checkPopulation());
        assertEquals(green, world.findTerritory("1").getOwner());
        assertEquals(4, world.findTerritory("2").checkPopulation());
        assertEquals(red, world.findTerritory("2").getOwner());
    }

    @Test
    public void testMoveTroopValid() {
        World world = createWorld(troopsSeparated);
        // Valid
        BasicOrder move1 = new BasicOrder("Gondor", "Mordor", new Troop(13, red), 'm');
        assertDoesNotThrow(() -> world.moveTroop(move1));
        assertDoesNotThrow(() -> world.moveTroop(world.findTerritory("Elantris"),
                                                new Troop(6, blue), 
                                                world.findTerritory("Scadrial")));
    }

    @Test
    public void testMoveTroopSize() {
        World world = createWorld(troopsSeparated);
        // Troop size 
        BasicOrder move3 = new BasicOrder("Elantris", "Scadrial", new Troop(8, blue), 'M');
        assertThrows(IllegalArgumentException.class, 
                        () -> world.moveTroop(move3), 
                        NOT_ENOUGH_TROOP_MSG);
    }

    @Test
    public void testMoveTroopNonExistTerritory() {
        World world = createWorld(troopsSeparated);
        // Territory name does not exist
        BasicOrder move4 = new BasicOrder("No", "Scadrial", new Troop(3, blue), 'M');
        BasicOrder move5 = new BasicOrder("Elantris", "No", new Troop(3, blue), 'M');
        assertThrows(NoSuchElementException.class, 
                        () -> world.moveTroop(move4), 
                        TERRITORY_NOT_FOUND_MSG);
        assertThrows(NoSuchElementException.class, 
                        () -> world.moveTroop(move5),
                        TERRITORY_NOT_FOUND_MSG);
    } 

    @Test
    public void testMoveTroopNotSameOwner() {
        World world = createWorld(troopsSeparated);
        // Not same owner
        BasicOrder move6 = new BasicOrder("Elantris", "Gondor", new Troop(3, blue), 'M');
        BasicOrder move7 = new BasicOrder("Gondor", "Oz", new Troop(3, blue), 'M');
        assertThrows(IllegalArgumentException.class,
                        () -> world.moveTroop(move6), 
                        NOT_SAME_OWNER_MSG);
        assertThrows(IllegalArgumentException.class, 
                        () -> world.moveTroop(move7), 
                        NOT_SAME_OWNER_MSG);
    }

    @Test
    public void testMoveTroopNotReachable() {
        World world = createWorld(troopsSeparated);
        // Not reachable
        BasicOrder move1 = new BasicOrder("Narnia", "Oz", new Troop(3, blue), 'M');
        BasicOrder move2 = new BasicOrder("Oz", "Hogwarts", new Troop(3, blue), 'M');
        assertThrows(IllegalArgumentException.class, 
                        () -> world.moveTroop(move1), 
                        NOT_REACHABLE_MSG);
        assertThrows(IllegalArgumentException.class, 
                        () -> world.moveTroop(move2), 
                        NOT_REACHABLE_MSG);
    }

    @Test
    public void testAttackValid() {
        World world = createWorld(troopsSeparated);
        BasicOrder atk1 = new BasicOrder("Gondor", "Oz", new Troop(13, red), 'a');
        assertDoesNotThrow(() -> world.attackATerritory(atk1));
        assertDoesNotThrow(() -> world.attackATerritory(world.findTerritory("Narnia"),
                                                        new Troop(6, green),
                                                        world.findTerritory("Elantris")));
    }

    @Test
    public void testAttackNonExistTerritory() {
        World world = createWorld(troopsSeparated);
        BasicOrder atk1 = new BasicOrder("Gondor", "No", new Troop(13, red), 'A');
        BasicOrder atk2 = new BasicOrder("No", "Elantris", new Troop(6, green), 'A');
        assertThrows(NoSuchElementException.class, 
                        () -> world.attackATerritory(atk1), 
                        TERRITORY_NOT_FOUND_MSG);
        assertThrows(NoSuchElementException.class, 
                        () -> world.attackATerritory(atk2), 
                        TERRITORY_NOT_FOUND_MSG);   
    }

    @Test
    public void testAttackSameOwner() {
        World world = createWorld(troopsSeparated);
        BasicOrder atk1 = new BasicOrder("Elantris", "Scadrial", new Troop(1, blue), 'A');
        BasicOrder atk2 = new BasicOrder("Gondor", "Mordor", new Troop(6, red), 'A');
        assertThrows(IllegalArgumentException.class, 
                        () -> world.attackATerritory(atk1), 
                        SAME_OWNER_MSG);
        assertThrows(IllegalArgumentException.class, 
                        () -> world.attackATerritory(atk2), 
                        SAME_OWNER_MSG);   
    }

    @Test
    public void testAttackNotAdjacent() {
        World world = createWorld(troopsSeparated);
        BasicOrder atk1 = new BasicOrder("Elantris", "Oz", new Troop(1, blue), 'A');
        BasicOrder atk2 = new BasicOrder("Gondor", "Roshar", new Troop(6, red), 'A');
        assertThrows(IllegalArgumentException.class, 
                        () -> world.attackATerritory(atk1), 
                        NOT_ADJACENT_MSG);
        assertThrows(IllegalArgumentException.class,
                        () -> world.attackATerritory(atk2),
                        NOT_ADJACENT_MSG);   
    }    
    
    @Test
    public void testCheckBasicOrder() {
        World world = createWorld(troopsSeparated);
        BasicOrder move = new BasicOrder("Elantris", "Scadrial", new Troop(8, blue), 'M');
        assertEquals(String.format(NOT_ENOUGH_TROOP_MSG, 
                        world.findTerritory("Elantris").checkPopulation(),
                        "Elantris", 8),
                     world.checkBasicOrder(move));

        BasicOrder atk = new BasicOrder("Gondor", "Mordor", new Troop(6, red), 'A');
        assertEquals(String.format(SAME_OWNER_MSG, "Mordor"), world.checkBasicOrder(atk));
    }

    @Test
    public void testDoAllBattles() {
        World world = createWorld(troopsSeparated);
        world.doAllBattles();
    }

    @Test
    public void testGetAllTerritories() {
        World world = createWorld();

        List<Territory> expected = new ArrayList<>();
        for (String name : names) {
            expected.add(new Territory(name));
        }

        assertTrue(world.getAllTerritories().containsAll(expected));
    }

    @Test
    public void testCheckIfAdjacent() {
        World world = createWorld();

        assertTrue(world.checkIfAdjacent("Narnia", "Midkemia"));
        assertTrue(world.checkIfAdjacent("Narnia", "Elantris"));
        assertFalse(world.checkIfAdjacent("Narnia", "Oz"));

        assertTrue(world.checkIfAdjacent("Scadrial", "Roshar"));
        assertFalse(world.checkIfAdjacent("Scadrial", "Gondor"));
    }

    @Test
    public void testFindTerritory() {
        World world = createWorld();

        assertEquals(world.findTerritory("Narnia"), new Territory("Narnia"));
        assertThrows(NoSuchElementException.class, () -> world.findTerritory("Remnants"));
        assertThrows(NoSuchElementException.class, () -> world.findTerritory(""));
    }

    @Test
    public void testDivideTerritories() {
        // test if exceptions are thrown correctly
        
        World world = createWorld(); // evolution 1 example world, has 9 territories
        
        assertThrows(IllegalArgumentException.class, 
                    () -> world.divideTerritories(-1), 
                    NOT_POSITIVE_MSG);
        assertThrows(IllegalArgumentException.class, 
                    () -> world.divideTerritories(0), 
                    NOT_POSITIVE_MSG);
        assertDoesNotThrow(() -> world.divideTerritories(1));
        assertThrows(IllegalArgumentException.class, 
                    () -> world.divideTerritories(2), 
                    INDIVISIBLE_MSG);
        assertDoesNotThrow(() -> world.divideTerritories(3));
        assertThrows(IllegalArgumentException.class, 
                    () -> world.divideTerritories(18), 
                    INDIVISIBLE_MSG);

        Map<Integer, List<Territory>> groups = world.divideTerritories(3);
        // test number of groups
        assertEquals(groups.size(), 3);
        // test keys
        assertEquals(groups.keySet(), new HashSet<Integer>(Arrays.asList(0, 1, 2)));
        // test number in each list, print names
        for (int i = 0; i < groups.size(); i++) {
            assertEquals(groups.get(i).size(), 3);
            System.out.println(String.format("Group %d:", i));
            groups.get(i).forEach(e -> System.out.println(e.getName()));
            System.out.println();
        }
    }

    @Test
    public void testcheckIfAdjacent() {
        World world = createWorldSimple();
        Territory t1 = new Territory("1");
        Territory t2 = new Territory("2");
        assertTrue(world.checkIfAdjacent(t1, t2));
    }

    @Test
    public void testAddUnitToAll() {
        World world = createWorldSimple();
        world.addUnitToAll(1);
        assertEquals(1, world.findTerritory("1").checkPopulation());
        assertEquals(1, world.findTerritory("2").checkPopulation());

        World world2 = createWorld(troopsConnected);
        world2.addUnitToAll(3);
        assertEquals(13, world2.findTerritory("Narnia").checkPopulation());
        assertEquals(6, world2.findTerritory("Roshar").checkPopulation());

        assertThrows(IllegalArgumentException.class, 
                    () -> world2.addUnitToAll(-1), 
                    NOT_POSITIVE_MSG);
    }

    @Test
    public void testCheckLost() {
        World world1 = createWorld(troopsConnected);
        assertFalse(world1.checkLost("red"));
        assertFalse(world1.checkLost("blue"));
        assertFalse(world1.checkLost("green"));

        World world2 = createWorld(troopsSamePlayer);
        assertFalse(world2.checkLost("red"));
        assertTrue(world2.checkLost("blue"));
        assertTrue(world2.checkLost("green"));
    }

    @Test
    public void testIsGameEnd() {
        World world1 = createWorld(troopsConnected);
        assertFalse(world1.isGameEnd());

        World world2 = createWorld(troopsSamePlayer);
        assertTrue(world2.isGameEnd());
    }

    @Test
    public void testGetWinner() {
        World world1 = createWorld(troopsConnected);
        assertEquals(null, world1.getWinner());

        World world2 = createWorld(troopsSamePlayer);
        assertEquals("red", world2.getWinner());
    }

    @Test
    public void testEquals() {
        World world1 = createWorld(troopsConnected);
        World world2 = createWorld(troopsSamePlayer);
        World world3 = createWorld(troopsSamePlayer);
        World world4 = createWorldSimple();
        assertEquals(world1, world2);
        assertEquals(world1, world3);
        assertEquals(world2, world3);
        assertNotEquals(world1, world4);
        assertNotEquals(world1, null);
        assertNotEquals(world1, new Graph<Territory>());
    }

    @Test
    public void testToString() {
        World world1 = createWorld(troopsConnected);
        World world2 = createWorld(troopsSamePlayer);
        assertNotEquals(world1.toString(), world2.toString());
    } 

    @Test
    public void testHashcode() {
        World world1 = createWorld(troopsConnected);
        World world2 = createWorld(troopsSamePlayer);
        assertNotEquals(world1.hashCode(), world2.hashCode());
    }
}
