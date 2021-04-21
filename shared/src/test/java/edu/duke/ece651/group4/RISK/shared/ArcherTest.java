package edu.duke.ece651.group4.RISK.shared;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static edu.duke.ece651.group4.RISK.shared.Constant.ARCHER;
import static edu.duke.ece651.group4.RISK.shared.Constant.JOB_DICTIONARY;
import static org.junit.jupiter.api.Assertions.*;

class ArcherTest {

    @Test
    public void test_upgrade() {
        Random rnd = new Random(0);
        Archer arch=new Archer(1);

        arch.upGrade(4,1000);
        assertEquals(arch.getJobName(),"Archer LV4");

        Arrow arr=arch.shoot();
        assertEquals(arr.getJobName(),"Arrow LV4");
        assertEquals(arch.checkReady(),false);

        arch.active();
        assertEquals(arch.checkReady(),true);

        arch.setJob("Archer LV6");
        assertEquals(arch.getLevel(),6);

        String testnames=JOB_DICTIONARY.get(ARCHER).get(4);
        assertEquals(testnames,"Archer LV4");
    }
}