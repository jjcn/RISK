package edu.duke.ece651.group4.RISK.client.model;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.List;

import static edu.duke.ece651.group4.RISK.client.RISKApplication.getWorld;

/**
 * A dialog
 */
public class ChatDialog implements IDialog {
    private String chatID;
    private String dialogPhoto;
    private String dialogName;
    private ArrayList<ChatPlayer> users;
    private IMessage lastMessage;
    private int unreadCount;

    protected ChatDialog(String id, String name, String photo,
                      ArrayList<ChatPlayer> users, IMessage lastMessage, int unreadCount) {
        this.chatID = id;
        this.dialogName = name;
        this.dialogPhoto = photo;
        this.users = users;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }

    /**
     * No need for: player avatar, last message, unread count
     * @param id is chat ID
     * @param name is the name of the chat
     * @param playerNames is the names of players in the chat
     */
    public ChatDialog(String id, String name, List<String> playerNames) {
        this(id, name, null,
                new ArrayList<ChatPlayer>(), null, 0);
        ArrayList<ChatPlayer> players = new ArrayList<ChatPlayer>();
        for (String playerName : playerNames) {
            players.add(new ChatPlayer(getWorld().getRoomID(), playerName));
        }
        this.users = players;
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
