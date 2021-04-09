package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class records information that is player-specific. 
 * Allows modifying of tech level & resource quantities.
 */
public class PlayerInfo implements Serializable {
    /**
     * Error messages
     */
    protected static final String FAILURE_TECH_LEVEL_UPGRADE_MSG =
            "Fails to upgrade %s's tech level.";

    protected static final long serialVersionUID = 9L;
    /**
     * Player's name
     */
    protected String playerName;
    /**
     * Player's current tech level
     */
    protected TechLevelInfo techLevelInfo;
    /**
     * Player's food and tech resources.
     */
    protected FoodResource foodResource;
    protected TechResource techResource;
    /**
     * A list of names of this player's allies.
     */
    protected List<String> allianceNames;

    protected PlayerInfo(String playerName, TechLevelInfo techLevelInfo,
                         FoodResource foodResource, TechResource techResource,
                         List<String> allianceNames) {
        this.playerName = playerName;
        this.techLevelInfo = techLevelInfo;
        this.foodResource = foodResource;
        this.techResource = techResource;
        this.allianceNames = allianceNames;
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
     * Create a player info with the player's name,
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
        this(playerName, new TechLevelInfo(1),
                new FoodResource(nFood), new TechResource(nTech),
                new ArrayList<>());
    }

    public PlayerInfo clone() {
        List<String> allianceNamesCopy = new ArrayList<>();
        allianceNamesCopy.addAll(allianceNames);
        return new PlayerInfo(playerName,
                new TechLevelInfo(techLevelInfo.getTechLevel()),
                new FoodResource(foodResource.getQuantity()),
                new TechResource(techResource.getQuantity()),
                allianceNamesCopy);
    }

    public String getName() {
        return playerName;
    }

    public int getTechLevel() {
        return techLevelInfo.getTechLevel();
    }

    public int getFoodQuantity() {
        return foodResource.getQuantity();
    }

    public int getTechQuantity() {
        return techResource.getQuantity();
    }

    public List<String> getAllianceNames() {
        return allianceNames;
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
     * Upgrade player's tech level by a number n.
     *
     * @param n is the number to add to tech level.
     *          Can be positive, 0, or negative.
     */
    public void upgradeTechLevelBy(int n) throws IllegalArgumentException {
        techLevelInfo.upgradeTechLevelBy(n);
    }

    /**
     * Consumes resource of a tech upgrade by a number n.
     *
     * @param n is the number to add to tech level.
     *          Can be positive, 0, or negative.
     */
    public void consumeResourceOfTechUpgrade(int n) {
        int techLevel = techLevelInfo.getTechLevel();
        int techLevelAfterMod = techLevel + n;
        try {
            int consumption = TechLevelInfo.calcConsumption(techLevel, techLevelAfterMod);
            consumeTech(consumption);
        } catch (IllegalArgumentException iae) {
            String err_msg = iae.getMessage() + "\n" +
                    String.format(FAILURE_TECH_LEVEL_UPGRADE_MSG, playerName);
            throw new IllegalArgumentException(err_msg);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o.getClass().equals(getClass())) {
            PlayerInfo otherPlayerInfo = (PlayerInfo) o;
            return otherPlayerInfo.playerName.equals(playerName)
                    && otherPlayerInfo.techLevelInfo.equals(techLevelInfo)
                    && otherPlayerInfo.foodResource.equals(foodResource)
                    && otherPlayerInfo.techResource.equals(techResource);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return playerName + "'s player info:\n" +
                "current tech level Info: " + techLevelInfo.toString() +
                foodResource.toString() + "\n" +
                techResource.toString() + "\n";
    }
}