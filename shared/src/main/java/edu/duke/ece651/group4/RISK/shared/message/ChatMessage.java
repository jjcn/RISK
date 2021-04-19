package edu.duke.ece651.group4.RISK.shared.message;

import java.util.List;
import java.util.Set;

import static edu.duke.ece651.group4.RISK.shared.Constant.MESS_CHAT;

public class ChatMessage extends BasicMessage {
    List<String> targetPlayers;
    public ChatMessage(String sender, List<String> targetPlayers){
        super(sender,MESS_CHAT, null);
        this.targetPlayers = targetPlayers;
    }

}
