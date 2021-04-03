package edu.duke.ece651.group4.RISK.shared;

import static org.junit.jupiter.api.Assertions.*;

import java.io.EOFException;
import java.util.Random;
import org.junit.jupiter.api.Test;

public class SoldierTest {
    @Test
    public void test_fight() {
        Random rnd = new Random(0);
        Soldier mySoldier = new Soldier(rnd);
        Soldier enemy = new Soldier(rnd);

        assertEquals(mySoldier.fight(enemy), false);
        assertEquals(mySoldier.fight(enemy), true);

    }

    @Test
    public void test_soldier() {
        Soldier mySoldier = new Soldier();
        Soldier clone = mySoldier.clone();
        assertEquals(mySoldier == clone, false);
    }

    @Test
    public void test_setupSoldier() {
        Soldier mySoldier = new Soldier();
        assertEquals(mySoldier.getBonus(), 0);
        mySoldier.setLevel(4);
        assertEquals(mySoldier.getBonus(), 8);
        assertEquals(mySoldier.getJobName(), "Soldier LV4");
        mySoldier.setJob("Soldier LV3");
        assertEquals(mySoldier.getLevel(), 3);

    }

    @Test
    public void test_upgradeSoldier() { 
        Soldier mySoldier= new Soldier();
        int remain=mySoldier.upGrade(1,5);
        assertEquals(remain,2);
        mySoldier.upGrade(3,200);
        assertEquals(mySoldier.getJobName(),"Soldier LV3");
        assertThrows(new IllegalArgumentException().getClass(), ()->mySoldier.upGrade(10,200));
        assertThrows(new IllegalArgumentException().getClass(), ()->mySoldier.upGrade(1,200));
        assertThrows(new IllegalArgumentException().getClass(), ()->mySoldier.upGrade(7,3));
        mySoldier.setJob("Soldier LV2");
        remain=mySoldier.upGrade(4,100);
        assertEquals(remain,56);
    }

}
