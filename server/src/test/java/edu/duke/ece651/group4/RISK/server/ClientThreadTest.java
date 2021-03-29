package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientThreadTest {
    private final static int TIME = 500;
    private final static int PORT = 7777;
    static ServerSocket hostSocket;
    Client theClient;
    World theWorld;
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


    private World createWorld(){
        Player p1 = new TextPlayer("Player0");
        Player p2 = new TextPlayer("p2");
        World world = new World(4);
        world.stationTroop("1",new Troop(5,p1));
        world.stationTroop("2",new Troop(5,p1));
        world.stationTroop("3",new Troop(5,p2));
        world.stationTroop("4",new Troop(5,p2));
        world.addConnection("1","3");
//        world.stationTroop("4",new Troop(5,p2));
//        world.stationTroop("5",new Troop(5,p2));
        return world;
    }

    private ClientThread createAClientThread() throws IOException {
        this.theWorld = createWorld();
        int PlayerID = 0;
        int playerNum = 1;
        HashMap<Integer, List<Territory>> groups=(HashMap<Integer, List<Territory>>) theWorld.divideTerritories(playerNum);
        PlayerState playerState = new PlayerState("Ready");
        HostState hostState = new HostState("ready");
        hostState.updateWarReport("warreport");
        int numOfPlayers = 1;
        String playerName = "Player0";
        CyclicBarrier barrier = new CyclicBarrier(numOfPlayers); ;
        hostState.addOnePlayerState(playerState);
        ClientThread theThread = new ClientThread(this.theWorld.clone(),playerName, PlayerID, barrier, playerState,
                this.theClient, hostState,groups.get(PlayerID));
        return theThread;
    }

    private BasicOrder createBasicOrder(String src, String des, char act, Player p){
        BasicOrder m = new BasicOrder(src,
                des,new Troop(1,p),
                act);
        return m;
    }
    @Test
    public void test_sendObjectsToClient() throws IOException, InterruptedException {
        new Thread(() -> {
            try {
                Socket socket = hostSocket.accept();
                this.theClient = new Client(socket);
            } catch (IOException ignored) {
            }
        }).start();
        Thread.sleep(TIME);
        new Thread(() -> {
            try {
                Client playerClient = new Client("localhost", String.valueOf(PORT));
                World worldRecv = (World) playerClient.recvObject();

                String name = (String) playerClient.recvObject();
                String warReport = (String) playerClient.recvObject();
                List<Territory> g = (List<Territory>)playerClient.recvObject();
                assertEquals(worldRecv, this.theWorld);
                assertEquals(warReport, "warreport");
                assertEquals(name, "Player0");
                assertNotNull(g);
                playerClient.close();
            } catch (IOException | ClassNotFoundException ignored) {
            }
        }).start();
        Thread.sleep(TIME);
        //Test ClientThread send
        ClientThread clientThread = createAClientThread();
        clientThread.sendWorldToClient();
        clientThread.sendPlayerNameToClient();
        clientThread.sendWarReportToClient();
        clientThread.sendInitTerritory();
        assertEquals(clientThread.isPlayerLost(), false);
        assertEquals(clientThread.isGameEnd(), false);
        //Test ClientThread Update the world
        clientThread.updateActionOnWorld(createBasicOrder("1","2",'M', new TextPlayer("Player0")));
        System.out.println("ClientThreadTest : update the move");
        clientThread.updateActionOnWorld(createBasicOrder("1","3",'A', new TextPlayer("Player0")));
        System.out.println("ClientThreadTest : update the attack");
        theClient.close();
    }

    @AfterAll
    public void shutDown() throws IOException {
        hostSocket.close();
        System.out.println("close server");
    }

}
