//package edu.duke.ece651.group4.RISK.server;
//
//import java.util.HashSet;
///*
// * This class handle the state of the host
// * It also records all states of players
// * hostState: 1. FinishUpdateWorld
// *            2. WaitForUpdateWorld
// * */
//public class HostState{
//    HashSet<PlayerState> statsOfPlayers;
//    String hostState;
//    String warReport;
//
//    public HostState(String hostState){
//        this.hostState = hostState;
//        this.statsOfPlayers = new HashSet<PlayerState>();
//        this.warReport = "";
//    }
//
//    /*
//     *  This updates the WarReport
//     *  @param r is the warReport need to be updated
//     * */
//    public void updateWarReport(String r){
//        this.warReport = r;
//    }
//
//    /*
//     *  This gets the WarReport
//     * */
//    public String getWarReport(){
//        return this.warReport;
//    }
//
//    /*
//     *  This helps to add one PlayerState to hostState
//     * */
//    public void addOnePlayerState(PlayerState s){
//        statsOfPlayers.add(s);
//    }
//
//    /*
//     * This helps to check if all players finish oneTurn of moving or attacking
//     * If all players get one turn done, host will update the world
//     * */
//    public boolean isAllPlayersDoneOneTurn(){
//        for(PlayerState s: statsOfPlayers){
//            if(!s.isDoneOneTurn()){
//                return false;
//            }
//        }
//        return true;
//    }
//
//    /*
//     * This helps to change hostState
//     * */
//    public void changeStateTo(String s){
//        this.hostState = s;
//    }
//
//    /*
//     * This helps to check if all thread closes.
//     * after all threads close, the host will close
//     * */
//    public boolean isALlThreadsQuit(){
//        for(PlayerState s: statsOfPlayers){
//            if(!s.isQuit()){
//                return false;
//            }
//        }
//        return true;
//    }
//
//    /*
//     * This helps each thread knows if the host finish update the world
//     * */
//    public boolean isFinishUpdate(){
//        return this.hostState.equals("finishUpdateWorld") ;
//    }
//}