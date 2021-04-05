package edu.duke.ece651.group4.RISK.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class WorldFactoryTest {
    WorldFactory wf = new WorldFactory();

    public List<Integer> calcAttributeSums(World world, String playerName) {
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
    
    @Test
    public void testCreation() {
        World world6 = wf.create6TerritoryWorld();
        List<String> playerNames = new ArrayList<>();
        playerNames.add("Alice");
        playerNames.add("Bob");
        wf.assignTerritories(world6, playerNames);
        
        assertEquals(2, world6.playerInfos.size());
        
        List<Integer> attributeSumsAlice = calcAttributeSums(world6, "Alice");
        List<Integer> attributeSumsBob = calcAttributeSums(world6, "Bob");
        assertEquals(attributeSumsAlice, attributeSumsBob);
        
        // visual display
        System.out.println(world6.getPlayerInfoByName("Alice").toString());
        System.out.println(world6.getPlayerInfoByName("Bob").toString());
        
        // visual display
        for (Territory terr : world6.getAllTerritories()) {
        	StringBuilder ans = new StringBuilder();
        	ans.append(terr.getName() + ": "+ terr.getOwner().getName() + ", ");
        	ans.append("area: " + terr.getArea() + ", ");
        	ans.append("food speed: " + terr.getFoodSpeed() + ", ");
        	ans.append("tech speed: " + terr.getTechSpeed() + ", ");
        	System.out.println(ans.toString());
        }
        
        
    }
}
