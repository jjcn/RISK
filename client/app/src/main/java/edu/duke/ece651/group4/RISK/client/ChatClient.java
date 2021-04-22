package edu.duke.ece651.group4.RISK.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.model.ChatMessageUI;
import edu.duke.ece651.group4.RISK.client.model.ChatPlayer;
import edu.duke.ece651.group4.RISK.shared.message.ChatMessage;
import org.apache.commons.lang3.SerializationUtils;

import static edu.duke.ece651.group4.RISK.client.Constant.LOG_FUNC_RUN;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.getUserName;
import static edu.duke.ece651.group4.RISK.shared.Constant.CHAT_SETUP_ACTION;

public class ChatClient extends Thread {
    private final String TAG = this.getClass().getSimpleName();
    SocketChannel chatChannel = null;
    String username;
    private final AtomicBoolean exit = new AtomicBoolean(false);
    private onReceiveListener receiveMsgListener;

    public ChatClient(String username, String hostname, int port) {
        try {
            chatChannel = SocketChannel.open();
            chatChannel.connect(new InetSocketAddress(hostname, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.username = username;
        this.receiveMsgListener = null;
    }

    public void exit() {
        exit.set(true);
        System.out.println("set exit to true in chat client");
    }


    @Override
    public void run() {
        try {
            this.send(new ChatMessage(username, null, null, 0, CHAT_SETUP_ACTION));
            waitToReceive();
            System.out.println("chat client exits");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * This keeps waiting for message from server
     */
    public void waitToReceive() throws IOException {
        while (!exit.get()) {
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            int readBytes = this.chatChannel.read(readBuffer);
            if (readBytes == 0) {
                continue;
            }

            if (readBytes == -1) {
                this.chatChannel.close();
                System.out.println("close channel");
            }
            ChatMessage chatMsgReceive = SerializationUtils.deserialize(readBuffer.array());
            readBuffer.clear();

            // TODO: syc in database

            //get cahtID
            String chatID = "";
            Set<String> targets = chatMsgReceive.getTargetsPlayers();
            if (targets.size() == 1) {
                for (String name : targets) {
                    chatID = name;
                }
            }

            // notify client and update UI
            ChatMessageUI receivedMsg = new ChatMessageUI(chatID, chatMsgReceive.getSource() + ": " + chatMsgReceive.getChatContent(),
                    new ChatPlayer(chatMsgReceive.getGameID(), chatMsgReceive.getSource()), chatMsgReceive.getTargetsPlayers());
            if (receiveMsgListener != null) {
                Log.i(TAG, LOG_FUNC_RUN + "ClientChat: " + username + " get from " + chatMsgReceive.getSource() + " saying " + chatMsgReceive.getChatContent());
                receiveMsgListener.onSuccess(receivedMsg);
            } else {
                Log.i(TAG, LOG_FUNC_RUN + "lsm null");
            }
        }

    }

    public void setReceiveMsgListener(onReceiveListener receiveMsgListener) {
        this.receiveMsgListener = receiveMsgListener;
    }

    //send a chatMessage to Server
    public void send(ChatMessage chatMessage) {
        byte[] chatBytes = SerializationUtils.serialize(chatMessage);
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
}