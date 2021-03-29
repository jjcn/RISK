package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.*;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class ClientThread extends Thread {
    World theWorld;
    PlayerState playerState;
    HostState hostState;
    Client theClient;
    String playerName;
    CyclicBarrier barrier;
    List<Territory> initTerritory;
    int playerID;

    public ClientThread(World w, String playerName, int playerID, 
                        CyclicBarrier barrier, 
                        PlayerState playerState,
                        Client theClient, HostState hoststate, 
                        List<Territory> initTerritory) {
        this.theWorld = w;
        this.playerState = playerState;
        this.theClient  = theClient;
        this.hostState = hoststate;
        this.playerName = playerName;
        this.barrier = barrier;
        this.playerID = playerID;
        this.initTerritory = initTerritory;
    }

    /*
     *  This sends a player info to each Client.
     * */
    protected void sendPlayerNameToClient() throws IOException {
        this.theClient.sendObject(this.playerName);
    }

    /*
     *  This send the newest worldMap to the Client
     * */
    protected void sendWorldToClient() throws IOException {
        World sendWorld = this.theWorld.clone();
        this.theClient.sendObject(sendWorld);

    }
    /*
     *  This send the newest WarReport to the Client
     * */
    protected void sendWarReportToClient() throws IOException {
        this.theClient.sendObject(this.hostState.getWarReport());
    }

    /*
     * This send init territory to each player.
     * */
    protected void sendInitTerritory() throws IOException {
        this.theClient.sendObject(this.initTerritory); 
    }

    protected void updatePlayerState() throws IOException, ClassNotFoundException {
        String s = (String) this.theClient.recvObject();
        System.out.println("receice state from client: " + s);
        if(s.equals("Exit")){
            playerState.changeStateTo("Exit");
            System.out.println("change state to Exit");
        }
    }

    /*
     * This is to select territory for each player.
     * */
    protected void selectUnits() throws IOException, ClassNotFoundException {
        int orderNum = this.initTerritory.size();
        System.out.println("To " + this.playerName + ", orderNum: " + orderNum );
        while(orderNum > 0) {
            PlaceOrder newOrder = null;
            newOrder = (PlaceOrder) this.theClient.recvObject();
            System.out.println("thread: start update selectunits");
            this.theWorld.stationTroop(newOrder.getDesName(),newOrder.getActTroop());
            System.out.println("thread: finish update selectunits");
            orderNum--;
        }

    }

    /*
     * This handles all orders from the Client
     * */
    protected void doActionPhase(){
        while( !playerState.isDoneOneTurn()) {
            BasicOrder newOrder = null;
            newOrder = (BasicOrder) receiveInfo(newOrder, this.theClient);
            updateActionOnWorld(newOrder);
        }
    }

    /*
     * This function is used to update world with any action received from the Client
     * This function has to be locked. This is because all players are sharing the
     * same world
     * */
    synchronized protected void updateActionOnWorld(BasicOrder receiveMessage){
        System.out.println(this.playerName +  receiveMessage.getActionName());
        if (receiveMessage.getActionName() == 'M') {
            this.theWorld.moveTroop(receiveMessage);
        } else if (receiveMessage.getActionName() == 'A') {
            this.theWorld.attackATerritory(receiveMessage);
        } else {
            playerState.changeStateTo("EndOneTurn");
        }
    }


    /*
     * This is a function to check if  the player in this clientThread lost the game
     * It will iterate all territories in the world to see if there is a terr that belongs to this Player
     * If a player lose, we will change the playerState to Lose
     * */
    protected boolean isPlayerLost(){
        return this.theWorld.checkLost(this.playerName);
    }


    /*
     * This is to check if there is only one winner in the world after the world
     * is updated by the host
     * */
    protected boolean isGameEnd(){
        return this.theWorld.isGameEnd();
    }

    @Override
    public void run(){
        System.out.println( this.playerName + " gets connected with the server.");
        System.out.println( "Please wait for other player to get connected.");
        try {
            barrier.await();
            sendPlayerNameToClient();
            System.out.println("To " + this.playerName + ", send the name" );
            sendWorldToClient();
            System.out.println("To " + this.playerName + ", send the world" );
            sendInitTerritory();
            System.out.println("To " + this.playerName + ", send territories" );
            barrier.await();
            selectUnits();
            System.out.println("To " + this.playerName + ", update select Units ");
            barrier.await();
            sendWorldToClient();
            System.out.println("To " + this.playerName + ", send the first complete world.");
            /*
             * keep receiving message from the Client to do action
             * */
            while(true) {
                doActionPhase();
                System.out.println(this.playerName + "finish this turn of moving and attacking.");
                barrier.await(); // Wait everyone to finish their actions

                while (!hostState.isFinishUpdate()) {} // wait hostState to update the world
                System.out.println(playerState.isExit());
                if(!playerState.isExit()){
                    sendWorldToClient();
                    sendWarReportToClient();
                    updatePlayerState();
                }
                System.out.println("Sent world and map");
                barrier.await();
                System.out.println("Done waiting for all threads");
                //Everyone need to change their state after this
                if(!playerState.isExit()){
                    if (isPlayerLost()) {
                        playerState.changeStateTo("Lose");
                        System.out.println(this.playerName + "lose the game now.");
                    }
                    else{
                        playerState.changeStateTo("Ready");
                    }
                }
                //check If the game ends, send message to all players and quit
                if(isGameEnd()){
                    playerState.changeStateTo("Quit");
                    // if we find a winner, and hostState will be endGame. Every thread should close
                    System.out.println(this.playerName + "quit the game.");
                    this.theClient.close();
                    break;
                }

                barrier.await();
                hostState.changeStateTo("WaitForUpdateWorld");
            }


        }catch (Exception ignored){

        }
    }

    protected Object receiveInfo(Object o, Client c){

        try {
            o = c.recvObject();
        } catch (Exception e) {
            System.out.println("Socket name problem!");
        }

        return o;
    }


}