package edu.duke.ece651.group4.RISK.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.duke.ece651.group4.RISK.shared.message.ChatMessage;
import org.apache.commons.lang3.SerializationUtils;

public class ChatClient extends Thread {

    SocketChannel chatChannel = null;
    String username;
    private final AtomicBoolean exit = new AtomicBoolean(false);

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