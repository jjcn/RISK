package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.Client;
import edu.duke.ece651.group4.RISK.shared.Order;
import edu.duke.ece651.group4.RISK.shared.PlaceOrder;
import edu.duke.ece651.group4.RISK.shared.RoomInfo;
import edu.duke.ece651.group4.RISK.shared.message.GameMessage;
import edu.duke.ece651.group4.RISK.shared.message.LogMessage;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static edu.duke.ece651.group4.RISK.server.ServerConstant.*;
import static edu.duke.ece651.group4.RISK.shared.Constant.*;

public class ClientThread extends Thread {
    List<Game> games;
    List<User> users;
    Client theClient;
    User ownerUser;
    Game gameOnGoing;
    AtomicInteger globalID;
    PrintStream out;

    public ClientThread(List<Game> games, List<User> users, Client theClient, AtomicInteger globalID) {
        this(games,users,theClient,globalID,System.out);
    }

    public ClientThread(List<Game> games, List<User> users, Client theClient, AtomicInteger globalID,PrintStream out) {
        this.games = games;
        this.users = users;
        this.theClient = theClient;
        this.ownerUser = null;
        this.globalID = globalID;
        this.out = out;
        this.gameOnGoing = null;
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
        HibernateTool.addUserInfo(newUser.userInfo); //store this userInfo into database
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
            out.println("Already is on a game, skip set up game ");
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
//                    out.println("Will send a room info to players");
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
        out.println(ownerUser.getUsername() + " creates a game" + gameOnGoing.getGameID() + " successfully and the number of the game support is " + gameOnGoing.getMaxNumUsers());
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
//            out.println(ownerUser.getUsername() + " joins a new game " + gameToJoin.getGameID());
        }
        else{
            gameToJoin.switchInUser(ownerUser); // this is synchronized function
//            out.println(ownerUser.getUsername() + " switches in  game " + gameToJoin.getGameID() + " AGAIN");
        }
        gameOnGoing = gameToJoin;
        out.println( "Game" + gameOnGoing.getGameID() + " has " + gameOnGoing.getUserNames().size() +" users now.");
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

    /*
    * This finds a game bases on a gameID
    * @param gameID
    * @return the game of that ID or null if not found
    * */
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
     * @return a arrayList<RoomInfo> that will be sent to this client
     * */
    protected ArrayList<RoomInfo> getAllGameInfo(){
        out.println(ownerUser.getUsername() + " push refresh button and the number of games now: " + games.size());
        ArrayList<RoomInfo> roomsInfo = new ArrayList<RoomInfo>();
        for(Game g : games){
            if(g.gInfo.gameState.isAlive()){
//                out.println("Game"+ g.getGameID() + " is active and should be shown in room list");
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
        if(gameOnGoing == null){
            return;
        }
        out.println("Game" + gameOnGoing.getGameID() + ": " + ownerUser.getUsername() + " Place Units Phase");
        if(gameOnGoing.gInfo.gameState.isDonePlaceUnits()){
            return;
        }
        gameOnGoing.barrierWait();
        // wait all players to join and runner to set up the game
        waitNotifyFromRunner(); // This is to make sure runner notify all after all waits
        // send the world info
        this.theClient.sendObject(gameOnGoing.getTheWorld());
        out.println("Game" + gameOnGoing.getGameID() + ": send world to " + ownerUser.getUsername() + " wait for orders" );
        // start to place Units
        List<PlaceOrder> placeOrders = (List<PlaceOrder> )this.theClient.recvObject();
        for(PlaceOrder p: placeOrders){
            gameOnGoing.placeUnitsOnWorld(p);
        }
        out.println("Game" + gameOnGoing.getGameID() + ": " + ownerUser.getUsername() + " finishes placing units and wait for others");
        // wait all players to finish placeUnits
        gameOnGoing.barrierWait();
        gameOnGoing.gInfo.gameState.setDonePlaceUnits(); // if user joins back, he does not need to do place unit phase

    }


    /*
     * PART4
     * 4.1 action phase
     *       4.10 do actions
     *       4.11 wait runner to update the world
     *       4.12 wait runner to set active players as updaing state
     * 4.2  updating states phase
     * Run Game for one turn
     * */
    protected void tryRunGameOneTurn() {
        if(gameOnGoing == null){
            return;
        }
        out.println("Game" + gameOnGoing.getGameID() + ": " + ownerUser.getUsername() + " action phase");
        doActionPhaseOneTurn();

        if(gameOnGoing.gInfo.gameState.getAPlayerState(ownerUser).equals(PLAYER_STATE_SWITCH_OUT)){
            out.println("Game" + gameOnGoing.getGameID() + ": Checking Phase :  " + ownerUser.getUsername() + " Switches Out");
            gameOnGoing = null;
            return;
        }
        
        out.println("Game" + gameOnGoing.getGameID() + ": " + ownerUser.getUsername() + " wait for runner update the world");
        boolean exit = false;
        while(!exit){
            if(!gameOnGoing.gInfo.gameState.isDoneUpdateGame()){
                exit = true;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        while(!gameOnGoing.gameState.isDoneUpdateGame()){gameOnGoing.waitTime(1);}
        out.println("Game" + gameOnGoing.getGameID() + ": " + ownerUser.getUsername() + " knows world is updated");

        waitNotifyFromRunner();
        updatePlayerStateOneTurn();
    }

    /*
    * 4.1 action phase
    * This does action phase for one turn
    * */
    protected void doActionPhaseOneTurn(){
        boolean exit = false;
        boolean start = true;
        while(!exit){
            if(start){
                this.theClient.sendObject(gameOnGoing.getTheWorld());
                start = false;
            }
            out.println("Game" + gameOnGoing.getGameID() + ": send world to " + ownerUser.getUsername() + " wait for orders" );
            Order order = (Order) this.theClient.recvObject();
            out.println("Game" + gameOnGoing.getGameID() + ": " + ownerUser.getUsername() + " has a order: " + order.getActionName());
            exit = gameOnGoing.tryUpdateActionOnWorld(order,ownerUser);
        }
    }

    /*
     * 4.2 action phase
     * This mainly update the player state after one turn
     * if switch out, set gameOnGoing = null
     * else if lose, change state to PLAYER_STATE_LOSE
     * else, change state to PLAYER_STATE_ACTION_PHASE
     * */
    protected void updatePlayerStateOneTurn(){
        //Go back to Games Page (Part2)
        if(gameOnGoing.isEndGame()){
            out.println("Game" + gameOnGoing.getGameID() + ": Checking Phase :  game END!!!! and winner is " + gameOnGoing.getTheWorld().getWinner() );
            this.theClient.sendObject(gameOnGoing.getTheWorld());
            gameOnGoing.switchOutUser(ownerUser);
            gameOnGoing = null;
            return;
        }

        /*        if(gameOnGoing.gameState.getAPlayerState(ownerUser).equals(PLAYER_STATE_SWITCH_OUT)){
            out.println("Game" + gameOnGoing.getGameID() + ": Checking Phase :  " + ownerUser.getUsername() + " Switches Out");
            gameOnGoing = null;
            }*/
        if(gameOnGoing.isUserLose(ownerUser)){
            out.println("Game" + gameOnGoing.getGameID() + ": Checking Phase :  " + ownerUser.getUsername() + " loses");
            gameOnGoing.gInfo.gameState.changAPlayerStateTo(ownerUser, PLAYER_STATE_LOSE);
        }
        else{
            out.println("Game" + gameOnGoing.getGameID() + ": Checking Phase :  " + ownerUser.getUsername() + " go back to do action");
            gameOnGoing.gInfo.gameState.changAPlayerStateTo(ownerUser, PLAYER_STATE_ACTION_PHASE);
        }
    }

    /*
     * This waits for notify from runner
     * */
    public void waitNotifyFromRunner(){
        out.println("Game" + gameOnGoing.getGameID() + ": " + ownerUser.getUsername() + " enter wait notify from runner");
        try {

            synchronized (gameOnGoing){
                out.println("Game" + gameOnGoing.getGameID() + ": " + ownerUser.getUsername() + " wait for runner's notify");
                gameOnGoing.gInfo.gameState.askUserWaiting(ownerUser);
                gameOnGoing.wait();
                gameOnGoing.gInfo.gameState.askUserDoneWaiting(ownerUser);
                out.println("Game" + gameOnGoing.getGameID() + ": " + ownerUser.getUsername() + " get notify from runner");
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
        //          if the game is old, SwitchInUser()
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
