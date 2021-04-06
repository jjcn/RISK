package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

import static edu.duke.ece651.group4.RISK.shared.Constant.SWITCH_OUT_ACTION;
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
            World t=new World(4);

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
        User u0 = new User(1,"user0","1");
        assertEquals(g.isUserLose(u),true);
        assertEquals(g.isEndGame(),false);

        assertThrows(new IllegalArgumentException().getClass(), () ->g.upgradeTechOnWorld("user0"));
        UpgradeTroopOrder uo=new UpgradeTroopOrder("A",0,1,0);
        g.upgradeTroopOnWorld(uo, "user0");

        UpgradeTechOrder ut=new UpgradeTechOrder(0);
        g.tryUpdateActionOnWorld(uo,u0);
        assertThrows(new IllegalArgumentException().getClass(), () ->g.tryUpdateActionOnWorld(ut,u0));

        PlaceOrder p=new PlaceOrder("A",new Troop(10,new TextPlayer("user0")));
        g.placeUnitsOnWorld(p);
        g.tryUpdateActionOnWorld(p,u0);

        MoveOrder m=new MoveOrder("A","B",new Troop(1,new TextPlayer("user0")),'M');
        assertThrows(new IllegalArgumentException().getClass(),() ->g.doMoveOnWorld(m,"user0"));
        assertThrows(new IllegalArgumentException().getClass(),() ->g.tryUpdateActionOnWorld(m,u0));


        AttackOrder a=new AttackOrder("A","C",new Troop(1,new TextPlayer("user0")),'A');
        assertThrows(new java.lang.ClassCastException().getClass(),() ->g.doMoveOnWorld(a,"user0"));
        assertThrows(new java.lang.IllegalArgumentException().getClass(),()  ->g.tryUpdateActionOnWorld(a,u0));



        BasicOrder d=new BasicOrder(null,null,null,'D');
        g.doDoneActionFor(u0);
        g.tryUpdateActionOnWorld(d,u0);

        BasicOrder eorder=new BasicOrder(null,null,null,SWITCH_OUT_ACTION);
        g.tryUpdateActionOnWorld(eorder,u0);
        g.updateGameAfterOneTurn();



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

        Game g4=createAGame(1,4);
        Game g5=createAGame(1,5);
        g4.setUpGame();
        g5.setUpGame();


    }


}