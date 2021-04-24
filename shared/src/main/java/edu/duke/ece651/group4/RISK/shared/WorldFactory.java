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
    protected static final long serialVersionUID = 20L;
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

    protected void assignArea(Map<Integer, List<Territory>> groups,
                              int totalArea, Random seed) {
        int nTerrs = groups.get(0).size();
        groups.values().forEach(terrs -> {
            List<Integer> areas = generateRandomFixedSumList(nTerrs, totalArea, seed);
            IntStream.range(0, nTerrs).forEach(i -> terrs.get(i).setArea(areas.get(i)));
        });
    }

    protected void assignFoodSpeed(Map<Integer, List<Territory>> groups,
                              int totalFoodSpeed, Random seed) {
        int nTerrs = groups.get(0).size();
        groups.values().forEach(terrs -> {
            List<Integer> foodSpeeds = generateRandomFixedSumList(nTerrs, totalFoodSpeed, seed);
            IntStream.range(0, nTerrs).forEach(i -> terrs.get(i).setFoodSpeed(foodSpeeds.get(i)));
        });
    }

    protected void assignTechSpeed(Map<Integer, List<Territory>> groups,
                                   int totalTechSpeed, Random seed) {
        int nTerrs = groups.get(0).size();
        groups.values().forEach(terrs -> {
            List<Integer> techSpeeds = generateRandomFixedSumList(nTerrs, totalTechSpeed, seed);
            IntStream.range(0, nTerrs).forEach(i -> terrs.get(i).setTechSpeed(techSpeeds.get(i)));
        });
    }

    protected void assignRandomAttributes (
    		Map<Integer, List<Territory>> groups,
    		int totalArea, int totalFoodSpeed, int totalTechSpeed,
			Random seed) {
        assignArea(groups, totalArea, seed);
    	assignFoodSpeed(groups, totalArea, seed);
    	assignTechSpeed(groups, totalTechSpeed, seed);
    }

    /*
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

    public World create4TerritoryWorld() {
        String[] names = {"A", "B", "C", "D"};
        World world = createTerritories(Arrays.asList(names));

        world.addConnection("A", "B");
        world.addConnection("A", "C");
        world.addConnection("A", "D");
        world.addConnection("B", "D");
        world.addConnection("C", "D");

        return world;
    }

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
     * Overloaded function that has hard-coded:
     *
     * total area = 10 * worldSize,
     * total food speed = 50 * worldSize,
     * total tech speed = 100 * worldSize.
     *
     * each player starts with 2000 food and 2000 tech resource.
     *
     * @param world is the world object.
     * @param playerNames is a list of names of players.
     */
    public Map<String, List<Territory>> assignTerritories(
            World world, List<String> playerNames) {
        // TODO: A hardcoded version.
        int worldSize = world.size();
        return assignTerritories(world,  playerNames,
                10 * worldSize, // area
                50 * worldSize, // food
                30 * worldSize, // tech
                200, 200,
                new Random(0));
    }

    /**
     * Assign a group of territories to every player,
     * meanwhile set the attributes of territories,
     * so that each group has the same sum of all attributes.
     *
     * Can change total area, food speed, tech speed in each group,
     * and the resource each player starts with.
     *
     * @param world is the world object
     * @param playerNames is a list of names of players
     * @param totalArea is the sum of area of a group of territories
     * @param totalFoodSpeed is the sum of food speed of a group of territories
     * @param totalTechSpeed is the sum of tech speed of a group of territories
     * @param startingFood is each player's food resource quantity at start
     * @param startingTech is each player's tech resource quantity at start
     * @param seed is a random seed
     * @return
     */
    public Map<String, List<Territory>> assignTerritories(
            World world, List<String> playerNames,
            int totalArea, int totalFoodSpeed, int totalTechSpeed,
            int startingFood, int startingTech,
            Random seed ) {

        int nGroup = playerNames.size();
        Map<Integer, List<Territory>> groups = world.divideTerritories(nGroup);

        // total size & total food speed & total tech speed is set here
        int worldSize = world.size();
        assignRandomAttributes (groups,
                totalArea, // size
                totalFoodSpeed, // food
                totalTechSpeed, // tech
                seed);

        Map<String, List<Territory>> ans = new HashMap<>();
        for (int i = 0; i < nGroup; i++) {
            String playerName = playerNames.get(i);
            List<Territory> territories = groups.get(i);
            // TODO: use TextPlayer for now. Change to other subclass of Player later.
            territories.forEach(terr -> terr.setOwnerTroop(0, new TextPlayer(playerName)));
            ans.put(playerName, territories);
        }

        // starting resources set here
        playerNames.forEach(name -> world.registerPlayer(new PlayerInfo(name, startingFood, startingTech)));

        return ans;
    }
}