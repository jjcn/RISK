package edu.duke.ece651.group4.RISK.shared;

import java.util.*;

/*
* This class handles the constant across all the project
* */
public class Constant {
    //Message
    public final static String SourceServer = "Server";
    public final static String SourceClient = "Client";

    //socket
    public static final String SOCKET_HOSTNAME = "vcm-18527.vm.duke.edu";
    public static final int SOCKET_PORT = 9999;

    //Message
    public static final String MESS_LOG = "MessageLog";
    public static final String MESS_GAME = "MessageGame";
    public static final String MESS_ACTION = "MessageAction";
    public static final String MESS_CHAT = "MessageChat";
    public static final String CHAT_SETUP_ACTION = "ChatSetUpAction";


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

    public static final double NORM_SPEED=1;
    public static final int NORM_RANGE=0;

    //Knight information
    public static  final List<String> KNIGHT_NAMES = Arrays.asList("Knight LV0","Knight LV1",
            "Knight LV2","Knight LV3","Knight LV4","Knight LV5","Knight LV6");
    public static final double KNIGHT_SPEED=0.75;
    public static final int KNIGHT_COST=10;

    //Archer information
    public static  final List<String> ARCHER_NAMES = Arrays.asList("Archer LV0","Archer LV1",
            "Archer LV2","Archer LV3","Archer LV4","Archer LV5","Archer LV6");
    public static final int ARCHER_RANGE=2;
    public static final int ARCHER_COST=15;

    public static  final List<String> SHIELD_NAMES = Arrays.asList("Shield LV0","Shield LV1",
            "Shield LV2","Shield LV3","Shield LV4","Shield LV5","Shield LV6");
    public static final int SHIELD_HP=40;
    public static final int SHIELD_COST=20;

    public static  final List<String> BREAKER_NAMES = Arrays.asList("Breaker LV0","Breaker LV1",
            "Breaker LV2","Breaker LV3","Breaker LV4","Breaker LV5","Breaker LV6");
    public static final int BREAKER_COST=5;
    public static final int BREAKER_BONUS=20;

    public static  final List<String> ARROW_NAMES = Arrays.asList("Arrow LV0","Arrow LV1",
            "Arrow LV2","Arrow LV3","Arrow LV4","Arrow LV5","Arrow LV6");

    public static final String SOLDIER="Soldier";

    public static final String KNIGHT="Knight";

    public static final String ARCHER="Archer";

    public static final String SHIELD="Shield";

    public static final String BREAKER="Breaker";
    public static final String ARROW="Arrow";

    public static final List<String> JOB_NAMES = Arrays.asList(KNIGHT,ARCHER,SHIELD,BREAKER);

    public static final Map<String, List<String>> JOB_DICTIONARY;
    static {
        JOB_DICTIONARY = new HashMap<>();
        JOB_DICTIONARY.put(KNIGHT, KNIGHT_NAMES);
        JOB_DICTIONARY.put(ARCHER, ARCHER_NAMES);

        JOB_DICTIONARY.put(BREAKER, BREAKER_NAMES);
        JOB_DICTIONARY.put(SHIELD, SHIELD_NAMES);
        JOB_DICTIONARY.put(SOLDIER, UNIT_NAMES);
        JOB_DICTIONARY.put(ARROW, ARROW_NAMES);
    }


    public static final char MOVE_ACTION ='M';
    public static final char ATTACK_ACTION='A';
    public static final char DONE_ACTION='D';
    public static final char UPTROOP_ACTION='U';
    public static final char UPTECH_ACTION='T';
    public static final char SWITCH_OUT_ACTION='E';
    public static final char ALLIANCE_ACTION='L';
    public static final char TRANSFER_TROOP_ACTION ='R';

    public static final String PLACEMENT_DONE="Placement Done\n";

    // Chat
    public static final int CHAT_PORT = 5678;

    // Alliance
    public static final int MAX_ALLOWED_ALLY_NUMBER = 1;

    // Tech level upgrade costs
    /**
     * Cumulative tech level upgrade costs.
     * The i-th entry is the total cost to upgrade tech level from 1 to i. (i starts from 0)
     */
    public static final List<Integer> CUM_TECH_LEVEL_UPGRADE_COSTS =
            Arrays.asList(new Integer[]{0, 0, 50, 125, 250, 450, 750}); // TODO: should depend on tech upgrade costs

    /**
     * Tech level upgrade costs.
     * The i-th entry is the cost to upgrade tech level from i to i + 1. (i starts from 0)
     */
    public static final List<Integer> TECH_LEVEL_UPGRADE_COSTS =
        Arrays.asList(new Integer[]{0, 50, 75, 125, 200, 300});

}
