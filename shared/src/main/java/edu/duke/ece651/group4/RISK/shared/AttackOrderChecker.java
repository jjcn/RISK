package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.Set;

/**
 * See "Evolution 1: 4. Turn structure" for rules related with attack order.
 * a. An attack order must specify the number of units to attack, the source territory,
 *    and the destination territory.
 * b. Units may only attack directly adjacent territories.
 * c. An attack order results in units attacking a territory controlled by 
 *    a different player.
 * 
 * Also checks if the order is an attack order.
 */
public class AttackOrderChecker implements Serializable {
    /**
     * Auto-generated serial version UID
     */
    private static final long serialVersionUID = -6158171671409577899L;
    /**
     * Error messages
     */
    protected final String NOT_ATTACK_ORDER_MSG = "This is not an attack order.";
    protected final String SAME_OWNER_MSG = 
        "Cannot attack %s, which belongs to you.";
    protected final String NOT_REACHABLE_MSG =
        "You tried to attack from %s to %s, which is out of reach for your troop";

    public AttackOrderChecker() {}

    /**
     * Checks if an attack order is legal.
     * @param order is the order given.
     * @param world is the world object.
     * @return null, if the order is legal;
     *         a String indicating the problem, if not.
     */
    protected String checkMyOrder(Order order, World world) {
        if (Character.toUpperCase(order.getActionName()) == 'A') {
            Territory start = world.findTerritory(order.getSrcName());
            Territory end = world.findTerritory(order.getDesName());
            Troop troop = order.getActTroop();
            // if the start and end have the same owner
            if (start.getOwner().equals(end.getOwner())) {
                return String.format(SAME_OWNER_MSG, 
                                    end.getName());
            }
            // if not reachable
            // TODO: check ranged attacks
            if (!world.getAdjacents(start).contains(end)) {
                return String.format(NOT_REACHABLE_MSG,
                                    start.getName(), 
                                    end.getName());
            }
            return null;
        }
        // if not an attack order
        return NOT_ATTACK_ORDER_MSG;
    }
    
}
