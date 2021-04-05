package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.Client;
import edu.duke.ece651.group4.RISK.shared.PlaceOrder;
import edu.duke.ece651.group4.RISK.shared.RoomInfo;
import edu.duke.ece651.group4.RISK.shared.message.GameMessage;
import edu.duke.ece651.group4.RISK.shared.message.LogMessage;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static edu.duke.ece651.group4.RISK.server.ServerConstant.*;
import static edu.duke.ece651.group4.RISK.shared.Constant.*;

public class ClientThread extends Thread {
    HashSet<Game> games;
    HashSet<User> users;
    Client theClient;
    User ownerUser;
    Game gameOnGoing;
    AtomicInteger globalID;
    PrintStream out;

    public ClientThread(HashSet<Game> games, HashSet<User> users, Client theClient, AtomicInteger globalID) {
        this.games = games;
        this.users = users;
        this.theClient = theClient;
        this.ownerUser = null;
        this.globalID = globalID;
        this.out = System.out;
    }

    public ClientThread(HashSet<Game> games, HashSet<User> users, Client theClient, AtomicInteger globalID,PrintStream out) {
        this.games = games;
        this.users = users;
        this.theClient = theClient;
        this.ownerUser = null;
        this.globalID = globalID;
        this.out = out;
    }

    /*
     * Part1 user
     *  1.SignUp
     *  2.SignIn
     *  3.ExitApp
     * */
    public String trySetUpUser()  {
        out.println("Start setup a new user");
        if (ownerUser != null) {
            return null;
        }
        while(true){
            LogMessage logMessage = (LogMessage) this.theClient.recvObject();
            String action = logMessage.getAction();

            if(action.equals(LOG_SIGNIN) ){

                String resIn = tryLogIn(logMessage.getUsername(), logMessage.getPassword());
                this.theClient.sendObject(resIn);
                if(resIn == null){
                    return null;
                }
            }
            if(action.equals(LOG_SIGNUP) ){
                String resUp = trySignUp(logMessage.getUsername(), logMessage.getPassword());
                this.theClient.sendObject(resUp);
            }
        }

    }

    /*
     * Part1.1
     * This tries to sign up a user.
     * @return null if succeed, a error message if fail
     * */
    synchronized protected String trySignUp(String username, String password) {
        if(username == null){return INVALID_SIGNUP;}
        for (User u : users) {
            if (u.checkUsername(username)) {
                return INVALID_SIGNUP;
            }
        }
        User newUser = new User(users.size(), username, password);
        users.add(newUser);
        out.println("This user signs up successfully");
        return null;
    }
    /*
     * Part1.2
     * This tries to let a user log in.
     * @return null if succeed, a error message if fail
     * */
    synchronized protected String tryLogIn(String username, String password) {
        for (User u : users) {
            if (u.checkUsernamePassword(username, password)) {
                out.println("This user logs in successfully");
                this.ownerUser = u;
                return null;
            }
        }
        return INVALID_LOGIN;
    }

    /*
     * Part2
     * Set up a game:
     *    1. Join a game
     *    2. Create a game
     *    3. Refresh games
     *    4. LogOut
     */
    protected void trySetUpGame()  {
        out.println(ownerUser.getUsername() + " tries to set up a game");
        if(gameOnGoing != null){
            return;
        }
        //1.send the gameInfo to Client
//        this.theClient.sendObject(getAllGameInfo());
        //2. select an option
        while(true){
            GameMessage gameMessage = (GameMessage) this.theClient.recvObject();
            String action = gameMessage.getAction();
            Object res = null;

            out.println(ownerUser.getUsername() + " get " + action);
            switch(action) {
                case GAME_CREATE:
                    res = tryCreateAGame(gameMessage);
                    break;
                case GAME_JOIN:
                    res = tryJoinAGame(gameMessage);
                    break;
                case GAME_REFRESH:
                    out.println("Will send a room info to players");
                    res = getAllGameInfo();
                    break;
                case GAME_EXIT:
                    ownerUser = null; // user log out
                    break;
                default:
                    res = "Invalid Action";
            }
            this.theClient.sendObject(res);
            if(res == null){
                return;
            }
        }
    }


    /*
     * Part2.1
     * creates a game
     * creates a game and a gameRunner to run the game
     * */
    synchronized protected String tryCreateAGame(GameMessage gMess){
        int maxNumPlayers = gMess.getNumPlayers();
        if(maxNumPlayers < 1 || maxNumPlayers > 5){
            return INVALID_CREATE;
        }
        this.gameOnGoing = new Game(globalID.getAndIncrement(), maxNumPlayers);
        games.add(gameOnGoing);
        GameRunner gameRunner = new GameRunner(gameOnGoing,out);
        gameOnGoing.addUser(ownerUser);
        gameRunner.start();
        out.println(ownerUser.getUsername() + " creates a game" + gameOnGoing.getGameID() + "successfully and the number of the game support is " + gameOnGoing.getMaxNumUsers());
        return null;
    }

    /*
     * Part2.2
     * Joins a game
     * If game is valid, let it join, else return error message
     * 1. If new game , just add users to game
     * 2. If old game , switchIn this user
     * 3. set gameOngoing = gameToJoin
     * */
     protected String tryJoinAGame(GameMessage gMess){
        int gameID = gMess.getGameID();
        Game gameToJoin = findGame(gameID);
        String res = checkJoinGame(gameToJoin);
        if(res != null){return res;}
        if(!gameToJoin.isFull()){
            gameToJoin.addUser(ownerUser); // this is synchronized function
            out.println(ownerUser.getUsername() + " joins a new game " + gameToJoin.getGameID());
        }
        else{
            gameToJoin.switchInUser(ownerUser); // this is synchronized function
            out.println(ownerUser.getUsername() + " switches in  game " + gameToJoin.getGameID() + " AGAIN");
        }
        gameOnGoing = gameToJoin;
        return null;
    }

    protected String checkJoinGame (Game gameToJoin){
        if(gameToJoin == null){ return INVALID_JOIN;}
        if(gameToJoin.isFull() && !gameToJoin.isUserInGame(ownerUser)){return INVALID_JOIN;}
        if(!gameToJoin.isFull()){
            if(gameToJoin.isUserInGame(ownerUser)){
                return  INVALID_JOIN;
            }
        }
        return null;
    }

    protected Game findGame(int gameID){
        if(gameID < 0){return null;}
        for(Game g : games){
            if(g.getGameID() == gameID){
                return g;
            }
        }
        return null;
    }

    /*
     * Part2.3
     * send all gameInfo to a client
     * */
    protected ArrayList<RoomInfo> getAllGameInfo(){
        out.println(ownerUser.getUsername() + " push refresh button and the number of games now: " + games.size());
        ArrayList<RoomInfo> roomsInfo = new ArrayList<RoomInfo>();
        for(Game g : games){
            if(g.gameState.isAlive()){
                roomsInfo.add(createARoomInfo(g));
            }
        }
        return roomsInfo;
    }
    protected RoomInfo createARoomInfo(Game g){
        return new RoomInfo(g.getGameID(), g.getUserNames(), g.getMaxNumUsers());
    }




    /*
     * Part3
     * place units for a new game
     * */
    protected void tryPlaceUnits(){
        if(gameOnGoing.gameState.isDonePlaceUnits()){
            return;
        }
        out.println("Game" + gameOnGoing.getGameID() + ": " + ownerUser.getUsername() + " wait to place units");
        // wait all players to join and runner to set up the game
        waitNotifyFromRunner();
        // send the world info
        this.theClient.sendObject(gameOnGoing.getTheWorld());
        // start to place Units
        List<PlaceOrder> placeOrders = (List<PlaceOrder> )this.theClient.recvObject();
        for(PlaceOrder p: placeOrders){
            gameOnGoing.placeUnitsOnWorld(p);
        }
        out.println("Game" + gameOnGoing.getGameID() + ": " + ownerUser.getUsername() + " finishes placing units and wait for others");
        // wait all players to finish placeUnits
        gameOnGoing.barrierWait();
        gameOnGoing.gameState.setDonePlaceUnits();
    }


    /*
    * PART4
    * Run Game for one turn
    * */
    protected void tryRunGameOneTurn() {
        if(gameOnGoing == null){
            return;
        }
        doActionPhaseOneTurn();
        while(!gameOnGoing.gameState.isDoneUpdateGame()){}
        waitNotifyFromRunner();
        checkResultOneTurn();
    }

    protected void doActionPhaseOneTurn(){
        this.theClient.sendObject(gameOnGoing.getTheWorld());
        // if Done or SwitchOut


    }

    /*
    * This mainly update the player state after one turn
    * If switchOut, change state to PLAYER_STATE_SWITCH_OUT and set gameOnGoing = null;
    * else if lose, change state to PLAYER_STATE_LOSE
    * else, change state to PLAYER_STATE_ACTION_PHASE
    * */
    protected void checkResultOneTurn(){
        //Go back to Games Page (Part2)
        if(gameOnGoing.gameState.getAPlayerState(ownerUser).equals(PLAYER_STATE_SWITCH_OUT)){
            gameOnGoing = null;
        }
        else if(gameOnGoing.isUserLose(ownerUser)){
            gameOnGoing.gameState.changAPlayerStateTo(ownerUser, PLAYER_STATE_LOSE);
        }
        else{
            gameOnGoing.gameState.changAPlayerStateTo(ownerUser, PLAYER_STATE_ACTION_PHASE);
        }
    }

    /*
    * This waits for notify from runner
    * */
    public void waitNotifyFromRunner(){
        try {
            synchronized (gameOnGoing){
                gameOnGoing.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // Part1.User
        //   1.1 LogIn
        //   1.2 SignUp
        //   1.3 Exit the App
        //      send feedback every time

        // Part2. init rooms:
        //    send all game info
        //    2.1 create a game
        //          start a gameRunner
        //    2.2 join a game
        //          if the game is new, add game.addUser
        //          if the game is old, loadGame()
        //    2.3 "refresh"
        //         send all game info
        //    2.4 LogOut

        // Part3. game init
        //  Initialization info including:
        //      send init World
        //      recv PlaceOrders


        // Part4 ActionsPhase:
        //        4.0  send World
        //        4.1  do actions for Each Turn
        //             Game will deal with Actions: Move, Attack, Upgrade, Done, SwitchOut
        //             After each turn, this thread will wait for gameRunner to update the results
        //        4.2
        //             4.21 If player lose:
        //                    keep sending results
        //             4.22 If player SwitchOut:
        //                    go back to 2.
        //                    set gameOneGoing = null

        while (true) {
            trySetUpUser(); //part1 above
            trySetUpGame(); //part2 above
            tryPlaceUnits(); //part3 above
            tryRunGameOneTurn();//part4 above
        }
    }
}
