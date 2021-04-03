package edu.duke.ece651.group4.RISK.shared;

import java.util.Arrays;

/**
 * This class create instances of pre-defined World templates.
 * To add new instances, add new create methods.
 */
public class WorldFactory {

    public WorldFactory() {}

    public World createRandomWorld(int numTerrs) {
        return new World(numTerrs);
    }

    public World createWorldUnconnected(String[] names) {
		World world = new World();
        Arrays.asList(names).forEach(name -> world.addTerritory(new Territory(name))); 
        return world;
	}

    /**
     * Territory layout is the same as that on Evolution 1 requirements.
     * 
     * N-----M--O--G
     * |   /  |/ \ | 
     * |  /   S ---M     
     * |/   / |  \ |
     * E------R----H
     * 
     * @return a world object.
     */
    public World createFantasyWorld() {
        String[] fantasyNames = {"Narnia", "Midkemia", "Oz", "Gondor", "Mordor",
 				           "Hogwarts", "Scadrial", "Elantris", "Roshar"};
        World world = createWorldUnconnected(fantasyNames);

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