package edu.duke.ece651.group4.RISK.shared;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static edu.duke.ece651.group4.RISK.shared.Constant.ARCHER;
import static edu.duke.ece651.group4.RISK.shared.Constant.JOB_DICTIONARY;
import static org.junit.jupiter.api.Assertions.*;

class ShieldTest {

    @Test
    public void test_upgrade() {
        Random rnd = new Random(0);
        Shield test=new Shield(1);

        test.upGrade(4,1000);
        assertEquals(test.getJobName(),"Shield LV4");

        test.defend(20);
        assertEquals(test.shieldExist(),true);
        test.defend(19);
        assertEquals(test.shieldExist(),true);
        test.defend(2);
        assertEquals(test.shieldExist(),false);
        Shield clo=test.clone();
        assertEquals(clo.shieldExist(),false);
        test.setJob("Shield LV5");
        assertEquals(test.getJobName(),"Shield LV5");
//
//        arch.active();
//        assertEquals(arch.checkReady(),true);
//
//        arch.setJob("Archer LV6");
//        assertEquals(arch.getLevel(),6);
//
//        String testnames=JOB_DICTIONARY.get(ARCHER).get(4);
//        assertEquals(testnames,"Archer LV4");
    }

}