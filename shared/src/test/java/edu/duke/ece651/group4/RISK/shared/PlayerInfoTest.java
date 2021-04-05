package edu.duke.ece651.group4.RISK.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PlayerInfoTest {
	@Test
	public void testConstructors() {
		PlayerInfo pInfoA = new PlayerInfo("A");
		assertEquals(pInfoA.playerName, "A");
		assertEquals(pInfoA.getFoodQuantity(), 0);
		assertEquals(pInfoA.getTechQuantity(), 0);
		
		PlayerInfo pInfo = new PlayerInfo("player", 100, 50);
		assertEquals(pInfo.playerName, "player");
		assertEquals(pInfo.getFoodQuantity(), 100);
		assertEquals(pInfo.getTechQuantity(), 50);
	}
	
	@Test
	public void testGetName() {
		PlayerInfo emptyNameInfo = new PlayerInfo("");
		assertEquals(emptyNameInfo.playerName, "");
		PlayerInfo pInfo = new PlayerInfo("!");
		assertEquals(pInfo.playerName, "!");
	}
	
	@Test
	public void testTechLevel() {
		PlayerInfo emptyNameInfo = new PlayerInfo("");
		assertEquals(emptyNameInfo.playerName, "");
		PlayerInfo pInfo = new PlayerInfo("!");
		assertEquals(pInfo.playerName, "!");
	}

	@Test
	public void TestGainResources() {
		PlayerInfo pInfo = new PlayerInfo("A", 50, 25);
		assertEquals(50, pInfo.getFoodQuantity());
		assertEquals(25, pInfo.getTechQuantity());
		
		pInfo.gainFood(10);
		pInfo.gainTech(0);
		assertEquals(50 + 10, pInfo.getFoodQuantity());
		assertEquals(25 + 0, pInfo.getTechQuantity());
		
		assertThrows(IllegalArgumentException.class,
					() -> pInfo.gainFood(-1));
		assertEquals(50 + 10, pInfo.getFoodQuantity());
	}

	@Test
	public void TestConsumeResources() {
		PlayerInfo pInfo = new PlayerInfo("A", 50, 25);
		assertEquals(50, pInfo.getFoodQuantity());
		assertEquals(25, pInfo.getTechQuantity());
		
		pInfo.consumeFood(10);
		pInfo.consumeTech(0);
		assertEquals(50 - 10, pInfo.getFoodQuantity());
		assertEquals(25 + 0, pInfo.getTechQuantity());
		
		assertThrows(IllegalArgumentException.class,
					() -> pInfo.consumeFood(-1));
		assertEquals(50 - 10, pInfo.getFoodQuantity());

		assertThrows(IllegalArgumentException.class,
					() -> pInfo.consumeTech(26));
		assertEquals(25 + 0, pInfo.getTechQuantity());
	}

	@Test
	public void testUpgradeTechLevel() {
		PlayerInfo pInfo = new PlayerInfo("player", 100, 100);
		assertEquals(pInfo.techLevel, 1);
		// try modify to -1
		assertThrows(IllegalArgumentException.class,
					() -> pInfo.modifyTechLevelBy(-2));
		// try modify to 7
		assertThrows(IllegalArgumentException.class,
					() -> pInfo.modifyTechLevelBy(6));
		// upgrade tech level
		pInfo.upgradeTechLevelBy1();
		assertEquals(pInfo.techLevel, 2);
	}

	@Test
	public void testTechLevel_not_enough_resource() {
		PlayerInfo pInfo = new PlayerInfo("A", 0, 0);
		assertEquals(pInfo.techLevel, 1);
		assertThrows(IllegalArgumentException.class, 
					() -> pInfo.upgradeTechLevelBy1());
		assertEquals(pInfo.techLevel, 1);
	}

	@Test
	public void testCalcUpgradeTechLevelConsumption() {
		assertEquals(125, PlayerInfo.calcUpgradeTechLevelConsumption(3, 4));
		assertEquals(50, PlayerInfo.calcUpgradeTechLevelConsumption(1, 2));
		assertEquals(300, PlayerInfo.calcUpgradeTechLevelConsumption(5, 6));
		assertThrows(IllegalArgumentException.class,
					() -> PlayerInfo.calcUpgradeTechLevelConsumption(6, 7));
		assertThrows(IllegalArgumentException.class,
					() -> PlayerInfo.calcUpgradeTechLevelConsumption(-1, 0));
	}
	
}