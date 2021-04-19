package edu.duke.ece651.group4.RISK.shared;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.io.Serializable;

import static edu.duke.ece651.group4.RISK.shared.Constant.PLACEMENT_DONE;

/**
 * This class models the world which constitutes a certain number of territories
 * connected with each other.
 * <p>
 * Maintains:
 * 1. territories in a graph structure;
 * 2. Info of all players in the world;
 * 3. a war report that stores the result of battles happened on one turn.
 * 4. an order checker that checks validity of move & attack orders;
 * 5. a random seed.
 */
public class World implements Serializable {
    /**
     * Auto-generated serialVersionUID
     */
    protected static final long serialVersionUID = 4696428798493622247L;
    /**
     * Error messages
     */
    protected final String INDIVISIBLE_MSG = "Number of territories is not divisible by number of groups.";
    protected final String NOT_POSITIVE_MSG = "Number should be positive.";
    protected final String TERRITORY_NOT_FOUND_MSG = "The territory specified by the name '%s' is not found.";
    protected final String NO_PLAYERINFO_MSG = "Player info of %s is not found.";
    /**
     * All territories in the world. Implemented with a graph structure.
     */
    protected Graph<Territory> territories;
    /**
     * A mapping of player's name to his/her info.
     */
    protected List<PlayerInfo> playerInfos;
    /**
     * A matrix storing the alliance relationship of players.
     */
    protected boolean[][] allianceMatrix;
    /**
     * Battle report
     */
    protected String report;
    /**
     * Order checker
     */
    protected final OrderChecker orderChecker;
    /**
     * Random seed to use with random division of territories.
     */
    protected final Random rnd;

    protected int roomID;

    /**
     * Construct a default world with an empty graph.
     */
    public World() {
        this(new Graph<Territory>());
    }

    /**
     * Construct world with a graph.
     *
     * @param terrs
     */
    public World(Graph<Territory> terrs) {
        this(terrs, new Random());
    }

    /**
     * Construct world with a graph and a random seed.
     *
     * @param terrs  is the number of territories.
     * @param random is the random seed.
     */
    public World(Graph<Territory> terrs, Random random) {
        this(terrs, new ArrayList<PlayerInfo>(), random, "");
    }

    protected World(Graph<Territory> terrs,
                    List<PlayerInfo> playerInfos,
                    Random random,
                    String report) {
        this.territories = terrs;
        this.playerInfos = playerInfos;
        this.orderChecker = new OrderChecker();
        this.rnd = random;
        this.report = report;
        this.roomID=0;
    }

    /**
     * Creates a world, specify a number of total territories and a random seed. The
     * territories created will share a random seed with the world. Territory names
     * are: 1, 2, 3, ... Number of total connections is random, and is propotional
     * to number of territories.
     *
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
     *
     * @param numTerrs is the number of territories.
     */
    public World(int numTerrs) {
        this(new Graph<Territory>(), new Random());

        for (int i = 1; i <= numTerrs; i++) {
            addTerritory(new Territory(String.format("%c", 'A' + i)));
        }
        territories.addRandomEdges(numTerrs, new Random());
    }

    /**
     * Overloading constructor that takes an array of territories names.
     *
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
     * Creates a deep copy of a world object.
     */
    public World clone() {
        List<Territory> old = territories.getVertices();
        List<Territory> cpy = new ArrayList<>();
        for (Territory item : old) {
            cpy.add(item.clone());
        }
        List<Integer> weightsCopy = territories.cloneWeights();
        boolean[][] adjMatrixCopy = territories.cloneAdjMatrix();
        World cpyWorld = new World(new Graph<>(cpy, weightsCopy, adjMatrixCopy),
                clonePlayerInfos(), this.rnd,
                new String(this.report == null ? null : new String(this.report)));
        cpyWorld.setRoomID(this.roomID);
        return cpyWorld;
    }

    /**
     * Creates a deep copy of playerInfos.
     */
    protected List<PlayerInfo> clonePlayerInfos() {
        List<PlayerInfo> newList = new ArrayList<>();
        for (PlayerInfo pInfo : playerInfos) {
            newList.add(pInfo.clone());
        }
        return newList;
    }

    /**
     * Get the total number of territories in the world.
     *
     * @return size of the world.
     */
    public int size() {
        return territories.size();
    }

    /**
     * Get the total number of players registered in the world.
     *
     * @return total number of players.
     */
    public int getNumPlayers() {
        return playerInfos.size();
    }

    /**
     * Get all the territories in the world.
     *
     * @return a list of all territories in the world.
     */
    public List<Territory> getAllTerritories() {
        return territories.getVertices();
    }

    /**
     * Get all the territories that are adjacent to a certain territory.
     *
     * @param terr is the territory to search adjacents.
     * @return a list of adjacent territories.
     */
    public List<Territory> getAdjacents(Territory terr) {
        return territories.getAdjacentVertices(terr);
    }

    /**
     * Get all the territories that are adjacent to a territory of certain name.
     *
     * @param terrName is the name of territory to search adjacents.
     * @return a list of adjacent territories.
     */
    public List<Territory> getAdjacents(String terrName) {
        return territories.getAdjacentVertices(findTerritory(terrName));
    }

    /**
     * Set a random seed on a territory.
     *
     * @param terr is the territory to set random seed.
     * @param seed is a random seed.
     */
    public void setRandom(Territory terr, Random seed) {
        terr.setRandom(seed);
    }

    /**
     * Set a random seed on a territory with certain name.
     *
     * @param terrName is the name of the territory to set random seed.
     * @param seed     is a random seed.
     */
    public void setRandom(String terrName, Random seed) {
        findTerritory(terrName).setRandom(seed);
    }


    /**
     * Get battle report.
     *
     * @return A report of all battles happened in one turn.
     */
    public String getReport() {
        return report;
    }

    protected void setReport(String report) {
        this.report = report;
    }

    /**
     * Add a territory to the world.
     *
     * @param terr is the territory to add.
     */
    public void addTerritory(Territory terr) {
        territories.addVertex(terr);
    }

    /**
     * Add connection between two adjacent territories so that they become adjacent.
     *
     * @param terr1 is a territory.
     * @param terr2 is the other territory.
     */
    public void addConnection(Territory terr1, Territory terr2) {
        territories.addEdge(terr1, terr2);
    }

    /**
     * Add connection between two adjacent territories by the name of these
     * territories.
     *
     * @param terrName1 is the name of a territory.
     * @param terrName2 is the name of the other territory.
     */
    public void addConnection(String terrName1, String terrName2) {
        addConnection(findTerritory(terrName1), findTerritory(terrName2));
    }

    /**
     * Register a player and a default info in the world.
     *
     * @param playerName is the player's name.
     */
    public void registerPlayer(String playerName) {
        registerPlayer(new PlayerInfo(playerName));
    }

    /**
     * Register a player's info in the world.
     *
     * @param pInfo is the player info to register.
     */
    public void registerPlayer(PlayerInfo pInfo) {
        playerInfos.add(pInfo);
    }

    /**
     * Find the info of a player by his name.
     *
     * @param playerName is the name of the player.
     * @return that player's playerInfo.
     */
    public PlayerInfo getPlayerInfoByName(String playerName) {
        for (PlayerInfo pInfo : playerInfos) {
            if (pInfo.getName().equals(playerName)) {
                return pInfo;
            }
        }
        throw new IllegalArgumentException(String.format(NO_PLAYERINFO_MSG, playerName));
    }

    /**
     * Get all names of players that are still in the game (which means he hasn't lose)
     * @return names of all players that are still in the game.
     */
    public Set<String> getAllPlayerNames() {
        return playerInfos.stream()
                .map(playerInfo -> playerInfo.getName())
                .filter(name -> !checkLost(name))
                .collect(Collectors.toSet());
    }

    /**
     * Get a list of all territories owned by a player.
     *
     * @param playerName is a player's name.
     * @return all territories owned by a player.
     */
    public List<Territory> getTerritoriesOfPlayer(String playerName) {
        List<Territory> ans = new ArrayList<>();
        for (Territory terr : getAllTerritories()) {
            if (terr.getOwner().getName().equals(playerName)) {
                ans.add(terr);
            }
        }
        return ans;
    }

    /**
     * Get a list of all territories that are not owned by a player.
     *
     * @param playerName is a player's name.
     * @return all territories owned by a player.
     */
    public List<Territory> getTerritoriesNotOfPlayer(String playerName) {
        List<Territory> ans = new ArrayList<>();
        for (Territory terr : getAllTerritories()) {
            if (!terr.getOwner().getName().equals(playerName)) {
                ans.add(terr);
            }
        }
        return ans;
    }

    /**
     * Station troop to a territory.
     *
     * @param terrName is the territory name.
     * @param troop    is a Troop object.
     */
    public void stationTroop(String terrName, Troop troop) {
        this.report = PLACEMENT_DONE;
        Territory terr = findTerritory(terrName);
        terr.setOwnerTroop(troop.checkTroopSize(), troop.getOwner());
    }

    /**
     * Station troop to a territory by specifying territory name and population.
     *
     * @param terrName   is the territory name.
     * @param population is the population of the troop.
     */
    public void stationTroop(String terrName, int population) {
        this.report = PLACEMENT_DONE;
        Territory terr = findTerritory(terrName);
        terr.setOwnerTroop(population, terr.getOwner());
    }

    /**
     * Overloaded function to Calculates the shortest path length between 2 vertices.
     *
     * @param startName
     * @param endName
     * @return
     */
    protected int calculateShortestPath(String startName, String endName) {
        return calculateShortestPath(findTerritory(startName), findTerritory(endName));
    }

    /**
     * Calculates the shortest path length between 2 vertices.
     *
     * @param start is the starting vertex.
     * @param end   is the ending vertex.
     * @return length of the shortest path .
     */
    protected int calculateShortestPath(Territory start, Territory end) {
        String NOT_REACHABLE_MSG = "Cannot reach from start to end.";
        /*
         * shortest distances from start to all vertices
         */
        Map<Territory, Integer> distances = new HashMap<>();
        /*
         * initialize distances: start: as its weight others: infinity (substitute with
         * Integer.MAX_VALUE)
         */
        for (Territory terr : getAllTerritories()) {
            if (terr.equals(start)) {
                distances.put(terr, terr.getArea());
            } else {
                distances.put(terr, Integer.MAX_VALUE);
            }
        }
        /*
         * add the start vertex to unvisited.
         */
        Set<Territory> unvisited = new HashSet<>();
        Set<Territory> visited = new HashSet<>();
        unvisited.add(start);
        /*
         * While the unvisited is not empty: 1. Choose an unvisited vertex, which should
         * be the one with the lowest distance from the start, and remove it from
         * unvisited. 2. Calculate new distances to direct neighbors by keeping the
         * lowest distance at each evaluation. 3. Add neighbors that are not yet visited
         * to the unvisited set.
         */
        while (unvisited.size() != 0) {
            Territory current = getSmallestDistanceTerr(unvisited, distances);
            unvisited.remove(current);
            for (Territory adjacent : getAdjacents(current)) {
                if (!visited.contains(adjacent) && adjacent.getOwner().equals(start.getOwner())) {
                    distances.put(adjacent,
                            Math.min(distances.get(current) + adjacent.getArea(), distances.get(adjacent)));
                    unvisited.add(adjacent);
                }
            }
            visited.add(current);
        }

        if (distances.get(end) == Integer.MAX_VALUE) {
            throw new IllegalArgumentException(NOT_REACHABLE_MSG);
        }
        return distances.get(end);
    }

    /**
     * Find the smallest distance territory is a set.
     * @param toVisit is a set of territories.
     * @param distances is the distances from the staring territory.
     * @return the smallest distance territory.
     */
    protected Territory getSmallestDistanceTerr(Set<Territory> toVisit, Map<Territory, Integer> distances) {
        Territory smallestDistanceVertex = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Territory vertex : toVisit) {
            int dist = distances.get(vertex);
            if (dist < lowestDistance) {
                lowestDistance = dist;
                smallestDistanceVertex = vertex;
            }
        }
        return smallestDistanceVertex;
    }

    /**
     * Calculate the quantity of resources consumed by a move order.
     * <p>
     * A. Each move order consumes "food" resources. Specifically, the cost of each
     * move is (total size of territories moved through) * (number of units moved).
     * <p>
     * B. The minimum total cost valid path is picked.
     *
     * @param order is the move order.
     * @return quantity of consumed resources.
     */
    protected int calculateMoveConsumption(MoveOrder order) {
        Territory start = findTerritory(order.getSrcName());
        Territory end = findTerritory(order.getDesName());
        Troop troop = order.getActTroop();

        int lengthShortestPath = calculateShortestPath(start, end);
        int nUnits = troop.size();
        int nConsumedResource = lengthShortestPath * nUnits;

        return nConsumedResource;
    }

    /**
     * A player consume food resources of a move.
     * @param order      is the move order.
     * @param playerName is the name of the player who committed this order.
     */
    public void consumeResourceOfMove(MoveOrder order, String playerName) {
        getPlayerInfoByName(playerName).consumeFood(calculateMoveConsumption(order));
    }

    /**
     * Moves a troop to a different a territory. Owner of the troop is not checked.
     * Also checks if the troop size is valid to send from the starting territory.
     * <p>
     * Consumes food resource.
     *
     * @param order      is a move order.
     * @param playerName is the player's name who committed this order.
     */
    public void moveTroop(MoveOrder order, String playerName) { // TODO: coupled upgrade and resource consumption
        Territory start = findTerritory(order.getSrcName());
        Territory end = findTerritory(order.getDesName());
        Troop troop = order.getActTroop();
        // check error of move order
        String errorMsg = orderChecker.checkOrder(order, this, getPlayerInfoByName(playerName));
        if (errorMsg != null) {
            throw new IllegalArgumentException(errorMsg);
        }
        // also check if player has enough food
        consumeResourceOfMove(order, playerName);
        // moves troop
        end.sendInTroop(start.sendOutTroop(troop));
    }

    /**
     * Calculate the quantity of resources consumed by an attack order. An attack
     * order costs 1 "food" resource per unit attacking.
     *
     * @param order is the attack order.
     * @return quantity of consumed resources.
     */
    protected int calculateAttackConsumption(AttackOrder order) {
        return order.getActTroop().size();
    }

    /**
     * A player consume food resources of an attack.
     * @param order      is the attack order.
     * @param playerName is the name of the player who committed this order.
     */
    public void consumeResourceOfAttack(AttackOrder order, String playerName) {
        getPlayerInfoByName(playerName).consumeFood(calculateAttackConsumption(order));
    }

    /**
     * Sends a troop to a territory with different owner, in order to engage in
     * battle on that territory. Also checks if the troop size is valid to send from
     * the starting territory.
     * <p>
     *
     * @param order      is the attack order.
     * @param playerName is the name of the player who committed this order.
     */
    public void attackATerritory(AttackOrder order, String playerName) { // TODO: coupled upgrade and resource consumption
        Territory start = findTerritory(order.getSrcName());
        Territory end = findTerritory(order.getDesName());
        Troop troop = order.getActTroop();
        // check error of attack order
        String errorMsg = orderChecker.checkOrder(order, this, getPlayerInfoByName(playerName));
        if (errorMsg != null) {
            throw new IllegalArgumentException(errorMsg);
        }
        // also check if player has enough food
        consumeResourceOfAttack(order, playerName);
        // moves troop
        end.sendInEnemyTroop(start.sendOutTroop(troop));
    }

    /**
     * Try to upgrade troop on a territory. If successful, tech resource will be
     * consumed.
     *
     * @param utOrder    is an UpgradeTroopOrder.
     * @param playerName is the name of the player who committed this order.
     */
    public void upgradeTroop(UpgradeTroopOrder utOrder, String playerName) {
        Territory terr = findTerritory(utOrder.getSrcName());
        PlayerInfo pInfo = getPlayerInfoByName(playerName);
        if (utOrder.getLevelAfter() > pInfo.techLevelInfo.getTechLevel()) {
            throw new IllegalArgumentException("Cannot upgrade beyond your tech level.");
        }
        try {
            int remainder = terr.upgradeTroop(utOrder, pInfo.getTechQuantity());
            int consumption = pInfo.getTechQuantity() - remainder;
            getPlayerInfoByName(playerName).consumeTech(consumption); // TODO: coupled upgrade and consumption
        } catch (IllegalArgumentException iae) {
            throw iae;
        }
    }

    /**
     * Consumes tech resource of an upgrade tech order.
     * @param upgradeTechOrder
     * @param playerName
     */
    public void consumeResourceOfPlayerTechUpgrade(UpgradeTechOrder upgradeTechOrder, String playerName) {
        getPlayerInfoByName(playerName).consumeResourceOfTechUpgrade(upgradeTechOrder.getNLevel());
    }

    /**
     * Try upgrade a player's tech level using an upgrade tech order.
     * <p>
     * Consumes tech resource.
     *
     * @param uTechOrder is an upgrade tech order
     * @param playerName is a player's name.
     */
    public void upgradePlayerTechLevelBy(UpgradeTechOrder uTechOrder, String playerName) {
        consumeResourceOfPlayerTechUpgrade(uTechOrder, playerName);
        getPlayerInfoByName(playerName).upgradeTechLevelBy(uTechOrder.getNLevel());
    }

    public void doUpgradeTechResourceConsumption(UpgradeTechOrder uTechOrder, String playerName) {
        consumeResourceOfPlayerTechUpgrade(uTechOrder, playerName);
    }

    /**
     * Try upgrade a player's tech level by 1.
     * <p>
     * Consumes tech resource.
     *
     * @param playerName is a player's name.
     */
    public void upgradePlayerTechLevelBy1(String playerName) {
        upgradePlayerTechLevelBy(new UpgradeTechOrder(1), playerName);
    }

    //TODO: form and break alliance functions
//    /**
//     * Two players form an alliance.
//     * @param playerName1 is the name of a player.
//     * @param playerName2 is the name of another player.
//     */
//    public void formAlliance(String playerName1, String playerName2) {
//        playerInfos.get(playerName1).formAlliance(playerName2);
//        playerInfos.get(playerName2).formAlliance(playerName1);
//    }
//
//    /**
//     * Two players break from an alliance.
//     * @param playerName1 is the name of a player.
//     * @param playerName2 is the name of another player.
//     */
//    public void breakAlliance(String playerName1, String playerName2) {
//        playerInfos.get(playerName1).breakAlliance(playerName2);
//        playerInfos.get(playerName2).breakAlliance(playerName1);
//    }

    /**
     * Iterate over all territories around the world, and do battles on them.
     *
     * @return A summary of battle info on all territories.
     */
    public String doAllBattles() {
        StringBuilder ans = new StringBuilder();
        for (Territory terr : territories.getVertices()) {
            ans.append(terr.doBattles());
        }
        this.report = ans.toString();
        return report;
    }

    /**
     * Requirement: At the end of turn, all players gain resources from the
     * territories he owns.
     */
    public void allPlayersGainResources() {
        for (PlayerInfo pInfo : playerInfos) {
            String playerName = pInfo.getName();
            for (Territory terr : getTerritoriesOfPlayer(playerName)) {
                pInfo.gainFood(terr.getFoodSpeed());
                pInfo.gainTech(terr.getTechSpeed());
            }
        }
    }

    /**
     * Add unit to all territories.
     * <p>
     * Requirement: At the end of every turn, add a level 0 unit to every territory.
     *
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
     * Add level 0 units to a territory.
     *
     * @param playerName is the name of the player who commits this action.
     * @param terrName   is the name of the territory.
     * @param num        is the number of level 0 units added to this territory.
     */
    public void addUnitToATerritory(String playerName, String terrName, int num) {
        if (num < 0) {
            throw new IllegalArgumentException(NOT_POSITIVE_MSG);
        }
        Territory terr = findTerritory(terrName);
        if (!playerName.equals(terr.getOwner().getName())) {
            throw new IllegalArgumentException("Not your territory.");
        }
        terr.addUnit(num);
    }

    /**
     * Check if two territories are adjacent to each other
     *
     * @param terr1 is a territory.
     * @param terr2 is the other territory.
     * @return true, if two territories are adjacent; false, if not.
     */
    public boolean checkIfAdjacent(Territory terr1, Territory terr2) {
        return territories.isAdjacent(terr1, terr2);
    }

    /**
     * Check if two territories are adjacent to each other by their names.
     *
     * @param name1 is the name of one territory to check.
     * @param name2 is the name of thr other territory.
     * @return true, if two territories are adjacent; false, if not.
     */
    public boolean checkIfAdjacent(String name1, String name2) {
        return checkIfAdjacent(findTerritory(name1), findTerritory(name2));
    }

    /**
     * Divide territories into n equal groups.
     *
     * @param nGroup is the number of groups the world is divided into.
     * @return a HashMap. The mapping being: group number -> grouped territories.
     * NOTE: group number starts from 0.
     */
    public Map<Integer, List<Territory>> divideTerritories(int nGroup) {
        // check if nGroup is an integer is greater than 0
        if (nGroup <= 0) {
            throw new IllegalArgumentException(NOT_POSITIVE_MSG);
        }
        // check if size / nGroup is an integer
        else if (territories.size() % nGroup != 0) {
            throw new IllegalArgumentException(INDIVISIBLE_MSG);
        }
        // initialize an index array
        int nTerritories = territories.size();
        int randomInds[] = IntStream.range(0, nTerritories).toArray();
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
     * Checks if a player has lost the game by losing all his territories.
     *
     * @param playerName is the player's name.
     * @return true, if player has lost. false, if not.
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
     *
     * @return true, if the game ends. false, if not.
     */
    public boolean isGameEnd() {
        // if a territory has an owner other than the first one, then the game hasn't
        // ended.
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
     *
     * @return winner's the name, if the game has ended. null, if there is no winner
     * yet.
     */
    public String getWinner() {
        if (isGameEnd()) {
            return territories.getVertices().get(0).getOwner().getName();
        }
        return null;
    }

    /**
     * Finds a territory by its name. If the territory exists, returns that
     * territory of that name. If not, an exception will be thrown.
     *
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

    @Override
    public boolean equals(Object other) {
        if (other != null && other.getClass().equals(getClass())) {
            World otherWorld = (World) other;
            return otherWorld.territories.equals(territories);
        } else {
            return false;
        }
    }

    /**
     * Calls Territory & PlayerInfo toString function
     */
    @Override
    public String toString() {
        StringBuilder ans = new StringBuilder();
        for (Territory terr : getAllTerritories()) {
            ans.append(terr.toString());
            ans.append("\n");
        }
        for (PlayerInfo pInfo : playerInfos) {
            ans.append(pInfo.toString());
            ans.append("\n");
        }
        return ans.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public void setRoomID(int id){
        this.roomID=id;
    }

    public int getRoomID(){
        return this.roomID;
    }

}