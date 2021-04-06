package edu.duke.ece651.group4.RISK.server;

public class UserInfo {

    final int userID;
    final String username;
    final String password;

    /*
    * This constructs a userInfo
    * */
    public UserInfo(int userID, String username, String password){
        this.userID = userID;
        this.username = username;
        this.password = password;
    }

    /*
    * This gets username
    * @return a string: username
    * */
    public String getUsername(){
        return username;
    }
    /*
     * This gets password
     * @return a string: password
     * */
    public String getPassword(){
        return password;
    }
    /*
     * This gets unique ID
     * @return int: userID
     * */
    public int getUserID(){
        return userID;
    }
}
