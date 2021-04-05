package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.*;
import edu.duke.ece651.group4.RISK.shared.message.GameMessage;
import edu.duke.ece651.group4.RISK.shared.message.LogMessage;
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
import java.util.concurrent.atomic.AtomicInteger;

import static edu.duke.ece651.group4.RISK.shared.Constant.*;
import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientThreadTest {
    private final static int TIME = 500;
    private final static int PORT = 7777;
    static ServerSocket hostSocket;
    Client theClient;
    World theWorld;
//    @BeforeAll
//    public void setUpAll() throws InterruptedException {
//        new Thread(() -> {
//            try {
//                hostSocket = new ServerSocket(PORT);// initialize the server
//            } catch (IOException ignored) {
//            }
//        }).start();
//        // pause to give the server some time to setup
//        Thread.sleep(TIME);
//    }
//
//
//    private World createWorld(){
//        Player p1 = new TextPlayer("Player0");
//        Player p2 = new TextPlayer("p2");
//        World world = new World(4);
//        world.stationTroop("1",new Troop(5,p1));
//        world.stationTroop("2",new Troop(5,p1));
//        world.stationTroop("3",new Troop(5,p2));
//        world.stationTroop("4",new Troop(5,p2));
//        world.addConnection("1","3");
////        world.stationTroop("4",new Troop(5,p2));
////        world.stationTroop("5",new Troop(5,p2));
//        return world;
//    }
//
//    private ClientThread createAClientThread() throws IOException {
//        this.theWorld = createWorld();
//        int PlayerID = 0;
//        int playerNum = 1;
//        HashMap<Integer, List<Territory>> groups=(HashMap<Integer, List<Territory>>) theWorld.divideTerritories(playerNum);
//        PlayerState playerState = new PlayerState("Ready");
//        HostState hostState = new HostState("ready");
//        hostState.updateWarReport("warreport");
//        int numOfPlayers = 1;
//        String playerName = "Player0";
//        CyclicBarrier barrier = new CyclicBarrier(numOfPlayers); ;
//        hostState.addOnePlayerState(playerState);
//        ClientThread theThread = new ClientThread(this.theWorld.clone(),playerName, PlayerID, barrier, playerState,
//                this.theClient, hostState,groups.get(PlayerID));
//        return theThread;
//    }
//
//    private BasicOrder createBasicOrder(String src, String des, char act, Player p){
//        BasicOrder m = new BasicOrder(src,
//                des,new Troop(1,p),
//                act);
//        return m;
//    }
//    @Test
//    public void test_sendObjectsToClient() throws IOException, InterruptedException {
//        new Thread(() -> {
//            try {
//                Socket socket = hostSocket.accept();
//                this.theClient = new Client(socket);
//            } catch (IOException ignored) {
//            }
//        }).start();
//        Thread.sleep(TIME);
//        new Thread(() -> {
//            try {
//                Client playerClient = new Client("localhost", String.valueOf(PORT));
//                World worldRecv = (World) playerClient.recvObject();
//
//                String name = (String) playerClient.recvObject();
//                String warReport = (String) playerClient.recvObject();
//                List<Territory> g = (List<Territory>)playerClient.recvObject();
//                assertEquals(worldRecv, this.theWorld);
//                assertEquals(warReport, "warreport");
//                assertEquals(name, "Player0");
//                assertNotNull(g);
//                playerClient.close();
//            } catch (IOException | ClassNotFoundException ignored) {
//            }
//        }).start();
//        Thread.sleep(TIME);
//        //Test ClientThread send
//        ClientThread clientThread = createAClientThread();
//        clientThread.sendWorldToClient();
//        clientThread.sendPlayerNameToClient();
//        clientThread.sendWarReportToClient();
//        clientThread.sendInitTerritory();
//        assertEquals(clientThread.isPlayerLost(), false);
//        assertEquals(clientThread.isGameEnd(), false);
//        //Test ClientThread Update the world
//        clientThread.updateActionOnWorld(createBasicOrder("1","2",'M', new TextPlayer("Player0")));
//        System.out.println("ClientThreadTest : update the move");
//        clientThread.updateActionOnWorld(createBasicOrder("1","3",'A', new TextPlayer("Player0")));
//        System.out.println("ClientThreadTest : update the attack");
//        theClient.close();
//    }
//
//    @AfterAll
//    public void shutDown() throws IOException {
//        hostSocket.close();
//        System.out.println("close server");
//    }

//    @Test
//    public void test_connection() throws IOException {
//        Client clientSocket = new Client("localhost",SOCKET_PORT); //vcm-18527.vm.duke.edu// new Client("",9999); //new Client("localhost",SOCKET_PORT);
//        String name = "wx";
//        String pwd = "123";
//        clientSocket.sendObject(new LogMessage(LOG_SIGNUP, name,pwd));
//        System.out.println("send signup");
//        String res = (String) clientSocket.recvObject();
//        System.out.println("rec feedback");
//        assertEquals(null,res);
//
////        clientSocket.sendObject(new LogMessage(LOG_SIGNUP, name,pwd));
////        System.out.println("send signup");
////        res = (String) clientSocket.recvObject();
////        System.out.println("rec feedback");
////        assertEquals(null,res);
//
//        clientSocket.sendObject(new LogMessage(LOG_SIGNIN, name,pwd));
//        System.out.println("send login");
//        res = (String) clientSocket.recvObject();
//        System.out.println("rec feedback");
//        assertEquals(null,res);
//
//    }

    private HashSet<User> createUsers(int num){
        HashSet<User> users = new HashSet<User>();
        for(int i = 0; i < num; i++){
            users.add(new User(i,"user"+i,"123" ));
        }
        return users;
    }
    private HashSet<Game> createGames(int num, int maxNumUsers){
        HashSet<Game> games = new HashSet<Game>();
        for(int i = 0; i< num; i++){
            games.add(new Game(i,maxNumUsers));
        }
        return games;
    }
    private ClientThread createAClientThread(int numUser, int numGames){
        HashSet<User> users =  createUsers(numUser);
        HashSet<Game> games = createGames(numGames, 2);
        return new ClientThread(games, users,null, new AtomicInteger(0));
    }
    @Test
    public void test_userLogIn(){
        ClientThread ct = createAClientThread(2, 1);
        assertEquals( null, ct.tryLogIn("user1", "123"));
        assertEquals( INVALID_LOGIN, ct.tryLogIn("user1", "1234"));
        assertEquals( INVALID_LOGIN, ct.tryLogIn("u2er1", "123"));
        assertEquals( INVALID_LOGIN, ct.tryLogIn(null, "123"));
        assertEquals( INVALID_LOGIN, ct.tryLogIn("user1", null));
        assertEquals( INVALID_LOGIN, ct.tryLogIn(null, null));
    }
    @Test
    public void test_SignUp(){
        ClientThread ct = createAClientThread(2, 1);
        assertEquals( INVALID_SIGNUP, ct.trySignUp("user1", "123"));
        assertEquals( INVALID_SIGNUP, ct.trySignUp("user1", "1234"));
        assertEquals( null, ct.trySignUp("u2er1", "123"));
        assertEquals( INVALID_SIGNUP, ct.trySignUp(null, "123"));
        assertEquals( INVALID_SIGNUP, ct.trySignUp("user1", null));
        assertEquals( INVALID_SIGNUP, ct.trySignUp(null, null));
    }


    @Test
    public void test_tryCreateAGame(){
        ClientThread ct = createAClientThread(1, 0);
        ct.trySignUp("s","1");
        ct.tryLogIn("s","1");
        assertEquals( null, ct.tryCreateAGame(new GameMessage(GAME_CREATE, -1, 4)));
        assertEquals(1, ct.games.size());
        assertEquals(true,ct.gameOnGoing!=null);
        Game g = ct.findGame(0);
        assertEquals(4,g.getMaxNumUsers());
        assertEquals( INVALID_CREATE, ct.tryCreateAGame(new GameMessage(GAME_CREATE, -1, 6)));
        assertEquals( INVALID_CREATE, ct.tryCreateAGame(new GameMessage(GAME_CREATE, -1, 1)));
        assertEquals( null, ct.tryCreateAGame(new GameMessage(GAME_CREATE, -1, 5)));
        g = ct.findGame(1);
        assertEquals(5,g.getMaxNumUsers());
    }


    @Test
    public void test_tryJoinAGame(){
        ClientThread ct = createAClientThread(1, 2);
        assertEquals( null, ct.tryLogIn("user0","123"));
        assertEquals(INVALID_JOIN, ct.tryJoinAGame(new GameMessage(GAME_JOIN, -1, -1)));
        assertEquals(INVALID_JOIN, ct.tryJoinAGame(new GameMessage(GAME_JOIN, 2, -1)));
        assertEquals(null, ct.tryJoinAGame(new GameMessage(GAME_JOIN, 1, -1)));
        assertEquals(INVALID_JOIN, ct.tryJoinAGame(new GameMessage(GAME_JOIN, 1, -1)));
        Game g = ct.findGame(1);
        g.addUser(new User(1,"user1", "123"));
        Game g0 = ct.findGame(0);
        g0.addUser(new User(1,"user1", "123"));
        g0.addUser(new User(3,"user3", "123"));
        assertEquals(INVALID_JOIN, ct.tryJoinAGame(new GameMessage(GAME_JOIN, 0, -1)));
        assertEquals(null, ct.tryJoinAGame(new GameMessage(GAME_JOIN, 1, -1)));
    }

    @Test
    public void test_getRoomInfo(){
        ClientThread ct = createAClientThread(1, 3);
        ArrayList<RoomInfo> roomsInfo = ct.getAllGameInfo();
        assertEquals(3, roomsInfo.size());
        ct = createAClientThread(1, 2);
        roomsInfo = ct.getAllGameInfo();
        assertEquals(2, roomsInfo.size());
    }

    @Test
    public void test_findGame(){
        ClientThread ct = createAClientThread(1, 3);
        assertEquals(null, ct.findGame(-1));
        Game g = new Game(0,2);
        assertEquals(g.getGameID(), ct.findGame(0).getGameID());
        assertEquals(g.getMaxNumUsers(), ct.findGame(0).getMaxNumUsers());
    }
}
