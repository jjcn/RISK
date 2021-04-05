package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import static edu.duke.ece651.group4.RISK.server.ServerConstant.*;
import static edu.duke.ece651.group4.RISK.shared.Constant.*;

public class Game {
    private final int gameID;
    private int maxNumUsers;
    private HashSet<User> usersOnGame;
    private World theWorld;
    private CyclicBarrier barrier; // Barrier is only used in PlaceUnitsPhase
    public GameState gameState;
    public Game(int gameID, int maxNumUsers) {
        this.gameID = gameID;
        this.maxNumUsers = maxNumUsers;
        this.usersOnGame = new HashSet<User>();
        this.theWorld = null; // This should use init function to get a world based on the number of players
        this.barrier = new CyclicBarrier(maxNumUsers);
        this.gameState = new GameState();
    }
    /*
    * This gets the gameID
    * @return gameID
    * */
    public int getGameID(){
        return this.gameID;
    }
    /*
    * get the max number of users
    * @return maxNumUsers
    * */
    public int getMaxNumUsers(){
        return maxNumUsers;
    }
    /*
    * get the clone of a world to send to user
    * @return a clone of the world
    * */
    public World getTheWorld(){
        return theWorld.clone();
    }

    /*
    * This is to get all usernames in this game.
    * @return a list of usernames in the game
    * */
    public ArrayList<String> getUserNames(){
        ArrayList<String> userNames = new ArrayList<>();
        for(User u: usersOnGame){
            userNames.add(u.getUsername());
        }
        return userNames;
    }


    /*
     * This is to check if a user in this game
     * @return true if he is in, false if not
     * */
    public boolean isUserInGame(User u){
        if(usersOnGame.contains(u)){
            return true;
        }
        return false;
    }

    /*
    *  try to add a User to this game
    *  @param u is the user
    *  @ return true if add successfully, false otherwise
    * */
    synchronized public boolean addUser(User u){
        if(isFull()){
            return false;
        }
        usersOnGame.add(u);
        gameState.addPlayerState(u);
        return true;
    }
    /*
     *  try to switch Out a User: change player state as switchout
     *  @param u is the user
     * */
    synchronized public void switchOutUser(User u){
        if(!isUserInGame(u)){
            return;
        }
        gameState.changAPlayerStateTo(u, PLAYER_STATE_SWITCH_OUT);
    }
    /*
     *  try to switch in a User: change player state as PLAYER_STATE_ACTION_PHASE
     *  It will wait until the gameState is in wait to update
     *  @param u is the user
     * */
    synchronized public void switchInUser(User u){
        if(!isUserInGame(u)){
            return;
        }
        while(!gameState.isWaitToUpdate()){}
        gameState.changAPlayerStateTo(u, PLAYER_STATE_ACTION_PHASE);
    }

    /*
    * This this barrier wait for all clientThread in this game
    * This will only be used in place Units Phase
    * */
    public void barrierWait(){
        try {
            this.barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }


    /*
    * This checks a user if lose
    * @return true if lose, false otherwise
    * */
    public boolean isUserLose(User u){
        return this.theWorld.checkLost(u.getUsername());
    }
    /*
     * This checks if the game is full
     * @return true if full, false otherwise
     * */
    public boolean isFull(){
        return usersOnGame.size() == maxNumUsers;
    }

    /*
    * This class check if the game is ended.
    * @return true if ended, false otherwise
    * */
    public boolean isEndGame(){
        return this.theWorld.isGameEnd();
    }



    synchronized protected void doDoneActionFor(User u){
        this.gameState.changAPlayerStateTo(u, PLAYER_STATE_END_ONE_TURN);
    }


    /*
     * This is to select territory for each player.
     * This function has to be locked. This is because all players are sharing the
     * same world
     * */
    synchronized protected void placeUnitsOnWorld(PlaceOrder p){
        this.theWorld.stationTroop(p.getDesName(),p.getActTroop());
    }
    /*
     * This is to upgrade for each player.
     * This function has to be locked. This is because all players are sharing the
     * same world
     * */
    synchronized protected void upgradeTroopOnWorld(Order order, String userName){
        UpgradeTroopOrder upgradeOrder = (UpgradeTroopOrder) order;
        theWorld.upgradeTroop(upgradeOrder, userName);

    }

    synchronized protected void upgradeTechOnWorld(String userName){
        theWorld.upgradePlayerTechLevelBy1(userName);
    }
    /*
     * This is to do move for each player.
     * This function has to be locked. This is because all players are sharing the
     * same world
     * */
    synchronized protected void doMoveOnWorld(Order order, String userName){
        MoveOrder moveOrder = (MoveOrder) order;
        this.theWorld.moveTroop(moveOrder, userName);
    }
    /*
     * This is to do attack for each player.
     * This function has to be locked. This is because all players are sharing the
     * same world
     * */
    synchronized protected void doAttackOnWorld(Order order, String userName){
        AttackOrder attackOrder = (AttackOrder) order;
        this.theWorld.attackATerritory(attackOrder, userName);
    }
    /*
    * This is the final update for the whole world after one turn
    * */
    public void updateGameAfterOneTurn(){
        this.theWorld.doAllBattles();
    }


    /*
    *  Those functions below is for gameRunner
    *
    *  1. setUpGame
    *       1.1 create a world based on the userNames
    * */
    public void setUpGame(){
        WorldFactory factory = new WorldFactory();
        switch(this.maxNumUsers){
            case 1:
            case 2:
            case 3:
                this.theWorld = factory.create6TerritoryWorld();
                break;
            case 4:
                this.theWorld = factory.create8TerritoryWorld();
                break;
            case 5:
                this.theWorld = factory.create10TerritoryWorld();
                break;
            default:
                break;
        }

        factory.assignTerritories(this.theWorld, getUserNames());
    }

}
