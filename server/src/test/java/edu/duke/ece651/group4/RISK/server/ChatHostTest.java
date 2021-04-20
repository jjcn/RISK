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

class ChatClient extends Thread {

    SocketChannel chatChannel = null;
    String clientName;
    private final AtomicBoolean exit = new AtomicBoolean(false);
    // constructor
    public ChatClient(String username, SocketChannel chatChannel) {
        this.clientName = username;
        this.chatChannel = chatChannel;
    }

    public void exit(){
        exit.set(true);
        System.out.println("set exit to true in chat client");
    }


    @Override
    public void run() {
        try {
            this.init();
            this.process();
            System.out.println("chatclient exits");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }

    public void init() throws IOException {
    }

    public void process() throws IOException {

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
            // debug
            System.out.println("The chat message is from: " + chatMsgRecv.getSource());
            System.out.println("Saying: " + chatMsgRecv.getChatContent());
        }
    }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatHostTest {
    ChatHost ch;
    Map<String,SocketChannel> clients = new HashMap<>();
//    Map<String,Client> clients = new HashMap<>();
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
            send(new ChatMessage("user" + i, null, null,  0, CHAT_SETUP_ACTION));
        }
    }


     synchronized void setUpAClient(String username){
        try {
            SocketChannel chatChannel = SocketChannel.open();
            chatChannel.connect(new InetSocketAddress(host, port));
//            System.out.println("get a new connection: " + username);
            clients.put(username,chatChannel);
            ChatClient chatClient = new ChatClient(username, chatChannel);
            chatClient.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    synchronized void setUpAClient(String username){
//        try {
//            Client c = new Client(host, port);
//            clients.put(username, c);
//            System.out.println("Test: get a new connection: " + username);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    void send(ChatMessage chatMessage){
//        System.out.println("Send " + chatMessage.getSource());
//        Client c = clients.get(chatMessage.getSource());
//        assertNotNull(c);
//        c.sendObject(chatMessage);
//    }
    void send(ChatMessage chatMessage){

        SocketChannel chatChannel = clients.get(chatMessage.getSource());
        assertNotNull(chatChannel);
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

    @Test
    public void test_run(){
        List<String> targets = new ArrayList<>();
        targets.add("user1");
        targets.add("user2");
        ChatMessage cM1 = new ChatMessage("user0", targets, "hello everyone!",  0, "");
        send(cM1);
    }


}