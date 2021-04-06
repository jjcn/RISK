package edu.duke.ece651.group4.RISK.server;

import org.junit.jupiter.api.Test;

import static edu.duke.ece651.group4.RISK.server.ServerConstant.*;
import static org.junit.jupiter.api.Assertions.*;

class PlayerStateTest {
    @Test
    public void test_basicFunction(){
        PlayerState p = new PlayerState("1");
        assertEquals(p.isDoneOneTurn(), false);
        assertEquals(PLAYER_STATE_ACTION_PHASE, p.getState());
        assertEquals(p.isActive(), true);
        p.updateStateTo(PLAYER_STATE_LOSE);
        assertEquals(PLAYER_STATE_LOSE, p.getState());
        assertEquals(p.isLose(), true);
        assertEquals(p.isDoneOneTurn(), true);
        p.updateStateTo(PLAYER_STATE_SWITCH_OUT);
        assertEquals(p.isSwitchOut(), true);
        assertEquals(p.isActive(), false);
        assertEquals(p.isDoneOneTurn(), true);
        p.updateStateTo(PLAYER_STATE_END_ONE_TURN);
        assertEquals(p.isUpdating(), false);
        p.updateStateTo(PLAYER_STATE_UPDATING);
        assertEquals(p.isUpdating(), true);
    }

}