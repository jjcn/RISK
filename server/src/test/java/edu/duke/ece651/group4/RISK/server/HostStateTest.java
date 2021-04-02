//package edu.duke.ece651.group4.RISK.server;
//
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class HostStateTest {
//
//    @Test
//    public void test_isFinishUpdate(){
//        HostState p = new HostState("WaitForUpdateWorld");
//        assertEquals(false, p.isFinishUpdate());
//        p.changeStateTo("finishUpdateWorld");
//        assertEquals(true, p.isFinishUpdate());
//    }
//
//    @Test
//    public void test_isALlThreadsQuit(){
//        HostState h = new HostState("WaitForUpdateWorld");
//        PlayerState s1 = new PlayerState("Ready");
//        h.addOnePlayerState(s1);
//        PlayerState s2 = new PlayerState("Ready");
//        h.addOnePlayerState(s2);
//        assertEquals(false, h.isALlThreadsQuit());
//        s2.changeStateTo("Quit");
//        assertEquals(false, h.isALlThreadsQuit());
//        s1.changeStateTo("Quit");
//        assertEquals(true, h.isALlThreadsQuit());
//    }
//
//    @Test
//    public void test_isAllPlayerDoneOneTurn(){
//        HostState h = new HostState("WaitForUpdateWorld");
//        PlayerState s1 = new PlayerState("Ready");
//        h.addOnePlayerState(s1);
//        PlayerState s2 = new PlayerState("Ready");
//        h.addOnePlayerState(s2);
//        assertEquals(false, h.isAllPlayersDoneOneTurn());
//        s2.changeStateTo("Lose");
//        assertEquals(false, h.isAllPlayersDoneOneTurn());
//        s1.changeStateTo("EndOneTurn");
//        assertEquals(true, h.isAllPlayersDoneOneTurn());
//    }
//    @Test
//    public void test_warReport(){
//        HostState h = new HostState("WaitForUpdateWorld");
//        h.updateWarReport("String r");
//        assertEquals(h.getWarReport(),"String r");
//    }
//}