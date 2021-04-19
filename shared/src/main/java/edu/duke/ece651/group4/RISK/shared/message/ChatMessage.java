package edu.duke.ece651.group4.RISK.shared.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static edu.duke.ece651.group4.RISK.shared.Constant.MESS_CHAT;

public class ChatMessage extends BasicMessage {
    List<String> targetPlayers;
    int gameID;
    String chatContent;
    public ChatMessage(String sender, List<String> targetPlayers, String chatContent, int gameID, String action){
        super(sender,MESS_CHAT, action);
        this.targetPlayers = targetPlayers;
        this.chatContent = chatContent;
        this.gameID = gameID;
    }

    public ChatMessage(String sender, List<String> targetPlayers, String chatContent, int gameID){
        this(sender,targetPlayers,chatContent,gameID,null);
    }

    public List<String> getTargetsPlayers(){
        return targetPlayers;
    }

    public int getGameID(){
        return gameID;
    }
    public String getChatContent(){
        return chatContent;
    }

}
