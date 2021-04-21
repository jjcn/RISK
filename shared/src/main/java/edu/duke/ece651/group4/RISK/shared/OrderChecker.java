package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.Set;

/**
 * This class checks if a basic order (Move & Attack) is valid.
 * a. The order should be a basic order.
 * b. One can only move troops on his territories.
 * c. When sending out a troop from A, 
 *    the troop size should not be larger than that on A. 
 */
public class OrderChecker implements Serializable {
    protected static final long serialVersionUID = 7L;
    /**
     * Error Messages
     */
    protected final String UNKNOWN_BASIC_ORDER_TYPE_MSG =
        "'%c' is not a valid basic order type.";
    protected final String NO_TROOP_STATIONED_MSG =
        "Error: You do not have troop stationed on %s.";
    protected final String NOT_ENOUGH_TROOP_MSG = 
        "Cannot move out a troop of size larger than %d on %s, " +
        "but you entered a troop of size %d.";

    /**
     * Order checkers for different types of orders.
     */
    protected AttackOrderChecker aoc;
    protected MoveOrderChecker moc;

    public OrderChecker() {
        aoc = new AttackOrderChecker();
        moc = new MoveOrderChecker();
    }

    /**
     * Checks if a move or attack order is legal.
     * @param order is the order given
     * @param committer is the name of the player who commits this order
     * @param world is the world object
     *
     * @return null, if the order is legal;
     *         a String indicating the problem, if not.
     */
    public String checkOrder(Order order, String committer, World world) {
        Territory start = world.findTerritory(order.getSrcName());

        // territory does not belong to the order sender
        if (!world.canMoveThrough(committer, start)) { // for evol3 alliance, able to move troop on other's terrs
            return String.format(NO_TROOP_STATIONED_MSG, start.getName());
        }

        // troop size larger than that on the territory
        if (start.getTroopSize(committer) < order.getActTroop().checkTroopSize()) {
            return String.format(NOT_ENOUGH_TROOP_MSG, 
                                start.checkPopulation(),
                                start.getName(),
                                order.getActTroop().checkTroopSize());
        }

        char orderType = Character.toUpperCase(order.getActionName());
        if (orderType == 'A') {
            return aoc.checkMyOrder(order, world);
        }
        else if (orderType == 'M') {
            return moc.checkMyOrder(order, world);
        }

        // not Attack or Move
        return String.format(UNKNOWN_BASIC_ORDER_TYPE_MSG, order.getActionName());
    }
}
