package edu.duke.ece651.group4.RISK.shared;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static edu.duke.ece651.group4.RISK.shared.Constant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        test.sendInAlly(new Troop(1, new TextPlayer(null, null, "p2")));
        System.out.println(test.getInfo());
    }


    @Test
    void Test_newBattle() throws IOException {
        Player p1 = new TextPlayer(null, null, "p1");
        Player p2 = new TextPlayer(null, null, "p2");
        Player p3 = new TextPlayer(null, null, "p3");
        Player p4 = new TextPlayer(null, null, "p4");
        Territory test = new Territory("test", p1, 10, new Random(0));

        assertEquals(test.checkUnitNum(UNIT_NAMES.get(0)),10);
        assertEquals(test.checkUnitNumAlly(UNIT_NAMES.get(0)),0);
        test.checkTypeNum(SOLDIER);
        test.checkTypeNumAllay(SOLDIER);
        assertThrows(new IllegalArgumentException().getClass(), () -> test.checkTypeNum("wrong"));

        test.transfer(SOLDIER,ARCHER, 0,2);
        assertEquals(test.getAttackRange(),ARCHER_RANGE);
        test.transfer(SOLDIER,SHIELD, 0,4);

        assertEquals(test.hasRangedTroop(),true);

        HashMap<String, Integer> tmpDict=new HashMap<>();
        tmpDict.put(ARCHER_NAMES.get(0),2);
        Troop tmp=new Troop(tmpDict,test.getOwner());

        test.sendOutRangedAttack(tmp);
        assertThrows(new IllegalArgumentException().getClass(),() ->test.sendOutRangedAttack(tmp));
        System.out.println(test.doBattles());
        test.sendOutRangedAttack(tmp);


        Troop enemy=new Troop(10,p2,new Random(1));
        enemy.transfer(SOLDIER,BREAKER, 0,2);
        enemy.transfer(SOLDIER,ARCHER, 0,2);
        Troop dem=new Troop(tmpDict,enemy.getOwner());
        Troop sent=enemy.sendOutRangedAttack(dem);
        enemy.receiveTroop(sent);

        Troop ally=new Troop(4,p3,new Random(2));
        Troop partner=new Troop(2,p4,new Random(3));
        test.sendInTroop(ally);
        assertEquals(test.checkUnitNumAlly(UNIT_NAMES.get(0)),4);

        test.doOneBattleMix(enemy,partner);

        System.out.println(test.getInfo());


    }

    @Test
    void Test_newBattleAll() throws IOException {
        Player p1 = new TextPlayer(null, null, "p1");
        Player p2 = new TextPlayer(null, null, "p2");
        Player p3 = new TextPlayer(null, null, "p3");
        Player p4 = new TextPlayer(null, null, "p4");
        Territory test = new Territory("test", p1, 4, new Random(0));

        test.transfer(SOLDIER,ARCHER, 0,2);
        test.transfer(SOLDIER,SHIELD, 0,2);


        HashMap<String, Integer> tmpDict=new HashMap<>();
        tmpDict.put(ARCHER_NAMES.get(0),2);


        Troop enemy=new Troop(10,p2,new Random(1));
        enemy.transfer(SOLDIER,BREAKER, 0,4);
        enemy.transfer(SOLDIER,ARCHER, 0,2);
        Troop dem=new Troop(tmpDict,enemy.getOwner());
        Troop sent=enemy.sendOutRangedAttack(dem);


        Troop ally=new Troop(2,p3,new Random(2));
        Troop partner=new Troop(10,p4,new Random(3));
        partner.transfer(SOLDIER,BREAKER, 0,4);
        test.sendInTroop(ally);
        test.sendInEnemyTroop(sent);
        test.sendInEnemyTroop(enemy);
        test.sendInEnemyTroop(partner);

        HashMap<String, Set<String>> relation =new HashMap<>();
        HashSet<String> r2=new HashSet<>();
        r2.add("p4");

        HashSet<String> r4=new HashSet<>();
        r4.add("p2");

        relation.put("p2",r2);
        relation.put("p4",r4);
        String rep=test.doBattlesMix(relation);
        System.out.println(rep);
        System.out.println(test.getInfo());

    }







}