package edu.duke.ece651.group4.RISK.server;

import static edu.duke.ece651.group4.RISK.server.ServerConstant.*;

/*
 * This class handles the state of players
 * Player State:
 * 1. PLAYER_STATE_ACTION_PHASE: ready to receive the message from the client
 * 2. PLAYER_STATE_LOSE: lose the game, this time, host will only send the world to each player but will not do any actions
 *    from them
 * 3. PLAYER_STATE_END_ONE_TURN : Finish one turn of the game, will wait for host to update the world
 * 4. PLAYER_STATE_SWITCH_OUT: user switch out from this game
 * 5. PLAYER_STATE_UPDATING: after the world updates the world, do final check on state
 * */
public class PlayerState extends State{
    final String username;
    /*
     * This construct a PlayerState
     * @param startState is the start state
     * */
    public PlayerState(String username){
        super(PLAYER_STATE_ACTION_PHASE);
        this.username = username;
    }

    public String getUsername(){
        return username;
    }
    /*
     * this check if the player loses
     * @return true, if it is. otherwise false
     * */
    public boolean isLose() {
        return getState().equals(PLAYER_STATE_LOSE) ;
    }
    /*
     * this check if the player switch out a game.
     * @return true, if it is. otherwise false
     * */
    public boolean isSwitchOut(){
        return getState().equals(PLAYER_STATE_SWITCH_OUT);
    }
    /*
     * this check if the player done one turn
     * @return true, if it is. otherwise false
     * */
    public boolean isDoneOneTurn(){
        return getState().equals( PLAYER_STATE_END_ONE_TURN) | isLose() | isSwitchOut();
    }
    /*
    * This is updating state where the players update's its own state.
    * */
    public boolean isUpdating(){return getState().equals(PLAYER_STATE_UPDATING);}

    public boolean isActive(){
        return !isSwitchOut();
    }
}
