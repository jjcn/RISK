package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.Arrays;

public class GraphFactory implements Serializable {
    protected static final long serialVersionUID = 5L;

	public GraphFactory() {}
	
    /**
     * Create a graph with no edges.
     * @param names is a list of name Strings.
     * @return a graph with no edges.
     */
	public Graph<String> createGraphUnconnected(String[] names) {
		Graph<String> graph = new Graph<>();
        Arrays.asList(names).forEach(name -> graph.addVertex(name));      
        return graph;
	}
	
	/**
     * Creates a graph of String type. 
     * 
     * Layout is the same as that on Evolution 1 requirements.
     * 
     * number of vertices = 9
     * 
     * N-----M--O--G 
     * |   /  |/ \ | 
     * |  /   S ---M      
     * |/   / |  \ | 
     * E------R----H 
     * 
     */
    public Graph<String> createStringGraphFantasy() {
    	String[] fantasyNames = {"Narnia", "Midkemia", "Oz", "Gondor", "Mordor",
 				 "Hogwarts", "Scadrial", "Elantris", "Roshar"};

        Graph<String> graph = createGraphUnconnected(fantasyNames);

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

    /**
     * Creates a graph of String type.
     * 
     * It is separated into two parts. 
     * 
     * number of vertices = 5
     * 
     * A--B--C
     * 
     * D--E
     * 
     */
    public Graph<String> createStringGraphSeparated() {
    	String[] simpleNames = {"A", "B", "C", "D", "E"};
    	
    	Graph<String> graph = createGraphUnconnected(simpleNames);
    	
    	graph.addEdge("A", "B");
    	graph.addEdge("B", "C");
    	graph.addEdge("D", "E");
    	
    	return graph;
    }
    
}
