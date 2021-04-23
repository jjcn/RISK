package edu.duke.ece651.group4.RISK.client.model;

import androidx.annotation.Nullable;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Date;
import java.util.Set;

public class ChatMessageUI implements IMessage {

    private String chatID;
    private String text;
    private ChatPlayer user;
    private Date createdAt;
    private Set<String> targets; // want to talk to ---

    public ChatMessageUI(String chatID, String text, ChatPlayer user, Set targets) {
        this(chatID, text, user, new Date(),targets);
    }

    public ChatMessageUI(String chatID, String text, ChatPlayer user, Date createdAt, Set targets){
        this.chatID = chatID;
        this.text = text;
        this.user = user;
        this.createdAt = createdAt;
        this.targets = targets;
    }

    public String getChatId() {
        return chatID;
    }

    public void setChatId(String chatID) {
        this.chatID = chatID;
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

    public Set<String> getTargets(){return targets;}

}
