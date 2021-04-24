package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static edu.duke.ece651.group4.RISK.server.ClientThreadTest.createUsers;
import static edu.duke.ece651.group4.RISK.server.ServerConstant.PLAYER_STATE_LOSE;
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

    public static Game createAGame(int gid, int numUser){
        Game g = new Game(gid+10000,numUser);
        for(int i = 0; i < numUser; i++){
            User u = new User(i+10000,"user" + i,"1234");
            g.addUser(u);
            World t=new World(4);
        }
        g.gInfo.gameState.setGameDead();
        return g;
    }


    public static Game createGame(int gid, List<User> users){
        Game g = new Game(gid+10000,users.size());
        assertEquals(false, g.isFull());
        for(User u: users){
            g.addUser(u);
        }
        g.gInfo.gameState.setGameDead();
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

    private void invalidSetUp(int num){
            Game g = createAGame(1,num);
            g.gInfo.gameState.setGameDead();
            g.setUpGame();
        }
    @Test
    public void test_setUpGame(){
        for(int num = 1; num <= 6; num++){
            Game g = createAGame(1,num);
            g.gInfo.gameState.setGameDead();
            g.setUpGame();
        }
        assertThrows(Exception.class,()->invalidSetUp(-1) );
        assertThrows(Exception.class,()->invalidSetUp(0) );
    }


    @Test
    public void test_getWorld() {
        Game g = createAGame(1,3);
        g.setUpGame();
        g.getTheWorld();
    }

    @Test
    public void test_basic(){
        List<User> users = createUsers(3);
        Game g = createGame(1,users);
        assertEquals(10001,g.getGameID());
        assertEquals(3,g.getMaxNumUsers());
        g.setUpGame();
        Game g1 = new Game(new GameInfo(10000,2));
        assertEquals(true, g.getTheWorld()!=null);
        assertEquals(true, g.getGameInfo()!=null);
        assertEquals(true, g.getUserNames()!=null);
        User user0 = users.get(0);
        assertEquals(false, g.isUserLose(user0));
        assertEquals(true, g.isFull());
        assertEquals(false, g.isEndGame());
        assertEquals(false, g.isAllPlayersSwitchOut());
        g.waitTime(1);
    }

    @Test
    public void test_isUserInGame() {
        Game g = createAGame(1,3);
        g.setUpGame();
        assertEquals(true, g.isUserInGame(new User(10000,"user0","1234")));
        assertEquals(false, g.isUserInGame(new User(40000,"user40000","1234")));
    }

    @Test
    public void test_switchOutAndInUser(){
        Game g = createAGame(1,3);
        g.setUpGame();
        g.switchOutUser(new User(10000,"user0","1234"));
        g.switchOutUser(new User(1000,"userxxx","1234"));
        g.switchInUser(new User(10000,"user0","1234"));
        g.switchInUser(new User(1000,"userxxx","1234"));
    }

    @Test
    public void test_placeUnits(){
        Game g = createAGame(1,3);
        g.setUpGame();
        PlaceOrder p = new PlaceOrder("A",new Troop(2,new TextPlayer("user0")));
        g.placeUnitsOnWorld(p);
        assertEquals(false, g.gInfo.gameState.isDonePlaceUnits());
        g.setDonePlacementPhase();
        assertEquals(true, g.gInfo.gameState.isDonePlaceUnits());
    }

    private void placeUnitsFor2Users(Game g, List<User> users){
        PlaceOrder p0 = new PlaceOrder("A",new Troop(5,new TextPlayer("user0")));
        PlaceOrder p1 = new PlaceOrder("B",new Troop(10,new TextPlayer("user0")));
        PlaceOrder p2 = new PlaceOrder("C",new Troop(5,new TextPlayer("user1")));
        PlaceOrder p3 = new PlaceOrder("D",new Troop(10,new TextPlayer("user1")));
        g.placeUnitsOnWorld(p0);
        g.placeUnitsOnWorld(p1);
        g.placeUnitsOnWorld(p2);
        g.placeUnitsOnWorld(p3);
    }

    @Test
    public void test_moveOrder(){
        List<User> users = createUsers(2);
        Game g = createGame(1,users);
        g.setUpGame();
        placeUnitsFor2Users(g,users);
        MoveOrder m= new MoveOrder("A","B",new Troop(1,new TextPlayer("user0")),'M');
        MoveOrder m_invalid= new MoveOrder("A","B",new Troop(10000,new TextPlayer("user0")),'M');
        assertEquals(false, g.tryUpdateActionOnWorld(m,users.get(0)));
        assertEquals(false, g.tryUpdateActionOnWorld(m_invalid,users.get(0)));
    }

    @Test
    public void test_attackOrder(){
        List<User> users = createUsers(2);
        Game g = createGame(1,users);
        g.setUpGame();
        placeUnitsFor2Users(g,users);
        AttackOrder a =new AttackOrder("A","C",new Troop(1,new TextPlayer("user0")),'A');
        AttackOrder a_invalid =new AttackOrder("A","A",new Troop(1,new TextPlayer("user0")),'A');
        g.updateGameAfterOneTurn();
        assertEquals(false, g.tryUpdateActionOnWorld(a,users.get(0)));
        assertEquals(false, g.tryUpdateActionOnWorld(a_invalid,users.get(0)));
    }

    @Test
    public void test_UPTECH_ACTION(){
        List<User> users = createUsers(2);
        Game g = createGame(1,users);
        g.setUpGame();
        placeUnitsFor2Users(g,users);
        UpgradeTechOrder ut= new UpgradeTechOrder(1);
        for(int i = 0; i< 6; i++){
            assertEquals(false, g.tryUpdateActionOnWorld(ut,users.get(0)));
        }
    }

    @Test
    public void test_UPTROOP_ACTION(){
        List<User> users = createUsers(2);
        Game g = createGame(1,users);
        g.setUpGame();
        placeUnitsFor2Users(g,users);
        UpgradeTroopOrder o=new UpgradeTroopOrder("A",0,1,1);
        UpgradeTroopOrder o_invalid=new UpgradeTroopOrder("A",0,100,100);
        g.updateGameAfterOneTurn();
        assertEquals(false, g.tryUpdateActionOnWorld(o,users.get(0)));
        assertEquals(false, g.tryUpdateActionOnWorld(o_invalid,users.get(0)));
    }

    @Test
    public void test_EXIT_ACTION(){
        List<User> users = createUsers(2);
        Game g = createGame(1,users);
        g.setUpGame();
        placeUnitsFor2Users(g,users);
        BasicOrder o=new BasicOrder(null,null,null,'D');
        BasicOrder o_switch=new BasicOrder(null,null,null,SWITCH_OUT_ACTION);
        assertEquals(true, g.tryUpdateActionOnWorld(o,users.get(0)));
        assertEquals(true, g.tryUpdateActionOnWorld(o_switch,users.get(0)));
    }

    @Test
    public void test_ALLIANCE_ACTION(){
        List<User> users = createUsers(2);
        Game g = createGame(1,users);
        g.setUpGame();
        placeUnitsFor2Users(g,users);
        AllianceOrder o = new AllianceOrder("user0","user1");
        AllianceOrder o_invalid = new AllianceOrder("user0123","user12");
        g.updateGameAfterOneTurn();
        assertEquals(false, g.tryUpdateActionOnWorld(o,users.get(0)));
        assertEquals(false, g.tryUpdateActionOnWorld(o_invalid,users.get(0)));
    }

    @Test
    public void test_TRANSFER_TROOP_ACTION(){
        List<User> users = createUsers(2);
        Game g = createGame(1,users);
        g.setUpGame();
        placeUnitsFor2Users(g,users);
        TransferTroopOrder o = new TransferTroopOrder("A", Constant.SOLDIER, Constant.KNIGHT, 0, 1);
        TransferTroopOrder o_invalid = new TransferTroopOrder("A", Constant.SOLDIER, Constant.KNIGHT, 100, 2);
        assertEquals(false, g.tryUpdateActionOnWorld(o,users.get(0)));
        assertEquals(false, g.tryUpdateActionOnWorld(o_invalid,users.get(0)));
    }

    @Test
    public void test_tryUpdateActionOnWorld(){
        List<User> users = createUsers(2);
        Game g = createGame(1,users);
        g.setUpGame();
        placeUnitsFor2Users(g,users);
        BasicOrder o=new BasicOrder(null,null,null,'S');
        assertEquals(true, g.tryUpdateActionOnWorld(o,users.get(0)));
        g.updateGameAfterOneTurn();
    }
//    @Test
//    public void test_sendWorld() throws IOException {
//        Game g = createAGame(10003, 2);
//
////        System.out.println(g.getUserNames().get(0));
//
//        g.setUpGame(); // every player now starts with 2000 food & 2000 tech
//
//        World w = g.getTheWorld();
//        User u = new User(1,"???","1");
//        User u0 = new User(1,"user0","1");
//        assertEquals(g.isUserLose(u),true);
//        assertEquals(g.isEndGame(),false);
//        g.upgradeTechOnWorld("user0");
//
//        UpgradeTroopOrder uo=new UpgradeTroopOrder("A",0,1,0);
//        g.upgradeTroopOnWorld(uo, "user0");
//
//        UpgradeTechOrder ut=new UpgradeTechOrder(0);
//        g.tryUpdateActionOnWorld(uo,u0);
//        g.tryUpdateActionOnWorld(ut,u0);
//
//        PlaceOrder p=new PlaceOrder("A",new Troop(10,new TextPlayer("user0")));
//        g.placeUnitsOnWorld(p);
//        g.tryUpdateActionOnWorld(p,u0);
//
//        MoveOrder m=new MoveOrder("A","B",new Troop(1,new TextPlayer("user0")),'M');
//        g.doMoveOnWorld(m,"user0");
//        g.tryUpdateActionOnWorld(m,u0);
//
//
//        AttackOrder a=new AttackOrder("A","A",new Troop(1,new TextPlayer("user0")),'A');
//        g.doAttackOnWorld(a,"user0");
//        g.tryUpdateActionOnWorld(a,u0);
//
//
//        BasicOrder d=new BasicOrder(null,null,null,'D');
//        g.doDoneActionFor(u0);
//        g.tryUpdateActionOnWorld(d,u0);
//
//        BasicOrder eorder=new BasicOrder(null,null,null,SWITCH_OUT_ACTION);
//        g.tryUpdateActionOnWorld(eorder,u0);
//        g.updateGameAfterOneTurn();
//
//
//
//        new Thread( ()-> {
//            try{
//                Socket s = hostSocket.accept();
//                Client clientInServer = new Client(s);
//                String strFromClient = (String) clientInServer.recvObject();
//                assertEquals(strFromClient, "Hi, this is client");
//                clientInServer.sendObject("Copy that, this is server");
//                World theWorld = (World) clientInServer.recvObject();
//                assertEquals(true, theWorld != null);
//                clientInServer.close();
//            } catch (IOException e) {
//            }
//        }
//        ).start();
//
//        Client clientSocket = new Client("127.0.0.1", PORT);
//        clientSocket.sendObject("Hi, this is client");
//        String strFromServer = (String) clientSocket.recvObject();
//        assertEquals(strFromServer, "Copy that, this is server");
//        clientSocket.sendObject(w);
//        clientSocket.close();
//    }




//
//
//    @Test
//    public void test_basic(){
//        Game g=new Game(10003,2);
//        assertEquals(g.getGameID(),2);
//        assertEquals(g.getMaxNumUsers(),2);
//
//        User u = new User(10001,"user0","1");
//        User u2 = new User(10002,"user0","1");
//        User u3 = new User(10003,"user0","1");
//        assertEquals(g.isUserInGame(u),false);
//        g.addUser(u);
//        g.addUser(u2);
//        assertEquals(g.addUser(u3),false);
//        assertEquals(g.isUserInGame(u),true);
//        g.switchOutUser(u3);
//        g.switchOutUser(u2);
//        assertEquals(g.isAllPlayersSwitchOut(),false);
//        g.switchInUser(u3);
//        g.switchInUser(u2);
//
//    }
//
//    @Test
//    public void test_MoveOrder(){
//        Game g=new Game(10004,2);
//        g.addUser(new User(0,"user0","1"));
//        g.addUser(new User(1,"user1","1"));
//        g.setUpGame();
//        PlaceOrder pp =new PlaceOrder("A",new Troop(5,new TextPlayer("user0")));
//        g.placeUnitsOnWorld(pp);
//        PlaceOrder pp1 =new PlaceOrder("B",new Troop(5,new TextPlayer("user0")));
//        g.placeUnitsOnWorld(pp1);
//        HashMap<String, Integer> myDict = new HashMap<>();
//        myDict.put("Soldier LV0", 5);
//        Troop dem = new Troop(myDict, new TextPlayer("user0"));
//        MoveOrder m =new MoveOrder("A","B",dem,'M');
//        g.doMoveOnWorld(m, "user0");
//        System.out.println(g.getTheWorld().findTerritory(m.getSrcName()).getInfo() );
//        System.out.println(g.getTheWorld().findTerritory(m.getDesName()).getInfo() );
//    }
//
//
//    @Test
//    public void test_playerInfo(){
//        Game g=new Game(10005,2);
//        g.addUser(new User(0,"user0","1"));
//        g.addUser(new User(1,"user1","1"));
//        g.setUpGame();
//        World theWorld = g.getTheWorld();
//        PlayerInfo pInfo = theWorld.getPlayerInfoByName("user0");
//    }
//
//    @Test
//    public void test_Turn(){
//        Game g=new Game(10006,2);
//        g.addUser(new User(0,"user0","1"));
//        g.addUser(new User(1,"user1","1"));
//        g.setUpGame();
//        assertEquals(1,g.gInfo.theWorld.getTurnNumber());
//        g.updateGameAfterOneTurn();
//        assertEquals(2,g.gInfo.theWorld.getTurnNumber());
//    }
}