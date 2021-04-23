package edu.duke.ece651.group4.RISK.shared;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static edu.duke.ece651.group4.RISK.shared.Constant.*;
import static org.junit.jupiter.api.Assertions.*;

class ArcherTest {

    @Test
    public void test_upgrade() {
        Random rnd = new Random(0);
        Archer arch=new Archer(1);
        Archer c=arch.clone();

        arch.upGrade(4,1000);
        assertEquals(arch.getJobName(),"Archer LV4");

        Arrow arr=arch.shoot();
        assertEquals(arr.getJobName(),"Arrow LV4");
        assertEquals(arch.checkReady(),false);

        Archer clo=arch.clone();
        assertEquals(clo.checkReady(),false);

        arch.active();
        assertEquals(arch.checkReady(),true);

        arch.setJob("Archer LV6");
        assertEquals(arch.getLevel(),6);

        assertEquals(arch.getRange(),ARCHER_RANGE);

        String testnames=JOB_DICTIONARY.get(ARCHER).get(4);
        assertEquals(testnames,"Archer LV4");
    }

    @Test
    public void testGetRange() {
        Archer archer = new Archer(1);
        assertEquals(Constant.ARCHER_RANGE, archer.getRange());
    }
}