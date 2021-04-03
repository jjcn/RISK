package edu.duke.ece651.group4.RISK.shared.message;

import java.io.Serializable;

import static edu.duke.ece651.group4.RISK.shared.Constant.MESS_GAME;

public class GameMessage extends BasicMessage implements Serializable {
    int gameID;
    int numPlayers;

    public GameMessage(String source, String type,String action, int gameID, int numPlayers) {
        super(source,type,action);
        this.gameID = gameID;
        this.numPlayers = numPlayers;
    }

    //This constructs a message for action "GAME_CREATE"
    public GameMessage(String action, int gameID, int numPlayers){
        this(null, MESS_GAME,action, gameID, numPlayers);
    }

    //This constructs a message for action "GAME_JOIN"
    public GameMessage(String action, int gameID){
        this(action, gameID, -1);
    }

    public int getGameID(){
        return gameID;
    }
    public int getNumPlayers(){
        return numPlayers;
    }
}
