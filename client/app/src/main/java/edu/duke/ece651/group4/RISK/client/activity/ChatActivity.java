package edu.duke.ece651.group4.RISK.client.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import edu.duke.ece651.group4.RISK.client.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static edu.duke.ece651.group4.RISK.client.RISKApplication.getCurrentRoomSize;

public class ChatActivity extends AppCompatActivity {
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
        chatListAdapter.setOnDialogClickListener(dialog -> {
            Intent intent = new Intent(ChatActivity.this,MessageActivity.class);
            startActivity(intent);
        });
        chatList.setAdapter(chatListAdapter);
    }

    private List getChats() {
        // TODO
//        ArrayList<chatDialog> chats = new ArrayList<>();
//        for(int i=0;i<getCurrentRoomSize();i++){
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.DAY_OF_MONTH,-(i*i));
//            calendar.add(Calendar.MINUTE,-(i*i));
//            chats.add(new chatDialog(i,));
//        }
//        return chats;
        return null;
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