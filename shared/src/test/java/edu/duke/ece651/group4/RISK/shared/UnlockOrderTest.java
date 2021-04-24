package edu.duke.ece651.group4.RISK.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

public class UnlockOrderTest {

    @Test
    public void testCreation() {
        UnlockOrder unlock = new UnlockOrder("red", Constant.KNIGHT);
        assertEquals(Constant.UNLOCK_ACTION, unlock.getActionName());
        assertThrows(NoSuchElementException.class, () -> unlock.getActTroop());
        assertThrows(NoSuchElementException.class, () -> unlock.getDesName());
        assertThrows(NoSuchElementException.class, () -> unlock.getSrcName());
        assertEquals("red", unlock.getPlayerName());
        assertEquals(Constant.KNIGHT, unlock.getTypeName());
    }

}
