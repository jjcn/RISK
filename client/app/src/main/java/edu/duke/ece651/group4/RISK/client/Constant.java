package edu.duke.ece651.group4.RISK.client;

public class Constant {
    public static final String SUCCESS_SIGNUP = "Sign up a new account successfully";
    public static final String SUCCESS_JOIN = "Join a room successfully";
    public static final String SUCCESS_CREATE = "Create a new room successfully";
    public static final String SUCCESS_START = "All users joined. Start game now";
    public static final String EMPTY_INPUT = "Input cannot be empty";
    public static final String PLACEMENT_MORE = "You have placed more soldiers than provided";
    public static final String PLACEMENT_LESS = "You have placed less soldiers";
    public static final String PLACEMENT_DONE = "All players have done their placements";

    //**************CONSTANTS FOR LOG***************//
    public static final String LOG_CREATE_FAIL = "///////////// Fail onCreate /////////////";
    public static final String LOG_CREATE_SUCCESS = "////////////Success onCreate/////////////";
    public static final String LOG_FUNC_FAIL = "////////////// Function Exception /////////////";
    public static final String LOG_FUNC_RUN = "////////////// Function running //////////////";

    //*************CONSTANTS FOR DEBUG************//
    public static final boolean DEBUG_MODE = true;
    public static final int TEST_NUM_USER = 1;

    //**************check receive type***************//
    public static final String WORLD = "world";
    public static final String ROOMS = "roomInfo";
    public static final String NAME = "name";
    public static final String MESSAGE = "message";
    public static final String WRG_MESSAGE = "fail to create world";


    public static final int MAXLEVEL = 6; //max soldier level
    public static final int PLACE_TOTAL = 15;//initial soldier allowed
}
