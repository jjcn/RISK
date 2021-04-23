package edu.duke.ece651.group4.RISK.shared;

import static edu.duke.ece651.group4.RISK.shared.Constant.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class TroopTest {
    
    @Test
    public void Test_checkTroopSize() {
        Troop test = new Troop(5, new TextPlayer("test"));
        assertEquals(test.checkTroopSize(), 5);
    }

    @Test
    public void Test_sendTroop() {
        Troop test = new Troop(5, new TextPlayer("test"));
        Troop sub = test.sendTroop(3);
        assertEquals(test.checkTroopSize(), 2);
        assertEquals(sub.checkTroopSize(), 3);

        HashMap<String, Integer> myDict = new HashMap<>();
        myDict.put("Soldier LV4", 2);
        myDict.put("Soldier LV0", 4);
        Troop dem = new Troop(myDict, new TextPlayer("test"));

        assertThrows(new IllegalArgumentException().getClass(), () -> test.sendTroop(dem));
        assertThrows(new IllegalArgumentException().getClass(), () -> test.dispatchCertainUnit("Soldier LV6"));
        assertEquals(test.checkUnitNum("Soldier LV6"),0);
    }

    @Test
    public void Test_real() {
        Troop test = new Troop(5, new TextPlayer("test"));
        HashMap<String, Integer> myDict = new HashMap<>();
        myDict.put("Soldier LV0", 5);
        Troop dem = new Troop(myDict, new TextPlayer("test"));
        test.sendTroop(dem);
        assertEquals(test.checkTroopSize(), 0);
        assertThrows(new IllegalArgumentException().getClass(), () -> test.sendTroop(dem));
    }



    @Test
    public void Test_upGrade() {
        Troop test = new Troop(10, new TextPlayer("test"));
        assertEquals(test.checkUnitNum("Soldier LV0"), 10);
        
        test.updateUnit("Soldier LV0", 2, 4, 2000); // promote 4 lv0 units by 2 levels

        assertEquals(test.checkUnitNum("Soldier LV2"), 4);
        assertEquals(test.checkUnitNum("Soldier LV0"), 6);
        assertEquals(test.getUnit("Soldier LV2").getJobName(), "Soldier LV2");

        assertEquals(test.checkTroopSize(), 10);

        // promote 4 lv2 units by 2 levels
        // need 4 * 44 = 176 > 170, only upgrade 3.
        assertThrows(new IllegalArgumentException().getClass(), () -> test.updateUnit("Soldier LV2", 2, 10, 170));
        assertThrows(new IllegalArgumentException().getClass(), () -> test.updateUnit("Soldier LV2", 2, 4, 170));
        assertEquals(test.checkUnitNum("Soldier LV4"), 3);
        assertEquals(test.checkUnitNum("Soldier LV2"), 1);

        test.updateUnit("Soldier LV0",5,6,2000000);

        assertEquals(test.getWeakest().getJobName(),"Soldier LV2");
        assertEquals(test.getStrongest().getJobName(),"Soldier LV5");
    }

    @Test
    public void Test_receiveTroop_int() {
        Troop test = new Troop(5, new TextPlayer("test"));
        test.updateUnit("Soldier LV0", 4, 4, 1000000);
        test.updateUnit("Soldier LV4", 1, 2, 1000000);
        HashMap<String, Integer> dict = new HashMap<>();
        dict.put("Soldier LV0", 1);
        dict.put("Soldier LV4", 1);
        dict.put("Soldier LV5", 1);

        Troop dem = new Troop(dict, test.getOwner());
        Troop sent = test.sendTroop(dem);
        Troop receive = new Troop(5, new TextPlayer("test"));

        receive.receiveTroop(sent);
        assertEquals(receive.checkTroopSize(), 8);
        assertEquals(receive.checkUnitNum("Soldier LV0"), 6);
        assertEquals(receive.checkUnitNum("Soldier LV5"), 1);
        System.out.println(receive.getSummary());
    }
    
    @Test
    public void Test_receiveTroop_String() {
        Troop test = new Troop(5, new TextPlayer("test"));
        test.updateUnit("Soldier LV0", 4, 4, 1000000);
        test.updateUnit("Soldier LV4", 1, 2, 1000000);
        HashMap<String, Integer> dict = new HashMap<>();
        dict.put("Soldier LV0", 1);
        dict.put("Soldier LV4", 1);
        dict.put("Soldier LV5", 1);

        Troop dem = new Troop(dict, test.getOwner());
        Troop sent = test.sendTroop(dem);
        Troop receive = new Troop(5, new TextPlayer("test"));

        receive.receiveTroop(sent);
        assertEquals(receive.checkTroopSize(), 8);
        assertEquals(receive.checkUnitNum("Soldier LV0"), 6);
        assertEquals(receive.checkUnitNum("Soldier LV5"), 1);
    }

    @Test
    public void Test_combat() {
        Random rnd = new Random(0);
        Troop test = new Troop(4, new TextPlayer("test"), rnd);
        Troop enemy = new Troop(4, new TextPlayer("test"), rnd);

        assertEquals(test.combat(enemy).checkTroopSize(), 0);

        Troop test2 = new Troop(5, new TextPlayer("test"), rnd);
        test2.updateUnit("Soldier LV0", 6, 5, 1000000);
        Troop enemy2 = new Troop(25, new TextPlayer("test"), rnd);
        test2.combat(enemy2);
        assertEquals(test2.checkTroopSize(), 5);
    }

    @Test
    public void Test_clone() {
        Random rnd = new Random(0);
        Troop test = new Troop(5, new TextPlayer("test"), rnd);
        Troop clone = test.clone();
        assertEquals(test.checkTroopSize(), clone.checkTroopSize());

        test.dispatchCertainUnit(UNIT_NAMES.get(0));
        test.dispatchCertainUnit(UNIT_NAMES.get(0));
        test.dispatchCertainUnit(UNIT_NAMES.get(0));
        test.dispatchCertainUnit(UNIT_NAMES.get(0));
        test.dispatchCertainUnit(UNIT_NAMES.get(0));
    }

    @Test
    public void Test_transfer() {
        Troop troop1 = new Troop(5, new TextPlayer("test"), new Random(0));
        troop1.transfer(Constant.SOLDIER, KNIGHT, 0,1);
        assertEquals(1, troop1.checkUnitNum("Knight LV0"));
        assertEquals(4, troop1.checkUnitNum("Soldier LV0"));
    }

    @Test
    public void Test_newBattle() {
        Troop troop1 = new Troop(5, new TextPlayer("test1"), new Random(0));
        troop1.transfer(SOLDIER,BREAKER, 0,2);

        Troop troop2 = new Troop(3, new TextPlayer("test2"), new Random(1));
        troop2.transfer(SOLDIER,SHIELD, 0,2);

//        troop2.updateUnit(SHIELD_NAMES.get(0),1,1,100);


        System.out.println(troop1.getSummary());
        System.out.println(troop2.getSummary());


        troop2=troop1.doOneCombat(troop2);
        System.out.println(troop1.getSummary());
        System.out.println(troop2.getSummary());


        troop2=troop1.doOneCombat(troop2);
        System.out.println(troop1.getSummary());
        System.out.println(troop2.getSummary());

        troop2=troop1.doOneCombat(troop2);
        System.out.println(troop1.getSummary());
        System.out.println(troop2.getSummary());

        troop2=troop1.doOneCombat(troop2);
        System.out.println(troop1.getSummary());
        System.out.println(troop2.getSummary());

        troop2=troop1.doOneCombat(troop2);
        System.out.println(troop1.getSummary());
        System.out.println(troop2.getSummary());
        assertEquals(troop2.checkTroopSize(),2);
        assertEquals(troop1.checkTroopSize(),2);


    }

    @Test
    public void Test_newBattleRange() {

        Troop troop1 = new Troop(3, new TextPlayer("test1"), new Random(0));
        troop1.transfer(SOLDIER,ARCHER, 0,2);
        Troop troop2 = new Troop(3, new TextPlayer("test2"), new Random(1));
//        troop2.transfer(SOLDIER,SHIELD, 0,2);
        assertEquals(troop1.getRange(),ARCHER_RANGE);
        System.out.println(troop1.getSummary());
        System.out.println(troop2.getSummary());
        HashMap<String, Integer> tmpDict=new HashMap<>();
        tmpDict.put(ARCHER_NAMES.get(0),2);
        Troop tmp=new Troop(tmpDict,troop1.getOwner());
        Troop troopRange=troop1.sendOutRangedAttack(tmp);
        assertThrows(new IllegalArgumentException().getClass(),() ->troop1.sendOutRangedAttack(tmp));
        troop1.archerReady();
        troop1.sendOutRangedAttack(tmp);


        System.out.println(troop2.getSummary());
        System.out.println(troopRange.getSummary());
        troopRange=troop2.doOneCombat(troopRange);
        System.out.println(troop2.getSummary());
        System.out.println(troopRange.getSummary());

        troopRange=troop2.doOneCombat(troopRange);
        System.out.println(troop2.getSummary());
        System.out.println(troopRange.getSummary());
       }

    @Test
    public void Test_newBattleSimple(){

        Troop troop1 = new Troop(10, new TextPlayer("test1"), new Random(0));

        Troop troop2 = new Troop(3, new TextPlayer("test2"), new Random(1));

        while(troop1.size()>0&&troop2.size()>0){
            System.out.println(troop1.getSummary());
            System.out.println(troop2.getSummary());
            troop2=troop1.doOneCombat(troop2);
        }
    }

    @Test
    public void Test_clone_bug() {
        HashMap<String, Integer> dict = new HashMap<>();
        dict.put(BREAKER_NAMES.get(0), 4);
        Troop troop1= new Troop(dict, new TextPlayer("test1"));
//        Troop troop1 = new Troop(10, new TextPlayer("test1"), new Random(0));
//        troop1.transfer(SOLDIER,BREAKER, 0,2);
        Troop test=troop1.clone();
        System.out.println(test.getSummary());

    }

}
