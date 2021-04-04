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
		PlayerInfo pInfo = new PlayerInfo("player");
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
}