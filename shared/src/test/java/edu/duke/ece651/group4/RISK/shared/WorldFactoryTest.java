package edu.duke.ece651.group4.RISK.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldFactoryTest {
	protected String arr[] = {"Alice", "Bob", "Cody", "Don", "Eve", 
                            "Frank", "Gene", "Harry", "Ivan", "Jack",
                            "Kelvin", "Lynn", "O", "Paul", "Quin",
                            "R", "Starr", "T", "U", "V",
                            "W", "X", "Y", "Z"};
	protected List<String> names = Arrays.asList(arr);
	
    /**
     * Creates a list with N player names.
     * @param nPlayer is the number of player names.
     * @return a list of player names.
     */ 
	protected List<String> createPlayerNames(int nPlayer) {
		return names.subList(0, nPlayer);
	}
	
	@Test
	public void testCreatePlayerNames() {
		assertEquals(3, createPlayerNames(3).size());
	}
	
    protected List<Integer> calcAttributeSums(World world, String playerName) {
    	List<Integer> ans = new ArrayList<>();
    	int sumArea = 0;
        int sumFoodSpeed = 0;
        int sumTechSpeed = 0;
        for (Territory terr : world.getTerritoriesOfPlayer(playerName)) {
        	sumArea += terr.getArea();
        	sumFoodSpeed += terr.getFoodSpeed();
        	sumTechSpeed += terr.getTechSpeed();
        }
        ans.add(sumArea);
        ans.add(sumFoodSpeed);
        ans.add(sumTechSpeed);
        return ans;
    }
    
    protected void assertEqualAttributeSums(World world, String p1Name, String p2Name) {
        List<Integer> attributeSums1 = calcAttributeSums(world, p1Name);
        List<Integer> attributeSums2 = calcAttributeSums(world, p2Name);
        assertEquals(attributeSums1, attributeSums2);
    }

    protected World setupWorld(List<String> playerNames){
        World world = null;
        WorldFactory factory = new WorldFactory();
        switch(playerNames.size()){
            case 2:
            	world = factory.create4TerritoryWorld();
                break;
            case 3:
            	world = factory.create6TerritoryWorld();
                break;
            case 4:
            	world = factory.create8TerritoryWorld();
                break;
            case 5:
            	world = factory.create10TerritoryWorld();
                break;
            default:
                break;
        }
        factory.assignTerritories(world, playerNames);
        return world;
    }
    
    /**
     * World creation test helper:
     * Creates a world that is has the size:
     * 2 players -> 4 territories
     * 3 players -> 6 territories
     * 4 players -> 8 territories
     * 5 players -> 10 territories
     * And assign players a group of territories randomly 
     * @param nPlayer is the number of players
     */
    protected void testCreationByNumPlayer(int nPlayer) {
    	List<String> playerNames = createPlayerNames(nPlayer);
    	World world = setupWorld(playerNames);
    	// test number of player infos registered
    	assertEquals(nPlayer, world.playerInfos.size());
    	// test number of territories
    	switch(nPlayer){
        case 2:
        	assertEquals(4, world.size());
            break;
        case 3:
        	assertEquals(6, world.size());
            break;
        case 4:
        	assertEquals(8, world.size());
            break;
        case 5:
        	assertEquals(10, world.size());
            break;
        default:
            break;
    	}
    	// test each player's territories has the same sum of each attribute
    	for (int i = 0; i < nPlayer; i++) {
    		assertEqualAttributeSums(world, playerNames.get(0), playerNames.get(i));
    	}
    }
    
    @Test
    public void testCreateNPlayerWorld() {
    	testCreationByNumPlayer(2);
    	testCreationByNumPlayer(3);
    	testCreationByNumPlayer(4);
    	testCreationByNumPlayer(5);

    }

    @Test
    public void testFantasy() {
        WorldFactory factory = new WorldFactory();
        factory.createFantasyWorld();

    }


}
