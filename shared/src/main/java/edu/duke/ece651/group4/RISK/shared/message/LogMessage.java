package edu.duke.ece651.group4.RISK.shared.message;

import static edu.duke.ece651.group4.RISK.shared.Constant.MESS_LOG;

/*
* This handles message related to logIn and SignUp
* Action: 1. LogIn 2. SignUp
* */
public class LogMessage extends BasicMessage{
    String password;
    String username;
    String action;
    public LogMessage(String source, String type,String username,String password, String action) {
        super(source, type);
        this.password = password;
        this.username = username;
        this.action = action;
    }

    public LogMessage(String username, String password,String action){
        this(MESS_LOG,username,username, password, action);
    }

    public String getPassword(){
        return password;
    }
    public String getUsername(){
        return username;
    }


}
