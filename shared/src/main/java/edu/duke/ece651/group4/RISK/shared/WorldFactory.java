package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    protected class AttributeBundle {
        int area;
        int foodSpeed;
        int techSpeed;

        public AttributeBundle(int area, int foodSpeed, int techSpeed) {
            this.area = area;
            this.foodSpeed = foodSpeed;
            this.techSpeed = techSpeed;
        }

        /**
         * Apply attributes to a territory.
         * @param terr is the territory to set attributes.
         */
        public void applyTo(Territory terr) {
            terr.setArea(area);
            terr.setFoodSpeed(foodSpeed);
            terr.setTechSpeed(techSpeed);
        }
    }

    public WorldFactory() {}

    protected List<Integer> generateRandomFixedSumList(int n, int sum, Random seed) {
        List<Integer> ans = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            int randIndex = seed.nextInt(n);
            ans.set(randIndex, ans.get(randIndex) + 1);
        }
        return ans;
    }

    protected List<AttributeBundle> generateBundlesWithTotal (
                                int nInGroup,
                                int totalArea,
                                int totalFoodSpeed,
                                int totalTechSpeed,
                                Random seed) {
        List<Integer> areas = generateRandomFixedSumList(nInGroup, totalArea, seed);
        List<Integer> foodSpeeds = generateRandomFixedSumList(nInGroup, totalFoodSpeed, seed);
        List<Integer> techSpeeds = generateRandomFixedSumList(nInGroup, totalTechSpeed, seed);

        List<AttributeBundle> ans = new ArrayList<>();
        for (int i = 0; i < nInGroup; i++) {
            ans.add(new AttributeBundle(areas.get(i), 
                                        foodSpeeds.get(i), 
                                        techSpeeds.get(i)));
        }
        return ans;
    }

    /**
     * Step 1: Create unconnected territories.
     * @param names is a list of territory names.
     * @return a world with unconnected territories.
     */
    protected World createTerritories(List<String> terrNames) {
		World world = new World();
        terrNames.forEach(name -> world.addTerritory(new Territory(name))); 
        return world;
	}

    /**
     * Step 2 : Done in create functions
     * 
     * Step 3 : use World.divideTerritories
     */

    /**
     * Step 4 : 
     * Set attributes to each group of territories.
     */

    /** 
     * The same bundle of attributes will be applied
     * across different groups.
     * 
     * For example, in terms of area:
     * group1: A=2 B=8;
     * group2: C=2 D=8.
     *
     * @param groups is groups of territories.
     * @param bundles is a list of attribute bundles to apply to a territory. 
     *        It is the same size as the number of territories in a group.
     * 
     * NOTE: three lists should have the same length.
     */
    protected void setAttributesSame(Map<Integer, List<Territory>> groups,
                                    List<AttributeBundle> bundles) {
        int nGroup = groups.size();
        int nInGroup = groups.get(0).size();
 
        for (int i = 0; i < nGroup; i++) {
            for (Territory terr : groups.get(i)) {
                for (int j = 0; j < nInGroup; j++) {
                    bundles.get(i).applyTo(terr);
                }
            }
        }
    }

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
     * assign a group of territories to every player.
     * @param world is the world to split into groups of territories.
     *        Should be a World object only with territories and connections.
     * @param playerNames is a list of player names.
     * @return a mapping, from a player's name to a group of territories.
     */
    public Map<String, List<Territory>> assignTerritories(
            World world, List<String> playerNames) {

        int nGroup = playerNames.size();
        int nInGroup = world.size() / playerNames.size();
        Map<Integer, List<Territory>> groups = world.divideTerritories(nGroup);

        // set total size & total food speed & total tech speed here
        // TODO: this is now hardcoded.
        int worldSize = world.size();
        List<AttributeBundle> bundles = 
        generateBundlesWithTotal(nInGroup, 
                                10 * worldSize, // size
                                50 * worldSize, // food
                                100 * worldSize, // tech
                                new Random(0));

        setAttributesSame(groups, bundles);

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