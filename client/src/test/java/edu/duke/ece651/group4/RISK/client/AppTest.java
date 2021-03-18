/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package edu.duke.ece651.group4.RISK.client;

import edu.duke.ece651.group4.RISK.shared.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PlayerAppTest {
    private static final int SLEEP_TIME = 500;
    private static final int PORT = 9999;
    static ServerSocket server;

    @BeforeAll
    static void beforeAll() throws InterruptedException {
        // initialize the server
        new Thread(() -> {
            try {
                server = new ServerSocket(PORT);
                // this should throw an IO exception
            } catch (IOException ignored) {
            }
        }).start();
        // pause to give the server some time to setup
        Thread.sleep(SLEEP_TIME);
    }

    @Test
    void appHasAGreeting() {

    }

    @Test
    public void testPlace() throws IOException, ClassNotFoundException, InterruptedException {

        new Thread(() -> {
            try {
                Socket socket = server.accept();
                Client theClient = new Client(socket);
                World testWorld = simpleWorld();
                List<Territory> list = new ArrayList<>();
                list.add(testWorld.findTerritory("terr1"));
                list.add(testWorld.findTerritory("terr2"));
                list.add(testWorld.findTerritory("terr3"));
                theClient.sendObject(list);
                for (int i = 0; i < 3; i++) {
                    PlaceOrder newOrder = (PlaceOrder) theClient.recvObject();
                    testWorld.stationTroop(newOrder.getDesName(), newOrder.getActTroop());
                }
                theClient.sendObject(testWorld.clone());
            } catch (Exception e) {
            }
        }).start();


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(bytes, true);
        String inputData = "5\n5\n6\n5\n5\n5\n";
        PlayerApp testApp = simpleApp(inputData, output);
        testApp.doPlacementPhase();
        assertEquals(testApp.getTheWorld().findTerritory("terr1").checkPopulation(), 5);
        assertEquals(testApp.getTheWorld().findTerritory("terr2").checkPopulation(), 5);
        assertEquals(testApp.getTheWorld().findTerritory("terr3").checkPopulation(), 5);
    }

    private World simpleWorld() {
        World world = new World();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        Player p1 = new TextPlayer(System.out, input, "p1");
        Player p2 = new TextPlayer(System.out, input, "p2");
        ArrayList<Territory> terrs = new ArrayList<Territory>();
        terrs.add(new Territory("terr1", p1, 1, new Random(0)));
        terrs.add(new Territory("terr2", p1, 1, new Random(0)));
        terrs.add(new Territory("terr3", p1, 1, new Random(0)));
        terrs.add(new Territory("terr4", p2, 1, new Random(0)));
        for (Territory terr : terrs) {
            world.addTerritory(terr);
        }
        world.addConnection("terr1", "terr2");
        world.addConnection("terr2", "terr3");
        world.addConnection("terr3", "terr4");
        return world;
    }

    private PlayerApp simpleApp(String data, PrintStream output) throws IOException {
        Client testClient = new Client("localhost", "9999");
        World testWorld = simpleWorld();
        Random rnd = new Random(0);
        String inputData = data;
        BufferedReader input = new BufferedReader(new StringReader(inputData));
        PlayerApp testApp = new PlayerApp(testClient, "p1", output, input, testWorld, 15, rnd, true);
        return testApp;
    }

    @Test
    public void testAction() throws IOException, ClassNotFoundException, InterruptedException {
        new Thread(() -> {
            try {
                Socket socket = server.accept();
                Client theClient = new Client(socket);
                World testWorld = simpleWorld();
                BasicOrder newOrder1 = (BasicOrder ) theClient.recvObject();
                testWorld.moveTroop(newOrder1);
                BasicOrder newOrder2 = (BasicOrder ) theClient.recvObject();
                testWorld.attackATerritory(newOrder2);
                BasicOrder newOrder3 = (BasicOrder ) theClient.recvObject();
                String message=testWorld.doAllBattles();
                theClient.sendObject(testWorld.clone());
                theClient.sendObject(new String(message));
            } catch (Exception e) {
            }
        }).start();

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            PrintStream output = new PrintStream(bytes, true);
            String inputData = "M\nterr1\nterr3\n5\nM\nterr1\nterr2\n1\nA\nterr3\nterr4\n1\nD\n";
            PlayerApp testApp = null;
            try {
                testApp = simpleApp(inputData, output);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                testApp.doActionPhase();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        assertEquals(testApp.getTheWorld().findTerritory("terr1").checkPopulation(), 0);
        assertEquals(testApp.getTheWorld().findTerritory("terr2").checkPopulation(), 2);
        assertEquals(testApp.getTheWorld().findTerritory("terr3").checkPopulation(), 0);
        assertEquals(testApp.getTheWorld().findTerritory("terr4").checkPopulation(), 1);
    }

    @Test
    public void testRun() throws IOException, ClassNotFoundException, InterruptedException {
        new Thread(() -> {
            try {
                Socket socket = server.accept();
                Client theClient = new Client(socket);
                World testWorld = simpleWorld();
                BasicOrder newOrder1 = (BasicOrder ) theClient.recvObject();
                testWorld.moveTroop(newOrder1);
                BasicOrder newOrder2 = (BasicOrder ) theClient.recvObject();
                testWorld.attackATerritory(newOrder2);
                BasicOrder newOrder3 = (BasicOrder ) theClient.recvObject();
                String message=testWorld.doAllBattles();
                theClient.sendObject(testWorld.clone());
                theClient.sendObject(new String(message));
            } catch (Exception e) {
            }
        }).start();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(bytes, true);
        String inputData = "M\nterr1\nterr3\n5\nM\nterr1\nterr2\n1\nA\nterr3\nterr4\n1\nD\n";
        PlayerApp testApp = null;
        try {
            testApp = simpleApp(inputData, output);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            testApp.runGame();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        assertEquals(testApp.getTheWorld().findTerritory("terr1").checkPopulation(), 0);
        assertEquals(testApp.getTheWorld().findTerritory("terr2").checkPopulation(), 2);
        assertEquals(testApp.getTheWorld().findTerritory("terr3").checkPopulation(), 0);
        assertEquals(testApp.getTheWorld().findTerritory("terr4").checkPopulation(), 1);
        assertEquals(testApp.getTheWorld().checkLost("p2"),true);
        assertEquals(testApp.getTheWorld().isGameEnd(),true);
        assertEquals(testApp.getTheWorld().getWinner(),"p1");
    }


}



