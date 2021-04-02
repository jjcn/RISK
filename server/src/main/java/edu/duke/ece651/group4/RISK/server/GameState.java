package edu.duke.ece651.group4.RISK.server;

import java.util.HashMap;

import static edu.duke.ece651.group4.RISK.shared.Constant.GAME_STATE_WAIT_TO_UPDATE;

/*
 * This class handle the state of the game
 * It also records all states of players
 * gameState: 1. GAME_STATE_WAIT_TO_UPDATE
 *            2. GAME_STATE_DONE_UPDATE
 * */

public class GameState {
    HashMap<String, PlayerState> playersState;
    String gameState;
    private boolean isOnActionPhase;
    private boolean isDonePlaceUnits;
    private boolean isALlUsersDone;

    public GameState(){
        playersState = new HashMap<String, PlayerState>();
        gameState = GAME_STATE_WAIT_TO_UPDATE;
        isOnActionPhase = false;
        isDonePlaceUnits = false;
        isALlUsersDone = false;
    }

    public void updateGameState(String s){
        gameState = s;
    }

    /*
     * When a user switch in, he/she has to wait for joining
     * until the game starts actionPhase
     * */
    synchronized public void startActionPhase(){ // used in client Thread
        isDonePlaceUnits = true;
        isOnActionPhase = true;
    }
    public void endActionPhase(){// used in game Runner
        isOnActionPhase = false;
    }
    public boolean isOnActionPhase(){
        return isOnActionPhase;
    }

    public boolean isDonePlaceUnits(){
        return isDonePlaceUnits;
    }

}
