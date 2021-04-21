package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.World;

import javax.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @ElementCollection(fetch=FetchType.EAGER)
    public GameState gameState;

    public GameInfo(int gameID, int maxNumUsers) {
        this.gameID = gameID;
        this.maxNumUsers = maxNumUsers;
        this.usersOnGame =  new ArrayList<>();
        this.theWorld = null;
        this.gameState = new GameState();
    }
}
