package edu.duke.ece651.group4.RISK.shared;

/**
 * An order that upgrades a player's technology level.
 * @param nLevel is the number to add to a player's technology level.
 */
public class UpgradeTechOrder implements Order {
    private Character actionName;
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
