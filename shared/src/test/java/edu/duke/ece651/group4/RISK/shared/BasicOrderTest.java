package edu.duke.ece651.group4.RISK.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BasicOrderTest {
    @Test
    void test_all_get() {
        Player p1 = new TextPlayer(null,null,"p1");
        Troop troop = new Troop(5, p1);
        BasicOrder order = new BasicOrder("src", "des", troop, 'M');
        assertEquals("src", order.getSrcName());
        assertEquals("des", order.getDesName());
        assertEquals('M', order.getActionName());
        assertEquals(troop, order.getActTroop());
    }
}