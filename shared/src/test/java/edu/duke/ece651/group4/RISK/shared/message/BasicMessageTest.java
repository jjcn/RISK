package edu.duke.ece651.group4.RISK.shared.message;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class BasicMessageTest {
    @Test
    public void addCase(){

        BasicMessage b= new BasicMessage("1","!","1");
        b.getAction();
        b.getSource();
        b.getType();

        HashSet<String> targetPlayers=new HashSet<>();
        targetPlayers.add("1");

        ChatMessage c=new ChatMessage("1", "2",targetPlayers , "3", 4);
        c.getChatContent();
        c.getAction();
        c.getChatID();
        c.getSource();
        c.getGameID();
        c.getTargetsPlayers();

        GameMessage g=new GameMessage("1",1,1);
        g.getGameID();
        g.getNumPlayers();

        LogMessage l= new LogMessage("1","!","1");
        l.getPassword();
        l.getUsername();

    }
}