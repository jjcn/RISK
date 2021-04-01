package edu.duke.ece651.group4.RISK.server;

public class UserInfo {

    final int userID;
    final String username;
    final String password;

    public UserInfo(int userID, String username, String password){
        this.userID = userID;
        this.username = username;
        this.password = password;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public int getUserID(){
        return userID;
    }
}
