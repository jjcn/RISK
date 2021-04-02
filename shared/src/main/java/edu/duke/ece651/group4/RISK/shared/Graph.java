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
    /**
     * All vertices.
     */
    protected List<T> vertices;
    /**
     * boolean matrix that stores adjacency relationship between vertices.
     * true: two vertices are adjacent.
     */
    protected boolean[][] adjMatrix;
    /**
     * Integer weights are assigned to all vertices.
     */
    protected List<Integer> weights; 

    public Graph(List<T> vertices, boolean[][] adjMatrix, List<Integer> weights) {
        this.vertices = vertices;
        this.adjMatrix = adjMatrix;
        this.weights = weights;
    }

    public Graph(List<T> vertices, boolean[][] adjMatrix) {
        this.vertices = vertices;
        this.adjMatrix = adjMatrix;
        this.weights = new ArrayList<>(vertices.size());
    }
    
    public Graph() {
        this(new ArrayList<>(), new boolean[0][0], new ArrayList<>());
    }

    /**
     * Get a deep copy of adjacency matrix.
     * @return a deep copy of adjacency matrix.s
     */
    public boolean[][] cloneAdjMatrix() {
        boolean[][] adjMatrixCopy = new boolean[size()][size()];
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                adjMatrixCopy[i][j] = adjMatrix[i][j];
            }
        }
        return adjMatrixCopy;
    }

    /**
     * Creates a spanning tree, then add several random connections to it (may with existing ones)
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

    public void setWeight(T vertex, int i) {
        weights.set(indexOfVertex(vertex), i);
    }

    protected int indexOfVertex(T vertex) {
        return vertices.indexOf(vertex);
    }   

    /**
     * Add a vertex to graph.
     * @param vertex is the vertex to add.
     */
    public void addVertex(T vertex) {
        // enlarge the adjacency matrix
        int n = vertices.size();
        boolean[][] newAdjMatrix = new boolean[n + 1][n + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                newAdjMatrix[i][j] = this.adjMatrix[i][j];
            }
        }
        adjMatrix = newAdjMatrix;
        // add to vertices
        vertices.add(vertex);
        weights.add(0);
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
     * Extract the data in a collection of vertices.
     * @param vertices is the vertex to extract data from.
     * @return a list of data.
     */
    /*
    public List<T> toData(List<Vertex> vertices) {
        List<T> ans = new ArrayList<>();
        for (Vertex v : vertices) {
            ans.add(v.getData());
        }
        return ans;
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
        Map<T, Integer> distances = new HashMap<>();
        for (T vertex : vertices) {
            if (vertex != start) {
                distances.put(vertex, Integer.MAX_VALUE);
            }
            else {
                distances.put(vertex, weights.get(indexOfVertex(start)));
            }
        }

        Set<T> tovisit = new HashSet<>();
        Set<T> visited = new HashSet<>();
        tovisit.add(start);
        visited.add(start);
        while (tovisit.size() != 0) {
            // set the node with smallest temp distance as the next vertex to visit
            T current = getSmallestDistanceVertex(tovisit, distances); 
            List<T> adjacents = getAdjacentsVertices(current);
            for (T adjacent : adjacents) {
                if (!visited.contains(adjacent)) {
                    int tentativeDistance = distances.get(current) + findWeight(adjacent);
                    distances.put(adjacent, Math.min(tentativeDistance, findWeight(adjacent)));
                }
            }
            visited.add(current);
        }

        return distances.get(indexOfVertex(end));
    }

    /**
     * Find the weight of a vertex specified by a key.
     * @param key is the vertex.
     * @return weight of that vertex.
     */
    protected int findWeight(T key) {
        return weights.get(indexOfVertex(key));
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

    /*
    @Override
    public Iterator<T> iterator() {
        return new GraphIterator();
    }

    public class GraphIterator implements Iterator<T> {
        private int position = 0;
        @Override
        public boolean hasNext() {
            if (position < getVertices().size()) {
                return true;
            }
            return false;
        }

        @Override
        public T next() {
            if(hasNext()) {
                return getVertices().get(position++);
            }
            return null;
        }

    }
    */

    private List<T> getAdjacentsVertices(T key) {
        return null;
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other.getClass().equals(getClass())) {
            Graph<T> otherGraph = (Graph<T>)other;
            boolean adjMatrixEquals = true;
            for (int i = 0; i < size(); i++) {
                for (int j = 0; j < size(); j++) {
                    if (otherGraph.adjMatrix[i][j] != adjMatrix[i][j]) {
                        adjMatrixEquals = false;
                    }
                }
            }
            return otherGraph.vertices.equals(vertices) &&
                    adjMatrixEquals;
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return vertices.toString() + adjMatrix.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}