package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.BasicOrder;
import edu.duke.ece651.group4.RISK.shared.World;

import java.util.HashSet;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Game {
    private final int gameID;
    private int numUsers;
    private int numUsersSwitchOut;
    private HashSet<User> usersOnGame;
    private World theWorld;
    private CyclicBarrier barrier;
    private CountDownLatch latch;
    private boolean isOnActionPhase;
    private boolean isDonePlaceUnits;
    public Game(int gameID, int numUsers) {
        this.gameID = gameID;
        this.numUsers = numUsers;
        this.usersOnGame = new HashSet<User>();
        this.theWorld = null; // This should use init function to get a world based on the number of players
        this.barrier = new CyclicBarrier(numUsers);
        this.isOnActionPhase = false;
        this.numUsersSwitchOut  = 0;
        this.isDonePlaceUnits = false;
        this.latch = new CountDownLatch(numUsers);
    }

    /*
    * When a user switch in, he/she has to wait for joining
    * until the game starts actionPhase
    * */
    synchronized public void startActionPhase(){
        this.latch = new CountDownLatch(numUsers - numUsersSwitchOut);
        isDonePlaceUnits = true;
        isOnActionPhase = true;
    }

    public void endActionPhase(){
        isOnActionPhase = false;
    }

    public boolean isOnActionPhase(){
        return isOnActionPhase;
    }

    public boolean isDonePlaceUnits(){
        return isDonePlaceUnits;
    }
    private boolean isFull(){
        return usersOnGame.size() == numUsers;
    }

    private boolean isEmpty(){
        return usersOnGame.size() == 0;
    }

    public boolean isUserInGame(User u){
        if(usersOnGame.contains(u)){
            return true;
        }
        return false;
    }
    /*
    *
    * */
    synchronized public  boolean addUser(User u){
        if(isFull()){
            return false;
        }
        usersOnGame.add(u);
        return true;
    }

    synchronized public boolean removeUser(User u){
        if(isEmpty()){
            return false;
        }
        usersOnGame.remove(u);
        numUsers -= 1;
        this.barrier = new CyclicBarrier(numUsers);
        return true;
    }

    synchronized public void switchOutUser(User u){
        if(!isUserInGame(u)){
            return;
        }
        this.numUsersSwitchOut += 1;
        this.barrier = new CyclicBarrier(numUsers - this.numUsersSwitchOut);
        return;
    }
    synchronized public void switchInUser(User u){
        if(!isUserInGame(u)){
            return;
        }
        this.numUsersSwitchOut -= 1;
        this.barrier = new CyclicBarrier(numUsers - this.numUsersSwitchOut);
        return;
    }

    public void latchCountDown(){
        this.latch.countDown();
    }

    public void barrierWait(){
        try {
            this.barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    protected void finishOneTurn(){
        try {
            this.latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // After all players finish their actions, do final updates after one turn

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
    synchronized protected void placeUnitsOnWorld(){

    }
    /*
     * This is to upgrade for each player.
     * This function has to be locked. This is because all players are sharing the
     * same world
     * */
    synchronized protected void UpgradeUnitsOnWorld(){
    }


}
