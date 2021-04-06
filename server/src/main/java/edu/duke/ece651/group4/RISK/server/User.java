package edu.duke.ece651.group4.RISK.server;


import java.util.HashSet;

public class User {
    UserInfo userInfo;

    /*
     * This construct the User
     * */
    public User(UserInfo userInfo){
        this.userInfo = userInfo;
    }

    /*
    *  This constructs a user with username, password
    * */
    public User(int id, String username, String password){
        this(new UserInfo(id, username,password));
    }

    /*
    * This gets username
    * @return a string: username
    * */
    public String getUsername(){
        return this.userInfo.getUsername();
    }

    /*
     * This checks if username and password fits this user
     * @return true if fits, false otherwise
     * */
    public boolean checkUsernamePassword(String username, String password){
        if(!checkUsername(username)){
            return false;
        }
        if(!this.userInfo.getPassword().equals(password)){
            return false;
        }
        return true;
    }

    /*
    * This checks if username fits with this user
    * @return true if it fits, false otherwise
    * */
    public boolean checkUsername(String username){
        if(username == null) {return false;}
        if(!this.userInfo.getUsername().equals(username)){
            return false;
        }
        return true;
    }

}
