package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.RISKApplication;
import edu.duke.ece651.group4.RISK.client.model.ChatDialog;
import edu.duke.ece651.group4.RISK.client.model.ChatMessage;

import java.util.*;

import static edu.duke.ece651.group4.RISK.client.RISKApplication.getCurrentRoomSize;

/**
 * Activity showing a list of chats
 */
public class ChatActivity extends AppCompatActivity implements DialogsListAdapter.OnDialogClickListener {
    private static final String TAG = ChatActivity.class.getSimpleName();
    private DialogsListAdapter chatListAdapter;
    private DialogsList chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatList = findViewById(R.id.chatList);
        initAdapter();
    }

    private void initAdapter() {
        chatListAdapter = new DialogsListAdapter(R.layout.item_dialog, null);
        chatListAdapter.setItems(getChats());
        /**
         * Listener for a short click
         */
        chatListAdapter.setOnDialogClickListener(this);

        chatList.setAdapter(chatListAdapter);
    }

    // TODO: chats: 1v1 & whole world
    private List<ChatDialog> getChats() {
        ArrayList<ChatDialog> chats = new ArrayList<>();
        List<String> allPlayerNames = (List<String>) RISKApplication.getAllPlayersName();
        /**
         * All players' chat room, id = 0
         */
        chats.add(new ChatDialog(0, "World Chat",  allPlayerNames));
        /**
         * One-to-one chat room
         */
        for (int i = 0; i < getCurrentRoomSize(); i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -(i * i));
            calendar.add(Calendar.MINUTE, -(i * i));

            chats.add(new ChatDialog(i + 1, "Private Chat " + (i + 1),
                    Arrays.asList(allPlayerNames.get(i))));
        }
        return chats;
    }

    @Override
    public void onDialogClick(IDialog dialog) {
        //TODO--: pass in players in chats
        Intent intent = new Intent(ChatActivity.this, MessageActivity.class);
        startActivity(intent);
    }

    /**
     * Called on receiving new messages
     * @param dialogId is the id of dialog that receives the message
     * @param message is the message
     */
    private void onNewMessage(String dialogId, IMessage message) {
        boolean isUpdated = chatListAdapter.updateDialogWithMessage(dialogId, message);
        if (!isUpdated) {
            //Dialog with this ID doesn't exist, so you can create new Dialog or update all dialogs list
        }
    }

    /**
     * Called on receiving new dialogs
     * @param dialog is a dialog
     */
    private void onNewDialog(IDialog dialog) {
        chatListAdapter.addItem(dialog);
    }
}