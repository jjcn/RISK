package edu.duke.ece651.group4.RISK.shared;

import com.sun.org.apache.xpath.internal.operations.Bool;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GraphTest {
    GraphFactory gf = new GraphFactory();
    
    String[] names = {"Narnia", "Midkemia", "Oz", "Gondor", "Mordor",
	                  "Hogwarts", "Scadrial", "Elantris", "Roshar"};

    @Test
    public void testBoolean() {
        assertEquals(true, Boolean.TRUE);
        Boolean b = true;
        assertEquals(true, b);
        assertEquals(Boolean.TRUE, b);
    }

    @Test
    public void testCreation() {
        Boolean[][] emptyAdjMatrix = new Boolean[0][0];
    	
        Graph<Integer> intGraph = new Graph<>();
        assertEquals(intGraph.size(), 0);
        assertEquals(intGraph.weights.size(), 0);
        assertArrayEquals(intGraph.adjMatrix, emptyAdjMatrix);
        
        Graph<Territory> terrGraph = new Graph<>();
        assertEquals(terrGraph.size(), 0);
        assertEquals(terrGraph.weights.size(), 0);
        assertArrayEquals(terrGraph.adjMatrix, emptyAdjMatrix);
        
        List<Character> vertices = new ArrayList<>(Arrays.asList('a', 'b', 'c'));
        Boolean adjMatrix[][] = {{false, true, true}, {true, false, true}, {true, true, false}};
        Graph<Character> charGraph = new Graph<>(vertices, adjMatrix);
        assertEquals(charGraph.size(), 3);
        assertEquals(charGraph.weights.size(), 0);
        assertArrayEquals(charGraph.adjMatrix, adjMatrix);
    }

    @Test
    public void testArrayCopyOf() {
        int size = 2;
        Boolean[][] mat = {{true, false}, {true, true}};
        Boolean[][] copy = Arrays.copyOf(mat, size);

        assertNotEquals(mat, copy);
    }

    @Test
    public void testArrayCopyConstructor() {
        List<Integer> list = new ArrayList<>(Arrays.asList(0, 1, 2));
        List<Integer> copy = new ArrayList<>(list);
        list.set(0, 5);

        assertEquals(copy.get(0), new Integer(0));
    }

    /**
     * Helper function that prints out adjacent matrix as 0's and 1's.
     * @param mat is a boolean adjacency matrix
     */
    public void print2dArray(Boolean[][] mat) {
        for (int i = 0; i < mat[0].length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                System.out.print((mat[i][j] == true ? 1 : 0) + " ");
            }
            System.out.println();
        }
    }

    /**
     * Helper function that checks if a square matrix is symmetric along its diagnosis.
     * @param mat
     * @return
     */
    public boolean isDiagonalSymmetric(Boolean[][] mat) {
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                if (mat[i][j] != mat[j][i]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if all elements on the diagonsis is boolean true.
     * @param mat
     * @return
     */
    public boolean isDiagonalTrue(Boolean[][] mat) {
        for (int i = 0; i < mat.length; i++) {
            if (mat[i][i] == true) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testAddRandomEdges() {
        Graph<Integer> intGraph = new Graph<>();
        intGraph.addRandomEdges(intGraph.size(), new Random(0));

        for (int i = 1; i <= 9; i++) {
            intGraph.addVertex(i);
        }
        intGraph.addRandomEdges(intGraph.size(), new Random(0));
        // print2dArray(intGraph.adjMatrix);
        assertTrue(isDiagonalSymmetric(intGraph.adjMatrix));
        assertFalse(isDiagonalTrue(intGraph.adjMatrix));
    }

    @Test
    public void testGetSize() {
        Graph<Integer> graph = new Graph<>();
        assertEquals(0, graph.size());
        assertEquals(0, graph.weights.size());
        graph.addVertex(1);
        assertEquals(1, graph.size());
        assertEquals(1, graph.weights.size());
        graph.addVertex(2);
        assertEquals(2, graph.size());
        assertEquals(2, graph.weights.size());
        graph.addVertex(3);
        assertEquals(3, graph.size());
        assertEquals(3, graph.weights.size());
    }

    @Test
    public void testGetVertices() {
        Graph<String> graph = gf.createStringGraphFantasy();
        
        List<String> expected = new ArrayList<>();
        for (String name: names) {
            expected.add(name);
        }
        
        assertTrue(graph.getVertices().containsAll(expected));
    }

    @Test
    public void testGetAdjacentVertices() {
        Graph<String> graph = gf.createStringGraphFantasy();

        List<String> adjsScadrial = graph.getAdjacentVertices("Scadrial");
        List<String> expectedScadrial = new ArrayList<>();
        String[] adjNamesScadrial =
            "Midkemia, Oz, Mordor, Hogwarts, Elantris, Roshar".split(", ");
        for (String name: adjNamesScadrial) {
            expectedScadrial.add(name);
        }
        assertTrue(adjsScadrial.containsAll(expectedScadrial));

        List<String> adjsGondor = graph.getAdjacentVertices("Gondor");
        List<String> expectedGondor = new ArrayList<>();
        String[] adjNamesGondor = "Oz, Mordor".split(", ");
        for (String name: adjNamesGondor) {
            expectedGondor.add(name);
        }
        assertTrue(adjsGondor.containsAll(expectedGondor));
    }

    public void printVerticesAndWeights(Graph graph) {
    	graph.vertices.forEach(v -> 
    						System.out.println(v.toString()
			    							   + " " 
			    							   + graph.getWeight(v)));
    }
    
    @Test
    public void testSetWeight() {
    	Graph<String> graph = gf.createStringGraphFantasy();
    	graph.setWeight("Scadrial", 10);
    	graph.setWeight("Mordor", 7);
    	
    	assertEquals(10, graph.getWeight("Scadrial"));
    	assertEquals(7, graph.getWeight("Mordor"));
    	assertEquals(0, graph.getWeight("Narnia"));
    	
    	//visual display of weights
    	//printVerticesAndWeights(graph);
    }
    
    @Test
    public void testSetWeights() {
    	Graph<String> graph = gf.createStringGraphFantasy();
    	
    	int[] weights = {1, 2, 3, 4, 5, 6, 7, 8, 9};
    	List<Integer> weightList = Arrays.stream(weights)
						    			 .boxed()
						    			 .collect(Collectors.toList());
    	graph.setWeights(weightList);
    	
    	assertEquals(9, graph.getWeight("Roshar"));
    	assertEquals(5, graph.getWeight("Mordor"));
    	assertEquals(1, graph.getWeight("Narnia"));
    }
    
    @Test
    public void testAddVertex() {
        Graph<Integer> graph = new Graph<>();
        List<Integer> expected = new ArrayList<>();
        
        graph.addVertex(1);
        expected.add(1);
        assertTrue(graph.getVertices().containsAll(expected));

        graph.addVertex(-1);
        expected.add(-1);
        assertTrue(graph.getVertices().containsAll(expected));
    }
    
    /**
     * Helper function that tests if adjacents of a vertex is as expected. 
     * @param graph is the graph
     * @param data is the vertex to find adjacents
     * @param expectedAdjs is the expected adjacents
     */
    public void assertEqualAdjacents(Graph<Integer> graph, int key, List<Integer> expectedAdjs) {
        assertTrue(graph.getAdjacentVertices(key).containsAll(expectedAdjs));
    }

    @Test
    public void testAddEdge() {
        Graph<Integer> graph = new Graph<>();
        for (int i = 1; i <= 4; i++) {
            graph.addVertex(i);
        }
        ArrayList<Integer> emptyList = new ArrayList<>();
        
        assertEqualAdjacents(graph, 1, emptyList);
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        assertEqualAdjacents(graph, 1, new ArrayList<Integer>(Arrays.asList(2, 3)));
        assertEqualAdjacents(graph, 2, new ArrayList<Integer>(Arrays.asList(1)));
        assertEqualAdjacents(graph, 3, new ArrayList<Integer>(Arrays.asList(1)));
        assertEqualAdjacents(graph, 4, emptyList);

        graph.addEdge(2, 3);
        assertEqualAdjacents(graph, 1, new ArrayList<Integer>(Arrays.asList(2, 3)));
        assertEqualAdjacents(graph, 2, new ArrayList<Integer>(Arrays.asList(1, 3)));
        assertEqualAdjacents(graph, 3, new ArrayList<Integer>(Arrays.asList(1, 2)));
        assertEqualAdjacents(graph, 4, emptyList);

        graph.addEdge(3, 4);
        assertEqualAdjacents(graph, 1, new ArrayList<Integer>(Arrays.asList(2, 3)));
        assertEqualAdjacents(graph, 2, new ArrayList<Integer>(Arrays.asList(1, 3)));
        assertEqualAdjacents(graph, 3, new ArrayList<Integer>(Arrays.asList(1, 2)));
        assertEqualAdjacents(graph, 4, new ArrayList<Integer>(3));
    }

    @Test
    public void testIsAdjacent() {
    	Graph<String> graph = gf.createStringGraphFantasy();
    	
    	assertTrue(graph.isAdjacent("Narnia", "Midkemia"));
    	assertTrue(graph.isAdjacent("Narnia", "Elantris"));
    	assertFalse(graph.isAdjacent("Narnia", "Oz"));
    }
    
    @Test
    public void testIsValid() {
        // empty graph
        Graph<String> graphEmpty = new Graph<>();
        assertTrue(graphEmpty.isValid());

        // one vertex
        Graph<Integer> graphOneVertex = new Graph<>();
        graphOneVertex.addVertex(1);
        assertFalse(graphOneVertex.isValid());

        // fantasy 
        Graph<String> graphFantasy = gf.createStringGraphFantasy();
        assertTrue(graphFantasy.isValid());

        // random using addRandomEdges()
        Graph<Integer> graphRandom = new Graph<>();
        for (int i = 1; i <= 9; i++) {
            graphRandom.addVertex(i);
        }
        graphRandom.addRandomEdges(graphRandom.size(), new Random());
        assertTrue(graphRandom.isValid());

        // not fully connected
        Graph<Integer> graphNotFullyConnected = new Graph<>();
        for (int i = 1; i <= 4; i++) {
            graphNotFullyConnected.addVertex(i);
        }
        graphNotFullyConnected.addEdge(1, 2);
        graphNotFullyConnected.addEdge(3, 4);
        assertFalse(graphNotFullyConnected.isValid());
    }

    @Test
    public void testHasPath() {
        // one vertex
        Graph<Integer> graphOneVertex = new Graph<>();
        graphOneVertex.addVertex(1);
        assertTrue(graphOneVertex.hasPath(1, 1));

        // fantasy 
        Graph<String> graphFantasy = gf.createStringGraphFantasy();
        assertTrue(graphFantasy.hasPath("Mordor", "Mordor"));
        assertTrue(graphFantasy.hasPath("Mordor", "Gondor"));
        assertTrue(graphFantasy.hasPath("Oz", "Scadrial"));

        // not fully connected
        Graph<Integer> graphNotFullyConnected = new Graph<>();
        for (int i = 1; i <= 4; i++) {
            graphNotFullyConnected.addVertex(i);
        }
        graphNotFullyConnected.addEdge(1, 2);
        graphNotFullyConnected.addEdge(1, 3);
        assertTrue(graphNotFullyConnected.hasPath(2, 3));
        assertFalse(graphNotFullyConnected.hasPath(2, 4));
    }

    // TODO: fix this method
    @Test
    public void testCalculateShortestPath() {
    	Graph<String> graph = gf.createStringGraphFantasy();
    	
    	// use troop size on evol1 picture1 as territory size.
    	int[] weights = {10, 12, 8, 13, 14, 3, 5, 6, 3};
    	List<Integer> weightList = Arrays.stream(weights)
						    			 .boxed()
						    			 .collect(Collectors.toList());
    	graph.setWeights(weightList);
    	
    	assertEquals(16, graph.calculateShortestPath("Narnia", "Elantris"));
    	assertEquals(19, graph.calculateShortestPath("Narnia", "Roshar"));
    	assertEquals(16, graph.calculateShortestPath("Oz", "Roshar"));
    	
    }
    
    public void testCalculateShortestPathNotReachable() {
    	Graph<String> graph = gf.createStringGraphSeparated();
    	
    	int[] weights = {1, 2, 3, 4, 5};
    	List<Integer> weightList = Arrays.stream(weights)
						    			  .boxed()
						    			  .collect(Collectors.toList());
    	graph.setWeights(weightList);
    	
    	assertThrows(IllegalArgumentException.class,
                     () -> graph.calculateShortestPath("A", "E"));
    }
    
    /*@Test
    public void testEquals() {
        assertEquals(new Graph<Integer>(), new Graph<Integer>());
        assertNotEquals(new Graph<String>(), null);

        assertEquals(gf.createStringGraphFantasy(), gf.createStringGraphFantasy());

        List<Integer> list1 = new ArrayList<>(Arrays.asList(1, 2, 3));
        List<Integer> list2 = new ArrayList<>(Arrays.asList(5, 6, 7));
        Boolean[][] adjMatrix1 = new Boolean[3][3];
        adjMatrix1[1][2] = true; adjMatrix1[2][1] = true;
        Boolean[][] adjMatrix2 = new Boolean[3][3];
        adjMatrix1[0][1] = true; adjMatrix1[1][0] = true;

        Graph<Integer> g11 = new Graph<>(list1, adjMatrix1);
        Graph<Integer> g12 = new Graph<>(list1, adjMatrix2);
        Graph<Integer> g21 = new Graph<>(list2, adjMatrix1);
        assertNotEquals(g11, g12);
        assertNotEquals(g21, g12);
    }*/

    @Test
    public void testToString() {
        Graph<String> graph1 = gf.createStringGraphFantasy();
        Graph<String> graph2 = new Graph<>();
        assertNotEquals(graph1.toString(), graph2.toString());
    } 

    @Test
    public void testHashcode() {
        Graph<String> graph1 = gf.createStringGraphFantasy();
        Graph<String> graph2 = new Graph<>();
        assertNotEquals(graph1.hashCode(), graph2.hashCode());
    }

}
