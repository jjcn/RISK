package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class records information that is player-specific. 
 * Allows modifying of tech level & resource quantities.
 */
public class PlayerInfo implements Serializable {
    /**
     * Auto generated serial version UID
     */
    protected static final long serialVersionUID = 9L;
    /**
     * Error messages
     */
    protected static final String FAILURE_TECH_LEVEL_UPGRADE_MSG =
            "Fails to upgrade %s's tech level.";
    protected static final String NOT_ENOUGH_RESOURCE_MSG =
            "Not enough resources.";

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
     * The type of units a player have already unlocked.
     */
    protected Set<String> unlockedTypes;
    /**
     * Maximum number of types a player can have (including basic soldier)
     */
    protected static final int MAX_TYPE_COUNT = 3;
    /**
     * The starting tech level that a player is allowed to unlock more types.
     */
    protected static final int MIN_TECH_LEVEL_TO_UNLOCK_TYPE = 1;
    /**
     * All type of units.
     */
    protected static final Set<String> ALL_UNLOCKABLE_TYPES = new HashSet<>(Constant.JOB_NAMES);
    /**
     * The tech resource consumption of all types.
     */
    protected static final Map<String, Integer> UNLOCK_COSTS;
    static {
        UNLOCK_COSTS = new HashMap<>();
        for (String type : ALL_UNLOCKABLE_TYPES) {
            UNLOCK_COSTS.put(type, 1);
        }
    }

    protected PlayerInfo(String playerName,
                         TechLevelInfo techLevelInfo,
                         FoodResource foodResource,
                         TechResource techResource,
                         Set<String> unlockedTypes) {
        this.playerName = playerName;
        this.techLevelInfo = techLevelInfo;
        this.foodResource = foodResource;
        this.techResource = techResource;
        this.unlockedTypes = unlockedTypes;
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
        this(playerName,
                new TechLevelInfo(1),
                new FoodResource(nFood),
                new TechResource(nTech),
                new HashSet<>()
        );
        this.unlockedTypes = initUnlockedTypes();
    }

    /**
     * Initialize the unlocked types.
     * Player can only have soldier at the beginning.
     *
     * @return Set of allowed types.
     */
    protected Set<String> initUnlockedTypes() {
        Set<String> ans = new HashSet<>();
        ans.add(Constant.SOLDIER);
        return ans;
    }

    protected Set<String> cloneAllowedTypes() {
        Set<String> ans = new HashSet<>();
        for (String type : unlockedTypes) {
            ans.add(type);
        }
        return ans;
    }

    public PlayerInfo clone() {
        return new PlayerInfo(
                playerName,
                new TechLevelInfo(techLevelInfo.getTechLevel()),
                new FoodResource(foodResource.getQuantity()),
                new TechResource(techResource.getQuantity()),
                cloneAllowedTypes()
                );
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

    public Set<String> getUnlockedTypes() { return unlockedTypes; }

    /**
     * Get all types that are unlockable for a player.
     *
     * @return all unlockable types for a player.
     */
    public Set<String> getUnlockableTypes() {
        try {
            checkCanUnlockMore();
        } catch (IllegalArgumentException iae) {
            return new HashSet<>();
        }
        Set<String> ans = new HashSet<>();
        for (String type : ALL_UNLOCKABLE_TYPES) {
            if (!unlockedTypes.contains(type)) {
                ans.add(type);
            }
        }
        return ans;
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
     * Check if the player's tech level can be upgraded by a number N.
     *
     * @param n is the number to add to player's tech level.
     *        Can be positive, 0, or negative.
     */
    public void checkUpgradeTechLevelBy(int n) throws IllegalArgumentException {
        techLevelInfo.checkUpgradeTechLevelBy(n);
        checkResourceConsumptionOfTechUpgrade(n);
    }

    /**
     * Try upgrade player's tech level by a number n.
     * check if the player has enough tech resources.
     * If so, upgrade tech level, but does not consume tech resource.
     *
     * @param n is the number to add to player's tech level.
     *        Can be positive, 0, or negative.
     * @throws IllegalArgumentException
     */
    public void upgradeTechLevelBy(int n) throws IllegalArgumentException {
        checkResourceConsumptionOfTechUpgrade(n);
        techLevelInfo.upgradeTechLevelBy(n);
    }

    /**
     * Check if there is enough tech resource to consume to
     * upgrade the player's tech level by a number N.
     * @param n is the number to add to player's tech level.
     *        Can be positive, 0, or negative.
     * @throws IllegalArgumentException
     */
    protected void checkResourceConsumptionOfTechUpgrade(int n) throws IllegalArgumentException {
        int techLevel = techLevelInfo.getTechLevel();
        int techLevelAfterMod = techLevel + n;
        try {
            int consumption = TechLevelInfo.calcConsumption(techLevel, techLevelAfterMod);
            techResource.checkConsume(consumption);
        } catch (IllegalArgumentException e) {
            String err_msg = NOT_ENOUGH_RESOURCE_MSG + "\n" +
                    String.format(FAILURE_TECH_LEVEL_UPGRADE_MSG, playerName);
            throw new IllegalArgumentException(err_msg);
        }
    }

    /**
     * Consumes resource of a tech upgrade by a number n.
     *
     * @param n is the number to add to tech level.
     *          Can be positive, 0, or negative.
     */
    public void consumeResourceOfTechUpgrade(int n) {
        checkResourceConsumptionOfTechUpgrade(n);
        int techLevel = techLevelInfo.getTechLevel();
        int techLevelAfterMod = techLevel + n;
        int consumption = TechLevelInfo.calcConsumption(techLevel, techLevelAfterMod);
        consumeTech(consumption);
    }

    /**
     * Check if a player can unlock more types.
     */
    protected void checkCanUnlockMore() throws IllegalArgumentException {
        if (unlockedTypes.size() >= MAX_TYPE_COUNT) {
            throw new IllegalArgumentException(
                    String.format("Reached maximum type count %d. Cannot unlock more types.", MAX_TYPE_COUNT)
            );
        }
    }

    /**
     * Check if a type is unlockable.
     * @param type is a name to check.
     */
    protected void checkTypeValidity(String type) throws IllegalArgumentException {
        if (!Constant.JOB_NAMES.contains(type)) {
            throw new IllegalArgumentException(String.format("%s is not an unlockable type.", type));
        }
        if (unlockedTypes.contains(type)) {
            throw new IllegalArgumentException(String.format("%s is already unlocked.", type));
        }
    }

    /**
     * Check if the player has reach the tech level to unlock more types.
     */
    protected void checkTechLevelToUnlockType() {
        int currentTechLevel = getTechLevel();
        if (currentTechLevel < MIN_TECH_LEVEL_TO_UNLOCK_TYPE) {
            throw new IllegalArgumentException(
                  String.format(
                          "You have to reach tech level %d to unlock more types of units, " +
                          "but your current tech level is %d.",
                          MIN_TECH_LEVEL_TO_UNLOCK_TYPE,
                          currentTechLevel
                  )
            );
        }
    }

    /**
     * Player tries to unlock a new type.
     * @param type is a type name.
     */
    public void unlockType(String type) throws IllegalArgumentException {
        checkCanUnlockMore();
        checkTypeValidity(type);
        checkTechLevelToUnlockType();
        consumeTech(UNLOCK_COSTS.get(type));
        unlockedTypes.add(type);
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