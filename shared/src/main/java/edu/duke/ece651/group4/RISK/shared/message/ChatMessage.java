package edu.duke.ece651.group4.RISK.shared.message;

import java.util.Set;
import static edu.duke.ece651.group4.RISK.shared.Constant.MESS_CHAT;

public class ChatMessage extends BasicMessage {
    Set<String> targetPlayers;
    int gameID;
    String chatID;
    String chatContent;

    public ChatMessage(String chatID, String sender, Set<String> targetPlayers, String chatContent, int gameID){
        this(sender,targetPlayers,chatContent,gameID,"");
        this.chatID = chatID;
    }

    public ChatMessage(String sender, Set<String> targetPlayers, String chatContent, int gameID, String action){
        super(sender,MESS_CHAT, action);
        this.targetPlayers = targetPlayers;
        this.chatContent = chatContent;
        this.gameID = gameID;
    }

    public Set<String> getTargetsPlayers(){
        return targetPlayers;
    }

    public int getGameID(){
        return gameID;
    }
    public String getChatContent(){
        return chatContent;
    }
    public String getChatID(){return chatID;}
}
