package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * An order that upgrades troop on a territory.
 * @param srcName is the name of territory to upgrade its troop.
 * @param levelBefore is the level of unit before upgrade.
 * @param levelAfter is the level of unit after the upgrade.
 * @param nUnit is the number of units to upgrade.
 */
public class UpgradeTroopOrder implements Order, Serializable {
    protected static final long serialVersionUID = 18L;

    protected static final String NO_DES_MSG = 
    "Place order does not have desitination territory.";
    protected static final String NO_TROOP_MSG = 
    "Place order does not have troop.";

    private Character actionName;
    private String srcName;
    private int levelBefore;
    private int levelAfter;
    private int nUnit;

    public UpgradeTroopOrder(String srcName,
                            int levelBefore, int levelAfter, 
                            int nUnit) {
        this.actionName = 'U';
        this.srcName = srcName;
        this.levelBefore = levelBefore;
        this.levelAfter = levelAfter;
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

    public int getLevelBefore() {
        return levelBefore;
    }

    public int getLevelAfter() {
        return levelAfter;
    }

    public int getNUnit() {
        return nUnit;
    }
} 
