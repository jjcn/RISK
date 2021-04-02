//package edu.duke.ece651.group4.RISK.server;
//
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class PlayerStateTest {
//    @Test
//    public void test_baiscFunction(){
//        PlayerState p = new PlayerState("Ready");
//        assertEquals("Ready", p.getState());
//        p.changeStateTo("Lose");
//        assertEquals("Lose", p.getState());
//        assertEquals(p.isLose(), true);
//        p.changeStateTo("EndOneTurn");
//        assertEquals(p.isDoneOneTurn(), true);
//        p.changeStateTo("Quit");
//        assertEquals(p.isQuit(),true);
//        assertEquals(p.isDoneOneTurn(), true);
//    }
//
//}