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

    /*
    * Run the game
    *   1.1 wait for all users join
    *   1.2 setUpGame based on the userNames
    *       notify All users to placeUnits and set up, etc.
    *   1.3 Join the while loop
    *       1.31 wait users to finish their one turn:  WaitingState
    *       1.32 update the worldMap and enter: DoneState
    *       1.33 Set users state as updatingState
    *       1.34 wait for all users to finish updating
    *       1.35 game end  check and go back to the while loop
    * */

    @Override
    public void run(){

        while(!game.isFull()){} // wait all users to join to start the game

        game.setUpGame();
        //Initialization
        game.notifyAll(); // notify all players to start send world and do placement

        //ActionPhase
        while(true){
            while(!game.gameState.isAllPlayersDoneOneTurn()){} // wait until all players finish updating their turn
            //Update the game
            game.updateGameAfterOneTurn();
            game.gameState.updateStateTo(GAME_STATE_DONE_UPDATE);
            game.gameState.setActivePlayersStateToUpdating();
            game.notifyAll(); // notify all players to enter updating state
            while(!game.gameState.isAllPlayersDoneUpdatingState()){} // wait until all players finish updating their state
            if(game.isEndGame()){
                game.gameState.setGameDead();
                break;
            }
            game.gameState.updateStateTo(GAME_STATE_WAIT_TO_UPDATE);
        }
    }

}
