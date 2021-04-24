package edu.duke.ece651.group4.RISK.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class TechInfoTest {

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new TechLevelInfo(1));
        assertThrows(IllegalArgumentException.class, () -> new TechLevelInfo(0));
        assertThrows(IllegalArgumentException.class, () -> new TechLevelInfo(7));
    }

    @Test
    public void testGetTechLevel() {
        TechLevelInfo techLevelInfo = new TechLevelInfo(1);
        assertEquals(1, techLevelInfo.getTechLevel());
        techLevelInfo.upgradeTechLevelBy(1);
        assertEquals(2, techLevelInfo.getTechLevel());
    }

    @Test
    public void testUpgradeTechLevelBy_overflow() {
        TechLevelInfo techLevelInfo = new TechLevelInfo(1);
        techLevelInfo.upgradeTechLevelBy(5);
        assertEquals(6, techLevelInfo.techLevel);
        assertThrows(IllegalArgumentException.class, () -> techLevelInfo.upgradeTechLevelBy(1));
        assertEquals(6, techLevelInfo.techLevel);
    }

    @Test
    public void testUpgradeTechLevelBy_underflow() {
        TechLevelInfo techLevelInfo = new TechLevelInfo(2);
        techLevelInfo.upgradeTechLevelBy(-1);
        assertEquals(1, techLevelInfo.techLevel);
        assertThrows(IllegalArgumentException.class, () -> techLevelInfo.upgradeTechLevelBy(-1));
        assertEquals(1, techLevelInfo.techLevel);
    }

    @Test
    public void testCalcConsumption() {
        assertEquals(125, TechLevelInfo.calcConsumption(3, 4));
        assertEquals(50, TechLevelInfo.calcConsumption(1, 2));
        assertEquals(300, TechLevelInfo.calcConsumption(5, 6));
        assertThrows(IllegalArgumentException.class,
                () -> TechLevelInfo.calcConsumption(6, 7));
        assertThrows(IllegalArgumentException.class,
                () -> TechLevelInfo.calcConsumption(-1, 0));
    }

    @Test
    public void testCheckTechLevelValidity() {
        assertDoesNotThrow(() -> TechLevelInfo.checkTechLevelValidity(1));
        assertThrows(IllegalArgumentException.class, () -> TechLevelInfo.checkTechLevelValidity(0));
        assertThrows(IllegalArgumentException.class, () -> TechLevelInfo.checkTechLevelValidity(7));
    }

    @Test
    public void testEquals() {
        TechLevelInfo t1 = new TechLevelInfo(1);
        TechLevelInfo t2 = new TechLevelInfo(1);
        assertEquals(t1, t2);
        assertNotEquals(1, t1);
        t1.upgradeTechLevelBy(1);
        t2.upgradeTechLevelBy(1);
        assertEquals(t1, t2);
        t2.upgradeTechLevelBy(1);
        assertNotEquals(t1, t2);
    }

    @Test
    public void testHashCode() {
        TechLevelInfo t1 = new TechLevelInfo(1);
        TechLevelInfo t2 = new TechLevelInfo(1);
        assertEquals(t1.hashCode(), t2.hashCode());
    }
}
