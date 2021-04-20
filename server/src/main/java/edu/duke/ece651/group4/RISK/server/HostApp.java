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
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static edu.duke.ece651.group4.RISK.shared.Constant.SOCKET_PORT;


public class HostApp implements Runnable {
    ServerSocket hostSocket;
    List<Game> games;
    List<User> users;
    AtomicInteger globalID;
    final PrintStream out;
    boolean test_mode;
    public HostApp(ServerSocket s, boolean test_mode){
        this.hostSocket = s;
        games = new ArrayList<Game>();
        users = new ArrayList<User>();
        this.globalID = new AtomicInteger(0);
        this.out = System.out;
        this.test_mode = test_mode;
    }
    public HostApp(ServerSocket s){
        this(s,false);
    }

    protected void addTestUsers(){
        users.add(new User(0,"xs","1"));
        users.add(new User(1,"ws","1"));
        users.add(new User(2,"jin","1"));
        users.add(new User(3,"sj","1"));
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
        addTestUsers();
        while(true) {
            try {
                Socket s = hostSocket.accept();
                Client theClient = new Client(s);
                ClientThread theThread = new ClientThread(games, users,theClient,globalID,out);
                out.println("A client joined.");
                theThread.start();
                if(test_mode){
                    return;
                }
            }catch(IOException e){
                out.println("HostApp: Issue with acceptConnection.");
            }
        }
    }

    public void run() {
        out.println("Server starts to run");
        acceptConnection();
    }

    public static void main(String[] args) throws IOException {
        ServerSocket hostSocket = new ServerSocket(SOCKET_PORT);
        HostApp hostApp = new HostApp(hostSocket);
        hostApp.run();
    }
}