package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * This class create instances of pre-defined World templates.
 * To add new instances, add new create methods.
 * 
 * The world object is constructed through a pipeline.
 * 
 * 1. Create unconnected territories.
 * 2. Add connections between territories.
 * 3. Assign each territory to a group.
 * 4. Set attributes of each territory: area, techSpeed, foodSpeed.
 *    Make sure each group have the same total number 
 *    of each attribute.
 */
public class WorldFactory implements Serializable {

    public WorldFactory() {}

    /**
     * Generate a random integer list with fixed sum.
     * @param length is the length of list
     * @param sum is the sum of all elements in list
     * @param seed is the random seed
     * @return random integer list with fixed sum
     */
    protected List<Integer> generateRandomFixedSumList(int length, int sum, Random seed) {
    	//initialize
        List<Integer> ans = new ArrayList<>();
        IntStream.range(0, length).forEach(i -> ans.add(0));
        //randomly add 1 to elements
        for (int i = 0; i < sum; i++) {
            int randIndex = seed.nextInt(length);
            ans.set(randIndex, ans.get(randIndex) + 1);
        }
        return ans;
    }

    protected void assignRandomAttributes (
    		Map<Integer, List<Territory>> groups,
    		int totalArea, int totalFoodSpeed, int totalTechSpeed,
			Random seed) {
    	int nTerrs = groups.get(0).size();
    	for (List<Territory> terrs : groups.values()) {
    		List<Integer> areas = generateRandomFixedSumList(nTerrs, totalArea, seed);
	        List<Integer> foodSpeeds = generateRandomFixedSumList(nTerrs, totalFoodSpeed, seed);
	        List<Integer> techSpeeds = generateRandomFixedSumList(nTerrs, totalTechSpeed, seed);
	        for (int i = 0; i < nTerrs; i++) {
	        	terrs.get(i).setArea(areas.get(i));
	        	terrs.get(i).setFoodSpeed(foodSpeeds.get(i));
	        	terrs.get(i).setTechSpeed(techSpeeds.get(i));
	        }
    	}
    }

    /**
     * Step 1: Create unconnected territories.
     * 
     * @param names is a list of territory names.
     * @return a world with unconnected territories.
     */
    protected World createTerritories(List<String> terrNames) {
		World world = new World();
        terrNames.forEach(name -> world.addTerritory(new Territory(name))); 
        return world;
	}

    /**
     * Step 2 : Add connections. Done in create functions.
     */

    /**
     * Below are fixed world templates.
     * If you want to generate a world with random connections, use World(int: nTerrs)
     */

    public World create6TerritoryWorld() {
        String[] names = {"A", "B", "C", "D", "E", "F"};
        World world = createTerritories(Arrays.asList(names));

        world.addConnection("A", "B");
        world.addConnection("B", "C");
        world.addConnection("C", "D");
        world.addConnection("D", "E");
        world.addConnection("E", "F");
        world.addConnection("F", "A");
        world.addConnection("A", "D");
        world.addConnection("B", "E");

        return world;
    }

    public World create8TerritoryWorld() {
        String[] names = {"A", "B", "C", "D", 
                          "E", "F", "G", "H"};
        World world = createTerritories(Arrays.asList(names));

        world.addConnection("A", "B");
        world.addConnection("A", "C");
        world.addConnection("A", "D");
        world.addConnection("A", "E");
        world.addConnection("A", "F");
        world.addConnection("A", "G");
        world.addConnection("A", "H");
        world.addConnection("B", "C");
        world.addConnection("B", "E");
        world.addConnection("D", "G");
        world.addConnection("F", "H");

        return world;
    }

    /**
     * Territory layout is the same as that on Evolution 1 requirements.
     * 
     * nTerritories = 9
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
        World world = createTerritories(Arrays.asList(fantasyNames));

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

    public World create10TerritoryWorld() {
        String[] names = {"A", "B", "C", "D", "E", 
                          "F", "G", "H", "I", "J"};
        World world = createTerritories(Arrays.asList(names));

        world.addConnection("A", "B");
        world.addConnection("A", "C");
        world.addConnection("B", "D");
        world.addConnection("C", "D");
        world.addConnection("C", "E");
        world.addConnection("E", "F");
        world.addConnection("F", "H");
        world.addConnection("G", "H");
        world.addConnection("E", "G");
        world.addConnection("C", "I");
        world.addConnection("E", "J");
        world.addConnection("I", "J");

        return world;
    }

    /**
     * Step 3 : use World.divideTerritories
     * 
     * Step 4 : Set owner & attributes to each group of territories.
     */

    /**
     * assign a group of territories to every player.
     * @param world is the world to split into groups of territories.
     *        Should be a World object only with territories and connections.
     * @param playerNames is a list of player names.
     * @return a mapping, from a player's name to a group of territories.
     */
    public Map<String, List<Territory>> assignTerritories(
            World world, List<String> playerNames) {

        int nGroup = playerNames.size();
        Map<Integer, List<Territory>> groups = world.divideTerritories(nGroup);

        // set total size & total food speed & total tech speed here
        // TODO: this is now hardcoded.
        int worldSize = world.size();
        assignRandomAttributes (groups,
        		10 * worldSize, // size
                50 * worldSize, // food
                100 * worldSize, // tech
                new Random(0));
        
        Map<String, List<Territory>> ans = new HashMap<>();
        for (int i = 0; i < nGroup; i++) {
            String playerName = playerNames.get(i);
            List<Territory> territories = groups.get(i);
            // TODO: use TextPlayer for now. Change to other subclass of Player later.
            territories.forEach(terr -> terr.initializeTerritory(0, new TextPlayer(playerName)));
            ans.put(playerName, territories);
        }
        // players start with 0 resource
        playerNames.forEach(name -> world.registerPlayer(name));

        return ans;
    }

}