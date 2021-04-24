package edu.duke.ece651.group4.RISK.client.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.model.ChatDialog;
import edu.duke.ece651.group4.RISK.client.model.ChatMessageUI;

import java.util.*;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
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
        setListener();
        Log.i(TAG, SUCCESS_CREATE);
    }


    /**
     * keep receive via chatClient
     */
    private void setListener() {
        Log.i(TAG, LOG_FUNC_RUN + "start set chat lsn");
        setChatListener(new onReceiveListener() {
            @Override
            public void onSuccess(Object o) {
                runOnUiThread(() -> {
                    if (o instanceof ChatMessageUI) {
                        Log.i(TAG, LOG_FUNC_RUN + "receive incoming msg success");
                        ChatMessageUI message = (ChatMessageUI) o;
                        // call function to deal with new incoming msg
                        onNewMessage(message.getChatId(), message);
                    } else {
                        onFailure("receive not ChatMessageUI");
                    }
                });
            }

            @Override
            public void onFailure(String errMsg) {
                Log.e(TAG, LOG_FUNC_FAIL + errMsg);
            }
        });
    }

    private void initAdapter() {
        Log.i(TAG,LOG_FUNC_RUN+"init Adapter");
        chatListAdapter = new DialogsListAdapter(R.layout.item_dialog, null);
        chatListAdapter.setItems(getChats());
        chatListAdapter.setOnDialogClickListener(this);
        chatList.setAdapter(chatListAdapter);
    }

    /**
     * chatID: constant for chat with whole world, the target player's name otherwise.
     * @return world chat room and private chat room.
     */
    private List<ChatDialog> getChats() {
        Log.i(TAG,LOG_FUNC_RUN+"get chats");
        ArrayList<ChatDialog> chats = new ArrayList<>();
        // World chat room, id = ""
        chats.add(new ChatDialog(WORLD_CHAT, "World Chat", new ArrayList<>(getAllPlayersName())));
        // private chat
        for (String playerName : getChatPlayersName()) {
            ArrayList<String> target = new ArrayList();
            target.add(playerName);
            chats.add(new ChatDialog(playerName, "Private Chat with " + playerName, target));
        }
        // update latest message info
        for(ChatMessageUI msg:getStoredMsg(null)){
            onNewMessage(msg.getChatId(),msg);
        }
        for(ChatDialog dialog : chats){
            Log.i(TAG,LOG_FUNC_RUN+dialog.getId());
        }
        return chats;
    }

    @Override
    public void onDialogClick(IDialog dialog) {
        Intent intent = new Intent(ChatActivity.this, MessageActivity.class);
        Log.i(TAG, LOG_FUNC_FAIL + "enter chatId: "+dialog.getId());
        intent.putExtra("TARGET", dialog.getId());
        startActivity(intent);
    }

    /**
     * Called on receiving new messages
     *
     * @param chatId is the id of dialog that receives the message
     * @param message  is the message
     */
    private void onNewMessage(String chatId, IMessage message) {
        Log.i(TAG, LOG_FUNC_RUN + "on new chatID: "+chatId);
        boolean isUpdated = chatListAdapter.updateDialogWithMessage(chatId, message);
        chatListAdapter.notifyDataSetChanged();
        if (!isUpdated) {
            //Dialog with this ID doesn't exist, so you can create new Dialog or update all dialogs list
            Log.e(TAG, LOG_FUNC_FAIL + "chatID not exist: "+chatId);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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