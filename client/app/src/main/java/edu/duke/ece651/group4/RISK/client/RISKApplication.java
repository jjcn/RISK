package edu.duke.ece651.group4.RISK.client;

import android.app.Application;
import android.util.Log;
import edu.duke.ece651.group4.RISK.client.listener.onJoinRoomListener;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.listener.onResultListener;
import edu.duke.ece651.group4.RISK.shared.*;
import edu.duke.ece651.group4.RISK.shared.message.GameMessage;
import edu.duke.ece651.group4.RISK.shared.message.LogMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.shared.Constant.*;

public class RISKApplication extends Application {
    private static final String TAG = RISKApplication.class.getSimpleName();
    private static Client playerClient;
    private static World theWorld;
    private int totalPopulation;
    private Random rnd;
    static String response;
    static ArrayList<RoomInfo> roomInfo;
    static String userName;
    static int currentRoomSize;
    static UpgradeTechOrder techOrder;
    static onResultListener techListener;

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(() -> {
            try {
                playerClient = new Client("vcm-18527.vm.duke.edu", SOCKET_PORT);
            } catch (IOException e) {
                Log.e(TAG, LOG_CREATE_FAIL);
                e.printStackTrace();
            }
        }).start();
        this.theWorld = null;
        this.totalPopulation = 15;
        this.rnd = new Random();
        this.techOrder = null;
        this.techListener = null;
        this.roomInfo = new ArrayList<>();
        this.userName = null;
        this.currentRoomSize = 0;
        Log.i(TAG, LOG_CREATE_SUCCESS);
    }


    /**
     * @return user name
     */
    public static String getUserName() {
        return userName;
    }

    /**
     * @return list of all the game rooms
     */
    public static ArrayList<RoomInfo> getRoomInfo() {
        return roomInfo;
    }

    /**
     * @return current world board using
     */
    public static World getWorld() {
        return theWorld;
    }

    /**
     * @return maximum level of soldier
     */
    public static int getMaxLevel() {
        return UNIT_NAMES.size() - 1;
    }


    /**
     * @return maximum number of players in current game
     */
    public static int getCurrentRoomSize() {
        return currentRoomSize;
    }

    public static List<Territory> getMyTerritory() {
        return theWorld.getTerritoriesOfPlayer(new TextPlayer(userName));
    }

    /**
     * @return list of names of soldier at each level
     */
    public static List<String> getLevelNames() {
        return UNIT_NAMES;
    }

    /**
     * @return list of all my territory
     */
    public static List<String> getMyTerrNames() {
        return transferToNames(theWorld.getTerritoriesOfPlayer(new TextPlayer(userName)));
    }

    /**
     * @return list of enemy territory
     */
    public static List<String> getEnemyTerrNames() {
        return transferToNames(theWorld.getTerritoriesNotOfPlayer(userName));
    }

    // helper function
    private static List<String> transferToNames(List<Territory> list) {
        List<String> names = new ArrayList<>();
        for (Territory item : list) {
            names.add(item.getName());
        }
        return names;
    }

    /**
     * @return list information of each territory
     */
    public static List<String> getWorldInfo() {
        List<Territory> terrs = theWorld.getAllTerritories();
        List<String> info = new ArrayList<>();

        for (Territory t : terrs) {
            info.add(t.getInfo());
        }
        return info;
    }

    /**
     * @return list information of the player
     */
    public static String getPlayerInfo() {
        PlayerInfo info = theWorld.getPlayerInfoByName(userName);
        StringBuilder result = new StringBuilder();
        result.append("Player name:  " + userName + "\n");
        result.append("Food Resource: " + info.getFoodQuantity() + "\n");
        result.append("Tech Resource: " + info.getTechQuantity() + "\n");
        result.append("Tech Level: " + info.getTechLevel() + "\n");
        result.append("My Territories: ");
        List<Territory> terrs = theWorld.getTerritoriesOfPlayer(userName);
        for (Territory t : terrs) {
            result.append(t.getName() + "  ");
        }
        result.append("\n");
        return result.toString();
    }


    /****************** send receive helper function *******************/

    /**
     * To send objects to server. onSuccess is called if no exception is called.
     * Android blocks direct send and receive.
     * new thread works parallel to the main thread (UI thread) thus objects should not be directly stored in Client.
     */
    public synchronized static void send(Object toSendO) {
        new Thread(() -> {
            try {
                playerClient.sendObject(toSendO);
            } catch (Exception e) {
                Log.e(TAG, LOG_FUNC_FAIL +"send function: " +e.toString());
            }
        }).start();
    }

    /**
     * Pair function with send to get objects from server.
     *
     * @param listener reminds main thread to get received info if success.
     */
    public synchronized static void receive(onReceiveListener listener) {
        new Thread(() -> {
            try {
                Object receivedO = playerClient.recvObject();
                listener.onSuccess(receivedO);
            } catch (Exception e) {
                Log.e(TAG, LOG_FUNC_FAIL + e.toString());
            }
        }).start();
    }

    /**
     * called when you want to send an object then receive null if success otherwise receive String explain why fail.
     */
    public static void sendAndReceiveResult(Object toSendO, onResultListener listener) {
        new Thread(() -> {
            Log.i(TAG, LOG_FUNC_RUN + "sendReceiveResult called");
            try {
                playerClient.sendObject(toSendO);
                Object receivedO = playerClient.recvObject();
                if (receivedO == null) {
                    listener.onSuccess();
                } else if (receivedO instanceof String) {
                    listener.onFailure((String) receivedO);
                } else {
                    Log.e(TAG, LOG_FUNC_FAIL + "result not string or null");
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }).start();
    }

    /**
     * called when you want to send an object then receive a World if success otherwise receive String explain why fail.
     */
    public static void sendAndReceiveWorld(Object toSendO, onReceiveListener listener) {
        new Thread(() -> {
            Log.e(TAG, "sendReceiveAndReceiveWorld called");
            try {
                playerClient.sendObject(toSendO);
                Object receivedO = playerClient.recvObject();
                if (receivedO instanceof String) {
                    listener.onFailure((String) receivedO);
                } else if (receivedO instanceof World) {
                    theWorld = (World) receivedO;
                    listener.onSuccess(theWorld);
                } else {
                    Log.e(TAG, LOG_FUNC_RUN + "receive not a world");
                }
                listener.onSuccess(receivedO);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }).start();
    }

    /**
     * called when you want to send an object then receive a List if success otherwise receive String explain why fail.
     * type: ROOM: receive a RoomInfo list for displaying of room activity.
     */
    public static void sendAndReceiveList(Object toSendO, onReceiveListener listener, String type) {
        new Thread(() -> {
            Log.e(TAG, "sendReceiveAndReceiveList called");
            try {
                playerClient.sendObject(toSendO);
                Object receivedO = playerClient.recvObject();
                if (receivedO instanceof String) {
                    listener.onFailure((String) receivedO);
                } else if (receivedO instanceof List) {
                    if (type == ROOMS) {
                        roomInfo = (ArrayList<RoomInfo>) receivedO;
                        listener.onSuccess(roomInfo);
                    }
                } else {
                    Log.e(TAG, LOG_FUNC_RUN + "receive not a List");
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }).start();
    }


    /******* function used for sign in and log in activity ******/

    protected static void sendAccountInfo(String actName,
                                          String name,
                                          String pwd, onResultListener listener) {
        LogMessage m = new LogMessage(actName, name, pwd);
        sendAndReceiveResult(m, listener);
    }

    /**
     * This send SignIn info
     *
     * @param name is username
     * @param pwd  is the password
     */
    public static void sendLogIn(String name, String pwd, onResultListener listener) {
        userName = name;
        sendAccountInfo(LOG_SIGNIN, name, pwd, listener);
    }

    /**
     * This send SignUP info
     *
     * @param name is username
     * @param pwd  is the password
     */
    public static void sendSignUp(String name, String pwd, onResultListener listener) {
        sendAccountInfo(LOG_SIGNUP, name, pwd, listener);
    }


    /*******function used for room activity******/

    /**
     * Used to update the list of game rooms
     */
    public static void refreshGameInfo(onReceiveListener listener) {
        GameMessage m = new GameMessage(GAME_REFRESH, -1, -1);
        sendAndReceiveList(m, listener, ROOMS);
    }

    /**
     * Used to send join a game message in the list of rooms, receive null if success otherwise a String.
     * on Success the waitGameStart function will be called to receive the upcoming World info.
     */
    public static void JoinGame(int gameID, onResultListener listenerString) {
        GameMessage m = new GameMessage(GAME_JOIN, gameID, -1);
        for (RoomInfo in : roomInfo) {
            if (in.getRoomID() == gameID) {
                currentRoomSize = in.getMaxNumPlayers();
            }
        }
        new Thread(() -> {
            Log.i(TAG, LOG_FUNC_RUN + "new thread on JoinRoom");
            try {
                sendAndReceiveResult(m,listenerString);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }).start();
    }

    public static void waitGameStart(onJoinRoomListener listenerWorld) {
            try {
                Object receivedWorld = playerClient.recvObject();
                if (receivedWorld instanceof World) {
                    Log.i(TAG, LOG_FUNC_RUN + "World received");
                    theWorld = (World) receivedWorld;
                    String report = theWorld.getReport();
                    if (report == "") { //new game
                        listenerWorld.onJoinNew();
                    } else {
                        listenerWorld.onBack();
                    }
                } else {
                    Log.e(TAG, LOG_FUNC_RUN + "not World received in start game");
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
    }

    /**
     * Used to send create a new game room. receive null if success otherwise a String.
     *      * on Success the waitGameStart function will be called to receive the upcoming World info.
     */
    public static void createGame(int playerNum, onResultListener listenerString) {
        GameMessage m = new GameMessage(GAME_CREATE, -1, playerNum);
        currentRoomSize = playerNum;
        new Thread(() -> {
            try {
                sendAndReceiveResult(m,listenerString);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }).start();
    }


    /******* function used for placement activity ******/

    /**
     * Used to send list of place order and wait new world
     */
    public static void doPlacement(List<PlaceOrder> placements, onReceiveListener listener) {
        sendAndReceiveWorld(placements,listener);
    }



    /******* function used for turn activity ******/

    /**
     * Used to construct a move order
     */
    public static MoveOrder buildMoveOrder(String src, String des, int num, String job) {
        HashMap<String, Integer> dict = new HashMap<>();
        dict.put(job, num);
        Troop target = new Troop(dict, new TextPlayer(userName));
        Log.i(TAG, LOG_FUNC_RUN + "MOVEORDER: num" + num  + "; MOVEORDER: job" + job);
        return new MoveOrder(src, des, target, MOVE_ACTION);
    }

    /**
     * Used to send a move order
     */
    public static String doOneMove(MoveOrder order) {
        try {
            MoveOrder tmp = new MoveOrder(order.getSrcName(), order.getDesName(), order.getActTroop().clone(), MOVE_ACTION);
            theWorld.moveTroop(order, userName);
            send(tmp);
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }


    /**
     * Used to construct an attack order
     */

    public static AttackOrder buildAttackOrder(String src, String des, int num, String job) {
        HashMap<String, Integer> dict = new HashMap<>();
        dict.put(job, num);
        Troop target = new Troop(dict, new TextPlayer(userName));
        return new AttackOrder(src, des, target, ATTACK_ACTION);
    }

    /**
     * Used to send an attack order
     */
    public static String doOneAttack(AttackOrder order) {
        try {
            AttackOrder tmp = new AttackOrder(order.getSrcName(), order.getDesName(), order.getActTroop().clone(), ATTACK_ACTION);
            theWorld.attackATerritory(order, userName);
            send(tmp);
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }

    /**
     * Used to construct an upgrade soldier level order
     */

    public static UpgradeTroopOrder buildUpOrder(String srcName,
                                                 int levelBefore, int levelAfter,
                                                 int nUnit) {
        return new UpgradeTroopOrder(srcName, levelBefore, levelAfter, nUnit);
    }

    /**
     * Used to send a soldier level upgrade order
     */
    public static String doSoliderUpgrade(UpgradeTroopOrder order) {
        try {
            UpgradeTroopOrder tmp = new UpgradeTroopOrder(order.getSrcName(), order.getLevelBefore(), order.getLevelAfter(), order.getNUnit());
            theWorld.upgradeTroop(order, userName);
            send(tmp);
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }


    /**
     * Used to send an tech level upgrade order
     */
    public static String doOneUpgrade(onResultListener listener) {
        techOrder = new UpgradeTechOrder(1);
        techListener = listener;
//        UpgradeTechOrder order =
//        try {
//            theWorld.upgradePlayerTechLevelBy1(userName);
//            send(order, listener);
//        } catch (Exception e) {
//            return e.getMessage();
//        }
        return null;
    }

    /**
     * Used to send an done order
     */
    public static void doDone(Order order, onReceiveListener listener) {
        if (techOrder != null) {
            try {
                Log.d(TAG, "UPgrade starts");
                theWorld.upgradePlayerTechLevelBy1(userName);
                send(techOrder);
                Log.d(TAG, "UPgrade send");
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        Log.d(TAG, "Done start");
        sendAndReceiveWorld(order,listener);
        Log.d(TAG, "Done end");
        techOrder = null;
        techListener = null;
    }


    public static void stayInGame(onReceiveListener listener) {
        sendAndReceiveWorld(null, listener);
    }

    public static void exitGame() {
        send(new BasicOrder(null, null, null, SWITCH_OUT_ACTION));
    }

    public static List<Territory> getEnemyTerritory() {
        return theWorld.getTerritoriesNotOfPlayer(userName);
    }

    public static void requireAlliance(String allyName) {
        Order allyOrder = new AllianceOrder(userName,allyName);
        send(allyOrder);
    }
}
