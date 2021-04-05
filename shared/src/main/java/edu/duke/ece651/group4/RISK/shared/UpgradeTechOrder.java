package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * An order that upgrades a player's tech level.
 * @param nLevel is the number to add to a player's tech level.
 */
public class UpgradeTechOrder implements Order, Serializable {

    private static final long serialVersionUID = 1L;

    protected static final String NO_SRC_MSG = 
        "Place order does not have source territory.";
    protected static final String NO_DES_MSG = 
        "Place order does not have desitination territory.";
    protected static final String NO_TROOP_MSG = 
        "Place order does not have troop.";

    private Character actionName;
    /**
     * How many tech level is upgraded
     */
    private int nLevel;

    public UpgradeTechOrder(int nLevel) {
        this.actionName = 'T';
        this.nLevel = nLevel;
    }

    @Override
    public Character getActionName() {
        return actionName;
    }

    @Override
    public String getSrcName() {
        throw new NoSuchElementException(NO_SRC_MSG);
    }

    @Override
    public String getDesName() {
        throw new NoSuchElementException(NO_DES_MSG);
    }

    @Override
    public Troop getActTroop() {
        throw new NoSuchElementException(NO_TROOP_MSG);
    }

    public int getNLevel() {
        return nLevel;
    }
}