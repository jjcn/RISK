package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.BasicOrder;
import edu.duke.ece651.group4.RISK.shared.PlaceOrder;
import edu.duke.ece651.group4.RISK.shared.World;
import edu.duke.ece651.group4.RISK.shared.WorldFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import static edu.duke.ece651.group4.RISK.server.ServerConstant.PLAYER_STATE_ACTION_PHASE;
import static edu.duke.ece651.group4.RISK.server.ServerConstant.PLAYER_STATE_SWITCH_OUT;

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
    public int getGameID(){
        return this.gameID;
    }
    public int getMaxNumUsers(){
        return maxNumUsers;
    }
    public World getTheWorld(){
        return theWorld.clone();
    }
    public ArrayList<String> getUserNames(){
        ArrayList<String> userNames = new ArrayList<>();
        for(User u: usersOnGame){
            userNames.add(u.getUsername());
        }
        return userNames;
    }



    public boolean isUserInGame(User u){
        if(usersOnGame.contains(u)){
            return true;
        }
        return false;
    }

    /*
    *  operations to User
    * */
    synchronized public boolean addUser(User u){
        if(isFull()){
            return false;
        }
        usersOnGame.add(u);
        gameState.addPlayerState(u);
        return true;
    }

    synchronized public void switchOutUser(User u){
        if(!isUserInGame(u)){
            return;
        }
        gameState.changAPlayerStateTo(u, PLAYER_STATE_SWITCH_OUT);
    }

    synchronized public void switchInUser(User u){
        if(!isUserInGame(u)){
            return;
        }
        while(!gameState.isWaitToUpdate()){}
        gameState.changAPlayerStateTo(u, PLAYER_STATE_ACTION_PHASE);
    }

    /*
    * Communication between threads
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
    * */
    public boolean isUserLose(User u){
        return this.theWorld.checkLost(u.getUsername());
    }
    public boolean isFull(){
        return usersOnGame.size() == maxNumUsers;
    }
    /*
    * This class check if the game is ended.
    * */
    public boolean isEndGame(){
        return this.theWorld.isGameEnd();
    }


    /*
     * This function is used to update world with any action received from the Client
     * This function has to be locked. This is because all players are sharing the
     * same world
     * */
    synchronized protected void updateActionOnWorld(){


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
    synchronized protected void UpgradeUnitsOnWorld(){

    }

    /*
    * This is the final update for the whole world after one turn
    * */
    public void updateGameAfterOneTurn(){

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
