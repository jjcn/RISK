package edu.duke.ece651.group4.RISK.client.activity;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.listener.onResultListener;
import edu.duke.ece651.group4.RISK.client.model.ChatMessageUI;
import edu.duke.ece651.group4.RISK.client.model.ChatPlayer;

import java.util.Date;

import static edu.duke.ece651.group4.RISK.client.Constant.LOG_FUNC_RUN;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;

/**
 * Reference: https://github.com/stfalcon-studio/ChatKit/blob/d10cfe3393a9d6ce150e817b22b019dcd17c55fa/sample/src/main/java/com/stfalcon/chatkit/sample/features/demo/def/DefaultMessagesActivity.java#L16
 */
public class MessageActivity extends AppCompatActivity
        implements MessageInput.InputListener {
    private static final String TAG = MessageActivity.class.getSimpleName();

    private MessagesListAdapter msgAdapter;
    private MessagesList msgList;
//    private static final int TOTAL_MSG = 100;
//    private Menu menu;
//    private int selectionCount;
//    private Date lastLoadedDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        this.msgList = findViewById(R.id.messagesList);
        msgAdapter = new MessagesListAdapter<>(getUserName(), null);
        msgList.setAdapter(msgAdapter);
//        msgAdapter.setLoadMoreListener(this);

        MessageInput input = findViewById(R.id.input);
        input.setInputListener(this);
//        input.setTypingListener(this);
//        input.setAttachmentsListener(this);
    }


    //TODO: get history info
    @Override
    protected void onStart() {
        super.onStart();
        // getHistoryMsg();
        msgAdapter.addToStart(new ChatMessageUI(0,"",new ChatPlayer(getRoomId(),"")), true);
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        ChatPlayer user = new ChatPlayer(getRoomId(), getUserName());
        ChatMessageUI message = new ChatMessageUI(0, input.toString(), user);
        sendOneMsg(message, new onResultListener() {
            @Override
            public void onSuccess() {
                msgAdapter.addToStart(message, true);
            }

            @Override
            public void onFailure(String errMsg) {
                showByToast(MessageActivity.this,errMsg);
                return;
            }
        });
        return true;
    }

    // todo: load msg
//    protected void loadMessages() {
//        new Handler().postDelayed(() -> {
//            ArrayList<ChatMessage> messages = MessagesFixtures.getMessages(lastLoadedDate);
//            lastLoadedDate = messages.get(messages.size() - 1).getCreatedAt();
//            msgAdapter.addToEnd(messages, false);
//        }, 1000);
//    }
//
////    add to implements:, MessagesListAdapter.OnLoadMoreListener
//    @Override
//    public void onLoadMore(int page, int totalItemsCount) {
//        if (totalItemsCount < TOTAL_MSG) {
//            loadMessages();
//        }
//    }




    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        this.menu = menu;
//        getMenuInflater().inflate(R.menu.message_menu,menu);
//        onSelectionChanged(0);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_delete:
//                messagesAdapter.deleteSelectedMessages();
//                break;
//            case R.id.action_copy:
//                messagesAdapter.copySelectedMessagesText(this, getMessageStringFormatter(), true);
//                AppUtils.showToast(this, R.string.copied_message, true);
//                break;
//        }
//        return true;
//    }

//    private MessagesListAdapter.Formatter<Message> getMessageStringFormatter() {
//        return message -> {
//            String createdAt = new SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
//                    .format(message.getCreatedAt());
//
//            String text = message.getText();
//            if (text == null) text = "[attachment]";
//
//            return String.format(Locale.getDefault(), "%s: %s (%s)",
//                    message.getUser().getName(), text, createdAt);
//        };
//    }
}
