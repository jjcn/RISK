package edu.duke.ece651.group4.RISK.shared;

import edu.duke.ece651.group4.RISK.shared.Order;
import edu.duke.ece651.group4.RISK.shared.Troop;

import java.io.Serializable;
import java.util.NoSuchElementException;

import static edu.duke.ece651.group4.RISK.shared.Constant.ALLIANCE_ACTION;


public class AllianceOrder implements Order, Serializable {
    protected static final long serialVersionUID = 25L;

    private String src;
    private String des;
    private Character actionName;
    protected static final String NO_TROOP_MSG =
            "Place order does not have troop.";

    /**
     * An order used to ally with other players.
     * @param des is the player making the request.
     * @param src is the target player he wants to ally with.
     */
    public AllianceOrder(String src, String des) {
        this.actionName = ALLIANCE_ACTION;
        this.src = src;
        this.des = des;
    }

    @Override
    public Character getActionName() {
        return actionName;
    }

    @Override
    public String getSrcName() {
        return src;
    }

    @Override
    public String getDesName() {
        return des;
    }

    @Override
    public Troop getActTroop() { throw new NoSuchElementException(NO_TROOP_MSG); }
}
