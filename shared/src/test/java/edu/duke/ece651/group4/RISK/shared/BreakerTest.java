package edu.duke.ece651.group4.RISK.shared;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static edu.duke.ece651.group4.RISK.shared.Constant.*;
import static org.junit.jupiter.api.Assertions.*;

class BreakerTest {

    @Test
    public void test_upgrade() {
        Random rnd = new Random(0);
        Shield test=new Shield(rnd,1);
        Breaker b=new Breaker(rnd,0);

        test.fight(b);
        assertEquals(test.shieldExist(),true);
        test.fight(b);
        assertEquals(test.shieldExist(),false);

        b.upGrade(3,1000);
        assertEquals(b.getJobName(),BREAKER_NAMES.get(3));

        Breaker clone=b.clone();
        assertEquals(clone.getBreakBonus(),20);
        b.setJob(BREAKER_NAMES.get(5));
        assertEquals(b.getJobName(),BREAKER_NAMES.get(5));
    }
}