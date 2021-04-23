package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.*;
import edu.duke.ece651.group4.RISK.shared.message.GameMessage;
import edu.duke.ece651.group4.RISK.shared.message.LogMessage;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static edu.duke.ece651.group4.RISK.server.GameTest.createGame;
import static edu.duke.ece651.group4.RISK.server.ServerConstant.PLAYER_STATE_SWITCH_OUT;
import static edu.duke.ece651.group4.RISK.shared.Constant.*;
import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientThreadTest {
    private final static int TIME = 1;
    private final static int PORT = 7777;
    static ServerSocket hostSocket;
    static String hostname = "localhost";
    Game g;
    public static Game createAliveGame(int gid, List<User> users){
        Game g = new Game(gid+10000,users.size());
        assertEquals(false, g.isFull());
        for(User u: users){
            g.addUser(u);
        }
        HibernateTool.deleteGameInfo(g.gInfo);
        HibernateTool.addGameInfo(g.gInfo);
//        GameRunner gr = new GameRunner(g);
//        gr.start();
        return g;
    }

    @BeforeAll
    public void setUpAll()  {
        new Thread(() -> {
            try {
                hostSocket = new ServerSocket(PORT);// initialize the server
//                GameInfo gameInfo
//                HibernateTool.addGameInfo(gameInfo);
                List<User> users = createUsers(2);
                g = createAliveGame(1111,users);
                HostApp hostApp = new HostApp(hostSocket);
                hostApp.run();
            } catch (IOException ignored) {
            }
        }).start();
        // pause to give the server some time to setup
    }

    public static List<User> createUsers(int num){
        List<User> users = new ArrayList<User>();
        for(int i = 0; i < num; i++){
            users.add(new User(i+10000,"user"+i,"123" ));
        }
        return users;
    }

    private static List<Game> createGames(int num, int maxNumUsers){
        List<Game> games = new ArrayList<Game>();
        for(int i = 0; i< num; i++){
            Game g = new Game(i+10000,maxNumUsers);
            g.gInfo.gameState.setGameDead();
            games.add(g);
        }
        return games;
    }

    private ClientThread createAClientThread(int numUser, int numGames){
        List<User> users =  createUsers(numUser);
        List<Game> games = createGames(numGames, 2);
        return new ClientThread(games, users,null, new AtomicInteger(0));
    }
    @Test
    public void test_userLogIn(){
        List<User> users =  createUsers(2);
        List<Game> games = createGames(1, 2);
        ClientThread ct = new ClientThread(games, users,null, new AtomicInteger(0));
        assertEquals( null, ct.tryLogIn("user1", "123"));
        assertEquals(true, ct.ownerUser == users.get(1));
        assertEquals(false, ct.ownerUser == users.get(0));
        assertEquals( INVALID_LOGIN, ct.tryLogIn("user1", "1234"));
        assertEquals( INVALID_LOGIN, ct.tryLogIn("u2er1", "123"));
        assertEquals( INVALID_LOGIN, ct.tryLogIn(null, "123"));
        assertEquals( INVALID_LOGIN, ct.tryLogIn("user1", null));
        assertEquals( INVALID_LOGIN, ct.tryLogIn(null, null));
    }

    @Test
    public void test_SignUp(){
        List<User> users =  createUsers(2);
        List<Game> games = createGames(1, 2);
        ClientThread ct = new ClientThread(games, users,null, new AtomicInteger(0));
        assertEquals( INVALID_SIGNUP, ct.trySignUp("user1", "123"));
        assertEquals( INVALID_SIGNUP, ct.trySignUp("user1", "1234"));
//        assertEquals( null, ct.trySignUp("user1", "123"));
        assertEquals(2,ct.users.size());
        assertEquals(1,ct.games.size());
        assertEquals( INVALID_SIGNUP, ct.trySignUp(null, "123"));
        assertEquals( INVALID_SIGNUP, ct.trySignUp("user1", null));
        assertEquals( INVALID_SIGNUP, ct.trySignUp(null, null));
    }

    @Test
    public void test_tryCreateAGame(){
        List<User> users =  createUsers(2);
        List<Game> games = createGames(10000, 2);
        ClientThread ct = new ClientThread(games, users,null, new AtomicInteger(0));
//        assertEquals(1000, ct.games.size());
        assertEquals(null, ct.tryLogIn("user0","123"));
        assertEquals( null, ct.tryCreateAGame(new GameMessage(GAME_CREATE, -1, 2)));
        ct.gameOnGoing.gInfo.gameState.setGameDead();
        HibernateTool.updateGameInfo(ct.gameOnGoing.gInfo);
        ct.games.get(1000).addUser(users.get(1));
        ct.games.get(1000).gInfo.gameState.changAPlayerStateTo(users.get(0), PLAYER_STATE_SWITCH_OUT);
        ct.games.get(1000).gInfo.gameState.changAPlayerStateTo(users.get(1), PLAYER_STATE_SWITCH_OUT);
        assertEquals(true,ct.gameOnGoing!=null);
        Game g = ct.findGame(10000);
        assertEquals(2,g.getMaxNumUsers());
    }


    @Test
    public void test_tryJoinAGame(){
        ClientThread ct = createAClientThread(1, 2);
        assertEquals( null, ct.tryLogIn("user0","123"));
        assertEquals(INVALID_JOIN, ct.tryJoinAGame(new GameMessage(GAME_JOIN, -1, -1)));
        assertEquals(INVALID_JOIN, ct.tryJoinAGame(new GameMessage(GAME_JOIN, 10002, -1)));
        assertEquals(null, ct.tryJoinAGame(new GameMessage(GAME_JOIN, 10000, -1)));
        assertEquals(null, ct.tryJoinAGame(new GameMessage(GAME_JOIN, 10001, -1)));
        Game g = ct.findGame(10001);
        g.addUser(new User(1,"user1", "123"));
        Game g0 = ct.findGame(10000);
        g0.addUser(new User(1,"user1", "123"));
        g0.addUser(new User(3,"user3", "123"));
        assertEquals(null, ct.tryJoinAGame(new GameMessage(GAME_JOIN, 10000, -1)));
        assertEquals(null, ct.tryJoinAGame(new GameMessage(GAME_JOIN, 10001, -1)));
    }

    private List<Game> createAliveGames(int num, int maxNumUsers){
        List<Game> games = new ArrayList<Game>();
        for(int i = 0; i< num; i++){
            Game g = new Game(i+10000,maxNumUsers);
            games.add(g);
        }
        return games;
    }
    @Test
    public void test_getRoomInfo(){
        List<User> users =  createUsers(2);
        List<Game> games = createAliveGames(3, 2);
        ClientThread ct = new ClientThread(games, users,null, new AtomicInteger(0));
        assertEquals( null, ct.tryLogIn("user0","123"));
        assertEquals(3, ct.getAllGameInfo().size());
        games.get(0).gInfo.gameState.setGameDead();
        assertEquals(2, ct.getAllGameInfo().size());
        games.get(1).gInfo.gameState.setGameDead();
        assertEquals(1, ct.getAllGameInfo().size());
        games.get(2).gInfo.gameState.setGameDead();
        assertEquals(0, ct.getAllGameInfo().size());

    }
    public void waitTime(int t){
        try {
            TimeUnit.SECONDS.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


//    @Test
//    public void test_trySetUpUser(){
//        List<User> users =  createUsers(2);
//        List<Game> games = createGames(1, 2);
//        System.out.println("test_trySetUpUser starts");
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//        CountDownLatch countDownLatch2 = new CountDownLatch(1);
//        Thread Thread1 = new Thread(() -> {
//            System.out.println("test_trySetUpUser thread1 enters");
//            try {
//                new Thread(() -> {
//                    try {
//                        System.out.println("test_trySetUpUser thread2 tries to connect");
//                        waitTime(5);
//                        Client theClient = new Client(hostname, PORT);
//                        System.out.println("test_trySetUpUser thread2 connected");
//                        waitTime(5);
//                        LogMessage gm = new LogMessage(LOG_SIGNIN,"user11","1");
//                        LogMessage gm_signup = new LogMessage(LOG_SIGNUP,"user11","1");
//                        theClient.sendObject(gm_signup);
//                        assertEquals(null, theClient.recvObject()) ;
//                        theClient.sendObject(gm);
//                        assertEquals(null, theClient.recvObject()) ;
//                        System.out.println("test_trySetUpUser thread2 finishes");
//                    } catch (Throwable e) {
//                        e.printStackTrace();
//                    }
//                }).start();
//
//                Socket socket = hostSocket.accept();
//                Client Client = new Client(socket);
//                ClientThread ct = new ClientThread(games, users,Client, new AtomicInteger(0));
//                System.out.println("test_trySetUpUser thread1 has a client");
//                synchronized(lock0){
//                    lock0.notifyAll();
//                }
//                ct.trySetUpUser();
//                System.out.println("test_trySetUpUser thread1 finishes");
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//        });
//        Thread1.start();
//    }
//    @Test
//    public void test_findGame(){
//        ClientThread ct = createAClientThread(1, 3);
//        assertEquals( null, ct.tryLogIn("user0","123"));
//        assertEquals(null, ct.findGame(-1));
//        Game g = new Game(10000,2);
//        assertEquals(g.getGameID(), ct.findGame(10000).getGameID());
//        assertEquals(g.getMaxNumUsers(), ct.findGame(10000).getMaxNumUsers());
//    }

//    private void simulateAClient(String username, String terr1, String terr2) throws IOException {
//        /*
//         * test Log
//         * */
//        Client client = new Client(hostname, PORT);
//        LogMessage gm = new LogMessage(LOG_SIGNIN,username,"1");
//        LogMessage gm_signup = new LogMessage(LOG_SIGNUP,username,"1");
//        client.sendObject(gm_signup);
//        client.recvObject();
//        client.sendObject(gm);
//        client.recvObject();
//
//        /*
//         * join Game
//         * */
//        GameMessage gc= new GameMessage(GAME_CREATE,-1,2);
//        GameMessage gj=  new GameMessage(GAME_JOIN,11111,2);
//        client.sendObject(gj);
//        client.recvObject();
//
//        /*
//         * Place Units
//         * */
//        client.recvObject(); // receive the world
//        List<Order> pOrders = new ArrayList<>();
//        pOrders.add(new PlaceOrder(terr1,new Troop(5,new TextPlayer(username))));
//        pOrders.add(new PlaceOrder(terr2,new Troop(10,new TextPlayer(username))));
//        client.sendObject(pOrders);
//    }

    @Test
    public void testAClient() throws IOException {
        new Thread(()->{
            try {
                testAClient2();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        /*
         * test Log
         * */
        Client client = new Client(hostname, PORT);
        LogMessage gm = new LogMessage(LOG_SIGNIN,"user0","1");
        LogMessage gm_signup = new LogMessage(LOG_SIGNUP,"user0","1");
        client.sendObject(gm_signup);
        client.recvObject();
        client.sendObject(gm);
        client.recvObject();

        /*
        * join Game
        * */
        GameMessage gc= new GameMessage(GAME_CREATE,-1,2);
        GameMessage gj=  new GameMessage(GAME_JOIN,11111,2);
        client.sendObject(gj);
        client.recvObject();

        /*
        * Place Units
        * */
        client.recvObject(); // receive the world
        List<Order> pOrders = new ArrayList<>();
        pOrders.add(new PlaceOrder("A",new Troop(5,new TextPlayer("user0"))));
        pOrders.add(new PlaceOrder("B",new Troop(10,new TextPlayer("user0"))));
        client.sendObject(pOrders);

        /*
         * Action
         * */
        client.recvObject();
        MoveOrder m= new MoveOrder("A","B",new Troop(1,new TextPlayer("user0")),'M');
        AttackOrder a =new AttackOrder("A","C",new Troop(1,new TextPlayer("user0")),'A');
        UpgradeTechOrder ut= new UpgradeTechOrder(1);
//        UpgradeTroopOrder utroop=new UpgradeTroopOrder("A",0,1,1);
//        AllianceOrder oA = new AllianceOrder("user0","user1");
//        TransferTroopOrder oTtroop = new TransferTroopOrder("A", Constant.SOLDIER, Constant.KNIGHT, 0, 1);
        BasicOrder oDone=new BasicOrder(null,null,null,'D');
        client.sendObject(m);
        client.sendObject(a);
        client.sendObject(ut);
        client.sendObject(oDone);

        client.recvObject();
        BasicOrder o_switch=new BasicOrder(null,null,null,SWITCH_OUT_ACTION);
        client.sendObject(o_switch);

    }


    public void testAClient2() throws IOException {
        /*
         * test Log
         * */
        Client client = new Client(hostname, PORT);
        LogMessage gm = new LogMessage(LOG_SIGNIN,"user1","1");
        LogMessage gm_signup = new LogMessage(LOG_SIGNUP,"user1","1");
        client.sendObject(gm_signup);
        client.recvObject();
        client.sendObject(gm);
        client.recvObject();

        /*
         * join Game
         * */
        GameMessage gc= new GameMessage(GAME_CREATE,-1,2);
        GameMessage gj=  new GameMessage(GAME_JOIN,11111,2);
        client.sendObject(gj);
        client.recvObject();

        /*
         * Place Units
         * */
        client.recvObject(); // receive the world
        List<Order> pOrders = new ArrayList<>();
        pOrders.add(new PlaceOrder("C",new Troop(5,new TextPlayer("user1"))));
        pOrders.add(new PlaceOrder("D",new Troop(10,new TextPlayer("user1"))));
        client.sendObject(pOrders);


        /*
        * Action
        * */
        client.recvObject();
        BasicOrder oDone=new BasicOrder(null,null,null,'D');
        client.sendObject(oDone);
        client.recvObject();


        BasicOrder o_switch=new BasicOrder(null,null,null,SWITCH_OUT_ACTION);
        client.sendObject(o_switch);
    }
//    private void simulateOneClientCreate(User u, Client theClient){
//        Object res = null;
//        //logIn
//        theClient.sendObject(new LogMessage(LOG_SIGNIN,u.getUsername(),u.getPassword()));
//        assertEquals(null,theClient.recvObject());
//        //Game
//        theClient.sendObject(new GameMessage(GAME_CREATE,-1,2));
//        assertEquals(null,theClient.recvObject());
//
//        //Place Units
//        World theWorld = (World) theClient.recvObject();
//        assertEquals(true, theWorld instanceof World);
//        assertEquals(true, theWorld != null);
//        List<Order> placeOrders = makePlaceOrders(theWorld,u);
//        theClient.sendObject(placeOrders);
//
//        //Start Do Action Phase
//        theWorld = (World) theClient.recvObject();
//        assertEquals(true, theWorld instanceof World);
//        assertEquals(true, theWorld != null);
//        BasicOrder bp = new BasicOrder(null,null,null,SWITCH_OUT_ACTION);
//        theClient.sendObject(bp);
//    }
//
//    private void simulateOneClientJoin(User u, Client theClient){
//        Object res = null;
//        //logIn
//        theClient.sendObject(new LogMessage(LOG_SIGNIN,u.getUsername(),u.getPassword()));
//        assertEquals(null,theClient.recvObject());
//        //Game
//        theClient.sendObject(new GameMessage(GAME_JOIN,10001,2));
//        assertEquals(null,theClient.recvObject());
//
//        //Place Units
//        World theWorld = (World) theClient.recvObject();
//        assertEquals(true, theWorld instanceof World);
//        assertEquals(true, theWorld != null);
//        List<Order> placeOrders = makePlaceOrders(theWorld,u);
//        theClient.sendObject(placeOrders);
//
//        //Start Do Action Phase
//        theWorld = (World) theClient.recvObject();
//        assertEquals(true, theWorld instanceof World);
//        assertEquals(true, theWorld != null);
//        BasicOrder bp = new BasicOrder(null,null,null,SWITCH_OUT_ACTION);
//        theClient.sendObject(bp);
//    }
//    private List<Order> makePlaceOrders(World theWorld, User u){
//        List<Territory> terrs = theWorld.getTerritoriesOfPlayer(u.getUsername());
//        List<Order>  placeOrders = new ArrayList<>();
//        int total = 15;
//        for(Territory t: terrs){
//            PlaceOrder p =new PlaceOrder(t.getName(),new Troop(total,new TextPlayer(u.getUsername())));
//            total = 0;
//            placeOrders.add(p);
//        }
//        return placeOrders;
//    }
//
//    @Test
//    public void test_wholeProcess(){
//        List<User> users =  createUsers(2);
//        List<Game> games = createGames(1, 2);
//        new Thread(() -> {
//            try {
//                Socket socket = hostSocket.accept();
//                this.theClient = new Client(socket);
//                ClientThread ct = new ClientThread(games, users,null, new AtomicInteger(0));
//                ct.start();
//            } catch (IOException ignored) {
//            }
//        }).start();
//
//        new Thread(() -> {
//            try {
//                Client theClient = new Client(hostname, PORT);
//                simulateOneClientCreate(users.get(0),theClient);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
//
//        new Thread(() -> {
//            try {
//                Client theClient = new Client(hostname, PORT);
//                simulateOneClientCreate(users.get(0),theClient);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
//
//        new Thread(() -> {
//            try {
//                try {
//                    TimeUnit.SECONDS.sleep(5);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Client theClient = new Client(hostname, PORT);
//                simulateOneClientJoin(users.get(1),theClient);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }

}
