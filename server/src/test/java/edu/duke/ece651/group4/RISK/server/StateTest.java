package edu.duke.ece651.group4.RISK.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StateTest {
    @Test
    public void test_State(){
        State s = new State("start");
        assertEquals("start",s.getState());
        s.updateStateTo("end");
        assertEquals("end",s.getState());
    }
}