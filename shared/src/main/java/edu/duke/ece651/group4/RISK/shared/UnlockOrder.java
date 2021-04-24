package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;

/**
 * An order that unlock a type for a player.
 *
 * Usage example:
 * Player A wants to unlock soldier: new UnlockOrder("A", Constant.SOLDIER)
 */
public class UnlockOrder implements Order, Serializable {

    protected Character actionName;
    protected String playerName;
    protected String typeName;

    public UnlockOrder(String playerName, String typeName) {
        this.actionName = Constant.UNLOCK_ACTION;
        this.typeName = typeName;
        this.playerName = playerName;
    }

    @Override
    public Character getActionName() {
        return this.actionName;
    }

    @Override
    public String getSrcName() {
        return null;
    }

    @Override
    public String getDesName() {
        return null;
    }

    @Override
    public Troop getActTroop() {
        return null;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getTypeName() {
        return typeName;
    }
}
