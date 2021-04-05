package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;

/**
 * This class records information that is player-specific. 
 * Allows modifying of tech level & resource quantities.
 */
public class PlayerInfo implements Serializable {
    protected static final long serialVersionUID = 9L;
    /**
     * Player's name
     */
    protected String playerName;
    /**
     * Player's current tech level
     */
    protected int techLevel; // player's current tech level
    /**
     * Tech level constraints
     */
    protected final int minTechLevel; // minimum tech level
    protected final int maxTechLevel; // max tech level a player can reach

    /**
     * Cumulative costs of upgrading from tech level 1 to N.
     * Two 0's are put at the start for easy indexing by techLevel.
     */
    protected static final int[] cumTechLevelUpgradeCosts = 
    {0, 0, 50, 125, 250, 450, 750}; // TODO: this is hardcoded for now, may put in a file?

    /**
     * Player's food and tech resources.
     */
    protected FoodResource foodResource;
    protected TechResource techResource;

    /**
     * Error messages
     */
    final static String TECHLEVEL_INVALID_MODIFY_MSG = 
    "Error: cannot modify the tech level of %s by %d.%n" +
    "The tech level after this modification will be %d,%n";
    final static String FAILURE_TECH_LEVEL_UPGRADE_MSG = 
    "Fails to upgrade %s's tech level.";
    final static String INVALID_TECH_LEVEL_MSG =
    "Specified tech level does not exist.";

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

    public PlayerInfo clone() {
        return new PlayerInfo(playerName, techLevel, 
                            minTechLevel, maxTechLevel, 
                            new FoodResource(foodResource.getQuantity()), 
                            new TechResource(techResource.getQuantity()));
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

    /**
     * A player gains food resource.
     * @param gain is the gain of food resource.
     */
    public void gainFood(int gain) {
        foodResource.gain(gain);
    }

    /**
     * A player gains tech resource.
     * @param gain is the gain of tech resource.
     */
    public void gainTech(int gain) {
        techResource.gain(gain);
    }

    /**
     * A player consumes food resource.
     * @param consumption is the consumption of food resource.
     */
    public void consumeFood(int consumption) {
        foodResource.consume(consumption);
    }

    /**
     * A player consumes tech resource.
     * @param consumption is the consumption of tech resource.
     */
    public void consumeTech(int consumption) {
        techResource.consume(consumption);
    }
    
    /**
     * Upgrades player's tech level by 1.
     */
    public void upgradeTechLevelBy1() {
        modifyTechLevelBy(1);
    }

    /**
     * Modify player's tech level by a number i.
     * 
     * @param i is the number to add to tech level. 
     *          Can be positive, 0, or negative.
     */
    protected void modifyTechLevelBy(int i) {
        int techLevelAfterMod = techLevel + i;
        String explanation_msg = String.format(TECHLEVEL_INVALID_MODIFY_MSG, 
                                            playerName, i, techLevelAfterMod); 
        if (techLevelAfterMod < minTechLevel) {
            String underflow_msg = "which is below the minimum tech level a player can have.";
            throw new IllegalArgumentException(explanation_msg + underflow_msg);
        }
        else if(techLevelAfterMod > maxTechLevel) {
            String overflow_msg = "which is beyond the maximum tech level a player can have.";
            throw new IllegalArgumentException(explanation_msg + overflow_msg);
        } else {
            try {
                consumeTech(calcUpgradeTechLevelConsumption(techLevel, techLevelAfterMod));
                techLevel += i;
            } catch (IllegalArgumentException iae) {
                String err_msg = iae.getMessage() + "\n" + 
                		String.format(FAILURE_TECH_LEVEL_UPGRADE_MSG, playerName);
                throw new IllegalArgumentException(err_msg);
            }
        }
    }

    /**
     * Calculate the tech resource consumption by an upgrade.
     * @param before is the tech level before an upgrade.
     * @param after is the tech level after an upgrade.
     * @return quantity of tech resource consumed by this upgrade.
     */
    protected static int calcUpgradeTechLevelConsumption(int before, int after) {
        try {
            return cumTechLevelUpgradeCosts[after] 
                    - cumTechLevelUpgradeCosts[before];
        } catch (IndexOutOfBoundsException ioobe) {
            throw new IllegalArgumentException(INVALID_TECH_LEVEL_MSG);
        }
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