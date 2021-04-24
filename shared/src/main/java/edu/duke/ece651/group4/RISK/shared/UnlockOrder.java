package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * An order that unlock a type for a player.
 *
 * Usage example:
 * Player A wants to unlock soldier: new UnlockOrder("A", Constant.SOLDIER)
 */
public class UnlockOrder implements Order, Serializable {
    /**
     * Error messages
     */
    protected static final String NO_SRC_MSG =
            "Unlock order does not have source territory.";
    protected static final String NO_DES_MSG =
            "Unlock order does not have destination territory.";
    protected static final String NO_TROOP_MSG =
            "Unlock order does not have troop.";

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

    public String getPlayerName() {
        return playerName;
    }

    public String getTypeName() {
        return typeName;
    }
}
