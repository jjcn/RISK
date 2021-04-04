package edu.duke.ece651.group4.RISK.shared;

/**
 * This class records information that is player-specific.
 * Allows reading of all info, 
 * and modifying of tech level & resource quantities.
 */
public class PlayerInfo {

    private String playerName; 

    private int techLevel; // player's current tech level
    private final int maxTechLevel; // max tech level a player can reach

    private FoodResource foodResource;
    private TechResource techResource;

    public PlayerInfo(String playerName, 
                    int techLevel, int maxTechLevel,
                    FoodResource foodResource,
                    TechResource techResource) {
        this.playerName = playerName;
        this.techLevel = techLevel;
        this.maxTechLevel = maxTechLevel;
        this.foodResource = foodResource;
        this.techResource = techResource;
    }

    /**
     * Default player info, only provide a player name.
     * start at tech level 1, can reach 6.
     * start with 2 resources with quantity 0, food & tech.
     * @param playerName
     */
    public PlayerInfo(String playerName) {
        this(playerName, 1, 6, 
            new FoodResource(),
            new TechResource());
    }

    public String getName() {
        return playerName;
    }

    public int getTechLevel() {
        return techLevel;
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

    public int getFoodQuantity() {
        return foodResource.getQuantity();
    }

    public int getTechQuantity() {
        return techResource.getQuantity();
    }

    public void setFoodQuantity(int i) {
        checkNonNegative(i);
        foodResource.setQuantity(i);
    }

    public void setTechQuantity(int i) {
        checkNonNegative(i);
        techResource.setQuantity(i);
    }

    public void modifyFoodQuantity(int i) {
        checkNonNegative(foodResource.getQuantity() + i);
        foodResource.setQuantity(foodResource.getQuantity() + i);
    }

    public void modifyTechQuantity(int i) {
        checkNonNegative(techResource.getQuantity() + i);
        techResource.setQuantity(techResource.getQuantity() + i);
    }
   
    protected void checkNonNegative(int i) {
        final String NEG_MSG = 
        "Error: Resource quantity will be negative after this action.";
        if (i < 0) {
            throw new IllegalArgumentException(NEG_MSG);
        }
    }
}