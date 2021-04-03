package edu.duke.ece651.group4.RISK.shared.message;

import java.io.Serializable;

import static edu.duke.ece651.group4.RISK.shared.Constant.*;

/*
* This handles message related to logIn and SignUp
* Action: 1. LogIn 2. SignUp
* */
public class LogMessage extends BasicMessage implements Serializable {
    String password;
    String username;
    public LogMessage(String source, String type, String action, String username,String password) {
        super(source, type, action);
        this.password = password;
        this.username = username;
    }

    public LogMessage(String action, String username, String password){
        this(SourceClient,MESS_LOG, action,username, password);
    }

    public String getPassword(){
        return password;
    }
    public String getUsername(){
        return username;
    }

}
