package edu.duke.ece651.group4.RISK.server;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static edu.duke.ece651.group4.RISK.server.ServerConstant.*;
import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    private GameState createAGameState(ArrayList<User> users){
        GameState gs = new GameState();
        for(User u : users){
            gs.addPlayerState(u);
        }
        return gs;
    }

    private ArrayList<User> createUsers(int num){
        ArrayList<User> users = new ArrayList<User>();
        for(int id = 0; id < num; id ++){
            users.add(new User(id, "user" + id, "123"));
        }
        return users;
    }

    @Test
    public void test_isAllPlayersDoneOneTurn(){
        ArrayList<User> users = createUsers(3);
        GameState gs = createAGameState(users);
        assertEquals(GAME_STATE_WAIT_TO_UPDATE, gs.getState());
        assertEquals(false, gs.isAllPlayersDoneOneTurn());
        assertEquals(false, gs.changAPlayerStateTo(new User(11,"1","1"), PLAYER_STATE_LOSE) );
        gs.changAPlayerStateTo(users.get(0), PLAYER_STATE_END_ONE_TURN );
        assertEquals(false, gs.isAllPlayersDoneOneTurn());
        gs.changAPlayerStateTo(users.get(1), PLAYER_STATE_END_ONE_TURN );
        assertEquals(false, gs.isAllPlayersDoneOneTurn());
        gs.changAPlayerStateTo(users.get(2), PLAYER_STATE_END_ONE_TURN );
        assertEquals(true, gs.isAllPlayersDoneOneTurn());
        gs.changAPlayerStateTo(users.get(2), PLAYER_STATE_SWITCH_OUT );
        assertEquals(true, gs.isAllPlayersDoneOneTurn());
        gs.changAPlayerStateTo(users.get(2), PLAYER_STATE_LOSE );
        assertEquals(true, gs.isAllPlayersDoneOneTurn());
        gs.changAPlayerStateTo(users.get(2), PLAYER_STATE_LOSE );
    }


    @Test
    public void test_isAllPlayersDoneUpdatingState(){
        ArrayList<User> users = createUsers(2);
        GameState gs = createAGameState(users);
        assertEquals(GAME_STATE_WAIT_TO_UPDATE, gs.getState());
        assertEquals(true, gs.isAllPlayersDoneUpdatingState());
        assertEquals(true, gs.changAPlayerStateTo(users.get(0), PLAYER_STATE_UPDATING ));
        assertEquals(false, gs.isAllPlayersDoneUpdatingState());
        gs.setActivePlayersStateToUpdating();
        assertEquals(false, gs.isAllPlayersDoneUpdatingState());
        assertEquals(PLAYER_STATE_UPDATING, gs.getAPlayerState(users.get(1)));
        gs.changAPlayerStateTo(users.get(1), PLAYER_STATE_SWITCH_OUT);
        assertEquals(false, gs.isAllPlayersDoneUpdatingState());
        gs.changAPlayerStateTo(users.get(0), PLAYER_STATE_ACTION_PHASE);
        assertEquals(true, gs.isAllPlayersDoneUpdatingState());
    }

    @Test
    public void test_gameState(){
        ArrayList<User> users = createUsers(2);
        GameState gs = createAGameState(users);
        assertEquals(false, gs.isDoneUpdateGame());
        gs.updateStateTo(GAME_STATE_DONE_UPDATE);
        assertEquals(true, gs.isDoneUpdateGame());
        assertEquals(false, gs.isDonePlaceUnits());
        gs.setDonePlaceUnits();
        assertEquals(true, gs.isDonePlaceUnits());
        assertEquals(true,gs.isAlive());
        gs.setGameDead();
        assertEquals(false,gs.isAlive());

    }

}