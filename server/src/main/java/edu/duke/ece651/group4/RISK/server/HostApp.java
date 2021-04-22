/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package edu.duke.ece651.group4.RISK.server;

import edu.duke.ece651.group4.RISK.shared.Client;
import edu.duke.ece651.group4.RISK.shared.Territory;
import edu.duke.ece651.group4.RISK.shared.World;
import org.checkerframework.checker.units.qual.C;

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

//    protected void addTestUsers(){
//        users.add(new User(0,"xs","1"));
//        users.add(new User(1,"wx","1"));
//        users.add(new User(2,"jin","1"));
//        users.add(new User(3,"sj","1"));
//    }

    /*
     * This setup connection between server and clients
     * it will create a ClientThread to deal with all stuff
     * including user logIn and Game Playing with this specific client.
     * This thread will exist if someone open a PlayerApp
     * and will close if this PlayerApp close. THe main reason we adopt this
     * design pattern is to save the resource of threads.
     *  */

    public void acceptConnection(){
//        addTestUsers();
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

    public void tryLoadUsersFromDatabase(){
        List<UserInfo> usInfo = HibernateTool.getUserInfoList();
        for(UserInfo uInfo: usInfo){
            users.add(new User(uInfo));
        }
    }

    public void tryLoadGamesFromDatabase(){
        List<GameInfo> gamesInfo = HibernateTool.getGameInfoList();
        for(GameInfo gInfo: gamesInfo){
            if(gInfo.gameState.isAlive()){
                Game g =new Game(gInfo);
                printGameInfo(gInfo);
                games.add(g);
                GameRunner gameRunner = new GameRunner(g,out);
                gameRunner.start();
            }
        }
    }

    protected static void printGameInfo(GameInfo gInfo){
        System.out.println("Loading: Game" + gInfo.gameID + "'s Info: ");
        System.out.println("         GameState: isAive: " + gInfo.gameState.isAlive());
        System.out.println("         GameState: isSetUp: " + gInfo.gameState.isSetUp());
        System.out.println("         GameState: isDonePlaceUnits: " + gInfo.gameState.isDonePlaceUnits());
        System.out.println("         GameState: isWaitToUpdate: " + gInfo.gameState.isWaitToUpdate());
        System.out.println("         GameState: isAllPlayersDoneUpdatingState: " + gInfo.gameState.isAllPlayersDoneUpdatingState());
        System.out.println("         GameState: isAllPlayersDoneOneTurn: " + gInfo.gameState.isAllPlayersDoneOneTurn());
        System.out.println("         GameState: isDoneUpdateGame: " + gInfo.gameState.isDoneUpdateGame());
        World w = gInfo.getTheWorld();
        System.out.println("         World Turn num: " + w.getTurnNumber());
        System.out.println("         World Report: " + w.getReport());
    }

//    public void loadGames(){
//
//    }
    public void run() {
        out.println("Server starts to run");
        tryLoadUsersFromDatabase();
        tryLoadGamesFromDatabase();
        acceptConnection();
    }

    public static void main(String[] args) throws IOException {
        ChatHost chatHost = new ChatHost();
        chatHost.start();
        ServerSocket hostSocket = new ServerSocket(SOCKET_PORT);
        HostApp hostApp = new HostApp(hostSocket);
        hostApp.run();
    }
}