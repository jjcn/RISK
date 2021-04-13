package edu.duke.ece651.group4.RISK.shared;

import java.util.Arrays;
import java.util.List;

/*
* This class handles the constant across all the project
* */
public class Constant {
    //Message
    public final static String SourceServer = "Server";
    public final static String SourceClient = "Client";

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


    //Game:
    public static final String GAME_CREATE = "GameCreate";
    public static final String GAME_JOIN = "GameJoin";
    public static final String GAME_REFRESH = "GameRefresh";
    public static final String GAME_EXIT = "GameExit";
    //invalid
    public static final String INVALID_CREATE = "Invalid: the number of players should be between 2 and 5 inclusive.";
    public static final String INVALID_JOIN = "Invalid join: you are not in this game!";

    //soldier information
    public static  final List<String> UNIT_NAMES = Arrays.asList("Soldier LV0","Soldier LV1",
            "Soldier LV2","Soldier LV3","Soldier LV4","Soldier LV5","Soldier LV6");
    public static  final List<Integer> UNIT_COSTS = Arrays.asList(0,3,11,30,55,90,140);
    public static final List<Integer> UNIT_BONUS= Arrays.asList(0,1,3,5,8,11,15);

    public static final char MOVE_ACTION ='M';
    public static final char ATTACK_ACTION='A';
    public static final char DONE_ACTION='D';
    public static final char UPTROOP_ACTION='U';
    public static final char UPTECH_ACTION='T';
    public static final char SWITCH_OUT_ACTION='E';
    public static final char ALLIANCE_ACTION='L';

    public static final String PLACEMENT_DONE="Placement Done\n";

}
