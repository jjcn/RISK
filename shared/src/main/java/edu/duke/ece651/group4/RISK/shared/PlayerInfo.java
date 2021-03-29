package edu.duke.ece651.group4.RISK.shared;

import java.util.HashSet;
import java.util.Set;

/**
 * This class records information that is player-specific.
 * Allows reading of all info, 
 * and modifying of tech level & resource quantities.
 */
public class PlayerInfo {

    private String playerName;
    private int techLevel;
    private Set<Resource> resources;

    public PlayerInfo(String playerName, 
                        int techLevel, 
                        Set<Resource> resources) {
        this.playerName = playerName;
        this.techLevel = techLevel;
        this.resources = resources;
    }

    public PlayerInfo(String playerName, 
                        int techLevel, 
                        Resource... resources) {
        this.playerName = playerName;
        this.techLevel = techLevel;
        Set<Resource> set = new HashSet<>();
        for (Resource r : resources) {
            set.add(r);
        }
        this.resources = set;
    }

    public String getName() {
        return playerName;
    }

    public int getTechLevel() {
        return techLevel;
    }

    public Set<Resource> getResouceList() {
        return resources;
    }

    /**
     * Changes the player's tech level.
     * @param i is the number to add to tech level. 
     *          Can be positive, 0, or negative.
     */
    public int modifyTechLevel(int i) {
        techLevel += i;
        return techLevel;
    }

    /**
     * Change the quantity of a resource.
     * @param resource is a particular resource to modify quantity.
     * @param i is the number to add to resource quantity. 
     *          Can be positive, 0, or negative.
     */
    public void modifyResource(Resource resource, int i) {
        for (Resource r : resources) {
            if (r.equalsName(resource)) {
                resource.modifyQuantity(i);
            }
        }
    }
}