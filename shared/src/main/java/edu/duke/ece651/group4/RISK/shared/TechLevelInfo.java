package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class TechLevelInfo implements Serializable {
    /**
     * Error messages
     */
    protected static final String INVALID_TECH_LEVEL_MSG =
            "Specified tech level does not exist.";
    protected static final String TECHLEVEL_INVALID_MODIFY_MSG =
            "Error: cannot modify tech level by %d.%n"
            + "current tech level is %d, after this modification it will be %d,%n";

    /**
     * Player's current tech level
     */
    protected int techLevel; // player's current tech level
    /**
     * Tech level constraints
     */
    protected static final int MIN_TECH_LEVEL = 1; // minimum tech level
    protected static final int MAX_TECH_LEVEL = 6; // max tech level a player can reach
    /**
     * Cumulative costs of upgrading from tech level 1 to N.
     * Two 0's are put at the start for easy indexing by techLevel.
     */
    protected static final List<Integer> CUM_TECH_LEVEL_UPGRADE_COSTS
            = Arrays.asList(new Integer[]{0, 0, 50, 125, 250, 450, 750});
    // TODO: this is hardcoded for now, may put in a file?

    public TechLevelInfo(int techLevel) {
        checkTechLevelValidity(techLevel);
        this.techLevel = techLevel;
    }

    public int getTechLevel() {
        return techLevel;
    }

    /**
     * Check if a tech level upgrade is valid.
     * @param n is the level to add onto current tech level.
     * @throws IllegalArgumentException
     */
    public void checkUpgradeTechLevelBy(int n) throws IllegalArgumentException {
        int techLevelAfterMod = techLevel + n;
        try {
            checkTechLevelValidity(techLevelAfterMod);
        } catch (IllegalArgumentException e) {
            String explanation_msg =
                    String.format(TECHLEVEL_INVALID_MODIFY_MSG, n, techLevel, techLevelAfterMod);
            throw new IllegalArgumentException(explanation_msg + e.getMessage());
        }
    }

    /**
     * Try upgrade the tech level by n.
     * @param n is the level to add onto current tech level.
     * @throws IllegalArgumentException
     */
    public void upgradeTechLevelBy(int n) throws IllegalArgumentException {
        checkUpgradeTechLevelBy(n);
        techLevel += n;
    }

    /**
     * Calculate the tech resource consumption by an upgrade.
     * @param levelBefore is the tech level before an upgrade.
     * @param levelAfter is the tech level after an upgrade.
     * @return the quantity of tech resource this tech level upgrade will consume.
     */
    public static int calcConsumption(int levelBefore, int levelAfter) throws IllegalArgumentException {
        try {
            return CUM_TECH_LEVEL_UPGRADE_COSTS.get(levelAfter)
                    - CUM_TECH_LEVEL_UPGRADE_COSTS.get(levelBefore);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(INVALID_TECH_LEVEL_MSG);
        }
    }

    /**
     * Check if a tech level falls within the range of permitted tech level.
     * @param techLevel is the tech level to check
     * @throws IllegalArgumentException
     */
    protected static void checkTechLevelValidity(int techLevel) throws IllegalArgumentException {
        if (techLevel < MIN_TECH_LEVEL) {
            String underflow_msg = "it is below the minimum tech level.";
            throw new IllegalArgumentException(underflow_msg);
        }
        else if (techLevel > MAX_TECH_LEVEL) {
            String overflow_msg = "it exceeds the maximum tech level.";
            throw new IllegalArgumentException(overflow_msg);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && this.getClass().equals(o.getClass())) {
            TechLevelInfo techLevelInfo = (TechLevelInfo) o;
            return this.techLevel == techLevelInfo.techLevel;
        }
        return false;
    }

    @Override
    public String toString() {
        return "TechLevelInfo{" +
                "tech level=" + techLevel + ", "
                + "min tech level=" + MIN_TECH_LEVEL + ", "
                + "max tech level=" + MAX_TECH_LEVEL + ", "
                + '}';
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

}
