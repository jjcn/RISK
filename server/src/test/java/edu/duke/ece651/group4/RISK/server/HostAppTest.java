package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.ServerSocket;
import java.util.HashSet;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class HostAppTest {

    private final static int TIME = 500;
    private final static int PORT = 5555;
    static ServerSocket hostSocket;


    @BeforeAll
    static void setUpAll() throws InterruptedException {
        new Thread(() -> {
            try {
                hostSocket = new ServerSocket(PORT);// initialize the server
                HostApp hostApp = new HostApp(hostSocket,2);
                String str = "s\n1\n";
                InputStream input = new ByteArrayInputStream(str.getBytes("UTF-8"));
                assertNotNull(input);
                System.setIn(input);
                assertEquals(false, hostApp.isNumeric(null));
                hostApp.run();
            } catch (IOException ignored) {
            }
        }).start();
        // pause to give the server some time to setup
        Thread.sleep(TIME);
    }

//    private void test_setupWorld() throws UnsupportedEncodingException {
//        this.hostApp = new HostApp(hostSocket,1);
//        String str = "q\ns\n2\n";
//        InputStream input = new ByteArrayInputStream(str.getBytes("UTF-8"));
//        assertNotNull(input);
//        System.setIn(input);
//        this.hostApp.setUpWorld();
//    }


    private HashSet<PlaceOrder> createOnePlaceOrders(){
        HashSet<PlaceOrder> orders = new HashSet<>();
        Troop troop = new Troop(15,new TextPlayer("Player0"), new Random());
        orders.add(new PlaceOrder("1", troop));
        orders.add(new PlaceOrder("2", troop));
        return orders;
    }

    private BasicOrder createOneDoneOrder(){
        return  new BasicOrder(null, null, null, 'D');
    }

    private void test_setUpClient1() throws InterruptedException {
        new Thread(() -> {
            try {
                Client c1 = new Client("localhost", String.valueOf(PORT));
                c1.recvObject();
                c1.recvObject();
                c1.recvObject();
                HashSet<PlaceOrder> orders = createOnePlaceOrders();
                for(PlaceOrder p: orders){
                    c1.sendObject(p);
                }
                 //setupUnits
                System.out.println("Player: Send PlaceOrder");
                c1.recvObject();
                System.out.println("Player: receive World");
                c1.sendObject(createOneDoneOrder());
                c1.recvObject();
                c1.recvObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(TIME);
    }


    @Test
    public void test_hostAppInOrder() throws InterruptedException {

        test_setUpClient1();

    }

}
