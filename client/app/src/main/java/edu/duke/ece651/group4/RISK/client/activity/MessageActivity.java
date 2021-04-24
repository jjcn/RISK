package edu.duke.ece651.group4.RISK.client.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
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

import java.util.*;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
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
    private String target;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        this.target = getIntent().getStringExtra("TARGET");
        Log.i(TAG, LOG_FUNC_RUN + "target: " + target);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(target.equals(WORLD_CHAT) ? "World" : target);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        this.msgList = findViewById(R.id.messagesList);

        initAdapter();
        getHistoryInfo();

        /**
         * keep receive via chatClient
         */
        setMsgListener(new onReceiveListener() {
            @Override
            public void onSuccess(Object o) {
                runOnUiThread(() -> {
                    if (o instanceof ChatMessageUI) {
                        ChatMessageUI message = (ChatMessageUI) o;
                        if(message.getRoomId().equals(getRoomId()) && message.getChatId().equals(target)) {
                            msgAdapter.addToStart(message, true);
                        }
                    } else {
                        onFailure("receive not ChatMessageUI");
                    }
                    Log.i(TAG, LOG_FUNC_RUN + "recv msg lsn done success");
                });
            }

            @Override
            public void onFailure(String errMsg) {
                Log.e(TAG, LOG_FUNC_FAIL + errMsg);
            }
        });
        Log.i(TAG, SUCCESS_CREATE);
    }

    private void initAdapter() {
        msgAdapter = new MessagesListAdapter<>(getUserName() + getRoomId(), null);
        msgList.setAdapter(msgAdapter);

        MessageInput input = findViewById(R.id.input);
        input.setInputListener(this);
    }

    /**
     * read from RISKApplication to syc the history messages.
     */
    private void getHistoryInfo() {
        Log.i(TAG,LOG_FUNC_RUN+"get history: id "+target);
        List<ChatMessageUI> stored = getStoredMsg(target);
        Log.i(TAG,LOG_FUNC_RUN+"get stored with size "+stored.size());
        for(ChatMessageUI message:stored){
                msgAdapter.addToStart(message, true);
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

    @Override
    public boolean onSubmit(CharSequence input) {
        ChatPlayer user = new ChatPlayer(getRoomId(), getUserName());
        Set<String> targets = new HashSet<>();
        if (target.equals(WORLD_CHAT)) {
            targets.addAll(getAllPlayersName());
            targets.remove(getUserName());
        } else {
            targets.add(target);
        }
        ChatMessageUI message = new ChatMessageUI(WORLD_CHAT.equals(target)? WORLD_CHAT : getUserName(), input.toString(), user, targets);

        Log.i(TAG, LOG_FUNC_RUN + "create new msg: " + "chatID = "+message.getChatId()+" text: "+message.getText());
        sendOneMsg(message, new onResultListener() {
            @Override
            public void onSuccess() {
//                message.setChatId();
                runOnUiThread(()->{
                    addMsg(message);
                    msgAdapter.addToStart(message, true);
                });
            }

            @Override
            public void onFailure(String errMsg) {
                showByToast(MessageActivity.this, errMsg);
            }
        });
        return true;
    }

    public Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            Log.i(TAG, LOG_FUNC_RUN + "bitmap: " + bitmapArray);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
