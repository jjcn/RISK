package edu.duke.ece651.group4.RISK.shared;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains instances of World.
 */
public class WorldLibrary {
    static Map<String, World> lib = new HashMap<>(); 

    public WorldLibrary() {
        lib = new HashMap<String, World>();
    }

    public World getWorldByName(String name) {
        return lib.get(name);
    }

    private World createSimpleWorld(int numTerrs) {
        return new World(numTerrs);
    }
}