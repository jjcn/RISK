package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.*;
import edu.duke.ece651.group4.RISK.shared.message.GameMessage;
import edu.duke.ece651.group4.RISK.shared.message.LogMessage;
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
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static edu.duke.ece651.group4.RISK.server.ServerConstant.PLAYER_STATE_SWITCH_OUT;
import static edu.duke.ece651.group4.RISK.shared.Constant.*;
import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientThreadTest {
    private final static int TIME = 500;
    private final static int PORT = 7777;
    static ServerSocket hostSocket;
    static String hostname = "localhost";
    Client theClient;
//    World theWorld;
    @BeforeAll
    public void setUpAll() throws InterruptedException {
        new Thread(() -> {
            try {
                hostSocket = new ServerSocket(PORT);// initialize the server
            } catch (IOException ignored) {
            }
        }).start();
        // pause to give the server some time to setup
        Thread.sleep(TIME);
    }

    public static List<User> createUsers(int num){
        List<User> users = new ArrayList<User>();
        for(int i = 0; i < num; i++){
            users.add(new User(i,"user"+i,"123" ));
        }
        return users;
    }
    private static List<Game> createGames(int num, int maxNumUsers){
        List<Game> games = new ArrayList<Game>();
        for(int i = 0; i< num; i++){
            games.add(new Game(i+10000,maxNumUsers));
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
        assertEquals( null, ct.trySignUp("u2er1", "123"));
        assertEquals(3,ct.users.size());
        assertEquals(1,ct.games.size());
        assertEquals( INVALID_SIGNUP, ct.trySignUp(null, "123"));
        assertEquals( INVALID_SIGNUP, ct.trySignUp("user1", null));
        assertEquals( INVALID_SIGNUP, ct.trySignUp(null, null));
    }

    @Test
    public void test_tryCreateAGame(){
        List<User> users =  createUsers(2);
        List<Game> games = createGames(1, 2);
        ClientThread ct = new ClientThread(games, users,null, new AtomicInteger(0));
        assertEquals(1, ct.games.size());
        assertEquals(null, ct.tryLogIn("user0","123"));
        assertEquals( null, ct.tryCreateAGame(new GameMessage(GAME_CREATE, -1, 2)));
        ct.games.get(1).addUser(users.get(1));
        ct.games.get(1).gInfo.gameState.changAPlayerStateTo(users.get(0), PLAYER_STATE_SWITCH_OUT);
        ct.games.get(1).gInfo.gameState.changAPlayerStateTo(users.get(1), PLAYER_STATE_SWITCH_OUT);
        assertEquals(2, ct.games.size());
        assertEquals(true,ct.gameOnGoing!=null);
        Game g = ct.findGame(10000);
        assertEquals(2,g.getMaxNumUsers());
        assertEquals( INVALID_CREATE, ct.tryCreateAGame(new GameMessage(GAME_CREATE, -1, 6)));
    }


    @Test
    public void test_tryJoinAGame(){
        ClientThread ct = createAClientThread(1, 2);
        assertEquals( null, ct.tryLogIn("user0","123"));
        assertEquals(INVALID_JOIN, ct.tryJoinAGame(new GameMessage(GAME_JOIN, -1, -1)));
        assertEquals(INVALID_JOIN, ct.tryJoinAGame(new GameMessage(GAME_JOIN, 2, -1)));
        assertEquals(null, ct.tryJoinAGame(new GameMessage(GAME_JOIN, 1, -1)));
        assertEquals(INVALID_JOIN, ct.tryJoinAGame(new GameMessage(GAME_JOIN, 1, -1)));
        Game g = ct.findGame(10001);
        g.addUser(new User(1,"user1", "123"));
        Game g0 = ct.findGame(10000);
        g0.addUser(new User(1,"user1", "123"));
        g0.addUser(new User(3,"user3", "123"));
        assertEquals(INVALID_JOIN, ct.tryJoinAGame(new GameMessage(GAME_JOIN, 0, -1)));
        assertEquals(null, ct.tryJoinAGame(new GameMessage(GAME_JOIN, 1, -1)));
    }

    @Test
    public void test_getRoomInfo(){
        List<User> users =  createUsers(2);
        List<Game> games = createGames(3, 2);
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

    @Test
    public void test_findGame(){
        ClientThread ct = createAClientThread(1, 3);
        assertEquals( null, ct.tryLogIn("user0","123"));
        assertEquals(null, ct.findGame(-1));
        Game g = new Game(10000,2);
        assertEquals(g.getGameID(), ct.findGame(10000).getGameID());
        assertEquals(g.getMaxNumUsers(), ct.findGame(10000).getMaxNumUsers());
    }


    private void simulateOneClientCreate(User u, Client theClient){
        Object res = null;
        //logIn
        theClient.sendObject(new LogMessage(LOG_SIGNIN,u.getUsername(),u.getPassword()));
        assertEquals(null,theClient.recvObject());
        //Game
        theClient.sendObject(new GameMessage(GAME_CREATE,-1,2));
        assertEquals(null,theClient.recvObject());

        //Place Units
        World theWorld = (World) theClient.recvObject();
        assertEquals(true, theWorld instanceof World);
        assertEquals(true, theWorld != null);
        List<Order> placeOrders = makePlaceOrders(theWorld,u);
        theClient.sendObject(placeOrders);

        //Start Do Action Phase
        theWorld = (World) theClient.recvObject();
        assertEquals(true, theWorld instanceof World);
        assertEquals(true, theWorld != null);
        BasicOrder bp = new BasicOrder(null,null,null,SWITCH_OUT_ACTION);
        theClient.sendObject(bp);
    }

    private void simulateOneClientJoin(User u, Client theClient){
        Object res = null;
        //logIn
        theClient.sendObject(new LogMessage(LOG_SIGNIN,u.getUsername(),u.getPassword()));
        assertEquals(null,theClient.recvObject());
        //Game
        theClient.sendObject(new GameMessage(GAME_JOIN,10001,2));
        assertEquals(null,theClient.recvObject());

        //Place Units
        World theWorld = (World) theClient.recvObject();
        assertEquals(true, theWorld instanceof World);
        assertEquals(true, theWorld != null);
        List<Order> placeOrders = makePlaceOrders(theWorld,u);
        theClient.sendObject(placeOrders);

        //Start Do Action Phase
        theWorld = (World) theClient.recvObject();
        assertEquals(true, theWorld instanceof World);
        assertEquals(true, theWorld != null);
        BasicOrder bp = new BasicOrder(null,null,null,SWITCH_OUT_ACTION);
        theClient.sendObject(bp);
    }
    private List<Order> makePlaceOrders(World theWorld, User u){
        List<Territory> terrs = theWorld.getTerritoriesOfPlayer(u.getUsername());
        List<Order>  placeOrders = new ArrayList<>();
        int total = 15;
        for(Territory t: terrs){
            PlaceOrder p =new PlaceOrder(t.getName(),new Troop(total,new TextPlayer(u.getUsername())));
            total = 0;
            placeOrders.add(p);
        }
        return placeOrders;
    }
    @Test
    public void test_wholeProcess(){
        List<User> users =  createUsers(2);
        List<Game> games = createGames(1, 2);
        new Thread(() -> {
            try {
                Socket socket = hostSocket.accept();
                this.theClient = new Client(socket);
                ClientThread ct = new ClientThread(games, users,null, new AtomicInteger(0));
                ct.start();
            } catch (IOException ignored) {
            }
        }).start();

        new Thread(() -> {
            try {
                Client theClient = new Client(hostname, PORT);
                simulateOneClientCreate(users.get(0),theClient);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                Client theClient = new Client(hostname, PORT);
//                synchronized (games) {
//                    games.notify();
//                }
                simulateOneClientCreate(users.get(0),theClient);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
//                synchronized (games){
//                    try {
//                        games.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Client theClient = new Client(hostname, PORT);
                simulateOneClientJoin(users.get(1),theClient);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
