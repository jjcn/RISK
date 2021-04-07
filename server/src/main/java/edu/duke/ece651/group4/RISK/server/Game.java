package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.*;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import static edu.duke.ece651.group4.RISK.server.ServerConstant.*;
import static edu.duke.ece651.group4.RISK.shared.Constant.*;

public class Game {
    private final int gameID;
    private int maxNumUsers;
    private List<User> usersOnGame;
    private World theWorld;
    private CyclicBarrier barrier; // Barrier is only used in PlaceUnitsPhase
    public GameState gameState;
    PrintStream out;
    public boolean waitingFLAG;
    public Game(int gameID, int maxNumUsers) {
        this(gameID,maxNumUsers,System.out);
    }

    public Game(int gameID, int maxNumUsers, PrintStream out) {
        this.gameID = gameID;
        this.maxNumUsers = maxNumUsers;
        this.usersOnGame =  new ArrayList<>();
        this.theWorld = null; // This should use init function to get a world based on the number of players
        this.barrier = new CyclicBarrier(maxNumUsers);
        this.gameState = new GameState();
        this.out = out;
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
        out.println("Game" + gameID + ": " + u.getUsername() + " switches out");
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
        out.println("Game" + gameID + ": " + u.getUsername() + " switches in");
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

    /*
    * This checks if all players switch out.
    * */
    public boolean isAllPlayersSwitchOut(){
        return gameState.isAllPlayersSwitchOut();
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
        try{
            out.println(upgradeOrder.getActionName()+" upgrade from " + upgradeOrder.getLevelBefore() + " to " + upgradeOrder.getLevelAfter());
            theWorld.upgradeTroop(upgradeOrder, userName);
            out.println(upgradeOrder.getActionName()+upgradeOrder.getLevelAfter());
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.println("Game" + gameID + ": " + userName + " upgrade Troop");
    }
    synchronized protected void doDoneActionFor(User u){
        this.gameState.changAPlayerStateTo(u, PLAYER_STATE_END_ONE_TURN);
        out.println("Game" + gameID + ": " + u.getUsername() + " Done action");
    }

    synchronized protected void upgradeTechOnWorld(String userName){
        try{
            theWorld.upgradePlayerTechLevelBy1(userName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.println("Game" + gameID + ": " + userName + " upgrade Tech");
    }
    /*
     * This is to do move for each player.
     * This function has to be locked. This is because all players are sharing the
     * same world
     * */
    synchronized protected void doMoveOnWorld(Order order, String userName){
        MoveOrder moveOrder = (MoveOrder) order;
        try{
            out.println(moveOrder.getActTroop().getSummary());
            this.theWorld.moveTroop(moveOrder, userName);
            out.println(moveOrder.getActionName() +"Troop size:" + moveOrder.getActTroop().checkTroopSize() + "from " + moveOrder.getSrcName() + " to " + moveOrder.getDesName());
            out.println(this.theWorld.findTerritory(moveOrder.getSrcName()).getInfo() );
            out.println(this.theWorld.findTerritory(moveOrder.getDesName()).getInfo() );
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.println("Game" + gameID + ": " + userName + " move action");
    }
    /*
     * This is to do attack for each player.
     * This function has to be locked. This is because all players are sharing the
     * same world
     * */
    synchronized protected void doAttackOnWorld(Order order, String userName){
        AttackOrder attackOrder = (AttackOrder) order;
        try{
            this.theWorld.attackATerritory(attackOrder, userName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.println("Game" + gameID + ": " + userName + " attack action");
    }
    /*
    * This is the final update for the whole world after one turn
    * */
    public void updateGameAfterOneTurn(){
        this.theWorld.doAllBattles();
        this.theWorld.allPlayersGainResources();
        this.theWorld.addUnitToAll(1);
    }

    /*
     * This function is used to update world with any order received from the Client
     * @param order is the order from client
     * @param u is the User who ask for this order
     * @return exit is the boolean value to check if exit this this action phase
     * */
    synchronized protected boolean tryUpdateActionOnWorld(Order order, User u){
        String userName = u.getUsername();
        Character action = order.getActionName();
        boolean exit = false;
        switch(action){
            case ATTACK_ACTION:
                doAttackOnWorld(order, userName);
                break;
            case MOVE_ACTION:
                doMoveOnWorld(order,userName);
                break;
            case UPTECH_ACTION:
                upgradeTechOnWorld(userName);
                break;
            case UPTROOP_ACTION:
                upgradeTroopOnWorld(order, userName);
                break;
            case DONE_ACTION:
                doDoneActionFor(u);
                exit = true;
                break;
            case SWITCH_OUT_ACTION:
                switchOutUser(u);
                exit = true;
                break;
            default:
                exit = true; // when user lose the game, server will receive null from client
                break;
        }
        return exit;
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
                this.theWorld = factory.create4TerritoryWorld();
                break;
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


    public void waitTime(int t){
        try {
            TimeUnit.SECONDS.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
