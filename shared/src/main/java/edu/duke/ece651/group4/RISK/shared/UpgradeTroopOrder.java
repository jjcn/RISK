package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * An order that upgrades troop on a territory.
 */
public class UpgradeTroopOrder implements Order, Serializable {
    /**
     * Auto-generated serial version UID
     */
    protected static final long serialVersionUID = 18L;

    /**
     * Error messages
     */
    protected static final String NO_DES_MSG = 
        "Upgrade troop order does not have destination territory.";
    protected static final String NO_TROOP_MSG = 
        "Upgrade troop order does not have troop.";

    /**
     * A character that uniquely identifies what type of action it is.
     */
    private Character actionName;
    /**
     * Name of the territory on which troop will be upgraded.
     */
    private String srcName;
    /**
     * Level of unit before upgrade.
     */
    private int levelBefore;
    /**
     * Level of unit after the upgrade.
     */
    private int levelAfter;
    /**
     * Number of units to upgrade.
     */
    private int nUnit;
    /**
     * Type of unit to upgrade.
     */
    private String typeName;

    public UpgradeTroopOrder(String srcName,
                             int levelBefore,
                             int levelAfter,
                             int nUnit,
                             String typeName) {
        this.actionName = Constant.UPTROOP_ACTION;
        this.srcName = srcName;
        this.levelBefore = levelBefore;
        this.levelAfter = levelAfter;
        this.nUnit = nUnit;
        this.typeName = typeName;
    }

    /**
     * Overloaded constructor that does not specify the type of unit to upgrade.
     *
     * Default unit type is Constant.SOLDIER
     */
    public UpgradeTroopOrder(String srcName,
                            int levelBefore,
                            int levelAfter,
                            int nUnit) {
        this(srcName,
            levelBefore,
            levelAfter,
            nUnit,
            Constant.SOLDIER);
    }

    /**
     * Set the unit type to upgrade.
     * @param typeName is a string indicating the type of unit,
     *                 defined in shared/Constant.
     */
    public void setUnitType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public Character getActionName() {
        return actionName;
    }

    @Override
    public String getSrcName() {
        return srcName;
    }

    @Override
    public String getDesName() {
        throw new NoSuchElementException(NO_DES_MSG);
    }

    @Override
    public Troop getActTroop() {
        throw new NoSuchElementException(NO_TROOP_MSG);
    }

    public int getLevelBefore() {
        return levelBefore;
    }

    public int getLevelAfter() {
        return levelAfter;
    }

    public int getNUnit() {
        return nUnit;
    }

    public String getTypeName() { return typeName; }
}
