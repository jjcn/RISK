package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.model.ChatDialog;
import edu.duke.ece651.group4.RISK.client.model.ChatMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static edu.duke.ece651.group4.RISK.client.RISKApplication.getCurrentRoomSize;

public class ChatActivity extends AppCompatActivity implements DialogsListAdapter.OnDialogClickListener {
    private static final String TAG = ChatActivity.class.getSimpleName();
    private DialogsListAdapter chatListAdapter;
    private DialogsList chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        impUI();
    }

    private void impUI() {
        chatList = findViewById(R.id.chatList);
        chatListAdapter = new DialogsListAdapter(R.layout.item_dialog, null);
        chatListAdapter.setItems(getChats());
        chatListAdapter.setOnDialogClickListener(this);
        chatList.setAdapter(chatListAdapter);
    }

    // TODO: chats: 1v1 & whole world
    private List<ChatDialog> getChats() {
        ArrayList<ChatDialog> chats = new ArrayList<>();
        ArrayList<String> allPlayerNames = getAllplayersName();
        /**
         * All players' chat room, id = 0
         */
        chats.add(new ChatDialog(0, "World Chat", allPlayerNames));
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
        //TODO--: pass in inchats players
        Intent intent = new Intent(ChatActivity.this, MessageActivity.class);
        startActivity(intent);
    }

//    //for example
//    private void onNewMessage(String dialogId, Message message_menu) {
//        boolean isUpdated = dialogsAdapter.updateDialogWithMessage(dialogId, message_menu);
//        if (!isUpdated) {
//            //Dialog with this ID doesn't exist, so you can create new Dialog or update all dialogs list
//        }
//    }
//
//    //for example
//    private void onNewDialog(Dialog dialog) {
//        dialogsAdapter.addItem(dialog);
//    }
}