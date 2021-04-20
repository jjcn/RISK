package edu.duke.ece651.group4.RISK.server;
import edu.duke.ece651.group4.RISK.shared.message.ChatMessage;
import org.apache.commons.lang3.SerializationUtils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static edu.duke.ece651.group4.RISK.shared.Constant.CHAT_SETUP_ACTION;
import static org.junit.jupiter.api.Assertions.*;



@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatHostTest {
    ChatHost ch;
    Map<String,ChatClient> clients = new HashMap<>();
    int port;
    String host = "localhost";

    protected void timeWait(int t){
        try {
            TimeUnit.SECONDS.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @BeforeAll
    public void  setUp(){
        port = 9988;
        ch = new ChatHost(port);// initialize the chat host
        ch.start();
        timeWait(1);
        for(int i = 0; i< 3; i++){
            setUpAClient("user" + i);
        }
    }


     synchronized void setUpAClient(String username){
        ChatClient chatClient = new ChatClient(username, host, port);
        clients.put(username,chatClient);
        chatClient.start();
        chatClient.send(new ChatMessage(username, null, null,  0, CHAT_SETUP_ACTION));
    }


    @Test
    public void test_run(){
        List<String> targets = new ArrayList<>();
        targets.add("user1");
        targets.add("user2");
        ChatMessage cM1 = new ChatMessage("user0", targets, "hello everyone!",  0, "");
        clients.get("user0").send(cM1);
    }

}

class ChatClient extends Thread {

    SocketChannel chatChannel = null;
    String username;
    private final AtomicBoolean exit = new AtomicBoolean(false);

    // constructor
//    public ChatClient(String username, SocketChannel chatChannel) {
//        this.username = username;
//        this.chatChannel = chatChannel;
//    }
    public ChatClient(String username, String hostname, int port){
        try {
            chatChannel = SocketChannel.open();
            chatChannel.connect(new InetSocketAddress(hostname, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.username = username;
    }

    public void exit(){
        exit.set(true);
        System.out.println("set exit to true in chat client");
    }


    @Override
    public void run() {
        try {
            waitToRecv();
            System.out.println("chat client exits");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }

    /*
    * This keeps waiting for message from server
    * */
    public void waitToRecv() throws IOException {
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
            ChatMessage chatMsgRecv = (ChatMessage) SerializationUtils.deserialize(readBuffer.array());
            readBuffer.clear();
            //deal with chatMsgRecV to notify android UI
            System.out.println("ClientChat: " + username + " get from " + chatMsgRecv.getSource() +" saying "+ chatMsgRecv.getChatContent());
        }
    }

    //send a chatMessage to Server
    public void send(ChatMessage chatMessage){
        byte[] chatBytes = SerializationUtils.serialize(chatMessage);
        ByteBuffer writeBuffer = ByteBuffer.wrap(chatBytes);
        try {
            chatChannel.write(writeBuffer);
            System.out.println("Test: "+ chatMessage.getSource()+ " send chat message to chatServer!");
        } catch (IOException e) {
            System.out.println("IOException when send message to chat server");
            e.printStackTrace();
            System.exit(0);
        }
        writeBuffer.clear();
    }
}