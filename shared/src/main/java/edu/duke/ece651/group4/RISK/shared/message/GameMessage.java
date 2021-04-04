package edu.duke.ece651.group4.RISK.shared.message;

import java.io.Serializable;
import java.util.HashSet;

import static edu.duke.ece651.group4.RISK.shared.Constant.*;


public class GameMessage extends BasicMessage implements Serializable {
    int gameID;
    int numPlayers;

    public GameMessage(String source, String type,String action, int gameID, int numPlayers) {
        super(source,type,action);
        this.gameID = gameID;
        this.numPlayers = numPlayers;
    }

    /*
    *  Part1
    *  This constructs gameMessage sent to Server from client
    * */
    public GameMessage(String action, int gameID, int numPlayers){
        this(SourceClient, MESS_GAME,action, gameID, numPlayers);
    }

    //This constructs a message for action "GAME_REFRESH" or "GAME_EXIT"
    public GameMessage(String action){
        this(action, -1, -1);
    }


    public int getGameID(){
        return gameID;
    }

    public int getNumPlayers(){
        return numPlayers;
    }


}
