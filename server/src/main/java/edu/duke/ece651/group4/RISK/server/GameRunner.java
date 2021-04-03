package edu.duke.ece651.group4.RISK.server;

import static edu.duke.ece651.group4.RISK.server.ServerConstant.GAME_STATE_DONE_UPDATE;
import static edu.duke.ece651.group4.RISK.server.ServerConstant.GAME_STATE_WAIT_TO_UPDATE;

/*
* This class is the runner for game.
* It will  update the final results after each turn
* */
public class GameRunner extends Thread{
    Game game;

    public GameRunner(Game g){
        this.game = g;
    }

    @Override
    public void run(){
        while(!game.isFull()){} // wait all users to join to start the game

        while(true){
            while(!game.gameState.isAllPlayersDoneOneTurn()){} // wait until all players finish updating their turn
            //Update the game
            game.updateGameAfterOneTurn();

            game.gameState.updateStateTo(GAME_STATE_DONE_UPDATE);
            game.gameState.setActivePlayersStateToUpdating();
            game.notifyAll(); // notify all players to enter updating state
            while(!game.gameState.isAllPlayersDoneUpdatingState()){} // wait until all players finish updating their state
            if(game.isEndGame()){
                break;
            }
            game.gameState.updateStateTo(GAME_STATE_WAIT_TO_UPDATE);
        }
    }

}
