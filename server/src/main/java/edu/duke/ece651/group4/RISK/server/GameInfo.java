package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.World;

import javax.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Table(name = "GameInfo")
@Entity
@Getter
@Setter
public class GameInfo {
    @Id
    public final int gameID;
    public int maxNumUsers;
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
        this.gameID = gameID;
        this.maxNumUsers = maxNumUsers;
        this.usersOnGame =  new ArrayList<>();
        this.theWorld = new World();
        this.gameState = new GameState();
    }
}
