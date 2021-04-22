package edu.duke.ece651.group4.RISK.server;

import java.io.Serializable;
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

public class GameState extends State  implements Serializable{
    HashSet<PlayerState> playerStates;
    private boolean isDonePlaceUnits;
    private boolean isAlive;
    private boolean isSetUp;
    public GameState(){
        super(GAME_STATE_WAIT_TO_UPDATE);
        playerStates = new HashSet<PlayerState>();
        isDonePlaceUnits = false;
        isAlive = true;
        isSetUp = false;
    }

    public void DoneSetUp(){
        isSetUp = true;
    }

    public boolean isSetUp(){
        return isSetUp;
    }
    /*
    * This set the game dead after the game ends
    * */
    public void setGameDead(){
        this.isAlive = false;
    }

    /*
     * This marks the DonePlaceUnits to make sure users will not join PlacePhase anytime
     * */
    synchronized void setDonePlaceUnits(){isDonePlaceUnits = true;}

    /*
     * This checks if a game is alive
     * */
    public boolean isAlive(){
        return isAlive;
    }

    /*
    * This change a player state to a specific state
    * @param u is the user
    * @param state is the playerState
    * @return true if success, false otherwise.
    * */
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

    /*
     * This helps to make sure that runner notify all players after them waits
     * This set playerState isWating to true
     * @param u is the user
     * @return true if success, false if fails
     * */
    synchronized public boolean askUserWaiting(User u){
        String username = u.getUsername();
        for(PlayerState ps : playerStates){
            if(ps.getUsername().equals(username)){
                ps.setWaiting();
                return true;
            }
        }
        return false;
    }

    /*
     * This helps to make sure that runner notify all players after them waits
     * This set playerState isWating to false
     * @param u is the user
     * @return true if success, false if fails
     * */
    synchronized public boolean askUserDoneWaiting(User u){
        String username = u.getUsername();
        for(PlayerState ps : playerStates){
            if(ps.getUsername().equals(username)){
                ps.doneWaiting();
                return true;
            }
        }
        return false;
    }

    /*
     * Get playerState of this user
     * @param u is the user
     * @return a string of the playerState
     * */
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

    /*
    *  This adds a playerState of the user
    *  @param a User
    * */
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
    * This checks if all players switch out
    * runner will end automatically if all players Switch out
    * */
    public boolean isAllPlayersSwitchOut(){
        for(PlayerState ps: playerStates){
            if(!ps.isSwitchOut()){
                return false;
            }
        }
        return true;
    }

    /*
    * We need this to make sure every players are waiting before
    * runner notify them all
    * check if all players waits for notify from runner
    * @return true if it is, false otherwise.
    * */
    public boolean isAllPlayersWaiting(){
        for(PlayerState ps: playerStates){
            if(!ps.isWaiting()){
                return false;
            }
        }
        return true;
    }

    /*
    * This checks if gameState is at GAME_STATE_DONE_UPDATE
    * @return true if it is, false otherwise
    * */
    public boolean isDoneUpdateGame(){return getState().equals(GAME_STATE_DONE_UPDATE);}

    /*
     * This checks if this game finishes donePlaceUnits
     * This is to make sure if a user joins the game again, it will not do placeUnits Phase
     * */
    public boolean isDonePlaceUnits(){
        return this.isDonePlaceUnits;
    }
    /*
     * This checks if gameState is at GAME_STATE_WAIT_TO_UPDATE
     * @return true if it is, false otherwise
     * */
    public boolean isWaitToUpdate(){return getState().equals(GAME_STATE_WAIT_TO_UPDATE);}

//    public GameState clone(){
//        GameState gs = new GameState();
//        for(PlayerState ps: playerStates){
//            gs.playerStates.add(ps.clone());
//        }
//        if(!gs.isAlive){
//            gs.setGameDead();
//        }
//        if(gs.isDonePlaceUnits){
//            gs.setDonePlaceUnits();
//        }
//        return gs;
//    }
}
