package edu.duke.ece651.group4.RISK.shared;

public class UpgradeTroopOrder implements Order {
    private Character actionName;
    /**
     * @param srcName is the name of territory to upgrade its troop.
     * @param levelBefore is the level of unit before upgrade.
     * @param levelAfter is the level of unit after the upgrade.
     * @param nUnit is the number of units to upgrade.
     */
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

    public String getSrcName() {
        return srcName;
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
