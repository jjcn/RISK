package edu.duke.ece651.group4.RISK.client.model;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.List;

import static edu.duke.ece651.group4.RISK.client.RISKApplication.getWorld;

public class ChatDialog implements IDialog {
    private String chatID;
    private String dialogPhoto;
    private String dialogName;
    private ArrayList<ChatPlayer> users;
    private IMessage lastMessage;
    private int unreadCount;

    public ChatDialog(String id, String name, String photo,
                      ArrayList<ChatPlayer> users, IMessage lastMessage, int unreadCount) {
        this.chatID = id;
        this.dialogName = name;
        this.dialogPhoto = photo;
        this.users = users;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }

    public ChatDialog(int id, String name, List<String> playersName, ChatMessage lastMessage, int unreadCount) {
        this(String.valueOf(id), name, null, new ArrayList<ChatPlayer>(), lastMessage, unreadCount);
//        for(String playerName: playersName){
//            users.add(new ChatPlayer(getWorld().getRoomID(),playerName));
//        }
    }

    @Override
    public String getId() {
        return chatID;
    }

    @Override
    public String getDialogPhoto() {
        return dialogPhoto;
    }

    @Override
    public String getDialogName() {
        return dialogName;
    }

    @Override
    public List<? extends IUser> getUsers() {
        return users;
    }

    @Override
    public IMessage getLastMessage() {
        return lastMessage;
    }

    @Override
    public void setLastMessage(IMessage message) {
        this.lastMessage = message;
    }

    @Override
    public int getUnreadCount() {
        return unreadCount;
    }
}
