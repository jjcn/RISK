package edu.duke.ece651.group4.RISK.shared;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.util.NoSuchElementException;

import java.util.Random;

import java.io.Serializable;

/**
 * This class models the world which constitutes
 * a certain number of territories connected with each other.
 * 
 * Maintains territories in a graph struture;
 * an order checker that checks validity of basic orders;
 * a random seed.
 */
public class World implements Serializable {
    /**
     * Error messages
     */
    protected final String INDIVISIBLE_MSG = 
        "Number of territories is not divisible by number of groups.";
    protected final String NOT_POSITIVE_MSG = 
        "Number should be positive.";
    protected final String TERRITORY_NOT_FOUND_MSG = 
        "The territory specified by the name '%s' is not found.";
    
    /**
     * All territories in the world. Implemented with a graph structure.
     */
    public Graph<Territory> territories;
    private final OrderChecker basicOrderChecker;
    private final Random rnd;

    /**
     * Construct a default world with an empty graph.
     */
    public World() {
        this(new Graph<Territory>());
    }

    /**
     * Construct world with a graph.
     * @param terrs
     */
    public World(Graph<Territory> terrs) {
        this(terrs, new Random());
    }

    /**
     * Construct world with a graph and a random seed.
     * @param terrs is the number of territories.
     * @param random is the random seed.
     */
    public World(Graph<Territory> terrs, Random random) {
        territories = terrs;
        basicOrderChecker = new OrderChecker();
        rnd = random;
    }

    /**
     * Creates a world, specify a number of total territories and a random seed.
     * The territories created will share a random seed with the world.
     * Territory names are: 1, 2, 3, ...
     * Number of total connections is random,
     * and is propotional to number of territories.
     * @param numTerrs is the number of territories.
     */
    public World(int numTerrs, Random random) {
        this(new Graph<Territory>(), random);

        for (int i = 1; i <= numTerrs; i++) {
            addTerritory(new Territory(String.format("%d", i), random));
        }
        territories.addRandomEdges(numTerrs, random);

    }

    /**
     * Overloading constructor that only has its number of territories specified.
     * @param numTerrs is the number of territories.
     */
    public World(int numTerrs) {
        this(new Graph<Territory>(), new Random());

        for (int i = 1; i <= numTerrs; i++) {
            addTerritory(new Territory(String.format("%d", i)));
        }
        territories.addRandomEdges(numTerrs, new Random());
    }

    /**
     * Overloading constructor that takes an array of territories names.
     * @param terrNames is an array of territory names.
     */
    public World(String[] terrNames) {
        this(new Graph<Territory>(), new Random());

        for (int i = 1; i <= terrNames.length; i++) {
            addTerritory(new Territory(terrNames[i]));
        }
        territories.addRandomEdges(terrNames.length, new Random());
    }
 
    /**
     * Get a deep copy of a world object.
     * @return a deep copy of the world object.
     */
    public World clone() {
        boolean[][] adjMatrixCopy = territories.cloneAdjMatrix();
        ArrayList<Territory> old = (ArrayList<Territory>)territories.getVertices();
        ArrayList<Territory> cpy=new ArrayList<>();
        for (Territory item : old) {
            cpy.add(item.clone());
        }
        return new World(new Graph<>(cpy,adjMatrixCopy), this.rnd);
    }

    /**
     * Get all the territories in the world.
     * @return a list of all territories in the world.
     */
    public List<Territory> getAllTerritories() {
        return territories.getVertices();
    }

    /**
     * Get all the territories that are adjacent to a certain territory.
     * @param terr is the territory to search adjacents.
     * @return a list of adjacent territories.
     */
    public List<Territory> getAdjacents(Territory terr) {
        return territories.getAdjacentVertices(terr);
    }

    /**
     * Get all the territories that are adjacent to a territory
     * of certain name.
     * @param terrName is the name of territory to search adjacents.
     * @return a list of adjacent territories.
     */
    public List<Territory> getAdjacents(String terrName) {
        return territories.getAdjacentVertices(findTerritory(terrName));
    }

    /**
     * Set a random seed on a territory.
     * @param terr is the territory to set random seed.
     * @param seed is a random seed.
     */
    public void setRandom(Territory terr, Random seed) {
        terr.setRandom(seed);
    }

    /**
     * Set a random seed on a territory with certain name.
     * @param terrName is the name of the territory to set random seed.
     * @param seed is a random seed.
     */
    public void setRandom(String terrName, Random seed) {
        findTerritory(terrName).setRandom(seed);
    }

    /**
     * Add a territory to the world.
     * @param terr is the territory to add.
     */
    public void addTerritory(Territory terr) {
        territories.addVertex(terr);
    }

    /**
     * Add connection between two adjacent territories so that they become adjacent.
     * @param terr1 is a territory.
     * @param terr2 is the other territory.
     */
    public void addConnection(Territory terr1, Territory terr2) {
        territories.addEdge(terr1, terr2);
    }

    /**
     * Add connection between two adjacent territories by the name of these territories.
     * @param name1 is the name of a territory.
     * @param name2 is the name of the other territory.
     */
    public void addConnection(String name1, String name2) {
        addConnection(findTerritory(name1), findTerritory(name2));
    }

    /**
     * Station troop to a territory.
     * @param terrName is the territory name.
     * @param troop is a Troop object.
     */
    public void stationTroop(String terrName, Troop troop) {
        Territory terr = findTerritory(terrName);
        terr.initializeTerritory(troop.checkTroopSize(), troop.getOwner());
    }

    /**
     * Station troop to a territory by specifying territory name and population.
     * @param terrName is the territory name.
     * @param population is the population of the troop.
     */
    public void stationTroop(String terrName, int population) {
        Territory terr = findTerritory(terrName);
        terr.initializeTerritory(population, terr.getOwner());
    }

    // TODO: moveTroop and attackTerritory shares similar arg list and behavior.
    // May integrrate into a single function:
    // public void executeOrder()

    /**
     * Moves a troop to a different a territory. Owner of the troop is not checked.
     * Also checks if the troop size is valid to send from the starting territory.
     * @param order
     */
    public void moveTroop(BasicOrder order) {
        Territory start = findTerritory(order.getSrcName());
        Territory end = findTerritory(order.getDesName());
        Troop troop = order.getActTroop();

        String errorMsg = basicOrderChecker.checkOrder(order, this);
        if (errorMsg != null) {
            throw new IllegalArgumentException(errorMsg);
        }
        
        start.sendOutTroop(troop);
        end.sendInTroop(troop);
    }

    /**
     * Overload
     * @param start is the territory the troop starts from.
     * @param troop is the troop to move.
     * @param end is the territory the troop ends in.
     */
    @Deprecated
    public void moveTroop(Territory start, Troop troop, Territory end) {
        BasicOrder order = new BasicOrder(start.getName(), end.getName(), 
                                            troop, 'M');
        moveTroop(order);
    }

    /**
     * Sends a troop to a territory with different owner, 
     * in order to engage in battle on that territory.
     * Also checks if the troop size is valid to send from the starting territory.
     * @param order is the attack order
     */
    public void attackATerritory(BasicOrder order) {
        Territory start = findTerritory(order.getSrcName());
        Territory end = findTerritory(order.getDesName());
        Troop troop = order.getActTroop();
        
        String errorMsg = basicOrderChecker.checkOrder(order, this);
        if (errorMsg != null) {
            throw new IllegalArgumentException(errorMsg);
        }
        
        start.sendOutTroop(troop);
        end.sendInEnemyTroop(troop);
    }

    /**
     * Overload
     * @param start is the territory the troop starts from.
     * @param troop is the troop to send.
     * @param end is the territory the troop ends in.
     */
    @Deprecated
    public void attackATerritory(Territory start, Troop troop, Territory end) {
        BasicOrder order = new BasicOrder(start.getName(), end.getName(), 
                                          troop, 'A');
        attackATerritory(order);
    }

    /**
     * Iterate over all territories around the world, and do battles on them.
     * @return A summary of battle info on all territories.
     */
    public String doAllBattles() {
        StringBuilder ans = new StringBuilder();
        for (Territory terr : territories.getVertices()) {
            ans.append(terr.doBattles());
        }
        return ans.toString();
    }

    /**
     * Check if two territories are adjacent to each other
     * @param terr1 is a territory.
     * @param terr2 is the other territory.
     * @return true, if two territories are adjacent;
     *         false, if not.
     */
    public boolean checkIfAdjacent(Territory terr1, Territory terr2) {
        return territories.isAdjacent(terr1, terr2);
    }

    /**
     * Check if two territories are adjacent to each other by their names.
     * @param name1 is the name of one territory to check.
     * @param name2 is the name of thr other territory.
     * @return true, if two territories are adjacent;
     *         false, if not.
     */
    public boolean checkIfAdjacent(String name1, String name2) {
        return checkIfAdjacent(findTerritory(name1), findTerritory(name2));
    }

    /**
     * Finds a territory by its name.
     * If the territory exists, returns that territory of that name.
     * If not, an exception will be thrown.
     * @param terrName is the territory name to search.
     * @return the specified territory.
     */
    public Territory findTerritory(String terrName) {
        for (Territory terr : territories.getVertices()) {
            if (terr.getName().equals(terrName)) {
                return terr;
            }
        }
        throw new NoSuchElementException(String.format(TERRITORY_NOT_FOUND_MSG, terrName));
    }

    /**
     * Divide territories into n equal groups.
     * @param nGroup is the number of groups the world is divided into.
     * @return a HashMap. The mapping being: group number -> grouped territories.
     * NOTE: group number starts from 0.
     */
    public Map<Integer, List<Territory>> divideTerritories(int nGroup) {
        // check if nGroup is an integer > 0
        if (nGroup <= 0) {
            throw new IllegalArgumentException(NOT_POSITIVE_MSG);
        }
        // check if size / nGroup is an integer
        else if (territories.size() % nGroup != 0) {
            throw new IllegalArgumentException(INDIVISIBLE_MSG);
        }
        // init an index array
        int nTerritories = territories.size();
        int randomInds[] = new int[nTerritories];
        for (int i = 0; i < nTerritories; i++) {
            randomInds[i] = i;
        }
        // shuffle indices to create random groups
        Shuffler shuffler = new Shuffler(this.rnd);
        shuffler.shuffle(randomInds);
        // divide
        List<Territory> terrList = territories.getVertices();
        Map<Integer, List<Territory>> groups = new HashMap<>();
        int nInGroup = nTerritories / nGroup;
        for (int group = 0; group < nGroup; group++) {
            List<Territory> terrs = new ArrayList<>();
            for (int i = 0; i < nInGroup; i++) {
                terrs.add(terrList.get(randomInds[group * nInGroup + i]));
            }
            groups.put(group, terrs);
        }

        return groups;
    }

    /**
     * Checks if an order is legal.
     * @param order is the order to check.
     * @return null, if the order is legal;
     *         a String indicating the problem, if not.
     */
    public String checkBasicOrder(BasicOrder order) {
        return basicOrderChecker.checkOrder(order, this);
    }

    /**
     * Add unit to all territories.
     * @param num is the number of units to add to every territory.
     */
    public void addUnitToAll(int num) {
        if (num < 0) {
            throw new IllegalArgumentException(NOT_POSITIVE_MSG);
        }
        for (Territory terr : getAllTerritories()) {
            terr.addUnit(num);
        }
    }

    /**
     * Checks if a player has lost the game by losing all his territories.
     * @param playerName is the player's name.
     * @return true, if player has lost.
     *         false, if not.
     */
    public boolean checkLost(String playerName) {
        for (Territory terr : getAllTerritories()) {
            if (terr.getOwner().getName().equals(playerName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the game has ended, that is, a player owns all the territories.
     * @return true, if the game ends.
     *         false, if not.
     */
    public boolean isGameEnd() {
        // if a territory has an owner other than the first one, then the game hasn't ended.
        Player player = territories.getVertices().get(0).getOwner();
        for (Territory terr : getAllTerritories()) {
            if (!terr.getOwner().equals(player)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the name of the game winner.
     * @return winner's the name, if the game has ended.
     *         null, if there is no winner yet.
     */
    public String getWinner() {
        if (isGameEnd()) {
            return territories.getVertices().get(0).getOwner().getName();
        }
        return null;
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other.getClass().equals(getClass())) {
            World otherWorld = (World)other;
            return otherWorld.territories.equals(territories);
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return territories.toString() + 
               basicOrderChecker.toString() +
               rnd.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

}