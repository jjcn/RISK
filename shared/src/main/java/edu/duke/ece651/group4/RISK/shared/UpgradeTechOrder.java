package edu.duke.ece651.group4.RISK.shared;

/**
 * An order that upgrade's a player's tech level.
 */
public class UpgradeTechOrder implements Order {
    private Character actionName;
    /**
     * The player's technology level is modified by n levels.
     */
    private int nLevel;
    
    public UpgradeTechOrder(Character actionName,  int nLevel) {
        this.actionName = 'T';
        this.nLevel = nLevel;
    }

    @Override
    public Character getActionName() {
        return actionName;
    }

    public int getNLevel() {
        return nLevel;
    }
}
