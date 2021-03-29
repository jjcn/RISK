package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * See "Evolution 1: 4. Turn structure" for rules related with move order.
 * a. A move order must specify the number of units to move, the source territory, and
 *    the destination territory.
 * b. Units moving with a move order must have a path formed by adjacent territories
 *    controlled by their player from the source to the destination.
 * c. Move orders move units from one territory to another territory controlled by the
 *    same player.
 * 
 * Also checks if the order is a move order.
 */
public class MoveOrderChecker implements Serializable {
    protected final String NOT_MOVE_ORDER_MSG = "This is not a move order.";
    protected final String NOT_SAME_OWNER_MSG = 
        "Cannot move troop to %s, which belongs to another player.";
    protected final String NOT_REACHABLE_MSG = 
        "Cannot reach from %s to %s. " +
        "Other players' territories are blocking the way.";

    public MoveOrderChecker() {}

    /**
     * Checks if a move order is legal.
     * @param order is the order given.
     * @param world is the world object.
     * @return null, if the order is legal;
     *         a String indicating the problem, if not.
     */
    protected String checkMyOrder(BasicOrder order, World world) {
        if (Character.toUpperCase(order.getActionName()) == 'M') {
            Territory start = world.findTerritory(order.getSrcName());
            Territory end = world.findTerritory(order.getDesName());
            Player owner = start.getOwner();
            // if the start and end do not have the same owner
            if (!start.getOwner().equals(end.getOwner())) {
                return String.format(NOT_SAME_OWNER_MSG, end.getName());
            }
            // if not reachable
            Queue<Territory> queue = new LinkedList<>();
            Set<Territory> visited = new HashSet<>();
            queue.add(start);
            visited.add(start);
            while (queue.size() != 0) {
                Territory key = queue.poll();
                List<Territory> adjacents = world.getAdjacents(key);
                for (Territory adjacent : adjacents) {
                    if (adjacent.equals(end)) {
                        return null;
                    }
                    if (!visited.contains(adjacent)) {
                        if (adjacent.getOwner().equals(owner)) {
                                visited.add(adjacent);
                                queue.add(adjacent);
                            }
                        }
                }
            }
            return String.format(NOT_REACHABLE_MSG, start.getName(), end.getName());
        }
        // if not move order
        return NOT_MOVE_ORDER_MSG;
    }
    
}
