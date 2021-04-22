package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.World;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@Setter
public class GameInfo implements Serializable {
    public int maxNumUsers;
    @Id
    public final int gameID;
    @ElementCollection(fetch=FetchType.EAGER)
    public List<User> usersOnGame;
    @Lob
    public World theWorld;
    @Lob
    public GameState gameState;

    public GameInfo() {
        gameID = -1;
        maxNumUsers = -1;
        usersOnGame = new ArrayList<>();
        theWorld = new World();
        gameState = new GameState();
    }

    public GameInfo(int gameID, int maxNumUsers) {
        this(gameID,maxNumUsers,new ArrayList<>(),new World(),new GameState());
//        this.gameID = gameID;
//        this.maxNumUsers = maxNumUsers;
//        this.usersOnGame =  new ArrayList<>();
//        this.theWorld = new World();
//        this.gameState = new GameState();
    }
    public GameInfo(int gameID, int maxNumUsers, List<User> usersOnGame,World theWorld,GameState gameState){
        this.gameID = gameID;
        this.maxNumUsers = maxNumUsers;
        this.usersOnGame =  usersOnGame;
        this.theWorld = theWorld;
        this.gameState = gameState;
    }

    public GameInfo clone(){
        List<User> users = new  ArrayList<>();
        for(User u : usersOnGame){
            users.add(u.clone());
        }
        GameInfo gInfo = new GameInfo(gameID,maxNumUsers,usersOnGame, theWorld.clone(), gameState.clone());
        return gInfo;
    }
}
