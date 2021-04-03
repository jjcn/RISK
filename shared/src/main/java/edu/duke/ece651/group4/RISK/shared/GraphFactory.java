package edu.duke.ece651.group4.RISK.shared;

import java.util.Arrays;
import java.util.List;

public class GraphFactory {
	
	public GraphFactory() {}
	
	/**
     * Creates a test graph of String type. 
     * 
     * Layout is the same as that on Evolution 1 requirements.
     * 
     * N-----M--O--G 
     * |   /  |/ \ | 
     * |  /   S ---M      
     * |/   / |  \ | 
     * E------R----H 
     * 
     */
    public Graph<String> createStringGraphFantasy() {
        Graph<String> graph = new Graph<>();
        
        String[] names = "Narnia, Midkemia, Oz, Gondor, Mordor, Hogwarts, Scadrial, Elantris, Roshar".split(", ");
        List<String> nameList = Arrays.asList(names);
        nameList.forEach(name -> graph.addVertex(name));

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
    
}
