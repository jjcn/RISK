package edu.duke.ece651.group4.RISK.shared;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import static edu.duke.ece651.group4.RISK.shared.Constant.SOCKET_HOSTNAME;
import static edu.duke.ece651.group4.RISK.shared.Constant.SOCKET_PORT;
import static org.junit.jupiter.api.Assertions.*;

class ClientTest {
    private final static int PORT = 1107;
    static ServerSocket hostSocket;
    static HashSet<String> testSet;
//    @BeforeAll
//    static void setUpAll() throws IOException {
//        hostSocket = new ServerSocket(PORT);
//    }
//
//    @Test
//    public void test_accept() throws IOException {
//        new Thread(() -> {
//            try {
//                Socket s = hostSocket.accept();
//                assertNotNull(s);
//                Client clientSocket = new Client(s);
//                clientSocket.close();
//            } catch (IOException ignored) {
//            }
//        }).start();
//        new Client("localhost", String.valueOf(PORT));
//    }
//
//    @Test
//    public void testSendAndRecvStringObject() throws IOException, ClassNotFoundException {
//        new Thread( ()-> {
//            try{
//                Socket s = hostSocket.accept();
//                Client clientInServer = new Client(s);
//                String strFromClient = (String) clientInServer.recvObject();
//                assertEquals(strFromClient, "Hi, this is client");
//                clientInServer.sendObject("Copy that, this is server");
//                clientInServer.close();
//            } catch (IOException | ClassNotFoundException e) {
//            }
//        }
//        ).start();
//        Client clientSocket = new Client("127.0.0.1", String.valueOf(PORT));
//        clientSocket.sendObject("Hi, this is client");
//        String strFromServer = (String) clientSocket.recvObject();
//        assertEquals(strFromServer, "Copy that, this is server");
//        clientSocket.close();
//    }
//    @Test
//    public void test_isClose() throws IOException {
//        new Thread( ()-> {
//            try{
//                Socket s = hostSocket.accept();
//                Client clientInServer = new Client(s);
//                clientInServer.close();
//            } catch (IOException e) {
//            }
//        }
//        ).start();
//        Client clientSocket = new Client("localhost", String.valueOf(PORT));
//        clientSocket.close();
//    }
//
//    @Test
//    public void test_sendAndRecvTerritoryObject() throws IOException, ClassNotFoundException {
//        new Thread( ()-> {
//            try{
//                Socket s = hostSocket.accept();
//                Client clientInServer = new Client(s);
//                Territory terr = (Territory) clientInServer.recvObject();
//                clientInServer.sendObject(terr);
//                clientInServer.close();
//            } catch (IOException | ClassNotFoundException e) {
//            }
//        }
//        ).start();
//        Client clientSocket = new Client("127.0.0.1", String.valueOf(PORT));
//        Territory terr = new Territory("PlayerA");
//        terr.addUnit(5);
//        clientSocket.sendObject(terr);
//        Territory terrRecv = (Territory) clientSocket.recvObject();
//        assertEquals(terr, terrRecv);
//        assertEquals(terr.getName(), terrRecv.getName());
//        assertEquals(terr.checkPopulation(), terrRecv.checkPopulation());
//        clientSocket.close();
//    }
//
//    @Test
//    public void test_sendAndRecvWorldOBject()throws IOException, ClassNotFoundException{
//
//        Player p1 = new TextPlayer("p1");
//        Player p2 = new TextPlayer("p2");
//        World world = new World(6);
//        world.stationTroop("6",new Troop(5,p1));
//        world.stationTroop("1",new Troop(5,p1));
//        world.stationTroop("2",new Troop(5,p1));
//        world.stationTroop("3",new Troop(5,p2));
//        world.stationTroop("4",new Troop(5,p2));
//        world.stationTroop("5",new Troop(5,p2));
//        System.out.println(new WorldTextView(world).displayWorld(world));
//
//        new Thread( ()-> {
//            try{
//                Socket s = hostSocket.accept();
//                Client clientInServer = new Client(s);
//                clientInServer.sendObject(world);
//                clientInServer.close();
//            } catch (IOException e) {
//            }
//        }
//        ).start();
//
//        Client clientSocket = new Client("127.0.0.1", String.valueOf(PORT));
//        World worldRecv = (World) clientSocket.recvObject();
//        List<Territory> groups = worldRecv.getAllTerritories();
//        System.out.println(new WorldTextView(worldRecv).displayWorld(worldRecv));
//        for(Territory t : groups){
//            System.out.println(t.getOwner().getName());
//            System.out.println(t.checkPopulation());
//        }
//        clientSocket.close();
//    }
//
//    @Test
//    public void test_connection() throws IOException {
//
//        Client clientSocket = new Client("vcm-19654.vm.duke.edu",SOCKET_PORT); //new Client("localhost",9999); //new Client("localhost",SOCKET_PORT);
//        while(true){}
//    }


//
//    @Test
//    public void test_thread(){
//        testSet = new HashSet<>();
//        CountDownLatch latch = new CountDownLatch(2);
//        new Thread( ()-> {
//            testSet.add("1");
//            latch.countDown();
//        }
//        ).start();
//        new Thread( ()-> {
//            testSet.add("2");
//            latch.countDown();
//        }
//        ).start();
//
//        try {
//            latch.await();
//            assertEquals(2, this.testSet.size());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

}
