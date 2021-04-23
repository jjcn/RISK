package edu.duke.ece651.group4.RISK.shared;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import java.io.Serializable;

/**
 * This class implements a generic graph data structure.
 * For this particular graph implementation:
 * - edge is not weighted.
 * - only one edge is allowed between 2 vertices.
 */
public class Graph<T> implements Serializable {
    protected static final long serialVersionUID = 4L;
    /**
     * All vertices.
     */
    protected List<T> vertices;
    /**
     * An integer weight is assigned to all vertices.
     */
    protected List<Integer> weights;
    /**
     * boolean matrix that stores adjacency relationship between vertices.
     * true: two vertices are adjacent.
     */
    protected Boolean[][] adjMatrix;
    
    public Graph(List<T> vertices, List<Integer> weights, Boolean[][] adjMatrix) {
        this.vertices = vertices;
        this.weights = weights;
        this.adjMatrix = adjMatrix;
    }

    public Graph(List<T> vertices, Boolean[][] adjMatrix) {
        this(vertices, new ArrayList<>(vertices.size()), adjMatrix);
    }
    
    public Graph() {
        this(new ArrayList<>(), new ArrayList<>(), new Boolean[0][0]);
    }

    public List<Integer> cloneWeights() {
        List<Integer> newList = new ArrayList<>();
        weights.forEach(w -> newList.add(w));
        return newList;
    }

    /**
     * Get a deep copy of adjacency matrix.
     * @return a deep copy of adjacency matrix.s
     */
    public Boolean[][] cloneAdjMatrix() {
        Boolean[][] adjMatrixCopy = new Boolean[size()][size()];
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                adjMatrixCopy[i][j] = adjMatrix[i][j];
            }
        }
        return adjMatrixCopy;
    }

    /**
     * Creates a spanning tree, then add several random connections to it (may be existing ones)
     * @param numNewEdges is the number of new connections introduced to the spanning tree.
     * @param rand is the Random object.
     */
    public void addRandomEdges(int numNewEdges, Random rand) {
        if (size() == 0 || size() == 1) {
            return;
        }
        for (int i = 0; i < size() - 1; i++) {
            adjMatrix[i][i + 1] = true;
            adjMatrix[i + 1][i] = true;
        }
        // add random connections to it
        while (numNewEdges > 0) {
            int i = 0;
            int j = 0;
            while (i == j) { // ensure that i != j
                i = rand.nextInt(size());
                j = rand.nextInt(size());
            }
            adjMatrix[i][j] = true;
            adjMatrix[j][i] = true;
            numNewEdges--;
        }
    }

    /**
     * Get the number of vertices in graph.
     * @return number of vertices in graph;
     */
    public int size() {
        return vertices.size();
    }

    /**
     * Get all vertices in the graph.
     * @return a list of all vertices in the graph.
     */
    public List<T> getVertices() {
        return vertices;
    }

    /**
     * Get all the vertices adjacent to a certain vertex.
     * @param key is the data in the vertex to find adjacents of.
     * @return a list of all adjacent vertices.
     */
    public List<T> getAdjacentVertices(T key) {
        List<T> ans = new ArrayList<>();
        int i = indexOfVertex(key);
        for (int j = 0; j < adjMatrix[i].length; j++) {
            if (adjMatrix[i][j] == true) {
                ans.add(vertices.get(j));
            }
        }
        return ans;
    }

    /**
     * Find the weight of a vertex specified by a key.
     * @param vertex is the vertex.
     * @return weight of that vertex.
     */
    public int getWeight(T vertex) {
    	return weights.get(indexOfVertex(vertex));
    }



    /**
     * Assign an integer weight to a vertex.
     * @param vertex is the vertex to set weight.
     * @param weight is the weight assigned to the vertex.
     */
    public void setWeight(T vertex, int weight) {
        weights.set(indexOfVertex(vertex), weight);
    }

    /**
     * Set the weights to all vertices using a list.
     * @param weights is a list of integer weights.
     */
    public void setWeights(List<Integer> weights) {
    	this.weights = weights;
    }
    
    /**
     * Find the index of a vertex in the list of vertices.
     * @param vertex is the vertex to find index.
     * @return the index of vertex in the list.
     */
    protected int indexOfVertex(T vertex) {
        return vertices.indexOf(vertex);
    }   

    /**
     * Add a vertex to graph.
     * @param vertex is the vertex to add.
     */
    public void addVertex(T vertex) {
        // enlarge the adjacency matrix
        expandAdjMatrixBy1();
        // add to vertices
        vertices.add(vertex);
        weights.add(0);
    }

    protected void expandAdjMatrixBy1() {
        int n = adjMatrix.length;
        Boolean[][] newAdjMatrix = new Boolean[n + 1][n + 1];
        for (int i = 0; i < n + 1; i++) {
            for (int j = 0; j < n + 1; j++) {
                newAdjMatrix[i][j] = false;
                if (i < n && j < n) {
                    newAdjMatrix[i][j] = adjMatrix[i][j];
                }
            }
        }
        adjMatrix = newAdjMatrix;
    }

    /**
     * Remove a vertex from graph.
     * @param key is the data in the vertex to move.
     */

    /*
    public void removeVertex(T key) {
        // TODO: Not required in evol1
    }
    */

    /**
     * Add edge between two vertices.
     * Duplicated edge will not be added.
     * @param v1 is one end of the edge.
     * @param v2 is the other end the edge.
     */
    public void addEdge(T v1, T v2) {
        int i = indexOfVertex(v1);
        int j = indexOfVertex(v2);
        adjMatrix[i][j] = true;
        adjMatrix[j][i] = true;
    }

    /**
     * Remove edge between two vertices.
     * @param key1 is the data in one end of the edge.
     * @param key2 is the data in other end the edge.
     */
    /*
    public void removeEdge(T key1,T key2) {
        // TODO: Not required in evol1
    }
    */

    /**
     * Check if two vertices in the graph is adjacent to each other.
     * @param v1 is a vertex
     * @param v2 is the other vertex
     * @return true, if two vertices are adjacent;
     *         false, if not.
     */
    public boolean isAdjacent(T v1, T v2) {
        int i = indexOfVertex(v1);
        int j = indexOfVertex(v2);
        return adjMatrix[i][j];
    }

    /**
     * Checks if a graph obeys certain rules. The rules are
     * - Each vertex be adjacent to one or more other vertices.
     - The vertices must form a connected graph (all vertices must be reachable from any
     other vertex).
     * @return true, if the graph obeys the rules;
     *         false, if not.
     */
    public boolean isValid() {
        return allHasAdjacents() && isConnectedGraph();
    }

    /**
     * Checks if a graph obeys the following rule:
     * - Each vertex is adjacent to one or more other vertices.
     * @return true, if the graph obeys the rules;
     *         false, if not.
     */
    protected boolean allHasAdjacents() {
        for (int i = 0; i < size(); i++) {
            boolean hasAdjacent = false;
            for (int j = 0; j < size(); j++) { // iterate over a line of adjacency matrix
                hasAdjacent = hasAdjacent || adjMatrix[i][j];
            }
            if (hasAdjacent == false) { // false if no adjacents found with this vertex
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a graph obeys the following rule:
     - The vertices must form a connected graph (all vertices must be reachable from any
     other vertex).
     * @return true, if the graph obeys the rules;
     *         false, if not.
     */
    protected boolean isConnectedGraph() {
        if (size() == 0) {
            return true;
        }
        List<T> vertices = getVertices();
        for (int i = 0; i < size(); i++) {
            if (!hasPath(vertices.get(0), vertices.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if v1 can reach v2.
     * @param start is the starting vertex.
     * @param end is the target vertex.
     * @return true, if v2 is reachable from v1;
     *         false, if not.
     */
    protected boolean hasPath(T start, T end) {
        if (start.equals(end)) {
            return true;
        }

        Queue<T> queue = new LinkedList<>();
        Set<T> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);
        while (queue.size() != 0) {
            T key = queue.poll();
            List<T> adjacents = getAdjacentVertices(key);
            for (T adjacent : adjacents) {
                if (adjacent.equals(end)) {
                    return true;
                }
                if (!visited.contains(adjacent)) {
                    visited.add(adjacent);
                    queue.add(adjacent);
                }
            }
        }
        return false;

    }

    /**
     * Calculates the shortest path length between 2 vertices.
     * @param start is the starting vertex.
     * @param end is the ending vertex.
     * @return length of the shortest path .
     */
    public int calculateShortestPath(T start, T end) {
    	String NOT_REACHABLE_MSG = "Cannot reach from start to end.";
		/*
		 * shortest distances from start to all vertices
		 */
        Map<T, Integer> distances = new HashMap<>();
        /*
         *  initialize distances:
         *  start: as its weight
         *  others: infinity (substitute with Integer.MAX_VALUE)
         */
        for (T vertex : vertices) {
            if (vertex.equals(start)) {
            	distances.put(vertex, getWeight(start));
            }
            else {
            	distances.put(vertex, Integer.MAX_VALUE);
            }
        }
        /*
         * add the start vertex to unvisited.
         */
        Set<T> unvisited = new HashSet<>();
        Set<T> visited = new HashSet<>();
        unvisited.add(start);
        /* While the unvisited is not empty:
		 * 1. Choose an unvisited vertex, 
		 *    which should be the one with the lowest distance from the start,
		 *    and remove it from unvisited.
		 * 2. Calculate new distances to direct neighbors by keeping the lowest distance at each evaluation.
		 * 3. Add neighbors that are not yet visited to the unvisited set.
         */
        while (unvisited.size() != 0) {
            T current = getSmallestDistanceVertex(unvisited, distances);
            unvisited.remove(current);
            for (T adjacent : getAdjacentVertices(current)) {
                if (!visited.contains(adjacent)) {
                    distances.put(adjacent, Math.min(distances.get(current) + getWeight(adjacent), 
                    		                         distances.get(adjacent)));
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
     * Find the smallest distance vertex.
     * @param tovisit
     * @return
     */
    protected T getSmallestDistanceVertex(Set<T> tovisit, Map<T, Integer> distances) {
        T smallestDistanceVertex = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (T vertex : tovisit) {
            int dist = distances.get(vertex);
            if (dist < lowestDistance) {
                lowestDistance = dist;
                smallestDistanceVertex = vertex;
            }
        }
        return smallestDistanceVertex;
    }


    /*@Override
    public boolean equals(Object other) {
        if (other != null && other.getClass().equals(getClass())) {
            Graph<T> otherGraph = (Graph<T>)other;
            boolean adjMatrixEquals = true;
            for (int i = 0; i < adjMatrix.length; i++) {
                for (int j = 0; j < adjMatrix.length; j++) {
                    if (otherGraph.adjMatrix[i][j] != adjMatrix[i][j]) {
                        adjMatrixEquals = false;
                    }
                }
            }
            return otherGraph.vertices.equals(vertices) && adjMatrixEquals;
        }
        else {
            return false;
        }
    }*/


    @Override
    public String toString() {
        return vertices.toString() + adjMatrix.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}