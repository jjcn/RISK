package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.Client;
import edu.duke.ece651.group4.RISK.shared.message.LogMessage;

import java.io.IOException;
import java.util.HashSet;

import static edu.duke.ece651.group4.RISK.shared.Constant.*;

public class ClientThread extends Thread {
    HashSet<Game> games;
    HashSet<User> users;
    Client theClient;
    User ownerUser;
    Game gameOnGoing;

    public ClientThread(HashSet<Game> games, HashSet<User> users, Client theClient) {
        this.games = games;
        this.users = users;
        this.theClient = theClient;
        this.ownerUser = null;
    }

    /*
     * This deals with users log In, Signup, etc.
     * UserMessage
     * */
    public String setUpUser() throws IOException, ClassNotFoundException {
        if (ownerUser != null) {
            return null;
        }

        while(true){
            LogMessage logMessage = (LogMessage) this.theClient.recvObject(); //receive a LogMessage
            String action = logMessage.getAction();
            if(action == LOG_IN){
                String resIn = tryLogIn(logMessage.getUsername(), logMessage.getPassword());
                this.theClient.sendObject(resIn);
                if(resIn == null){
                    return null;
                }
            }
            if(action == LOG_SIGNUP){
                String resUp = trySignUp(logMessage.getUsername(), logMessage.getPassword());
                this.theClient.sendObject(resUp);
            }

        }

    }

    /*
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

    protected void runGame() {

    }

    protected void setUpGame() {
        if(gameOnGoing!=null){
            return;
        }
    }

    @Override
    public void run() {
        // 1.User
        //   1.1 LogIn
        //   1.2 SignUp
        //   1.3 Exit the App

        // 2. init games:
        //    2.1 create a game
        //          start a gameRunner
        //    2.2 join a game
        //          if the game is new, add game.addUser
        //          if the game is old, loadGame()
        //    2.3 LogOut

        // 3. game play: When game is active (has the game runner) || playerIsInThisGame:
        //  3.1 Initialization info including:
        //      send init World
        //      send territories
        //      recv assigned units
        //      send World again
        //  3.2 ActionsPhase:
        //        3.21 Each Turn
        //             Game will deal with Actions: Move, Attack, Upgrade, Done, Exit, SwitchOut
        //             After each Done or Exit, this thread will wait for gameRunner to update the results
        //        3.22 If player lose:
        //             no exits: keep sending results
        //             exits: change the barrier in game runner.
        //        3.21 If player SwitchOut:
        //             go back to 2.
        //             after delete the gameRunner, make sure store the game. (Everyone should wait until the user is back)
        while (true) {
            try {
                setUpUser();
                break;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            setUpGame();
            runGame();
        }
    }
}
