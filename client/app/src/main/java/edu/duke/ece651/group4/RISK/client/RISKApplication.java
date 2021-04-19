package edu.duke.ece651.group4.RISK.client;

import android.app.Application;
import android.util.Log;
import com.stfalcon.chatkit.commons.models.IMessage;
import edu.duke.ece651.group4.RISK.client.listener.onJoinRoomListener;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.listener.onResultListener;
import edu.duke.ece651.group4.RISK.shared.*;
import edu.duke.ece651.group4.RISK.shared.message.GameMessage;
import edu.duke.ece651.group4.RISK.shared.message.LogMessage;

import java.io.IOException;
import java.util.*;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.shared.Constant.*;

public class RISKApplication extends Application {
    private static final String TAG = RISKApplication.class.getSimpleName();
    private static Client playerClient;
    private static World theWorld;
    private Random rnd;
    static ArrayList<RoomInfo> roomInfo;
    private static RoomInfo currentRoom;
    static String userName;
    static int currentRoomSize;
    static boolean updatedTech;

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
        this.rnd = new Random();
        this.roomInfo = new ArrayList<>();
        this.userName = null;
        this.currentRoomSize = 0;
        this.updatedTech = false;
        Log.i(TAG, LOG_CREATE_SUCCESS);
    }


    public static void setWorld(World theWorld) {
        RISKApplication.theWorld = theWorld;
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

//    public static RoomInfo getCurrRoomInfo(){return currentRoom;}

    public static List<Territory> getMyTerritory() {
        return theWorld.getTerritoriesOfPlayer(userName);
    }

    /**
     * @return list of names of soldier at each level
     */
    public static List<String> getLevelNames() {
        return UNIT_NAMES;
    }

    public static String getAllianceName() {
        Log.i(TAG, LOG_FUNC_RUN + "start get alliance");
        Set<String> allyNames = getWorld().getAllianceNames(userName);
        if (allyNames.isEmpty()) {
            return NO_ALLY;
        }
        StringBuilder names = new StringBuilder();
        String sep = "";
        for (String allis : allyNames) {
            names.append(sep + allis);
            sep = ", ";
        }
        Log.i(TAG, LOG_FUNC_RUN + "getAlliance returned");
        return names.toString();
    }

    /**
     * @return list of all my territory
     */
    public static List<String> getMyTerrNames() {
        return transferToNames(theWorld.getTerritoriesOfPlayer(userName));
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
        if(theWorld == null){
            Log.e(TAG,LOG_FUNC_FAIL+"getWorldInfo world null");
        }
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
        if(theWorld == null){
            Log.e(TAG,LOG_FUNC_FAIL+"getPlayerInfo world null");
        }
        PlayerInfo info = theWorld.getPlayerInfoByName(userName);
        StringBuilder result = new StringBuilder();
        result.append("Player name:  " + userName + "\n");
        result.append("Tech Level: " + info.getTechLevel() + "\n");
        result.append("Alliance: " + getAllianceName());
        result.append("Food Resource: " + info.getFoodQuantity() + "\n");
        result.append("Tech Resource: " + info.getTechQuantity() + "\n");
//        result.append("My Territories: ");
//        Log.i(TAG,LOG_FUNC_RUN+"start add terr names");
//        List<Territory> terrs = theWorld.getTerritoriesOfPlayer(userName);
//        for (Territory t : terrs) {
//            result.append(t.getName() + "  ");
//        }
//        result.append("\n");
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
                Log.e(TAG, LOG_FUNC_FAIL + "send function: " + e.toString());
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
    public synchronized static void sendAndReceiveResult(Object toSendO, onResultListener listener) {
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
    public synchronized static void sendAndReceiveWorld(Object toSendO, onReceiveListener listener) {
        new Thread(() -> {
            Log.i(TAG, "sendReceiveAndReceiveWorld called");
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
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }).start();
    }

    /**
     * called when you want to send an object then receive a List if success otherwise receive String explain why fail.
     * type: ROOM: receive a RoomInfo list for displaying of room activity.
     */
    public synchronized static void sendAndReceiveList(Object toSendO, onReceiveListener listener, String type) {
        new Thread(() -> {
            Log.e(TAG, "sendReceiveAndReceiveList called");
            try {
                playerClient.sendObject(toSendO);
                Object receivedO = playerClient.recvObject();
                if (receivedO instanceof String) {
                    listener.onFailure((String) receivedO);
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
     * Used to send join a game message_menu in the list of rooms, receive null if success otherwise a String.
     * on Success the waitGameStart function will be called to receive the upcoming World info.
     */
    public static void JoinGame(int roomID, onResultListener listenerString) {
        Log.i(TAG, LOG_FUNC_RUN + "Join game");
        GameMessage m = new GameMessage(GAME_JOIN, roomID, -1);
        for (RoomInfo room : roomInfo) {
            if (room.getRoomID() == roomID) {
                currentRoomSize = room.getMaxNumPlayers();
            }
        }
        new Thread(() -> {
            Log.i(TAG, LOG_FUNC_RUN + "new thread on JoinRoom");
            try {
                sendAndReceiveResult(m, listenerString);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }).start();
    }

    /**
     * receive World from server and decide if this is a new game or not by checking report.
     * If new game call onJoinNew in listener and onBack if the player is join back.
     *
     * @param listenerWorld
     */
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
     * * on Success the waitGameStart function will be called to receive the upcoming World info.
     */
    public static void createGame(int playerNum, onResultListener listenerString) {
        GameMessage m = new GameMessage(GAME_CREATE, -1, playerNum);
        currentRoomSize = playerNum;
        new Thread(() -> {
            try {
                sendAndReceiveResult(m, listenerString);
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
        sendAndReceiveWorld(placements, listener);
    }


    /******* function used for turn activity ******/

    /**
     * Used to construct a move order
     */
    public static MoveOrder buildMoveOrder(String src, String des, int num, String job) {
        HashMap<String, Integer> dict = new HashMap<>();
        dict.put(job, num);
        Troop target = new Troop(dict, new TextPlayer(userName));
        Log.i(TAG, LOG_FUNC_RUN + "MOVEORDER: num" + num + "; MOVEORDER: job" + job);
        return new MoveOrder(src, des, target, MOVE_ACTION);
    }

    /**
     * Used to send a move order
     */
    public static String doOneMove(MoveOrder order, onResultListener listener) {
        try {
            MoveOrder tmp = new MoveOrder(order.getSrcName(), order.getDesName(), order.getActTroop().clone(), MOVE_ACTION);
            theWorld.moveTroop(order, userName);
            sendAndReceiveResult(tmp, listener);
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
    public static String doOneAttack(AttackOrder order, onResultListener listener) {
        try {
            AttackOrder tmp = new AttackOrder(order.getSrcName(), order.getDesName(), order.getActTroop().clone(), ATTACK_ACTION);
            theWorld.attackATerritory(order, userName);
            sendAndReceiveResult(tmp, listener);
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
    public static String doSoldierUpgrade(UpgradeTroopOrder order) {
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
    public static void doOneUpgrade(onResultListener listener) {
        if (updatedTech) {
            listener.onFailure("You can only upgrade once.");
        }
        UpgradeTechOrder techOrder = new UpgradeTechOrder(1);
        try {
            theWorld.doUpgradeTechResourceConsumption(techOrder, userName);
            send(techOrder);
        } catch (Exception e) {
            listener.onFailure(e.getMessage());
        }
        updatedTech = true;
    }

    /**
     * Used to send an done order
     */
    public static void doDone(onReceiveListener listener) {
        Log.d(TAG, "Done start");
        Order order = new BasicOrder(null, null, null, DONE_ACTION);
        sendAndReceiveWorld(order, listener);
        Log.d(TAG, "Done end");
        updatedTech = false;
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
        Order allyOrder = new AllianceOrder(userName, allyName);
        send(allyOrder);
    }

    public static int getRoomId() {
        return theWorld.getRoomID();
    }


    /*************** function for chat **************/
    // TODO
    public static void sendOneMsg(IMessage message, onReceiveListener listener) {

    }

    public static void getHistoryMsg() {

    }

    public static ArrayList<String> getAllPlayersName() {
        ArrayList<String> playerNames = new ArrayList<>();
        return playerNames;
    }
}
