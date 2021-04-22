package edu.duke.ece651.group4.RISK.shared;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static edu.duke.ece651.group4.RISK.shared.Constant.ALLIANCE_ACTION;
import static org.junit.jupiter.api.Assertions.*;

class AllianceOrderTest {
    @Test
    public void Test_Basic() {
//        Player p1 = new TextPlayer(null,null,"p1");
//        Troop troop = new Troop(5, p1);
        AllianceOrder order = new AllianceOrder("src", "des");
        assertEquals("src", order.getSrcName());
        assertEquals("des", order.getDesName());
        assertEquals(ALLIANCE_ACTION, order.getActionName());
        assertThrows(new NoSuchElementException().getClass(),() -> order.getActTroop());


    }
}