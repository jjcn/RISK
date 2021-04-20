package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * An order that transfers troop on a territory to another type.
 * Does not change unit level.
 *
 * Usage demo:
 * In order to transfer two LV3 soldier -> LV3 knight on a territory named "Hogwarts",
 * construct a TransferTroopOrder as follows:
 *
 * TransferTroopOrder ttOrder = new TransferTroopOrder("Hogwarts", Constant.SOLDIER, Constant.KNIGHT, 3, 2);
 *
 * NOTE: you MUST use the Strings defined in shared/Constant for 'typeBefore' and 'typeBefore' fields.
 */
public class TransferTroopOrder implements Order, Serializable {
    /**
     * Error messages
     */
    protected static final String NO_DES_MSG =
            "Place order does not have destination territory.";
    protected static final String NO_TROOP_MSG =
            "Place order does not have troop.";

    /**
     * A character that uniquely identifies what type of action it is.
     */
    private Character actionName;
    /**
     * Name of the territory on which troop will be upgraded.
     */
    private String srcName;
    /**
     * Type of unit before upgrade.
     */
    private String typeBefore;
    /**
     * Type of unit after upgrade.
     */
    private String typeAfter;
    /**
     * Specify the level of unit to transfer.
     */
    private int unitLevel;
    /**
     * Number of units to transfer.
     */
    private int nUnit;

    public TransferTroopOrder(String srcName,
                              String typeBefore, String typeAfter,
                              int unitLevel, int nUnit) {
        this.actionName = Constant.TRANSFER_TROOP_ACTION;
        this.srcName = srcName;
        this.typeBefore = typeBefore;
        this.typeAfter = typeAfter;
        this.unitLevel = unitLevel;
        this.nUnit = nUnit;
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

    public String getTypeBefore() {
        return typeBefore;
    }

    public String getTypeAfter() {
        return typeAfter;
    }

    public int getUnitLevel() {
        return unitLevel;
    }

    public int getNUnit() {
        return nUnit;
    }
}
