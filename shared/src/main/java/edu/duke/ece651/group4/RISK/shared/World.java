package edu.duke.ece651.group4.RISK.shared;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Random;

import java.util.NoSuchElementException;

/**
 * This class models the world which constitutes a certain number of territories.
 */
public class World {
    /**
     * Error messages
     */
    final String INDIVISIBLE_MSG = "Number of territories is not divisible by number of groups.";
    final String TERRITORY_NOT_FOUND_MSG = "The territory specified by the name '%s' is not found.";

    /**
     * All territories in the world. Implemented with a graph structure.
     */
    public Graph<Territory> territories;

    public World() {
        this.territories = new Graph<Territory>();
    }

    /**
     * Add a territory to the world.
     * @param terr is the territory to add.
     */
    public void addTerritory(Territory terr) {
        territories.addVertex(new Vertex(terr));
    }

    /**
     * Add connection between two adjacent territories.
     * @param terr1 is a one of the two adjacent territories.
     * @param terr2 is the other territory.
     */
    public void addConnection(Territory terr1, Territory terr2) {
        territories.addEdge(terr1, terr2);
    }

    /**
     * Move a troop to a different a territory. Owner of the troop is not checked.
     * @param start is the territory the troop starts from.
     * @param troop is the troop to move.
     * @param end is the territory the troop ends in.
     */
    public void moveTroop(Territory start, Troop troop, Territory end) {
        start.sendOutTroop(troop);
        end.sendInTroop(troop);
    }

    /**
     * Iterate over all territories around the world, and do battles on them.
     */
    public void doAllBattles() {
        for (Vertex v : territories.getVertices()) {
            v.getData().doOneBattle();
        }
    }

    /**
     * Divide territories into n equal groups.
     * @return a HashMap. The mapping being: group number -> grouped territories.
     * NOTE: group number starts from 0.
     */
    public HashMap<Integer, List<Territory>> divideTerritories(int nGroup) {
        // check if size / n is an integer
        if (size % nGroup != 0) {
            throw new IllegalArgumentException(INDIVISIBLE_MSG);
        }
        // create a array of indices
        int nTerritories = territories.getSize();
        int randomInds[] = new int[nTerritories]; 
        for (int i = 0; i < nTerritories; i++) {
            random[i] = i;
        }
        // shuffle indices to create random groups
        shuffle(randomInds);
        // divide
        List<Vertex> vertices = territories.getVertices();
        Map<Integer, Territory[]> groups = new HashMap<>(); 
        int nInGroup = nTerritories / nGroup;
        for (int group = 0; group < nGroup; group++) {
            List<Territory> terrs = new ArrayList<>();
            for (int i = 0; i < nInGroup; i++) {
                int ind = group * nInGroup + i;
                terrs.add(vertices.get(i).getData());
            }
            groups.put(group, terrs);
        }

        return groups;
    }

    /**
     * Shuffle an array by Fisher-Yates shuffle algorithm.
     * @param arr is the array to shuffle.
     */
    private void shuffle(int[] arr) {
        Random rand = new Random(); 
        for (int i = randomInd.length - 1; i >= 0; i--) {
            int randomNum = rand.nextInt(i + 1);
            int swap = arr[randomNum]; 
            arr[randomNum] = arr[i];
            arr[i] = swap;
        }
    }

    /**
     * Searchs a territory by name. 
     * If the territory exists, returns that territory specified by that name.
     * If not, an exception will be thrown.
     * @param terrName is the territory name to search.
     * @return the specified territory.
     */
    public Territory findTerritory(String terrName) {
        for (Vertex v : territories.getVertices()) {
            if (v.getData().getName().equals(terrName)) {
                return terr;
            }
        }
        throw new NoSuchElementException(String.format(TERRITORY_NOT_FOUND_MSG, terrName));
    }

    /**
     * Set the owner of a teritory to a certain player.
     * @param terrName is the territory name.
     * @param ownerName is the player name.
     */
    public void setTerritoryOwner(String terrName, String ownerName) {
        Territory terr = findTerritory(terrName);
        terr.setOwner(ownerName);
    }
}

