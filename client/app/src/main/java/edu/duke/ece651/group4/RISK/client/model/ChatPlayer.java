package edu.duke.ece651.group4.RISK.client.model;

import com.stfalcon.chatkit.commons.models.IUser;

public class ChatPlayer implements IUser {
    private String roomID;
    private String name;

    public ChatPlayer(int roomID, String name) {
        this.roomID = String.valueOf(roomID);
        this.name = name;
    }

    @Override
    public String getId() {
        return roomID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return null;
    }
}
