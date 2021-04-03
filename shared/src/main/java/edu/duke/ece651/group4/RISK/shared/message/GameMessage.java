package edu.duke.ece651.group4.RISK.shared.message;

import java.io.Serializable;
import java.util.HashSet;

import static edu.duke.ece651.group4.RISK.shared.Constant.*;


public class GameMessage extends BasicMessage implements Serializable {
    int gameID;
    int numPlayers;
    HashSet<String> userNames;

    public GameMessage(String source, String type,String action, int gameID, int numPlayers, HashSet<String> userNames) {
        super(source,type,action);
        this.gameID = gameID;
        this.numPlayers = numPlayers;
        this.userNames = new HashSet<>(userNames);
    }

    /*
    *  Part1
    *  This constructs gameMessage sent to Server from client
    * */
    public GameMessage(String action, int gameID, int numPlayers){
        this(SourceClient, MESS_GAME,action, gameID, numPlayers, null);
    }

    //This constructs a message for action "GAME_REFRESH"
    public GameMessage(String action){
        this(action, -1, -1);
    }

    /*
    * Part2
    * This constructs gameMessage sent to Client from server
    * */
    //2.1
    // send usernames after fresh button
    public GameMessage(int gameID, HashSet<String> userNames){
        this(SourceServer, MESS_GAME, null, gameID, userNames.size(), userNames);
    }

    public int getGameID(){
        return gameID;
    }

    public int getNumPlayers(){
        return numPlayers;
    }

    public HashSet<String> getPlayerNames(){
        return userNames;
    }

}
