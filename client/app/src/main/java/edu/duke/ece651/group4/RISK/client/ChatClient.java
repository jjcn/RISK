package edu.duke.ece651.group4.RISK.client;

import android.util.Log;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.model.ChatMessageUI;
import edu.duke.ece651.group4.RISK.client.model.ChatPlayer;
import edu.duke.ece651.group4.RISK.shared.message.ChatMessage;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import static edu.duke.ece651.group4.RISK.client.Constant.LOG_FUNC_RUN;
import static edu.duke.ece651.group4.RISK.client.Constant.WORLD_CHAT;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.addMsg;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.getRoomId;
import static edu.duke.ece651.group4.RISK.shared.Constant.CHAT_SETUP_ACTION;

public class ChatClient extends Thread {
    private final String TAG = this.getClass().getSimpleName();

    SocketChannel chatChannel = null;
    String username;
    private final AtomicBoolean exit = new AtomicBoolean(false);
    private onReceiveListener chatReceiveListener;
    private onReceiveListener msgReceiveListener;

    public ChatClient(String username, String hostname, int port) {
        try {
            chatChannel = SocketChannel.open();
            chatChannel.connect(new InetSocketAddress(hostname, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.username = username;
        this.chatReceiveListener = null;
        this.msgReceiveListener = null;
    }

    public void exit() {
        exit.set(true);
        System.out.println("set exit to true in chat client");
    }


    @Override
    public void run() {
        try {
            initReceive();
            waitToReceive();
            System.out.println("chat client exits");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * should send a notification to serer at start.
     */
    private void initReceive() {
        ChatMessage message = new ChatMessage(username, null, null, 0, CHAT_SETUP_ACTION);
        byte[] chatBytes = SerializationUtils.serialize(message);
        ByteBuffer writeBuffer = ByteBuffer.wrap(chatBytes);
        new Thread(() -> {
            try {
                chatChannel.write(writeBuffer);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }).start();
        writeBuffer.clear();
    }

    /**
     * This keeps waiting for message from server.
     */
    public void waitToReceive() throws IOException {
        while (!exit.get()) {
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            int readBytes = this.chatChannel.read(readBuffer);
            if (readBytes == 0) {
                continue;
            }

            // wait here until get message
            if (readBytes == -1) {
                this.chatChannel.close();
                System.out.println("close channel");
            }
            ChatMessage chatMsgReceive = SerializationUtils.deserialize(readBuffer.array());
            readBuffer.clear();


            // notify client and update UI
            ChatMessageUI receivedMsg = new ChatMessageUI(chatMsgReceive.getChatID(), chatMsgReceive.getSource() + ": " + chatMsgReceive.getChatContent(),
                    new ChatPlayer(chatMsgReceive.getGameID(), chatMsgReceive.getSource()), chatMsgReceive.getTargetsPlayers());
            addMsg(receivedMsg);
            if (chatReceiveListener != null) {
                Log.i(TAG, LOG_FUNC_RUN +"chatID "+ chatMsgReceive.getChatID() + "ClientChat: " + username
                        + " get from " + chatMsgReceive.getSource() + " saying " + chatMsgReceive.getChatContent());
                chatReceiveListener.onSuccess(receivedMsg);
                if (msgReceiveListener != null) {
                    msgReceiveListener.onSuccess(receivedMsg);
                }
            } else {
                Log.i(TAG, LOG_FUNC_RUN + "lsm null");
            }
        }
    }

    public void setMsgListener(onReceiveListener receiveMsgListener) {
        this.msgReceiveListener = receiveMsgListener;
    }

    public void setChatListener(onReceiveListener receiveMsgListener) {
        this.chatReceiveListener = receiveMsgListener;
    }

    /**
     * send a chatMessage to Server. refresh the MessageActivity by listener and ChatActivity by existed chatListener.
     *
     * @param message to send.
     */
    public void send(ChatMessageUI message) {
        new Thread(() -> {
            Log.i(TAG,LOG_FUNC_RUN+"send id: "+message.getChatId());
            ChatMessage chatMessage = new ChatMessage(message.getChatId(), username, message.getTargets(), message.getText(), getRoomId());
            Log.i(TAG,LOG_FUNC_RUN+message.getTargets().size());
            byte[] chatBytes = SerializationUtils.serialize(chatMessage);
            ByteBuffer writeBuffer = ByteBuffer.wrap(chatBytes);
            try {
                chatChannel.write(writeBuffer);
                // for one player himself, the chatID is the target for him to send
                String chatID = WORLD_CHAT;
                if(!chatID.equals(message.getChatId())) {
                    for(String target: message.getTargets()) {
                        message.setChatId(target);
                    }
                    Log.i(TAG,LOG_FUNC_RUN+"change id to "+message.getChatId());
                }
                Log.i(TAG,LOG_FUNC_RUN+"when update msg I send, id = "+message.getChatId());
                chatReceiveListener.onSuccess(message); // update msg in chat from user himself
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
            writeBuffer.clear();
        }).start();
    }
}