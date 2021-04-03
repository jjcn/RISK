package edu.duke.ece651.group4.RISK.shared;

import java.util.Arrays;
import java.util.List;

/*
* This class handles the constant across all the project
* */
public class Constant {
    //socket
    public static final String SOCKET_HOSTNAME = "localhost";
    public static final int SOCKET_PORT = 9999;

    //Message
    public static final String MESS_LOG = "MessageLog";
    public static final String MESS_GAME = "MessageGame";
    public static final String MESS_ACTION = "MessageAction";

    //Log:
    public static final String LOG_SIGNIN = "LogIn";
    public static final String LOG_SIGNUP = "LogSignUp";
    public static final String LOG_EXIT = "LogExit";
    //invalid
    public static final String INVALID_LOGIN = "Invalid username or password.";
    public static final String INVALID_SIGNUP = "This username is already used by others.";


    // Action:
    public static final String ACTION_MOVE = "ActionMove";
    public static final String ACTION_DONE = "ActionDone";
    public static final String ACTION_ATK = "ActionAttack";
    public static final String ACTION_UPGRADE = "ActionUpgrade";
    public static final String ACTION_EXIT =  "ActionSwitchOut";

    //Game:
    public static final String GAME_CREATE = "GameCreate";
    public static final String GAME_JOIN = "GameJoin";
    public static final String GAME_EXIT = "GameExit";

    //PlayerState
    public static final String PLAYER_STATE_READY = "PLAYER_STATE_READY";
    public static final String PLAYER_STATE_LOSE = "PLAYER_STATE_LOSE";
    public static final String PLAYER_STATE_END_ONE_TURN = "PLAYER_STATE_END_ONE_TURN";
    public static final String PLAYER_STATE_QUIT = "PLAYER_STATE_QUIT";
    public static final String PLAYER_STATE_SWITCH_OUT ="PLAYER_STATE_SWITCH_OUT" ;
    public static final String PLAYER_STATE_EXIT = "PLAYER_STATE_EXIT";

    //GameState
    public static final String GAME_STATE_WAIT_TO_UPDATE = "GAME_STATE_WAIT_TO_UPDATE";
    public static final String GAME_STATE_DONE_UPDATE = "GAME_STATE_DONE_UPDATE";

    //soldier information
    public static  final List<String> UNIT_NAMES = Arrays.asList("Soldier LV0","Soldier LV1",
            "Soldier LV2","Soldier LV3","Soldier LV4","Soldier LV5","Soldier LV6");
    public static  final List<Integer> UNIT_COSTS = Arrays.asList(0,3,11,30,55,90,140);
    public static final List<Integer> UNIT_BONUS= Arrays.asList(0,1,3,5,8,11,15);

}
