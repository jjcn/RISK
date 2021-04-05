package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoomInfo implements Serializable {
    protected static final long serialVersionUID = 11L;
    int roomID;
    List<String> userNames;
    final int maxNumPlayers;


    public RoomInfo(int roomID, List<String> userNames, int maxNumPlayers ){
        this.roomID = roomID;
        this.userNames  = userNames;
        this.maxNumPlayers = maxNumPlayers;
    }

    public int getRoomID(){return roomID;}
    public int getMaxNumPlayers(){return maxNumPlayers;}
    public  List<String> getUserNames(){
        return userNames;
    }


}
