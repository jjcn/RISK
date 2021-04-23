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
    protected final String INDIVISIBLE_MSG =
            "Number of territories is not divisible by number of groups.";
    protected final String NOT_POSITIVE_MSG =
            "Number should be positive.";
    protected final String TERRITORY_NOT_FOUND_MSG =
            "The territory specified by the name '%s' is not found.";
    protected final String NO_PLAYERINFO_MSG =
            "Player info of %s is not found.";
    protected static final String CANT_ALLY_WITH_ONESELF =
            "Cannot ally with yourself.";
    protected static final String CANT_FORM_ALLIANCE_MSG =
            "Cannot form alliance. %s has already reached a maximum number of allies: %d.";
    protected static final String CANT_BREAK_ALLIANCE_MSG =
            "Cannot break alliance. %s and %s are not alliances.";

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
    protected Boolean[][] allianceMatrix;
    /**
     * Battle report. Also used to check the status of the world.
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
    /**
     *
     */
    protected int roomID;
    /**
     * A counter that keeps track of current turn number.
     */
    protected int nTurn;

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
        this(terrs, new ArrayList<PlayerInfo>(), new Boolean[0][0], random, "");
    }

    protected World(Graph<Territory> terrs,
                    List<PlayerInfo> playerInfos,
                    Boolean[][] allianceMatrix,
                    Random random,
                    String report) {
        this.territories = terrs;
        this.playerInfos = playerInfos;
        this.allianceMatrix = allianceMatrix;
        this.orderChecker = new OrderChecker();
        this.rnd = random;
        this.report = report;
        this.roomID = 0;
        this.nTurn = 1;
    }

    /**
     * Creates a world, specify a number of total territories and a random seed. The
     * territories created will share a random seed with the world. Territory names
     * are: 1, 2, 3, ... Number of total connections is random, and is proportional
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
        Boolean[][] adjMatrixCopy = territories.cloneAdjMatrix();
        List<PlayerInfo> playerInfoCopy = clonePlayerInfos();
        Boolean[][] allianceMatrixCopy = cloneAllianceMatrix();
        World cpyWorld = new World(new Graph<>(cpy, weightsCopy, adjMatrixCopy),
                playerInfoCopy,
                allianceMatrixCopy,
                this.rnd,
                new String(this.report == null ? null : new String(this.report)));
        cpyWorld.setRoomID(this.roomID);
        cpyWorld.setTurnNumber(this.nTurn);
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
     * Creates a deep copy of allianceMatrix.
     */
    protected Boolean[][] cloneAllianceMatrix() {
        int n = allianceMatrix.length;
        Boolean[][] cpy = new Boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cpy[i][j] = allianceMatrix[i][j];
            }
        }
        return cpy;
    }

    /**
     * Expand the alliance matrix by 1 on both dimensions.
     */
    protected void expandAllianceMatrixBy1() {
        int n = allianceMatrix.length;
        Boolean[][] newAllianceMatrix = new Boolean[n + 1][n + 1];
        for (int i = 0; i < n + 1; i++) {
            for (int j = 0; j < n + 1; j++) {
                newAllianceMatrix[i][j] = false;
                if (i < n && j < n) {
                    newAllianceMatrix[i][j] = allianceMatrix[i][j];
                }
            }
        }
        allianceMatrix = newAllianceMatrix;
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
     * Get all the territories within a range from a territory.
     * @param start is the name of a territory.
     * @param range is the max distance from this territory.
     * @return a list of all territories whose distance from the a territory
     *         is smaller or equal to range.
     */
    public List<Territory> getAllTerritoriesWithinRange(Territory start, int range) {
        List<Territory> ans = new ArrayList<>();
        Set<Territory> allTerritoriesSet = new HashSet<>();
        allTerritoriesSet.addAll(getAllTerritories());
        for (Territory end : getAllTerritories()) {
            if (calculateShortestPath(start, end, allTerritoriesSet) <= range) {
                ans.add(end);
            }
        }
        return ans;
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

    public int getRoomID(){
        return this.roomID;
    }

    public void setRoomID(int id){
        this.roomID = id;
    }

    public int getTurnNumber() {
        return nTurn;
    }

    public void setTurnNumber(int nTurn) { this.nTurn = nTurn; }

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
        expandAllianceMatrixBy1();
        playerInfos.add(pInfo);
    }

    /**
     * Find the info of a player by his name.
     *
     * @param playerName is the name of the player.
     * @return that player's playerInfo.
     */
    public PlayerInfo getPlayerInfoByName(String playerName) {
        for (int i = 0; i < playerInfos.size(); i++) {
            PlayerInfo pInfo = playerInfos.get(i);
            if (pInfo.getName().equals(playerName)) {
                return pInfo;
            }
        }
        throw new IllegalArgumentException(String.format(NO_PLAYERINFO_MSG, playerName));
    }

    /**
     * Get all names of players that are still in the game (which means he hasn't lose)
     *
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
            if (terr.getOwnerName().equals(playerName)) {
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
            if (!terr.getOwnerName().equals(playerName)) {
                ans.add(terr);
            }
        }
        return ans;
    }

    /**
     * Get the territories with a player's troop stationed on it.
     * @param playerName is a player's name.
     * @return all territories that is stationed by this player's troop.
     */
    public List<Territory> getTerritoriesWithMyTroop(String playerName) {
        List<Territory> ans = new ArrayList<>();
        for (Territory terr : getAllTerritories()) {
            if (terr.getOwnerName().equals(playerName)) {
                ans.add(terr);
            }
            try {
                String allianceName = terr.getAllianceName();
                if (allianceName.equals(playerName)) {
                    ans.add(terr);
                }
            } catch (IllegalArgumentException e) {}
        }
        return ans;
    }

    /**
     * Get a list of all territories a player is allowed to move through.
     *
     * @param playerName is a player's name.
     * @return all territories owned by a player and his alliance.
     */
    public List<Territory> getTerritoriesOfPlayerAndAlliance(String playerName) {
        List<Territory> ans = new ArrayList<>();
        for (Territory terr : getAllTerritories()) {
            if (canMoveThrough(playerName, terr)) {
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
        Territory terr = findTerritory(terrName);
        terr.setOwnerTroop(population, terr.getOwner());
    }

    /**
     * Set the report to PLACEMENT_DONE.
     */
    public void setReportToPlacementDone() {
        this.report = Constant.PLACEMENT_DONE;
    }

    /**
     * Checks if a player's troop can move through a territory.
     * @param moverName is the name of the player who tries to move through this territory.
     * @param terr is the name of the territory.
     * @return true, if this player's troop is allowed to move through this territory;
     *         false, if not.
     */
    public boolean canMoveThrough(String moverName, Territory terr) {
        return getAllowedOwnerNames(moverName).contains(terr.getOwnerName());
    }

    /**
     * Get all names of players that allow a player to move through their territories.
     * @param playerName is the name of a player.
     * @return a set that contains the names of all players that
     *         allow a player to move through their territories.
     */
    public Set<String> getAllowedOwnerNames(String playerName) {
        Set<String> allowedOwnerNames = getAllianceNames(playerName);
        allowedOwnerNames.add(playerName);
        return allowedOwnerNames;
    }

    /**
     * Get all the territories that allows a player to move through.
     * @param playerName is the name a of player.
     * @return A set of all the territories that allows a player to move through.
     */
    public Set<Territory> getAllowedTerritories(String playerName) {
        Set<Territory> ans = new HashSet<>();
        Set<String> allowedOwnerNames = getAllowedOwnerNames(playerName);
        for (Territory terr : getAllTerritories()) {
            if (allowedOwnerNames.contains(terr.getOwnerName())) {
                ans.add(terr);
            }
        }
        return ans;
    }

    /**
     * Overloaded function to calculate the shortest path length between 2 territories
     * for a certain player.
     *
     * @param start is the starting territory.
     * @param end is the ending territory.
     * @return the shortest path between two territories for a certain player.
     */
    protected int calculateShortestPath(Territory start, Territory end, String playerName) {
        Set<Territory> allowedTerrs = getAllowedTerritories(playerName);
        return calculateShortestPath(start, end, allowedTerrs);
    }

    protected int calculateShortestPath(String startName, String endName, String playerName) {
        return calculateShortestPath(findTerritory(startName), findTerritory(endName), playerName);
    }

    /**
     * Overloaded function to calculate the shortest path between two territories.
     *
     * @param start is the starting territory.
     * @param end is the ending territory.
     * @return the shortest path between two territories.
     */
    protected int calculateShortestPath(Territory start, Territory end) {
        Set<Territory> allTerrs = new HashSet<>();
        allTerrs.addAll(getAllTerritories());
        return calculateShortestPath(start, end, allTerrs);
    }

    protected int calculateShortestPath(String startName, String endName) {
        return calculateShortestPath(findTerritory(startName), findTerritory(endName));
    }

    /**
     * Calculates the shortest path length between 2 territories for certain territories allowed.
     *
     * @param start is the starting territory.
     * @param end   is the ending territory.
     * @param allowedTerrs is the territories allowed to move through.
     * @return length of the shortest path.
     */
    protected int calculateShortestPath(Territory start, Territory end, Set<Territory> allowedTerrs) {
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
         * While the unvisited is not empty:
         * 1. Choose an unvisited vertex, which should
         * be the one with the lowest distance from the start,
         * and remove it from unvisited.
         * 2. Calculate new distances to direct neighbors by keeping the
         * lowest distance at each evaluation.
         * 3. Add neighbors that are not yet visited to the unvisited set.
         */
        while (unvisited.size() != 0) {
            Territory current = getSmallestDistanceTerr(unvisited, distances);
            unvisited.remove(current);
            for (Territory adjacent : getAdjacents(current)) {
                if (!visited.contains(adjacent) && allowedTerrs.contains(adjacent)) {
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
     *
     * @param toVisit   is a set of territories.
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
        String moverName = start.getOwnerName();

        int lengthShortestPath = calculateShortestPath(start, end, moverName);
        int nUnits = troop.size();
        double troopSpeed = troop.checkSpeed();
        int nConsumedResource = (int) (lengthShortestPath * nUnits * troopSpeed);

        return nConsumedResource;
    }

    /**
     * A player consume food resources of a move.
     * Also checks if a player has enough food to consume.
     *
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
    public void moveTroop(MoveOrder order, String playerName) {
        Territory start = findTerritory(order.getSrcName());
        Territory end = findTerritory(order.getDesName());
        Troop troop = order.getActTroop();
        // check error of move order
        String errorMsg = orderChecker.checkOrder(order, playerName, World.this);
        if (errorMsg != null) {
            throw new IllegalArgumentException(errorMsg);
        }
        // check if player has enough food
        consumeResourceOfMove(order, playerName);
        // moves troop.
        // Order checker already performed checks of whether the troop is allowed to be sent into destination.
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
     *
     * @param order      is the attack order.
     * @param playerName is the name of the player who committed this order.
     */
    public void consumeResourceOfAttack(AttackOrder order, String playerName) {
        getPlayerInfoByName(playerName).consumeFood(calculateAttackConsumption(order));
    }

    /**
     * Try to send all the troop of player B stationed on player A's territories
     * back to the nearest territory owned by B.
     *
     * A and B are in one alliance.
     * A: name of the attacker, attackerName
     * B: name of player who is attacked, allyName
     *
     * If A breaks an alliance with B, and B has units in A’s territories,
     * then B’s units return to the nearest (break ties randomly) B-owned territory
     * before any other actions are resolved (i.e. are available to defend those territories)
     *
     * @param attackerName is the name of the player who attacks
     * @param allyName is the name of the player who is attacked
     */
    protected void sendBackAllianceTroop(String attackerName, String allyName) {
        for (Territory terr : getTerritoriesOfPlayer(attackerName)) {
            if (terr.hasAllianceTroop()) {
                Territory nearest = getNearestTerritory(terr, allyName);
                Troop kickedOutTroop = terr.kickOut();
                assert(kickedOutTroop != null);
                nearest.sendInTroop(kickedOutTroop);
            }
        }
        breakAlliance(attackerName, allyName);
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
        String startOwnerName = start.getOwnerName();
        Territory end = findTerritory(order.getDesName());
        String endOwnerName = end.getOwnerName();
        Troop troop = order.getActTroop();
        // check error of attack order
        String errorMsg = orderChecker.checkOrder(order, playerName, this);
        if (errorMsg != null) {
            throw new IllegalArgumentException(errorMsg);
        }
        // also check if player has enough food
        consumeResourceOfAttack(order, playerName);
        // check if the destination belongs to committer's ally, if so, break they alliance
        if (getAllianceNames(playerName).contains(endOwnerName)) {
            sendBackAllianceTroop(startOwnerName, endOwnerName);
        }
        // moves troop
        // TODO: can only attack adjacent territories now
        // if it HAS ranged units, send ranged attack to end territory
        // if not, move to end territory
        Troop sendout = troop.hasRanged() ?
                start.sendOutRangedAttack(troop):
                start.sendOutTroop(troop);
        end.sendInEnemyTroop(sendout);
    }

    /**
     * Get a territory's nearest territory that belongs to a certain player.
     * Checks if a path is valid through the move.
     *
     *  B’s units return to the nearest (break ties randomly) B-owned territory
     *  Q1: "nearest" in terms of total territory size?
     *  Q2: If this territory is blocked, can B move back?
     *
     * @param terr is a territory.
     * @return nearest territory with the same owner.
     */
    protected Territory getNearestTerritory(Territory terr, String playerName) {
        Territory nearest = terr;
        List<Territory> allTerritories = getTerritoriesOfPlayer(playerName);
        int smallestDistance = Integer.MAX_VALUE;
        for (Territory terrToCheck : allTerritories) {
            if (!terrToCheck.equals(terr)) {
                int distance = Integer.MAX_VALUE;
                try {
                    distance = calculateShortestPath(terr, terrToCheck, playerName);
                } catch (IllegalArgumentException iae) {

                }
                if (distance < smallestDistance) {
                    smallestDistance = distance;
                    nearest = terrToCheck;
                }
            }
        }
        return nearest;
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
     * Transfer units to another type via a transfer troop order.
     * Level of units stay the same.
     *
     * @param ttOrder is a transfer troop order.
     * @param playerName is the name of the player who gives this order.
     */
    public void transferTroop(TransferTroopOrder ttOrder, String playerName) throws IllegalArgumentException {
        Territory terr = findTerritory(ttOrder.getSrcName());
        String typeBefore = ttOrder.getTypeBefore();
        String typeAfter = ttOrder.getTypeAfter();
        // check if a player can transfer to this type
        if (!isTypeUnlocked(playerName, typeAfter)) {
            throw new IllegalArgumentException(
                    String.format("%s has not unlocked %s.", playerName, typeAfter)
            );
        }
        int unitLevel = ttOrder.getUnitLevel();
        int nUnit = ttOrder.getNUnit();

        int transferCost = terr.transfer(typeBefore, typeAfter, unitLevel, nUnit);
        getPlayerInfoByName(playerName).consumeTech(transferCost);
    }

    /**
     * Consumes tech resource of an upgrade tech order.
     *
     */
    public void consumeResourceOfPlayerTechUpgrade(UpgradeTechOrder uTechOrder, String playerName) {
        getPlayerInfoByName(playerName).consumeResourceOfTechUpgrade(uTechOrder.getNLevel());
    }

    /**
     * Consume tech resource of a tech upgrade.
     *
     * @param uTechOrder is an upgrade tech order.
     * @param playerName is the name of the player who upgrades tech level.
     */
    public void doUpgradeTechResourceConsumption(UpgradeTechOrder uTechOrder, String playerName) {
        consumeResourceOfPlayerTechUpgrade(uTechOrder, playerName);
    }

    /**
     * Try upgrade a player's tech level using an upgrade tech order.
     *
     * @param uTechOrder is an upgrade tech order
     * @param playerName is a player's name.
     */
    public void upgradePlayerTechLevelBy(UpgradeTechOrder uTechOrder, String playerName) {
        getPlayerInfoByName(playerName).upgradeTechLevelBy(uTechOrder.getNLevel());
    }

    /**
     * Try upgrade a player's tech level by 1.
     *
     * @param playerName is a player's name.
     */
    public void upgradePlayerTechLevelBy1(String playerName) {
        upgradePlayerTechLevelBy(new UpgradeTechOrder(1), playerName);
    }

    /**
     * Get a mapping of all player's names to the names of their alliance.
     * @return A map of player name -> alliance names
     */
    protected Map<String, Set<String>> getAllPlayersAlliance() {
        checkIfAllianceSuccess();
        Map<String, Set<String>> ans = new HashMap<>();
        for (PlayerInfo pInfo : playerInfos) {
            String playerName = pInfo.getName();
            ans.put(playerName, getAllianceNames(playerName));
        }
        return ans;
    }

    /**
     * Get the names of all alliances of a player.
     *
     * @param playerName is the name of the player to find alliance.
     * @return a set of names of all alliances of a player.
     */
    public Set<String> getAllianceNames(String playerName) {
        Set<String> ans = new HashSet<>();
        int playerIndex = playerInfos.indexOf(getPlayerInfoByName(playerName));
        for (int i = 0; i < playerInfos.size(); i++) {
            // need mutual approval to form alliance
            if (allianceMatrix[playerIndex][i] == true &&
                    allianceMatrix[i][playerIndex] == true) {
                ans.add(playerInfos.get(i).getName());
            }
        }
        return ans;
    }

    /**
     * Check if two players are allies.
     *
     * @param p1Name is the name of player 1.
     * @param p2Name is the name of player 2.
     * @return true, if p1 and p2 are allies;
     *         false, if not.
     */
    public boolean isAlliance(String p1Name, String p2Name) {
        return getAllianceNames(p1Name).contains(p2Name) &&
                getAllianceNames(p2Name).contains(p1Name);
    }

    /**
     * Check if a player can form another alliance
     * by checking if his number of alliance has reached a limit.
     *
     * @param playerName is the name of the player to check if he can form another alliance.
     */
    protected void checkCanFormAlliance(String playerName) {
        if (getAllianceNames(playerName).size() >= Constant.MAX_ALLOWED_ALLY_NUMBER) {
            throw new IllegalArgumentException(
                    String.format(CANT_FORM_ALLIANCE_MSG, playerName, Constant.MAX_ALLOWED_ALLY_NUMBER)
            );
        }
    }

    /**
     * Player 1 tries forming alliance with player 2.
     * A player can only form ONE alliance of two with another player.
     * Note: Player 2 has to form alliance with player 1 ON THE SAME TURN
     * so they can form an alliance successfully.
     *
     * @param p1Name is the name of player 1.
     * @param p2Name is the name of player 2.
     */
    public void tryFormAlliance(String p1Name, String p2Name) throws IllegalArgumentException {
        if (p1Name.equals(p2Name)) {
            throw new IllegalArgumentException(CANT_ALLY_WITH_ONESELF);
        }
        int p1Index = playerInfos.indexOf(getPlayerInfoByName(p1Name));
        int p2Index = playerInfos.indexOf(getPlayerInfoByName(p2Name));
        // if either of the player already reached alliance number limit, throw exception
        checkCanFormAlliance(p1Name);
        checkCanFormAlliance(p2Name);
        allianceMatrix[p1Index][p2Index] = true;
    }

    /**
     * Check if an alliance is successfully formed between two players.
     *
     * For an alliance to form, two players has to send to each other form alliance ON THE SAME TURN
     */
    protected void checkIfAllianceSuccess() {
        int n = playerInfos.size();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (allianceMatrix[i][j] == true) {
                    if (allianceMatrix[j][i] == false || (i == j)) {
                        allianceMatrix[i][j] = false;
                    }
                }
            }
        }
    }

    /**
     * Check if alliances are successfully formed.
     */
    public void doCheckIfAllianceSuccess() {
        checkIfAllianceSuccess();
    }

    /**
     * Two players break an alliance.
     *
     * @param p1Name is the name of player 1.
     * @param p2Name is the name of player 2.
     */
    public void breakAlliance(String p1Name, String p2Name) {
        int p1Index = playerInfos.indexOf(getPlayerInfoByName(p1Name));
        int p2Index = playerInfos.indexOf(getPlayerInfoByName(p2Name));
        if (allianceMatrix[p1Index][p2Index] == false) {
            throw new IllegalArgumentException(
                    String.format(CANT_BREAK_ALLIANCE_MSG, p1Name, p2Name)
            );
        }
        allianceMatrix[p1Index][p2Index] = false;
        allianceMatrix[p2Index][p1Index] = false;
    }

    /**
     * A Player tries to unlock a new type.
     * @param playerName is the name of a player.
     * @param type is a type name.
     * @throws IllegalArgumentException
     */
    public void unlockType(String playerName, String type) throws IllegalArgumentException {
        getPlayerInfoByName(playerName).unlockType(type);
    }

    /**
     * Get the types a player is allowed to have now.
     * @param playerName is the name of a player.
     * @return a set of all types the player can have now.
     */
    public Set<String> getUnlockedTypes(String playerName) throws IllegalArgumentException {
        return getPlayerInfoByName(playerName).getUnlockedTypes();
    }

    /**
     * Get the types a player can unlock.
     * @param playerName  is the name of a player.
     * @return
     */
    public Set<String> getUnlockableTypes(String playerName) throws IllegalArgumentException {
        return getPlayerInfoByName(playerName).getUnlockableTypes();
    }

    /**
     * Check if a type is unlocked for a certain player.
     *
     * @param playerName is the name of the player.
     * @param type is a type name.
     * @return true, if the type is already unlocked for that player.
     *          false, if not.
     */
    public boolean isTypeUnlocked(String playerName, String type) {
        return getPlayerInfoByName(playerName).isTypeUnlocked(type);
    }

    /**
     * Iterate over all territories around the world, and do battles on them.
     *
     * @return A summary of battle info on all territories.
     */
    public String doAllBattles() {
        // turn number + 1
        nTurn++;
        // update report
        StringBuilder ans = new StringBuilder();
        for (Territory terr : territories.getVertices()) {
            ans.append(terr.doBattlesMix(getAllPlayersAlliance()));
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
        if (!playerName.equals(terr.getOwnerName())) {
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
            if (terr.getOwnerName().equals(playerName)) {
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
            return territories.getVertices().get(0).getOwnerName();
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



//    @Override
//    public boolean equals(Object other) {
//        if (other != null && other.getClass().equals(getClass())) {
//            World otherWorld = (World) other;
//            return otherWorld.territories.equals(territories);
//        } else {
//            return false;
//        }
//    }



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

}