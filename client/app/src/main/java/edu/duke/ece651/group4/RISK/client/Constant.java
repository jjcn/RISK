package edu.duke.ece651.group4.RISK.client;

import java.util.HashMap;

public class Constant {
    //*************CONSTANTS FOR DEBUG************//
    public static final boolean DEBUG_MODE = false;
    public static final boolean SINGLE_TEST = false;
    public static final int TEST_NUM_USER = 1;

    //**************CONSTANTS FOR LOG***************//
    public static final String LOG_CREATE_FAIL = "///////////**** Fail onCreate ****///////////";
    public static final String LOG_CREATE_SUCCESS = "//////////Success onCreate///////////";
    public static final String LOG_FUNC_FAIL = "///////////**** Function Exception ****/////////";
    public static final String LOG_FUNC_RUN = "//////////// Function running ///////////";

    //*************FOR USAGE IN CODE**************//
    // for notices
    public static final String SUCCESS_SIGNUP = "Sign up a new account successfully";
    public static final String SUCCESS_JOIN = "Join a room successfully";
    public static final String SUCCESS_CREATE = "Create a new room successfully";
    public static final String SUCCESS_START = "All users joined. Start game now";
    public static final String EMPTY_INPUT = "Input cannot be empty";
    public static final String CHOOSE_MAP = "Please choose number of players";
    public static final String PLACEMENT_MORE = "You have placed more soldiers than provided";
    public static final String PLACEMENT_LESS = "You have placed less soldiers";
    public static final String PLACEMENT_DONE = "All players have done their placements";
    public static final String WAIT_PLACEMENT = "Wait for other players to place...";
    public static final String TURN_END = "All players have commit their actions";
    public static final String LOSE_MSG = "Sorry, you lose~";
    public static final String STAY_INSTR = "Do you want to stay in this room?";
    public static final String CONFIRM = "Do you want to finish this turn?";
    public static final String CONFIRM_ACTION = "(You cannot perform further action this turn if choose yes.)";
    public static final String CHOOSE_USER_INSTR = "You want to ally with: ";
    public static final String NO_ALLY = "None";
    public static final String SEND_CHAT_FAIL = "Failed to send the message";
    public static final String UPTECH_CONFIRM = "Do you want to upgrade tech?";
    // actions
    public static final String UI_MOVE = "Move";
    public static final String UI_DONE = "Done";
    public static final String UI_ATK = "Attack";
    public static final String UI_UPTROOP = "Upgrade soldiers";
    public static final String UI_UPTECH = "Upgrade Max tech";
    public static final String UI_SWITCH_OUT = "SwitchOut";
    public static final String UI_ALLIANCE = "Alliance";
    public static final String UI_CHANGETYPE = "Change soldier type";
    // For data transfer
    public static final String TERR = "Territory";

    public static final String WORLD_CHAT = "";
    //**************check receive type***************//
    public static final String ROOMS = "roomInfo";
    public static final String NAME = "name";
    public static final String MESSAGE = "message_menu";
    public static final String WRG_MESSAGE = "fail to create world";
    public static final String EXIT_GAME_MESSAGE = "Exit Game";

    public static final int PLACE_TOTAL = 15;//initial soldier allowed
    public static final HashMap<Integer, Integer> MAPS = new HashMap<Integer, Integer>() {
        {
            put(2, R.drawable.terrs4);
            put(3, R.drawable.terrs6);
            put(4, R.drawable.terrs8);
            put(5, R.drawable.terrs10);
            put(1, R.drawable.terrtest);
            put(0, R.drawable.terrtest);
        }
    };
    public static final String COLOR_EGG =
            "Developers in Group4 in ECE651: Xian Sun\n Zian Geng\n Shenxi Jin\n Wenxin Xu\n"+
                    "*************************************" +
                    "*********** Special thanks **********" +
                    "**** to the best TA in the world ****" +
                    "************* Kewei Xia *************" +
                    "*************************************";
}
