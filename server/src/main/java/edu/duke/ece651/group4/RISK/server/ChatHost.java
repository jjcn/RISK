package edu.duke.ece651.group4.RISK.server;
//package org.apache.commons.lang3;

import java.io.IOException;
import edu.duke.ece651.group4.RISK.shared.message.ChatMessage;
import org.apache.commons.lang3.SerializationUtils;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

import static edu.duke.ece651.group4.RISK.shared.Constant.CHAT_PORT;
import static edu.duke.ece651.group4.RISK.shared.Constant.CHAT_SETUP_ACTION;

public class ChatHost extends Thread {
    PrintStream out = System.out;
    static Selector selector;
    ServerSocketChannel serverSocketChannel;
    Map<String, SocketChannel> clientMap = new HashMap<>();
    ByteBuffer readBuffer = ByteBuffer.allocate(1024); // for read
    int chatPort;
    boolean isClose = false;
    public ChatHost(int chatPort){
        this.chatPort = chatPort;
    }
    public ChatHost(){
        this(CHAT_PORT);
    }

    protected void init() throws IOException {
        out.println("ChatHost starts init");
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(chatPort));
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        out.println("ChatHost finishes init");
    }

    /*
    * This starts selector to accept any active channel
    * */
    protected void acceptConnection(){
        while(!isClose){
            try {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                for (Iterator<SelectionKey> it = keyIterator; it.hasNext(); ) {
                    SelectionKey key = it.next();
                    if(!key.isValid()){continue;}
                    handleSelectedKey(key);
                }
                selectedKeys.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void tryExit(){
        this.isClose = true;
    }
    /*
    * This handles selectedKey
    * 1.accept
    * 2.readable
    *   2.1 read
    *     2.1.1 init info
    *     2.1.2 send info
    *   2.2 send
    * */
    protected void handleSelectedKey(SelectionKey key){
        try {
            if(key.isAcceptable()){
//                serverSocketChannel = (ServerSocketChannel) key.channel();
                SocketChannel clientChannel = serverSocketChannel.accept();
                clientChannel.configureBlocking(false);
                clientChannel.register(selector,SelectionKey.OP_READ);
                out.println("ChatHost has a new ChatClient");
            }
            else if (key.isReadable()){
                SocketChannel clientChannel = (SocketChannel) key.channel();
                readBuffer.clear();
                int readBytes = clientChannel.read(readBuffer);
                if(readBytes == -1){
                    clientChannel.close();
                }
                if(readBytes > 0){
                    readBuffer.flip();
                    ChatMessage chatMessage = SerializationUtils.deserialize(readBuffer.array());
                    String action = chatMessage.getAction();
                    if(action.equals(CHAT_SETUP_ACTION)){ //set up a user chat for init
                        setUpUserChat(chatMessage, clientChannel);
                    }
                    else{
                        handleChatMessage(chatMessage);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Key is cancelled");
            key.cancel();
            e.printStackTrace();
        }
    }

    protected void setUpUserChat(ChatMessage chatMessage,SocketChannel clientChannel){
        String sender = chatMessage.getSource();
        if(this.clientMap.containsKey(sender)){
            this.clientMap.remove(sender);
        }
        this.clientMap.put(sender,clientChannel);
        out.println("ChatHost: " + sender + " sets up successfully");
    }

    protected void handleChatMessage(ChatMessage chatMessage) throws IOException {
        String sender = chatMessage.getSource();
        List<String> targets = chatMessage.getTargetsPlayers();
        for(String target : targets){
            SocketChannel clientChannel = this.clientMap.get(target);
            if(clientChannel == null){
                out.println("ChatHost: no channel exits for " + target);
                continue;
            }
            out.println("ChatHost: " + sender + " send a message to " + target + " successfully -- " + chatMessage.getChatContent());
            ByteBuffer sendBuffer = ByteBuffer.wrap(SerializationUtils.serialize(chatMessage));
            clientChannel.write(sendBuffer);
            sendBuffer.clear();
        }
    }

    @Override
    public void run() {
        try {
            this.init();
            this.acceptConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.println("ChatHost exits!!!!!!");
                if (this.selector != null) {
                    this.selector.close();
                }
                if (this.serverSocketChannel != null) {
                    this.serverSocketChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
