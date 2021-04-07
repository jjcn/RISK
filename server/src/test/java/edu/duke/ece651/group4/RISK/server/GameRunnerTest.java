package edu.duke.ece651.group4.RISK.server;

import org.junit.jupiter.api.Test;

import java.util.List;

import static edu.duke.ece651.group4.RISK.server.ClientThreadTest.createUsers;
import static edu.duke.ece651.group4.RISK.server.ServerConstant.*;
import static org.junit.jupiter.api.Assertions.*;

class GameRunnerTest {

    private Game createAGameAndRunner(List<User> users){
        int numPlayers = users.size();
        Game g = new Game(0, numPlayers);
        GameRunner gRunner = new GameRunner(g);
        gRunner.start();
        for( int i = 0; i <numPlayers; i++ ){
            g.addUser(users.get(i));
        }
        return g;
    }

    @Test
    public void test_gameRunner(){
        List<User> users = createUsers(2);
        Game g = createAGameAndRunner(users);
        User u0 = users.get(0);
        User u1 = users.get(1);
        g.gameState.askUserWaiting(u0);
        g.gameState.askUserWaiting(u1);
        g.gameState.changAPlayerStateTo(u0,PLAYER_STATE_END_ONE_TURN);
        g.gameState.changAPlayerStateTo(u1,PLAYER_STATE_END_ONE_TURN);
        g.gameState.changAPlayerStateTo(u0,PLAYER_STATE_SWITCH_OUT);
        g.gameState.changAPlayerStateTo(u1,PLAYER_STATE_ACTION_PHASE);
        g.gameState.changAPlayerStateTo(u1,PLAYER_STATE_SWITCH_OUT);
        g.gameState.changAPlayerStateTo(u0,PLAYER_STATE_SWITCH_OUT);
        g.gameState.changAPlayerStateTo(u1,PLAYER_STATE_SWITCH_OUT);
    }

}