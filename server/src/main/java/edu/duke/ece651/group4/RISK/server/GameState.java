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
    HashSet<PlayerState> playersState;
    private boolean isDonePlaceUnits;

    public GameState(){
        super(GAME_STATE_WAIT_TO_UPDATE);
        playersState = new HashSet<PlayerState>();
        isDonePlaceUnits = false;
    }

    public boolean changAPlayerStateTo(String username, String state){
        for(PlayerState ps : playersState){
            if(ps.getUsername().equals(username)){
                ps.updateStateTo(state);
            }
        }
        return false;
    }

    /*
    * This set active Players (not SwitchOut) ' state to PLAYER_STATE_UPDATING
    * */
    public void setActivePlayersStateToUpdating(){
        for (PlayerState ps : playersState) {
            if(ps.isActive()){
                ps.updateStateTo(PLAYER_STATE_UPDATING);
            }
        }
    }

    public void addPlayerState(String username){
        playersState.add(new PlayerState(username));
    }
    /*
    * This checks if all players finish one turn
    * game will update the game after it
    * */
    public boolean isAllPlayersDoneOneTurn(){
        for (PlayerState ps : playersState) {
            if(!ps.isDoneOneTurn()){return false;}
        }
        return true;
    }

    /*
     * This checks if all players finish updating their state
     * game will enter GAME_STATE_WAIT_TO_UPDATE state to do action
     * */
    public boolean isAllPlayersDoneUpdatingState(){
        for (PlayerState ps : playersState) {
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
