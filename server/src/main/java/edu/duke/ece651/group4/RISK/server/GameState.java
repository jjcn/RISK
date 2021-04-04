package edu.duke.ece651.group4.RISK.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static edu.duke.ece651.group4.RISK.server.ServerConstant.*;

/*
 * This class handle the state of the game
 * It also records all states of players
 * gameState: 1. GAME_STATE_WAIT_TO_UPDATE
 *            2. GAME_STATE_DONE_UPDATE
 * */

public class GameState extends State{
    HashSet<PlayerState> playerStates;
    private boolean isDonePlaceUnits;
    private boolean isAlive;
    public GameState(){
        super(GAME_STATE_WAIT_TO_UPDATE);
        playerStates = new HashSet<PlayerState>();
        isDonePlaceUnits = false;
        isAlive = true;
    }

    public void setGameDead(){
        this.isAlive = false;
    }
    synchronized void setDonePlaceUnits(){isDonePlaceUnits = true;}
    public boolean isAlive(){
        return isAlive;
    }

    synchronized public boolean changAPlayerStateTo(User u, String state){
        String username = u.getUsername();
        for(PlayerState ps : playerStates){
            if(ps.getUsername().equals(username)){
                ps.updateStateTo(state);
                return true;
            }
        }
        return false;
    }

    public String getAPlayerState(User u){
        String username = u.getUsername();
        String state = null;
        for(PlayerState ps: playerStates){
            if(ps.getUsername().equals(username)){
                state = ps.getState();
            }
        }
        return state;
    }
    /*
    * This set active Players (not SwitchOut) ' state to PLAYER_STATE_UPDATING
    * */
    public void setActivePlayersStateToUpdating(){
        for (PlayerState ps : playerStates) {
            if(ps.isActive()){
                ps.updateStateTo(PLAYER_STATE_UPDATING);
            }
        }
    }

    synchronized public void addPlayerState(User u){
        playerStates.add(new PlayerState(u.getUsername()));
    }
    /*
    * This checks if all players finish one turn
    * game will update the game after it
    * */
    public boolean isAllPlayersDoneOneTurn(){
        for (PlayerState ps : playerStates) {
            if(!ps.isDoneOneTurn()){return false;}
        }
        return true;
    }

    /*
     * This checks if all players finish updating their state
     * game will enter GAME_STATE_WAIT_TO_UPDATE state to do action
     * */
    public boolean isAllPlayersDoneUpdatingState(){
        for (PlayerState ps : playerStates) {
            if(ps.isUpdating()){return false;}
        }
        return true;
    }

    /*
    *
    * */
    public boolean isDoneUpdateGame(){return getState().equals(GAME_STATE_DONE_UPDATE);}

    public boolean isDonePlaceUnits(){
        return this.isDonePlaceUnits;
    }

}
