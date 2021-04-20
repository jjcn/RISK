package edu.duke.ece651.group4.RISK.client.model;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;
import java.util.Set;

public class ChatMessageUI implements IMessage {
    private String chatID;
    private String text;
    private ChatPlayer user;
    private Date createdAt;
    private Set<String> targets; // want to talk to ---

    public ChatMessageUI(int chatID, String text, ChatPlayer user, Set targets) {
        this(String.valueOf(chatID), text, user, new Date(),targets);
    }

    public ChatMessageUI(String chatID, String text, ChatPlayer user, Date createdAt, Set targets){
        this.chatID = chatID;
        this.text = text;
        this.user = user;
        this.createdAt = createdAt;
        this.targets = targets;
    }

    @Override
    public String getId() {
        return chatID;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setChatID(int id){
        this.chatID = String.valueOf(id);
    }

    public Set<String> getTargets(){return targets;}
}
