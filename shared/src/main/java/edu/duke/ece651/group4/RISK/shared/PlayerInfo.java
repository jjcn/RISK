package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;

/**
 * This class records information that is player-specific. Allows reading of all
 * info, and modifying of tech level & resource quantities.
 */
public class PlayerInfo implements Serializable {

    final String TECHLEVEL_INVALID_MODIFY_MSG = "Modifying tech level by %s is not suppported.";

    protected String playerName;

    protected int techLevel; // player's current tech level
    protected final int minTechLevel; // minimum tech level
    protected final int maxTechLevel; // max tech level a player can reach

    protected FoodResource foodResource;
    protected TechResource techResource;

    protected PlayerInfo(String playerName, int techLevel, int minTechLevel, int maxTechLevel,
            FoodResource foodResource, TechResource techResource) {
        this.playerName = playerName;
        this.techLevel = techLevel;
        this.minTechLevel = minTechLevel;
        this.maxTechLevel = maxTechLevel;
        this.foodResource = foodResource;
        this.techResource = techResource;
    }

    /**
     * Create player info with only the player's name provided.
     * 
     * Starts with 0 food & tech resources.
     * 
     * @param playerName is the name of player.
     */
    public PlayerInfo(String playerName) {
        this(playerName, 0, 0);
    }

    /**
     * Create player info with the player's name, 
     * and initialize the quantities of food & tech resource.
     * 
     * Player starts at tech level 1, 
     * and cannot set tech level beyond the interval
     * [0, 6]. (inclusive)
     * 
     * @param playerName is the name of player.
     * @param nFood is the initial quantity of food resource.
     * @param nTech is the initial quantity of tech resource.
     */
    public PlayerInfo(String playerName, int nFood, int nTech) {
        this(playerName, 1, 0, 6, new FoodResource(nFood), new TechResource(nTech));
    }

    public String getName() {
        return playerName;
    }

    public int getTechLevel() {
        return techLevel;
    }

    public int getFoodQuantity() {
        return foodResource.getQuantity();
    }

    public int getTechQuantity() {
        return techResource.getQuantity();
    }

    public void setFoodQuantity(int i) {
        foodResource.setQuantity(i);
    }

    public void setTechQuantity(int i) {
        techResource.setQuantity(i);
    }
    
    /**
     * Modify a player's tech level by i.
     * 
     * @param i is the number to add to tech level. 
     *          Can be positive, 0, or negative.
     */
    public void modifyTechLevel(int i) {
        int techLevelAfterMod = techLevel + i;
        if (minTechLevel < techLevelAfterMod 
            && techLevelAfterMod < maxTechLevel) {
            techLevel += i;
        } else {
            throw new IllegalArgumentException(String.format(TECHLEVEL_INVALID_MODIFY_MSG, i));
        }
    }

    public void modifyFoodQuantity(int i) {
        foodResource.setQuantity(foodResource.getQuantity() + i);
    }

    public void modifyTechQuantity(int i) {
        techResource.setQuantity(techResource.getQuantity() + i);
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other.getClass().equals(getClass())) {
            PlayerInfo otherPlayerInfo = (PlayerInfo) other;
            return otherPlayerInfo.getName().equals(playerName) && otherPlayerInfo.getTechLevel() == getTechLevel()
                    && otherPlayerInfo.getFoodQuantity() == getFoodQuantity()
                    && otherPlayerInfo.getTechQuantity() == getTechQuantity();
        } else {
            return false;
        }
    }
}