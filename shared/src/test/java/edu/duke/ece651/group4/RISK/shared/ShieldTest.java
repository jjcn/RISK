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
        Shield test=new Shield(rnd,1);

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

        test.setLP(40);


    }

    @Test
    public void test_fight() {
        Random rnd = new Random(0);
        Shield test=new Shield(rnd,1);
        Breaker b=new Breaker(rnd,0);
        test.fight(b);
        assertEquals(test.shieldExist(),true);
        test.fight(b);
        assertEquals(test.shieldExist(),false);

        Shield test2=new Shield(rnd,0);
        Knight k=new Knight(rnd,3);

        assertEquals(test2.fight(k),false);
        assertEquals(test2.fight(k),false);


        assertEquals(test2.shieldExist(),true);

        assertEquals(test2.fight(k),false);


        assertEquals(test2.shieldExist(),false);

    }


}