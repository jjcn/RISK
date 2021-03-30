package edu.duke.ece651.group4.RISK.shared;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains instances of World.
 */
public class WorldLibrary {
    Map<String, World> lib = new HashMap<>(); 
    
    public WorldLibrary() {
        lib = new HashMap<String, World>();
        lib.put("random6", createRandomWorld(6));
        lib.put("random10", createRandomWorld(10));
        lib.put("random12", createRandomWorld(12));
        lib.put("fantasy", createFantasyWorld());
    }

    public World getWorldByName(String name) {
        return lib.get(name);
    }

    private World createRandomWorld(int numTerrs) {
        return new World(numTerrs);
    }

    /**
     * Territory layout is the same as that on Evolution 1 requirements.
     * N-----M--O--G
     * |   /  |/ \ | 
     * |  /   S ---M     
     * |/   / |  \ |
     * E------R----H
     * @return a world object.
     */
    private World createFantasyWorld() {
        World world = new World();
        String names[] = 
        "Narnia, Midkemia, Oz, Gondor, Mordor, Hogwarts, Scadrial, Elantris, Roshar".split(", ");
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
        return world;
    }
}