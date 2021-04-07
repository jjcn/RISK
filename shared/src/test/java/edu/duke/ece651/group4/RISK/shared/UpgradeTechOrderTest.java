package edu.duke.ece651.group4.RISK.shared;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class UpgradeTechOrderTest {

    @Test
    public void testBasic() {

        UpgradeTechOrder test=new UpgradeTechOrder(1);
        assertEquals(test.getActionName(),'T');
        assertEquals(test.getNLevel(),1);
        assertThrows(new NoSuchElementException().getClass(), ()->test.getSrcName());
        assertThrows(new NoSuchElementException().getClass(), ()->test.getDesName());
        assertThrows(new NoSuchElementException().getClass(), ()->test.getActTroop());
    }

}