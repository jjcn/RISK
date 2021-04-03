package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.Client;
import edu.duke.ece651.group4.RISK.shared.RoomInfo;
import edu.duke.ece651.group4.RISK.shared.message.GameMessage;
import edu.duke.ece651.group4.RISK.shared.message.LogMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import static edu.duke.ece651.group4.RISK.shared.Constant.*;

public class ClientThread extends Thread {
    HashSet<Game> games;
    HashSet<User> users;
    Client theClient;
    User ownerUser;
    Game gameOnGoing;
    AtomicInteger globalID;

    public ClientThread(HashSet<Game> games, HashSet<User> users, Client theClient, AtomicInteger globalID) {
        this.games = games;
        this.users = users;
        this.theClient = theClient;
        this.ownerUser = null;
        this.globalID = globalID;
    }


    /*
     * Part1 user
     *  1.SignUp
     *  2.SignIn
     *  3.ExitApp
     * */
    public String trySetUpUser()  {
        System.out.println("Start setup user ");
        System.out.println("the number of user now: " + users.size());
        if (ownerUser != null) {
            return null;
        }
        while(true){
//            LogMessage logMessage = null; //receive a LogMessage
            LogMessage logMessage = (LogMessage) this.theClient.recvObject();
            String action = logMessage.getAction();
            System.out.println("get Message: " + action);
            if(action.equals(LOG_SIGNIN) ){
                System.out.println("User tries to log in  ");
                String resIn = tryLogIn(logMessage.getUsername(), logMessage.getPassword());
                this.theClient.sendObject(resIn);
                if(resIn == null){
                    return null;
                }
            }
            if(action.equals(LOG_SIGNUP) ){
                System.out.println("User tries to sign up  ");
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
    protected String trySignUp(String username, String password) {
        for (User u : users) {
            if (u.checkUsername(username)) {
                return INVALID_SIGNUP;
            }
        }
        User newUser = new User(users.size(), username, password);
        users.add(newUser);
        return null;
    }
    /*
     * Part1.2
     * This tries to let a user log in.
     * @return null if succeed, a error message if fail
     * */
    protected String tryLogIn(String username, String password) {
        for (User u : users) {
            if (u.checkUsernamePassword(username, password)) {
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
        if(gameOnGoing != null){
            return;
        }
        //1.send the gameInfo to Client
        //2. select an option
        while(true){
            GameMessage gameMessage = (GameMessage) this.theClient.recvObject();
            String action = gameMessage.getAction();
            Object res = null;
            if(action.equals(GAME_CREATE)){
                res = tryCreateAGame(gameMessage);
            }
            if(action.equals(GAME_JOIN)){
                res = tryJoinAGame(gameMessage);
            }
            if(action.equals(GAME_REFRESH)){
                res = getAllGameInfo();
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
        if(maxNumPlayers <= 0 || maxNumPlayers > 5){
            return "Invalid: the number of players should be between 2 and 5 inclusive.";
        }
        Game newGame = new Game(globalID.getAndIncrement(), maxNumPlayers);
        games.add(newGame);
        return null;
    }

    /*
     * Part2.2
     * Joins a game
     * If game is valid, let it join, else return error message
     * 1. If game starts, just set gameOnGoing = game
     * 2. If game waits, just add users to game, and set gameOnGoing = game
     * */
    synchronized protected String tryJoinAGame(GameMessage gMess){

        int gameID = gMess.getGameID();
        Game gameToJoin = findGame(gameID);
        if(gameToJoin == null){ return "Invalid GameID";}
        if(!gameToJoin.isUserInGame(ownerUser)){return "Invalid join: you are not in this game!";}
        gameOnGoing = gameToJoin;
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
        ArrayList<RoomInfo> roomsInfo = new ArrayList<RoomInfo>();
        for(Game g : games){
            roomsInfo.add(createARoomInfo(g));
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
    protected  void tryPlaceUnits(){
        if(gameOnGoing.gameState.isDonePlaceUnits()){
            return;
        }
        // start to place Units

    }

    /*
    * Part4
    * Run Game for one turn
    *
    * */
    protected void tryRunGameOneTurn() {
        if(gameOnGoing == null){
            return;
        }
        doActionPhaseOneTurn();
        while(!gameOnGoing.gameState.isDoneUpdateGame()){}
        waitBeforeEnterUpdatingState();
        checkResultOneTurn();
    }

    protected void doActionPhaseOneTurn(){
        // if Done or SwitchOut

    }

    protected void checkResultOneTurn(){
        //if switchOut, change state to PLAYER_STATE_SWITCH_OUT and set gameOnGoing = null;
        //else if lose, change state to PLAYER_STATE_LOSE
        //else, change state to PLAYER_STATE_ACTION_PHASE
    }

    public void waitBeforeEnterUpdatingState(){
        try {
            gameOnGoing.wait();
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

        // Part2. init games:
        //    send all game info
        //    2.1 create a game
        //          start a gameRunner
        //    2.2 join a game
        //          if the game is new, add game.addUser
        //          if the game is old, loadGame()
        //    2.3 "fresh"
        //         send all game info
        //    2.4 LogOut

        // Part3. game init
        //  Initialization info including:
        //      send init World
        //      send territories
        //      recv assigned units
        //      send World again


        // Part4 ActionsPhase:
        //        4.1  do actions for Each Turn
        //             Game will deal with Actions: Move, Attack, Upgrade, Done, Exit, SwitchOut
        //             After each Done or Exit, this thread will wait for gameRunner to update the results
        //        4.2
        //             4.21 If player lose:
        //                    no exits: keep sending results
        //                    exits: change the barrier in game runner.
        //             4.22 If player SwitchOut:
        //                    go back to 2.
        //                    after delete the gameRunner, make sure store the game. (Everyone should wait until the user is back)


        while (true) {
            trySetUpUser(); //part1 above
            trySetUpGame(); //part2 above
            tryPlaceUnits(); //part3 above
            tryRunGameOneTurn();//part4 above
        }
    }
}
