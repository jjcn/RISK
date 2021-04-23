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
    protected final String MELEE_CANT_ATTACK_MSG =
        "Your melee units tried to attack %s from %s, which are not adjacent territories. %n" +
        "Melee units can only attack territories directly adjacent to your territories.";
    protected final String RANGED_OUT_OF_REACH_MSG =
        "Your ranged units tried to attack %s from %s,%n" +
        "Which is %d away, but your units only have a range of %d.%n" +
        "Ranged units can only attack territories within their attack range.";

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
                return String.format(SAME_OWNER_MSG, end.getName());
            }
            // melee units can only attack adjacent territories
            if (!troop.hasRanged()) {
                if (!world.getAdjacents(start).contains(end)) {
                    return String.format(MELEE_CANT_ATTACK_MSG,
                            end.getName(),
                            start.getName());
                }
            }
            // ranged units can only attack territories within range
            else {
                int distance = world.calculateShortestPath(start, end);
                int troopRange = start.getAttackRange();
                if (troopRange < distance) {
                    return String.format(RANGED_OUT_OF_REACH_MSG,
                            end.getName(),
                            start.getName(),
                            distance,
                            troopRange);
                }
            }
            return null;
        }
        // if not an attack order
        return NOT_ATTACK_ORDER_MSG;
    }
    
}
