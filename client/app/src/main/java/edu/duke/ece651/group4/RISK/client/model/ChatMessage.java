package edu.duke.ece651.group4.RISK.client.model;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;
import java.util.List;

public class ChatMessage implements IMessage {
    private String chatID;
    private String text;
    private ChatPlayer user;
    private Date createdAt;
    private List<String> targets; // want to talk to ---

    public ChatMessage(int id, String text, ChatPlayer user) {
        this(String.valueOf(id), text, user, new Date());
    }

    public ChatMessage(String id, String text, ChatPlayer user) {
        this(id, text, user, new Date());
    }

    public ChatMessage(String chatID, String text, ChatPlayer user, Date createdAt){
        this.chatID = chatID;
        this.text = text;
        this.user = user;
        this.createdAt = createdAt;
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

    public List<String> getTargets(){return targets;}
}
