package edu.duke.ece651.group4.RISK.server;

public class GameRunner extends Thread{
    Game game;
    public GameRunner(Game g){
        this.game = g;
    }

}
