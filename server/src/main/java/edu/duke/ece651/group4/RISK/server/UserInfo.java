package edu.duke.ece651.group4.RISK.server;

import javax.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
public class UserInfo implements Serializable {
    final String username;
    final String password;

    @Id
    final int userID;

    // default constructor for hibernate
    public UserInfo() {
        userID = -1;
        username = "";
        password = "";
    }
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
