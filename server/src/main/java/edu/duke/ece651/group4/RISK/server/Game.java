package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.BasicOrder;
import edu.duke.ece651.group4.RISK.shared.World;

import java.util.HashSet;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Game {
    final int gameID;
    int numUsers;
    HashSet<User> usersOnGame;
    World theWorld;
    CyclicBarrier barrier;
    public Game(int gameID, int numUsers) {
        this.gameID = gameID;
        this.numUsers = numUsers;
        this.usersOnGame = new HashSet<User>();
        this.theWorld = null; // This should use init function to get a world based on the number of players
        this.barrier = new CyclicBarrier(numUsers);
    }

    private boolean isFull(){
        return usersOnGame.size() == numUsers;
    }
    private boolean isEmpty(){
        return usersOnGame.size() == 0;
    }

    synchronized public  boolean addUser(User u){
        if(isFull()){
            return false;
        }
        usersOnGame.add(u);
        return true;
    }

    synchronized public  boolean removeUser(User u){
        if(isEmpty()){
            return false;
        }
        usersOnGame.remove(u);
        numUsers -= 1;
        this.barrier = new CyclicBarrier(numUsers);
        return true;
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
