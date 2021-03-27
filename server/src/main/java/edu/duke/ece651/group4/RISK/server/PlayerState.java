package edu.duke.ece651.group4.RISK.server;

/*
 * This class handles the state of players
 * Player State:
 * 1. Ready: ready to receive the message from the client
 * 2. Lose: lose the game, this time, host will only send the world to each player but will not do any actions
 *   from them
 * 3. EndOneTurn: Finish one turn of the game, will wait for host to update the world
 * 4. Quit: quit the game. If we find the winner, each thread will send winner message to each client and close the thread
 * */
public class PlayerState{

    String state;
    /*
     * This contruct a PlayerState
     * @param startState is the start state
     * */
    public PlayerState(String startState){
        state = startState;
    }

    /*
     * this returns a state
     * */
    public String getState() {
        return state ;
    }
    /*
     * this check if the player loses
     * @return true, if it is. otherwise false
     * */
    public boolean isLose() {
        return getState().equals("Lose") ;
    }
    /*
     * this check if the player quits
     * @return true, if it is. otherwise false
     * */
    public boolean isQuit(){
        return getState().equals( "Quit");
    }
    /*
     * this check if the player done one turn
     * @return true, if it is. otherwise false
     * */
    public boolean isDoneOneTurn(){
        return getState().equals( "EndOneTurn") | isLose() | isQuit() | isExit();
    }
    /*
     * This change a player state
     * @param s is the new state for player
     * */
    public void changeStateTo(String s){
        this.state = s;
    }

    /*
     * this check if the client exit
     * @return true, if it is. otherwise false
     * */
    public boolean isExit(){
        return getState().equals( "Exit");
    }
}
