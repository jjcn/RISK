package edu.duke.ece651.group4.RISK.shared;

import java.util.HashMap;

/**
 * This class records information that is player-specific.
 */
public class PlayerStatus {
    /**
     * Types of resources.
     */
    private enum Resource {FOOD, TECH};

    private String playerName;
    private int techLevel;
    private HashMap<Resource, Integer> resourceList;

    public PlayerStatus(String playerName, 
                        int techLevel, 
                        HashMap<Resource, Integer> resourceList) {
        this.playerName = playerName;
        this.techLevel = techLevel;
        this.resourceList = resourceList;
    }

    public PlayerStatus(String playerName) {
        this(playerName, 1, null);
        HashMap<Resource, Integer> map = new HashMap<>();
        for (Resource r : Resource.values()) {
            map.put(r, 0);
        }
        this.resourceList = map;
    }

    public String getName() {
        return playerName;
    }

    public int getTechLevel() {
        return techLevel;
    }

    public HashMap<Resource, Integer> getResouceList() {
        return resourceList;
    }

    /**
     * Changes the tech level.
     * @param i is the number to add to tech level. 
     *          Can be positive, 0, or negative.
     */
    public int modifyTechlevel(int i) {
        techLevel += i;
        return techLevel;
    }

    /**
     * Change the quantity of a resource.
     * @param resource is the enum type of the resource.
     * @param i is the number to add to resource quantity. 
     *          Can be positive, 0, or negative.
     */
    public void modifyResources(Resource resource, int i) {
        resourceList.put(resource, resourceList.get(resource) + i);
    }
}