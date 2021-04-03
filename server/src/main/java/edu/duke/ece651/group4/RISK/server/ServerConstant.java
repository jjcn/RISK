package edu.duke.ece651.group4.RISK.server;

//Player State:
/*
* 1. PLAYER_STATE_ACTION_PHASE:
*
*
* 2. PLAYER_STATE_END_ONE_TURN:
*
*       while(!gameState.isFinishUpdateGame){}
*
*
* 3. PLAYER_STATE_UPDATING:
*          3.1 PLAYER_STATE_SWITCH_OUT:
*          3.2 PLAYER_STATE_LOSE:
*          while(gameState != GAME_STATE_WAIT_TO_UPDATE){}
* */


//Game State:
/*
* 1. GAME_STATE_WAIT_TO_UPDATE:
*       while(true){
*           if(gameState.isAllPlayersEndOneTurn){
*               //set all active Player as PLAYER_STATE_UPDATING
*           }
*       }
*
*
* 2. GAME_STATE_DONE_UPDATE:
*       if(gameState.isAllPlayersEndUpdate){
*           GAME_STATE_WAIT_TO_UPDATE
*       }
*
*
* */

public class ServerConstant {
    //PlayerState
    public static final String PLAYER_STATE_ACTION_PHASE = "PLAYER_STATE_ACTION_PHASE";
    public static final String PLAYER_STATE_LOSE = "PLAYER_STATE_LOSE";
    public static final String PLAYER_STATE_END_ONE_TURN = "PLAYER_STATE_END_ONE_TURN";
    public static final String PLAYER_STATE_UPDATING = "PLAYER_STATE_UPDATING";
    public static final String PLAYER_STATE_SWITCH_OUT ="PLAYER_STATE_SWITCH_OUT" ;

    //GameState
    public static final String GAME_STATE_WAIT_TO_UPDATE = "GAME_STATE_WAIT_TO_UPDATE";
    public static final String GAME_STATE_DONE_UPDATE = "GAME_STATE_DONE_UPDATE";
}

