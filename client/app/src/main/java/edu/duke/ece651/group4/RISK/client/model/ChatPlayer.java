package edu.duke.ece651.group4.RISK.client.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.stfalcon.chatkit.commons.models.IUser;

/**
 * A ChatPlayer that participates in dialogs.
 *
 * Quote: " If user doesn't have avatar image, you can return null,
 * and image will not be shown in incoming message item:"
 * from:
 * https://github.com/stfalcon-studio/ChatKit/blob/master/docs/COMPONENT_MESSAGES_LIST.md
 */
public class ChatPlayer implements IUser {
    private String roomID;
    private String name;
    private String Avatar;

    public ChatPlayer(int roomID, String name) {
        this.roomID = String.valueOf(roomID);
        this.name = name;
        this.Avatar = name;
    }

    @Override
    public String getId() {
        return name + roomID;
    }

    public String getRoomId() {
        return roomID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return Avatar;
    }
}
