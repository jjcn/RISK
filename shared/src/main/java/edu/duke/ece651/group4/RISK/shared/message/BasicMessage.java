package edu.duke.ece651.group4.RISK.shared.message;

/*
 * This is the basic message between client and server
 * source is the username who send this message
 * type is the message type: 1. MESS_LOG, 2. MESS_GAME, 3. MESS_ACTION
 * action is the user's intended action:
 *        LogMessage's action:  1. LOG_IN 2.LOG_SIGNUP, 3. LOG_EXIT
 *        ActionMessage's action: 1.ACTION_MOVE, 2. ACTION_MOVE,  3. ACTION_UPGRADE,  4.ACTION_DONE, 5.ACTION_EXIT
 *        GameMessage;s action: 1.GAME_CREATE 2.GAME_JOIN 3.GAME_EXIT
 *  */
public class BasicMessage {
    String source;
    String type;
    String action;
    public BasicMessage(String source, String type){
        this.source = source;
        this.type = type;
    }

    public String getType(){
        return type;
    }

    public String getSource(){
        return source;
    }
    public String getAction(){
        return action;
    }
}
