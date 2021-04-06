package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.Client;
import edu.duke.ece651.group4.RISK.shared.UpgradeTroopOrder;
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
        Game g = createAGame(1, 2);
//        System.out.println(g.getUserNames().get(0));
        g.setUpGame();
        World w = g.getTheWorld();
        User u = new User(1,"???","1");
        assertEquals(g.isUserLose(u),true);
        assertEquals(g.isEndGame(),false);
        assertThrows(new IllegalArgumentException().getClass(), () ->g.upgradeTechOnWorld("user0"));
        UpgradeTroopOrder uo=new UpgradeTroopOrder("A",0,1,0);
        g.upgradeTroopOnWorld(uo, "user0");




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

    @Test
    public void test_basic(){
        Game g=new Game(2,2);
        assertEquals(g.getGameID(),2);
        assertEquals(g.getMaxNumUsers(),2);

        User u = new User(1,"user0","1");
        User u2 = new User(2,"user0","1");
        User u3 = new User(3,"user0","1");
        assertEquals(g.isUserInGame(u),false);
        g.addUser(u);
        g.addUser(u2);
        assertEquals(g.addUser(u3),false);
        assertEquals(g.isUserInGame(u),true);
        g.switchOutUser(u3);
        g.switchOutUser(u2);
        assertEquals(g.isAllPlayersSwitchOut(),false);
        g.switchInUser(u3);
        g.switchInUser(u2);


    }
}