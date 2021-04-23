//package edu.duke.ece651.group4.RISK.server;
//
//import edu.duke.ece651.group4.RISK.shared.World;
//import edu.duke.ece651.group4.RISK.shared.WorldFactory;
//import lombok.Getter;
//import lombok.Setter;
//
//import javax.persistence.*;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//@Entity
//@Getter
//@Setter
//public class GameInfoTrial  implements Serializable {
//    int maxNumUsers;
//
//    @Id
//    final int gameID;
//
//    @ElementCollection(fetch= FetchType.EAGER)
//    public List<User> usersOnGame;
//    @Lob
//    public World theWorld;
//    @Lob
//    public GameState gameState;
//
//    public GameInfoTrial() {
//        gameID = -1;
//        maxNumUsers = -1;
//        usersOnGame = new ArrayList<>();
//        theWorld = new World();
//        gameState = new GameState();
//    }
//
//    public GameInfoTrial(int gameID, int maxNumUsers) {
//        this.gameID = gameID;
//        this.maxNumUsers = maxNumUsers;
//        this.usersOnGame =  new ArrayList<>();
//        this.usersOnGame.add(new User(0,"user0","1"));
//        this.usersOnGame.add(new User(1,"user1","1"));
//        this.theWorld = new World();
//        this.gameState = new GameState();
//        WorldFactory factory = new WorldFactory();
//        this.theWorld = factory.create4TerritoryWorld();
//    }
//}
