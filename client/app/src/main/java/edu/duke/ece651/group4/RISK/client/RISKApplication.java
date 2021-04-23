package edu.duke.ece651.group4.RISK.client;

import android.app.Application;
import android.util.Log;
import edu.duke.ece651.group4.RISK.client.listener.onJoinRoomListener;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.listener.onResultListener;
import edu.duke.ece651.group4.RISK.client.model.ChatMessageUI;
import edu.duke.ece651.group4.RISK.shared.*;
import edu.duke.ece651.group4.RISK.shared.message.ChatMessage;
import edu.duke.ece651.group4.RISK.shared.message.GameMessage;
import edu.duke.ece651.group4.RISK.shared.message.LogMessage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.shared.Constant.*;

public class RISKApplication extends Application {
    private static final String TAG = RISKApplication.class.getSimpleName();
    private static Client playerClient;
    private static World theWorld;
    static ArrayList<RoomInfo> roomInfo;
    static String userName;
    static int currentRoomSize;
    static boolean updatedTech;
    private static ThreadPoolExecutor threadPool;
    private static ChatClient chatClient;
    private static List<ChatMessageUI> storedMsg;

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(() -> {
            try {
                playerClient = new Client(SOCKET_HOSTNAME, SOCKET_PORT);
            } catch (IOException e) {
                Log.e(TAG, LOG_CREATE_FAIL);
                e.printStackTrace();
            }
        }).start();
        theWorld = null;
        roomInfo = new ArrayList<>();
        userName = null;
        currentRoomSize = 0;
        updatedTech = false;
        chatClient = null;
        threadPool = new ThreadPoolExecutor(2, 5, 1,
                TimeUnit.MINUTES, new LinkedBlockingDeque<>(10));
        storedMsg = new ArrayList<>();
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
    public static int getMaxSoldierLevel() {
        return UNIT_NAMES.size() - 1;
    }


    /**
     * @return maximum number of players in current game
     */
    public static int getCurrentRoomSize() {
        return currentRoomSize;
    }

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
        Set<String> allyNames = theWorld.getAllianceNames(userName);
        if (allyNames.isEmpty()) {
            return NO_ALLY;
        }
        StringBuilder names = new StringBuilder();
        String sep = "";
        for (String allis : allyNames) {
            names.append(sep).append(allis);
            sep = ", ";
        }
        return names.toString();
    }

    /**
     * @return list of all my territory
     */
    public static List<String> getMyTerrNames() {
        return transferToNames(theWorld.getTerritoriesOfPlayer(userName));
    }

    /**
     * @return list of all territory stationed by my troop
     */
    public static List<String> getTerrNamesWithMyTroop() {
        return transferToNames(theWorld.getTerritoriesWithMyTroop(userName));
    }

    /**
     * @return list of all my and ally's territory
     */
    public static List<String> getMyAndAllyTerrNames() {
        return transferToNames(theWorld.getTerritoriesOfPlayerAndAlliance(userName));
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
        if (theWorld == null) {
            Log.e(TAG, LOG_FUNC_FAIL + "getWorldInfo world null");
        }
        List<Territory> terrs = theWorld.getAllTerritories();
        List<String> info = new ArrayList<>();

        for (Territory t : terrs) {
            info.add(t.getInfo());
        }
        return info;
    }

    public static int getTechLevel() {
        return theWorld.getPlayerInfoByName(userName).getTechLevel();
    }

    /**
     * @return list information of the player
     */
    public static String getPlayerInfo() {
        if (theWorld == null) {
            Log.e(TAG, LOG_FUNC_FAIL + "getPlayerInfo world null");
        }
        PlayerInfo info = theWorld.getPlayerInfoByName(userName);
        StringBuilder result = new StringBuilder();
        result.append("Player name:  ").append(userName).append("\n");
        result.append("Tech Level: ").append(getTechLevel()).append("\n");
        result.append("Alliance: ").append(getAllianceName()).append("\n");
        result.append("Food Resource: ").append(info.getFoodQuantity()).append("\n");
        result.append("Tech Resource: ").append(info.getTechQuantity()).append("\n");
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
    public synchronized static void send(Object toSendO, onResultListener listener) {
        threadPool.execute(() -> {
            try {
                playerClient.sendObject(toSendO);
                listener.onSuccess();
            } catch (Exception e) {
                Log.e(TAG, LOG_FUNC_FAIL + "send function: " + e.toString());
                listener.onFailure(e.toString());
            }
        });
    }

    /**
     * Pair function with send to get objects from server.
     *
     * @param listener reminds main thread to get received info if success.
     */
    public synchronized static void receive(onReceiveListener listener) {
        threadPool.execute(() -> {
            try {
                Object receivedO = playerClient.recvObject();
                listener.onSuccess(receivedO);
            } catch (Exception e) {
                Log.e(TAG, LOG_FUNC_FAIL + e.toString());
            }
        });
    }

    /**
     * called when you want to send an object then receive null if success otherwise receive String explain why fail.
     */
    public synchronized static void sendAndReceiveResult(Object toSendO, onResultListener listener) {
        threadPool.execute(() -> {
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
        });
    }

    /**
     * called when you want to send an object then receive a World if success otherwise receive String explain why fail.
     */
    public synchronized static void sendAndReceiveWorld(Object toSendO, onReceiveListener listener) {
        threadPool.execute(() -> {
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
                    Log.e(TAG, LOG_FUNC_RUN + "receive not a world: " + receivedO.getClass());
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        });
    }

    /**
     * called when you want to send an object then receive a List if success otherwise receive String explain why fail.
     * type: ROOM: receive a RoomInfo list for displaying of room activity.
     */
    public synchronized static void sendAndReceiveList(Object toSendO, onReceiveListener listener, String type) {
        threadPool.execute(() -> {
            Log.e(TAG, "sendReceiveAndReceiveList called");
            try {
                playerClient.sendObject(toSendO);
                Object receivedO = playerClient.recvObject();
                if (receivedO instanceof String) {
                    listener.onFailure((String) receivedO);
                    listener.onFailure((String) receivedO);
                } else if (receivedO instanceof List) {
                    if (type.equals(ROOMS)) {
                        roomInfo = (ArrayList<RoomInfo>) receivedO;
                        listener.onSuccess(roomInfo);
                    }
                } else {
                    Log.e(TAG, LOG_FUNC_RUN + "receive not a List instead: " + receivedO.getClass());
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        });
    }


    /******* function used for sign in and log in activity ******/

    protected static void sendAccountInfo(String actName,
                                          String name,
                                          String pwd, onResultListener listener) {
        LogMessage m = new LogMessage(actName, name, pwd);
        sendAndReceiveResult(m, listener);
    }

    /**
     * This send LogIn info
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

    /******function used for room activity******/

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
        GameMessage m = new GameMessage(GAME_JOIN, roomID, -1);
        for (RoomInfo room : roomInfo) {
            if (room.getRoomID() == roomID) {
                currentRoomSize = room.getMaxNumPlayers();
            }
        }
        sendAndReceiveResult(m, listenerString);
    }

    /**
     * receive World from server and decide if this is a new game or not by checking report.
     * If new game call onJoinNew in listener and onBack if the player is join back.
     *
     * @param listenerWorld listener for receiving a World to start game
     */
    public static void waitGameStart(onJoinRoomListener listenerWorld) {
        try {
            Object receivedWorld = playerClient.recvObject();
            if (receivedWorld instanceof World) {
                Log.i(TAG, LOG_FUNC_RUN + "World received");
                theWorld = (World) receivedWorld;
                String report = theWorld.getReport();
                Log.i(TAG, LOG_FUNC_RUN + report);
                if (report.equals("")) { //new game
                    listenerWorld.onJoinNew();
                } else {
                    listenerWorld.onBack();
                }
            } else {
                Log.e(TAG, LOG_FUNC_RUN + "not World received in start game and instead: " + receivedWorld.getClass());
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
        sendAndReceiveResult(m, listenerString);
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
    public static MoveOrder buildMoveOrder(String src, String des, int num, String typeName, int unitLevel) {
        String job = buildJobName(typeName, unitLevel);
        HashMap<String, Integer> dict = new HashMap<>();
        dict.put(job, num);
        Troop target = new Troop(dict, new TextPlayer(userName));
        return new MoveOrder(src, des, target, MOVE_ACTION);
    }

    /**
     * Used to send a move order
     */
    public static void doOneMove(MoveOrder order, onResultListener listener) {
        try {
            Log.i(TAG,order.getActTroop().getSummary());

            Troop clo=order.getActTroop().clone();
            Log.i(TAG,clo.getSummary());
            MoveOrder tmp = new MoveOrder(order.getSrcName(), order.getDesName(), clo, MOVE_ACTION);
            theWorld.moveTroop(order, userName);
            send(tmp, listener);
        } catch (Exception e) {
            listener.onFailure(e.getMessage());
        }
    }


    /**
     * Used to construct an attack order
     */

    public static AttackOrder buildAttackOrder(String src, String des, int num, String typeName, int unitLevel) {
        String job = buildJobName(typeName, unitLevel);
        HashMap<String, Integer> dict = new HashMap<>();
        dict.put(job, num);
        Troop target = new Troop(dict, new TextPlayer(userName));
        return new AttackOrder(src, des, target, ATTACK_ACTION);
    }

    /**
     * Used to send an attack order
     */
    public static void doOneAttack(AttackOrder order, onResultListener listener) {
        try {



            Log.i(TAG,order.getActTroop().getSummary());

            Troop clo=order.getActTroop().clone();
            Log.i(TAG,clo.getSummary());

            AttackOrder tmp = new AttackOrder(order.getSrcName(), order.getDesName(), clo, ATTACK_ACTION);

            theWorld.attackATerritory(order, userName);

            send(tmp, listener);
        } catch (Exception e) {
            listener.onFailure(e.getMessage());

        }
    }

    /**
     * Used to construct an upgrade soldier level order
     */

    public static UpgradeTroopOrder buildUpOrder(String srcName,
                                                 int levelBefore, int levelAfter,
                                                 int nUnit, String type) {
        UpgradeTroopOrder order = new UpgradeTroopOrder(srcName, levelBefore, levelAfter, nUnit);
        order.setUnitType(type);
        return order;
    }

    public static TransferTroopOrder buildTransferTroopOrder(String srcName, String typeAfter, int unitLevel, int nUnit) {
        return new TransferTroopOrder(srcName, typeAfter, unitLevel, nUnit);
    }

    /**
     * First check if can upgrade: World throw exception if cannot,
     * otherwise send a soldier level upgrade order to server.
     */
    public static void doSoldierUpgrade(UpgradeTroopOrder order, onResultListener listener) {
        try {
//            UpgradeTroopOrder tmp = new UpgradeTroopOrder(order.getSrcName(), order.getLevelBefore(), order.getLevelAfter(), order.getNUnit());
            theWorld.upgradeTroop(order, userName);
            send(order, listener);
        } catch (Exception e) {
            listener.onFailure(e.getMessage());
        }
    }

    /**
     * First check if can transfer: World throw exception if cannot.
     * Send a soldier transfer order.
     */
    public static void doSoldierTransfer(TransferTroopOrder order, onResultListener listener) {
        try {
            TransferTroopOrder tmp = new TransferTroopOrder(order.getSrcName(), order.getTypeAfter(), order.getUnitLevel(), order.getNUnit());
            theWorld.transferTroop(tmp, userName);
            send(tmp, listener);
        } catch (Exception e) {
            listener.onFailure(e.getMessage());
        }
    }


    /**
     * Used to send an tech level upgrade order
     */
    public static void doOneUpgrade(onResultListener listener) {
        if (updatedTech) {
            listener.onFailure("You can only upgrade once.");
        } else {
            UpgradeTechOrder techOrder = new UpgradeTechOrder(1);
            try {
                theWorld.doUpgradeTechResourceConsumption(techOrder, userName);
                send(techOrder, listener);
            } catch (Exception e) {
                listener.onFailure(e.getMessage());
            }
            updatedTech = true;
        }
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
        // todo: send watch_action?
        Order order = new BasicOrder(null, null, null, DONE_ACTION);
        sendAndReceiveWorld(order, listener);
    }

    public static void switchGame() {
        send(new BasicOrder(null, null, null, SWITCH_OUT_ACTION),
                new onResultListener() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(String errMsg) {
                    }
                });
    }

    public static void backLogin() {
        GameMessage m = new GameMessage(GAME_EXIT, -1, -1);
        send(m, new onResultListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    public static List<Territory> getEnemyTerritory() {
        return theWorld.getTerritoriesNotOfPlayer(userName);
    }

    public static void requireAlliance(String allyName) {
        Order allyOrder = new AllianceOrder(userName, allyName);
        send(allyOrder, new onResultListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }


    /*************** function for chat **************/

    public static int getRoomId() {
        return theWorld.getRoomID();
    }

    public static void initChat() {
        new Thread(() -> {
            chatClient = new ChatClient(userName, SOCKET_HOSTNAME, CHAT_PORT);
            try {
                chatClient.start();
            } catch (Exception e) {
                Log.e(TAG, "initChat: " + e.toString());
            }
        }).start();
    }

    public static void setChatListener(onReceiveListener listener) {
        new Thread(() -> chatClient.setChatListener(listener)).start();
    }

    public static void setMsgListener(onReceiveListener listener) {
        new Thread(() -> chatClient.setMsgListener(listener)).start();
    }

    public static void sendOneMsg(ChatMessageUI message, onResultListener listener) {
        chatClient.send(message);
        listener.onSuccess();
    }

    public static void addMsg(ChatMessageUI msg) {
        storedMsg.add(msg);
    }

    public static List<ChatMessageUI> getStoredMsg(String chatID) {
        if(chatID == null){
            return storedMsg;
        }
        List historyMsg = new ArrayList();
        for (ChatMessageUI msg : storedMsg) {
            if (msg.getChatId().equals(chatID)) {
                historyMsg.add(msg);
            }
        }
        return historyMsg;
    }

    public static Set<String> getAllPlayersName() {
        return theWorld.getAllPlayerNames();
    }

    public static List<String> getChatPlayersName() {
        List<String> chatPlayerNames = new ArrayList<>(getAllPlayersName());
        chatPlayerNames.remove(userName);
        return chatPlayerNames;
    }
}
