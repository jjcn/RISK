package edu.duke.ece651.group4.RISK.server;

import org.junit.jupiter.api.Test;

import static edu.duke.ece651.group4.RISK.server.ServerConstant.*;
import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    private GameState createAGameState(int numPlayers){
        GameState gs = new GameState();
        for(int i = 0; i < numPlayers; i++){
            gs.addPlayerState(String.valueOf(i));
        }
        return gs;
    }

    @Test
    public void test_isAllPlayersDoneOneTurn(){
        GameState gs = createAGameState(3);
        assertEquals(GAME_STATE_WAIT_TO_UPDATE, gs.getState());
        assertEquals(false, gs.isAllPlayersDoneOneTurn());
        assertEquals(false, gs.changAPlayerStateTo("110", PLAYER_STATE_LOSE) );
        gs.changAPlayerStateTo("0", PLAYER_STATE_END_ONE_TURN );
        assertEquals(false, gs.isAllPlayersDoneOneTurn());
        gs.changAPlayerStateTo("1", PLAYER_STATE_END_ONE_TURN );
        assertEquals(false, gs.isAllPlayersDoneOneTurn());
        gs.changAPlayerStateTo("2", PLAYER_STATE_END_ONE_TURN );
        assertEquals(true, gs.isAllPlayersDoneOneTurn());
        gs.changAPlayerStateTo("2", PLAYER_STATE_SWITCH_OUT );
        assertEquals(true, gs.isAllPlayersDoneOneTurn());
        gs.changAPlayerStateTo("2", PLAYER_STATE_LOSE );
        assertEquals(true, gs.isAllPlayersDoneOneTurn());
        gs.changAPlayerStateTo("2", PLAYER_STATE_LOSE );
    }

}