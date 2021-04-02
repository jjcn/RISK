package edu.duke.ece651.group4.RISK.server;

/*
* This class is the runner for game.
* It will do calculate and update the final results after each turn
* */
public class GameRunner extends Thread{
    Game game;
    public GameRunner(Game g){
        this.game = g;
    }

    @Override
    public void run(){
        while(true){

            game.finishOneTurn();
            if(game.isEndGame()){
                break;
            }
        }
    }

}
