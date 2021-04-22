package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.RISKApplication;
import edu.duke.ece651.group4.RISK.client.model.ChatDialog;

import java.util.*;

import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;

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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chats");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        chatList = findViewById(R.id.chatList);
        initAdapter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            moveTaskToBack(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initAdapter() {
        chatListAdapter = new DialogsListAdapter(R.layout.item_dialog, null);
        chatListAdapter.setItems(getChats());
        chatListAdapter.setOnDialogClickListener(this);
        chatList.setAdapter(chatListAdapter);
    }

    private List<ChatDialog> getChats() {
        ArrayList<ChatDialog> chats = new ArrayList<>();
        /**
         * All players' chat room, id = ""
         */
        chats.add(new ChatDialog("", "World Chat", getChatPlayersName()));
        /**
         * One-to-one chat room
         */
        for (String playerName:getChatPlayersName()) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.DAY_OF_MONTH, -(i * i));
//            calendar.add(Calendar.MINUTE, -(i * i));
            chats.add(new ChatDialog(playerName, "Private Chat with " + playerName,
                    Arrays.asList(playerName)));
        }
        return chats;
    }

    @Override
    public void onDialogClick(IDialog dialog) {
        Intent intent = new Intent(ChatActivity.this, MessageActivity.class);
        intent.putExtra("TARGET", dialog.getId());
        startActivity(intent);
    }

    /**
     * Called on receiving new messages
     *
     * @param dialogId is the id of dialog that receives the message
     * @param message  is the message
     */
    // todo: private msg
    private void onNewMessage(String dialogId, IMessage message) {
        boolean isUpdated = chatListAdapter.updateDialogWithMessage(dialogId, message);
        if (!isUpdated) {
            //Dialog with this ID doesn't exist, so you can create new Dialog or update all dialogs list
        }
    }

//    /**
//     * Called on receiving new dialogs
//     *
//     * @param dialog is a dialog
//     */
//    private void onNewDialog(IDialog dialog) {
//        chatListAdapter.addItem(dialog);
//    }
}