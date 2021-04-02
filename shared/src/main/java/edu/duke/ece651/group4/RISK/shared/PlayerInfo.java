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
    private int techLevel; // player's current tech level
    private final int maxTechLevel; // max tech level a player can reach
    private Set<Resource> resources;

    public PlayerInfo(String playerName, 
                        int techLevel, int maxTechLevel,
                        Set<Resource> resources) {
        this.playerName = playerName;
        this.techLevel = techLevel;
        this.maxTechLevel = maxTechLevel;
        this.resources = resources;
    }

    public PlayerInfo(String playerName, 
                        int techLevel, int maxTechLevel,
                        Resource... resources) {
        this.playerName = playerName;
        this.techLevel = techLevel;
        this.maxTechLevel = maxTechLevel;
        Set<Resource> set = new HashSet<>();
        for (Resource r : resources) {
            set.add(r);
        }
        this.resources = set;
    }

    /**
     * Default player info, only provide a player name.
     * start at tech level 1, can reach 6.
     * start with 2 resources with quantity 0, food & tech.
     * @param playerName
     */
    public PlayerInfo(String playerName) {
        this(playerName, 1, 6, 
            new Resource("food", 0),
            new Resource("tech", 0));
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
    public void modifyTechLevel(int i) {
        final String TECHLEVEL_INVALID_MODIFY_MSG = 
            "Modifying tech level by %s is not suppported.";
        if (0 < techLevel + i && techLevel + i > maxTechLevel) {
            techLevel += i;
        }
        else {
            throw new IllegalArgumentException(
                String.format(TECHLEVEL_INVALID_MODIFY_MSG, i));
        } 
    }

    /**
     * Change the quantity of a resource.
     * @param resource is a particular resource to modify quantity.
     * @param i is the number to add to resource quantity. 
     *          Can be positive, 0, or negative.
     */
    public void modifyResource(Resource resource, int i) {
        // TODO: use lambdas?
        for (Resource r : resources) {
            if (r.equalsName(resource)) {
                resource.modifyQuantity(i);
            }
        }
    }
}