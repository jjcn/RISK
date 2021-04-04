/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.Client;
import edu.duke.ece651.group4.RISK.shared.Territory;
import edu.duke.ece651.group4.RISK.shared.World;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import static edu.duke.ece651.group4.RISK.shared.Constant.SOCKET_PORT;


public class HostApp implements Runnable {
    ServerSocket hostSocket;
    HashSet<Game> games;
    HashSet<User> users;
    AtomicInteger globalID;
    public HostApp(ServerSocket s){
        this.hostSocket = s;
        games = new HashSet<Game>();
        users = new HashSet<User>();
        this.globalID = new AtomicInteger(0);
    }



    /*
     * This setup connection between server and clients
     * it will create a ClientThread to deal with all stuff
     * including user logIn and Game Playing with this specific client.
     * This thread will exist if someone open a PlayerApp
     * and will close if this PlayerApp close. THe main reason we adopt this
     * design pattern is to save the resource of threads.
     *  */

    public void acceptConnection(){
            while(true) {
                try {
                    System.out.println("Start to listen to clients");
                    Socket s = hostSocket.accept();
                    Client theClient = new Client(s);
                    ClientThread theThread = new ClientThread(games, users,theClient,globalID);
                    System.out.println("Get one Client");
                    theThread.start();
                }catch(IOException e){
                    System.out.println("HostApp: Issue with acceptConnection.");
                }
            }

    }

    public void run() {
        acceptConnection();
    }

    public static void main(String[] args) throws IOException {
        ServerSocket hostSocket = new ServerSocket(SOCKET_PORT);
        HostApp hostApp = new HostApp(hostSocket);
        System.out.println("Server starts to run");
        hostApp.run();
    }
}