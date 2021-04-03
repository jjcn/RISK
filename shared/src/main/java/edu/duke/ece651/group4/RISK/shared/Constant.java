package edu.duke.ece651.group4.RISK.shared;
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
    public static final String GAME_REFRESH = "GameRefresh";
    public static final String GAME_EXIT = "GameExit";


}
