package edu.duke.ece651.group4.RISK.server;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static edu.duke.ece651.group4.RISK.shared.Constant.CHAT_PORT;

public class ChatHost {
    PrintStream out = System.out;
    static Selector selector;
    ServerSocketChannel serverSocketChannel;
    Map<String, SocketChannel> clientMap = new HashMap<>();
    ByteBuffer readBuffer = ByteBuffer.allocate(1024); // for read
    ByteBuffer sendBuffer = ByteBuffer.allocate(1024); // for send


    protected void init() throws IOException {
        out.println("ChatHost starts init");
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(CHAT_PORT));
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        out.println("ChatHost finishes init");
    }

    /*
    * This starts selector to accept any active channel
    * */
    protected void acceptConnection(){
        while(true){
            try {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                for (Iterator<SelectionKey> it = keyIterator; it.hasNext(); ) {
                    SelectionKey key = it.next();
                    handleSelectedKey(key);
                }
                selectedKeys.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                    //read the object
                    //send it to destination
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
