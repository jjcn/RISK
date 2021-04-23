package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.World;

import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import static edu.duke.ece651.group4.RISK.server.ServerConstant.GAME_STATE_DONE_UPDATE;
import static edu.duke.ece651.group4.RISK.server.ServerConstant.GAME_STATE_WAIT_TO_UPDATE;

/*
 * This class is the runner for game.
 * It will  update the final results after each turn
 * */
public class GameRunner extends Thread{
    Game game;
    PrintStream out;
    public GameRunner(Game g, PrintStream out){
        this.game = g;
        this.out = out;
    }
    public GameRunner(Game g){
        this.game = g;
        out = System.out;
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
    protected void notifyAllUsers(){
        boolean exit0 = false;
        while(!exit0){
            if(game.gInfo.gameState.isAllPlayersWaiting()){
                exit0 = true;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        while(!game.gameState.isAllPlayersWaiting()){} // This is to make sure runner notify all after all waits
        synchronized(game){
            game.notifyAll(); // notify all players to start send world and do placement
        }
    }


    @Override
    public void run(){
        out.println("Game" +game.getGameID()+" runner waits for all players to join");
        boolean exit = false;
        while(!exit){
            if(game.isFull()){
                out.println("Game" +game.getGameID()+" is FULL!!!!!!");
                exit = true;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        while(!game.isFull()){game.waitTime(1);}
        // wait all users to join to start the game
        if(!game.gInfo.gameState.isSetUp()){
            game.setUpGame();
            HibernateTool.updateGameInfo(game.getGameInfo());
            out.println("Game" +game.getGameID()+" runner finishes sets up");
            notifyAllUsers(); //Initialization
            out.println("Game" +game.getGameID()+" runner notifies all players before action phase");
        }
        if(!game.gInfo.gameState.isDonePlaceUnits()){
            notifyAllUsers(); //notify all users to place units
        }
        else {
            out.println("Game" + game.getGameID() + " is loaded to enter action phase!");
        }


        //ActionPhase
        while(true){
            boolean exit2 = false;
            while(!exit2){
                if(game.gInfo.gameState.isAllPlayersDoneOneTurn()){
                    exit2 = true;
                }
                try {
                  TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
            }
//            while(!game.gameState.isAllPlayersDoneOneTurn()){game.waitTime(1);}
            //Update the game
            game.updateGameAfterOneTurn();
            out.println("Game" +game.getGameID() +" turn number is " + game.gInfo.getTheWorld().getTurnNumber());
            game.gInfo.gameState.updateStateTo(GAME_STATE_DONE_UPDATE);
            game.gInfo.gameState.setActivePlayersStateToUpdating();
            out.println("Game" +game.getGameID()+" runner set all active players updating state");
            notifyAllUsers(); // notify all players to enter updating state
            out.println("Game" +game.getGameID()+" runner notifies all players");

            boolean exit3 = false;
            while(!exit3){
                if(game.gInfo.gameState.isAllPlayersDoneUpdatingState()){
                    exit3 = true;
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            while(!game.gameState.isAllPlayersDoneUpdatingState()){
//                game.waitTime(1);
//            }
            // wait until all players finish updating their state

            out.println("Game" +game.getGameID()+" runner knows all players are done updating");
            if(game.isEndGame() || game.gInfo.gameState.isAllPlayersSwitchOut()){
                game.gInfo.gameState.setGameDead();
                out.println("Game" +game.getGameID()+" runner ends, set this game dead");
//                HibernateTool.deleteGameInfo(game.gInfo);
                break;
            }
            game.gInfo.gameState.updateStateTo(GAME_STATE_WAIT_TO_UPDATE);
            HibernateTool.updateGameInfo(game.getGameInfo());
        }
    }

}
