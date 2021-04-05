package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class RoomInfo implements Serializable {
    protected static final long serialVersionUID = 11L;
    int roomID;
    ArrayList<String> userNames;
    final int maxNumPlayers;


    public RoomInfo(int roomID, ArrayList<String> userNames,int maxNumPlayers ){
        this.roomID = roomID;
        this.userNames  = userNames;
        this.maxNumPlayers = maxNumPlayers;
    }

    public int getRoomID(){return roomID;}
    public int getMaxNumPlayers(){return maxNumPlayers;}
    public  ArrayList<String> getUserNames(){
        return userNames;
    }


}
