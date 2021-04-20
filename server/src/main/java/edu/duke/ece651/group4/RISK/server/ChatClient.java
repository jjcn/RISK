package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.message.ChatMessage;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

//public class ChatClient extends Thread {
//
//    SocketChannel chatChannel = null;
//    String username;
//    private final AtomicBoolean exit = new AtomicBoolean(false);
//
//    // constructor
//    public ChatClient(String username, SocketChannel chatChannel) {
//        this.username = username;
//        this.chatChannel = chatChannel;
//    }
//
//    public void exit(){
//        exit.set(true);
//        System.out.println("set exit to true in chat client");
//    }
//
//
//    @Override
//    public void run() {
//        try {
//            this.init();
//            this.process();
//            System.out.println("chatclient exits");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//
//        }
//    }
//
//    public void init() throws IOException {
//    }
//
//    public void process() throws IOException {
//
//        while (!exit.get()) {
//            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
//            int readBytes = this.chatChannel.read(readBuffer);
//            if (readBytes == 0) {
//                continue;
//            }
//
//            if (readBytes == -1) {
//                this.chatChannel.close();
//                System.out.println("close channel");
//            }
//            ChatMessage chatMsgRecv = (ChatMessage) SerializationUtils.deserialize(readBuffer.array());
//            readBuffer.clear();
//            // debug
//            System.out.println("ClientChat: " + username + "get from " chatMsgRecv.getSource() +" saying "+ chatMsgRecv.getChatContent());
//        }
//    }
//
//    public void send(ChatMessage chatMessage){
//        byte[] chatBytes = SerializationUtils.serialize(chatMessage);
//        ByteBuffer writeBuffer = ByteBuffer.wrap(chatBytes);
//        try {
//            chatChannel.write(writeBuffer);
//            System.out.println("Test: "+ chatMessage.getSource()+ " send chat message to chatServer!");
//        } catch (IOException e) {
//            System.out.println("IOException when send message to chat server");
//            e.printStackTrace();
//            System.exit(0);
//        }
//        writeBuffer.clear();
//    }
//}