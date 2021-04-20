package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * An order that transfers troop on a territory to another type.
 * @param srcName is the name of territory to upgrade its troop.
 * @param levelBefore is the level of unit before upgrade.
 * @param levelAfter is the level of unit after the upgrade.
 * @param nUnit is the number of units to upgrade.
 */
public class TransferTroopOrder implements Order, Serializable {

    protected static final String NO_DES_MSG =
            "Place order does not have destination territory.";
    protected static final String NO_TROOP_MSG =
            "Place order does not have troop.";

    private Character actionName;
    private String srcName;
    private String typeBefore;
    private String typeAfter;
    private int unitLevel;
    private int nUnit;

    public TransferTroopOrder(String srcName,
                              String typeBefore, String typeAfter,
                              int unitLevel, int nUnit) {
        this.actionName = 'R';
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
