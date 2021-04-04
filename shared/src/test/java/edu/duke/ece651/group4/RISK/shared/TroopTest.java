package edu.duke.ece651.group4.RISK.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Random;

public class TroopTest {
    /*
    @Test
    void Test_checkTroopSize() {
        Troop test = new Troop(5, new TextPlayer("test"));
        assertEquals(test.checkTroopSize(), 5);
    }

    @Test
    void Test_sendTroop() {
        Troop test = new Troop(5, new TextPlayer("test"));
        Troop sub = test.sendTroop(3);
        assertEquals(test.checkTroopSize(), 2);
        assertEquals(sub.checkTroopSize(), 3);

        HashMap<String, Integer> myDict = new HashMap<>();
        myDict.put("Soldier LV4", 2);
        myDict.put("Soldier LV0", 4);
        Troop dem = new Troop(myDict, new TextPlayer("test"));
        assertThrows(new IllegalArgumentException().getClass(), () -> test.sendTroop(dem));

    }

    @Test
    void Test_upGrade() {
        Troop test = new Troop(10, new TextPlayer("test"));
        assertEquals(test.checkUnitNum("Soldier LV0"), 10);
        test.updateUnit(0, 2, 4, 2000);

        assertEquals(test.checkUnitNum("Soldier LV2"), 4);
        assertEquals(test.checkUnitNum("Soldier LV0"), 6);
        assertEquals(test.getUnit("Soldier LV2").getJobName(), "Soldier LV2");

        assertEquals(test.checkTroopSize(), 10);

        assertThrows(new IllegalArgumentException().getClass(), () -> test.updateUnit(2, 2, 4, 170));
        assertEquals(test.checkUnitNum("Soldier LV4"), 3);
    }

    @Test
    void Test_receiveTroop() {
        Troop test = new Troop(5, new TextPlayer("test"));
        test.updateUnit(0, 4, 4, 1000000);
        test.updateUnit(4, 1, 2, 1000000);
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
    void Test_combat() {
        Random rnd = new Random(0);
        Troop test = new Troop(4, new TextPlayer("test"), rnd);
        Troop enemy = new Troop(4, new TextPlayer("test"), rnd);

        assertEquals(test.combat(enemy).checkTroopSize(), 1);

        Troop test2 = new Troop(5, new TextPlayer("test"), rnd);
        test2.updateUnit(0, 6, 5, 1000000);
        Troop enemy2 = new Troop(25, new TextPlayer("test"), rnd);
        test2.combat(enemy2);
        assertEquals(test2.checkTroopSize(), 5);
    }

    @Test
    void Test_clone() {
        Random rnd = new Random(0);
        Troop test = new Troop(4, new TextPlayer("test"), rnd);
        Troop clone = test.clone();
        assertEquals(test.checkTroopSize(), clone.checkTroopSize());
    }
    */
}
