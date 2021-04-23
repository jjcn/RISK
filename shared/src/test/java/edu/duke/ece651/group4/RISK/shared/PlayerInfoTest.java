package edu.duke.ece651.group4.RISK.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
		PlayerInfo pInfo = new PlayerInfo("");
		assertEquals(1, pInfo.getTechLevel());
		
		PlayerInfo rich = new PlayerInfo("rich", 999, 999);
		rich.upgradeTechLevelBy(1);
		assertEquals(2, rich.getTechLevel());
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
		PlayerInfo pInfo = new PlayerInfo("player", 0, 999);
		assertEquals(1, pInfo.techLevelInfo.getTechLevel());
		// try modify to -1
		assertThrows(IllegalArgumentException.class,
					() -> pInfo.upgradeTechLevelBy(-2));
		// try modify to 7
		assertThrows(IllegalArgumentException.class,
					() -> pInfo.upgradeTechLevelBy(6));
		// upgrade tech level by 1
		pInfo.upgradeTechLevelBy(1);
		assertEquals(2, pInfo.techLevelInfo.getTechLevel());
		// upgrade tech level by 2
		pInfo.upgradeTechLevelBy(2);
		assertEquals(4, pInfo.techLevelInfo.getTechLevel());
	}

	@Test
	public void testTechLevel_not_enough_resource() {
		PlayerInfo pInfo = new PlayerInfo("A", 0, 0);
		assertEquals(1, pInfo.techLevelInfo.getTechLevel());
		assertThrows(IllegalArgumentException.class, 
					() -> pInfo.consumeResourceOfTechUpgrade(1));
		assertEquals(1, pInfo.techLevelInfo.getTechLevel());
	}

	/**
	 * Helper function that tests if two sets of Strings are equal
	 * @param set1
	 * @param set2
	 */
	protected void assertSetEquals(Set<String> set1, Set<String> set2) {
		assertEquals(set1.size(), set2.size());
		assertTrue(set1.containsAll(set2));
		assertTrue(set2.containsAll(set1));
	}

	/**
	 * Helper function that creates a new set from an indefinite number of objects
	 * @param objects are the objects
	 * @param <T> is the object type
	 * @return a new set containing these objects
	 */
	protected <T> Set<T> newSet(T... objects) {
		Set<T> set = new HashSet<T>();
		Collections.addAll(set, objects);
		return set;
	}

	@Test
	public void testAllowedType() {
		PlayerInfo pInfo = new PlayerInfo("newPlayer", 9999, 9999);
		pInfo.reachMinTechLevelToUnlockType();

		Set<String> expected = new HashSet<>();
		expected.add(Constant.SOLDIER);
		assertSetEquals(expected, pInfo.unlockedTypes);

		expected.add(Constant.KNIGHT);
		pInfo.unlockType(Constant.KNIGHT);
		assertSetEquals(expected, pInfo.unlockedTypes);

		expected.add(Constant.BREAKER);
		pInfo.unlockType(Constant.BREAKER);
		assertSetEquals(expected, pInfo.unlockedTypes);

		assertThrows(IllegalArgumentException.class, () -> pInfo.unlockType(Constant.ARCHER));
		assertSetEquals(expected, pInfo.unlockedTypes);
	}

	@Test
	public void testGetUnlockedAndUnlockableTypes() {
		PlayerInfo pInfo = new PlayerInfo("newPlayer", 9999, 9999);
		pInfo.reachMinTechLevelToUnlockType();
		pInfo.unlockType(Constant.KNIGHT);

		assertSetEquals(newSet(Constant.SOLDIER, Constant.KNIGHT), pInfo.getUnlockedTypes());
		assertSetEquals(newSet(Constant.ARCHER, Constant.BREAKER, Constant.SHIELD), pInfo.getUnlockableTypes());

		pInfo.unlockType(Constant.ARCHER);
		assertThrows(IllegalArgumentException.class, () -> pInfo.unlockType(Constant.SHIELD));
		assertSetEquals(newSet(Constant.SOLDIER, Constant.KNIGHT, Constant.ARCHER), pInfo.getUnlockedTypes());
		assertSetEquals(new HashSet<>(), pInfo.getUnlockableTypes());
	}

	@Test
	public void testUnlockTypeInvalid() {
		PlayerInfo pInfo = new PlayerInfo("newPlayer", 9999, 9999);
		pInfo.reachMinTechLevelToUnlockType();
		assertThrows(IllegalArgumentException.class,
				() -> pInfo.unlockType("UnknownType"));
		assertThrows(IllegalArgumentException.class,
				() -> pInfo.unlockType(Constant.SOLDIER));

		pInfo.unlockType(Constant.KNIGHT);
		assertThrows(IllegalArgumentException.class,
				() -> pInfo.unlockType(Constant.KNIGHT));
	}

	@Test
	public void testUnlockTypeLowTechLevel() {
		PlayerInfo pInfo = new PlayerInfo("newPlayer", 9999, 9999);
		pInfo.techLevelInfo.techLevel = 0;
		assertThrows(IllegalArgumentException.class,
				() -> pInfo.unlockType(Constant.KNIGHT));
	}

	@Test
	public void testUnlockType_not_enough_resource() {
		PlayerInfo pInfo = new PlayerInfo("A", 0, 0);
		assertThrows(IllegalArgumentException.class,
				() -> pInfo.unlockType(Constant.KNIGHT));
	}

	@Test
	public void testUnlockCosts() {
		PlayerInfo pInfo = new PlayerInfo("A", 0, 0);
		assertEquals(1, pInfo.UNLOCK_COSTS.get(Constant.KNIGHT));
		assertEquals(1, pInfo.UNLOCK_COSTS.get(Constant.ARCHER));
		assertEquals(1, pInfo.UNLOCK_COSTS.get(Constant.SHIELD));
		assertEquals(1, pInfo.UNLOCK_COSTS.get(Constant.BREAKER));
	}

	@Test
	public void testIsTypeUnlocked() {
		PlayerInfo pInfo = new PlayerInfo("newPlayer", 9999, 9999);
		assertTrue(pInfo.isTypeUnlocked(Constant.SOLDIER));
		assertFalse(pInfo.isTypeUnlocked(Constant.KNIGHT));
	}

	@Test
	public void testClone() {
		PlayerInfo pInfo = new PlayerInfo("");
		PlayerInfo clonePInfo = pInfo.clone();

		assertFalse(pInfo == clonePInfo);
		assertEquals(pInfo, clonePInfo);
		assertEquals(pInfo.playerName, clonePInfo.playerName);
		assertEquals(pInfo.techLevelInfo, clonePInfo.techLevelInfo);
		assertEquals(pInfo.foodResource, clonePInfo.foodResource);
		assertEquals(pInfo.techResource, clonePInfo.techResource);
		assertEquals(pInfo.unlockedTypes, clonePInfo.unlockedTypes);
	}

	@Test
	public void testModifyAndClone() {
		PlayerInfo pInfo = new PlayerInfo("", 9999, 9999);
		pInfo.unlockAllTypes();
		PlayerInfo clonePInfo = pInfo.clone();

		assertFalse(pInfo == clonePInfo);
		assertEquals(pInfo, clonePInfo);
		assertEquals(pInfo.playerName, clonePInfo.playerName);
		assertEquals(pInfo.techLevelInfo, clonePInfo.techLevelInfo);
		assertEquals(pInfo.foodResource, clonePInfo.foodResource);
		assertEquals(pInfo.techResource, clonePInfo.techResource);
		assertEquals(pInfo.unlockedTypes, clonePInfo.unlockedTypes);
	}

	@Test
	public void testEquals() {
		PlayerInfo empty1 = new PlayerInfo("");
		PlayerInfo empty2 = new PlayerInfo("");
		PlayerInfo empty3 = new PlayerInfo("", 0, 0);
		PlayerInfo nonempty = new PlayerInfo("", 0, 1);

		assertEquals(empty1, empty2);
		assertEquals(empty1, empty3);
		assertNotEquals(empty1, nonempty);
	}
}