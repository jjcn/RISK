package edu.duke.ece651.group4.RISK.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphTest {
    String[] names1 = 
        "Narnia, Midkemia, Oz, Gondor, Mordor, Hogwarts, Scadrial, Elantris, Roshar".split(", ");

    /**
     * Creates a test graph. Same as the one on Evolution 1 requirements.
     */
    public Graph<String> createTestGraph1() {
        Graph<String> graph = new Graph<>();
        for (String name: names1) {
            graph.addVertex(name);
        }
        graph.addEdge("Narnia", "Midkemia");
        graph.addEdge("Narnia", "Elantris");
        graph.addEdge("Midkemia", "Elantris");
        graph.addEdge("Midkemia", "Scadrial");
        graph.addEdge("Midkemia", "Oz");
        graph.addEdge("Oz", "Scadrial");
        graph.addEdge("Oz", "Mordor");
        graph.addEdge("Oz", "Gondor");
        graph.addEdge("Gondor", "Mordor");
        graph.addEdge("Elantris", "Scadrial");
        graph.addEdge("Elantris", "Roshar");
        graph.addEdge("Scadrial", "Roshar");
        graph.addEdge("Scadrial", "Hogwarts");
        graph.addEdge("Scadrial", "Mordor");
        graph.addEdge("Mordor", "Hogwarts");

        return graph;
    }

    @Test
    public void testCreation() {
        Graph<Integer> intGraph = new Graph<>();
        Graph<String> stringGraph = new Graph<>();
        Graph<Territory> terrGraph = new Graph<>();
    }

    @Test
    public void testGetSize() {
        Graph<Integer> graph = new Graph<>();
        assertEquals(0, graph.size());
        graph.addVertex(1);
        assertEquals(1, graph.size());
        graph.addVertex(2);
        assertEquals(2, graph.size());
        graph.addVertex(3);
        assertEquals(3, graph.size());
        /*
        graph.removeVertex(3);
        assertEquals(2, graph.size());
        graph.removeVertex(2);
        assertEquals(1, graph.size());
        graph.removeVertex(0);
        assertEquals(1, graph.size());
        */
    }

    @Test
    public void testGetVertices() {
        Graph<String> graph = createTestGraph1();
        
        List<String> expected = new ArrayList<>();
        for (String name: names1) {
            expected.add(name);
        }
        
        assertTrue(graph.getVertices().containsAll(expected));
    }

    @Test
    public void testGetAdjacentVertices() {
        Graph<String> graph = createTestGraph1();

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
    
    @Test
    public void testRemoveVertex() {
        // TODO
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
    public void testRemoveEdge() {
        // TODO
    }


}
