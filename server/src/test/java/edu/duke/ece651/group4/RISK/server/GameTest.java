package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.Client;
import edu.duke.ece651.group4.RISK.shared.World;
import edu.duke.ece651.group4.RISK.shared.WorldFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    Game g;
    private final static int PORT = 1107;
    static ServerSocket hostSocket;
    static HashSet<String> testSet;

    @BeforeAll
    static void setUpAll() throws IOException {
        hostSocket = new ServerSocket(PORT);
    }

    private Game createAGame(int gid, int numUser){
        Game g = new Game(gid,numUser);
        for(int i = 0; i < numUser; i++){
            User u = new User(i,"user" + i,"1234");
            g.addUser(u);
        }
        return g;
    }

    @Test
    public void test_barrier(){
        this.g = createAGame(1,3);
        new Thread(() -> {
            System.out.println("wait for others");
            this.g.barrierWait();
            System.out.println("Done wait");
        }).start();
        new Thread(() -> {
            System.out.println("wait for others");
            this.g.barrierWait();
            System.out.println("Done wait");
        }).start();
        new Thread(() -> {
            System.out.println("wait for others");
            this.g.barrierWait();
            System.out.println("Done wait");
        }).start();
    }

    @Test

    public void test_setUpGame(){
        Game g = createAGame(1,3);
        g.setUpGame();
    }


    @Test
    public void test_getWorld() {

        Game g = createAGame(1, 1);
//        System.out.println(g.getUserNames().get(0));
        g.setUpGame();
        g.getTheWorld();
    }

    @Test
    public void test_sendWorld() throws IOException {
        Game g = createAGame(1, 1);
//        System.out.println(g.getUserNames().get(0));
        g.setUpGame();
        World w = g.getTheWorld();

        new Thread( ()-> {
            try{
                Socket s = hostSocket.accept();
                Client clientInServer = new Client(s);
                String strFromClient = (String) clientInServer.recvObject();
                assertEquals(strFromClient, "Hi, this is client");
                clientInServer.sendObject("Copy that, this is server");
                World theWorld = (World) clientInServer.recvObject();
                assertEquals(true, theWorld != null);
                clientInServer.close();
            } catch (IOException e) {
            }
        }
        ).start();

        Client clientSocket = new Client("127.0.0.1", PORT);
        clientSocket.sendObject("Hi, this is client");
        String strFromServer = (String) clientSocket.recvObject();
        assertEquals(strFromServer, "Copy that, this is server");
        clientSocket.sendObject(w);

        clientSocket.close();
    }
}