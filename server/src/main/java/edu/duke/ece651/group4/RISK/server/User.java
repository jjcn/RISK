package edu.duke.ece651.group4.RISK.server;

import java.util.HashSet;

public class User {
    UserInfo userInfo;
    HashSet<Game> activeGames;
    /*
     * This construct the User
     * */
    public User(UserInfo userInfo){
        this.userInfo = userInfo;
        this.activeGames = new HashSet<Game>();
    }

    public User(int id, String username, String password){
        this(new UserInfo(id, username,password));
    }

    public String getUsername(){
        return this.userInfo.getUsername();
    }

    public boolean checkUsernamePassword(String username, String password){
        if(!checkUsername(username)){
            return false;
        }
        if(this.userInfo.getPassword() != password){
            return false;
        }
        return true;
    }

    public boolean checkUsername(String username){
        if(this.userInfo.getUsername() != username){
            return false;
        }
        return true;
    }
}
