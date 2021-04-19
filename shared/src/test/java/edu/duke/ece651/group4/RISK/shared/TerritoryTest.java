package edu.duke.ece651.group4.RISK.shared;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TerritoryTest {

    @Test
    void Test_sendTroop() throws IOException {
        Player p1 = new TextPlayer(null, null, "p1");
        Territory test= new Territory("test");
        test.setOwnerTroop(5,p1);
        test.sendInTroop(new Troop(5,p1));
        assertEquals(test.checkPopulation(),10);
        test.sendOutTroop(new Troop(4,p1));
        assertEquals(test.checkPopulation(),6);

        test.sendInEnemyTroop(new Troop(4,p1));
        assertEquals(test.checkPopulation(),6);
    }

    @Test
    void Test_addUnit() {
        Player p1 = new TextPlayer(null, null, "p1");
        Territory test= new Territory("test");
        test.setOwnerTroop(5,p1);
        test.addUnit(5);
        assertEquals(test.checkPopulation(),10);
    }

    @Test
    void Test_initializeTerritory() {
        Territory test= new Territory("test");
        Player p1 = new TextPlayer(null, null, "p1");
        test.setOwnerTroop(5,p1);
        assertEquals(test.checkPopulation(),5);
    }

    @Test
    void Test_doBattles() throws IOException {
        Player p1 = new TextPlayer(null, null, "p1");
        Player p2 = new TextPlayer(null, null, "p2");
        Player p3 = new TextPlayer(null, null, "p3");
        Territory test = new Territory("test", p1, 4, new Random(0));


        test.sendInEnemyTroop(new Troop(2, p2, new Random(0)));
        test.sendInEnemyTroop(new Troop(1, p3, new Random(0)));
        System.out.println(test.doBattles());


        assertEquals(test.getOwner().getName(), "p1");
        assertEquals(test.checkPopulation(),4);
        test.doBattles();
        test.sendInEnemyTroop(new Troop(10, p3, new Random(0)));
        test.doBattles();
        assertEquals(test.getOwner().getName(), "p1");

        test.sendInEnemyTroop(new Troop(1, p2, new Random(0)));
        test.sendInEnemyTroop(new Troop(1, p2, new Random(0)));
        Territory clo=test.clone();
        test.doBattles();
        assertEquals(test.getOwner().getName(), "p1");
        HashMap<String,Integer> dict=clo.checkTroopInfo();
        assertEquals(dict.get("Soldier LV0"),4);

    }

    @Test
    void Test_equals() throws IOException {
        Player p1 = new TextPlayer(null, null, "p1");
        Territory test = new Territory("test", p1, 4, new Random(0));
        Territory test2= new Territory("test",p1,4,new Random(0));
        Territory test3= new Territory("test1",p1,4,new Random(0));

        assertEquals(test.equals(test2),true);
        assertEquals(test.equals(test3),false);
        assertEquals(test.equals(p1),false);
        assertEquals(test,test2);
        Territory test4=test3.clone();
        assertEquals(test3.equals(test4),true);
        test4.setRandom(new Random(0));
        assertEquals(test3.equals(test4),true);

    }

    @Test
    void Test_Report() throws IOException {
        Player p1 = new TextPlayer(null, null, "p1");
        Territory test = new Territory("test", p1, 4, new Random(0));
        System.out.println(test.getInfo());

    }




}