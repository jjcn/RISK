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
        this.techOrder=null;
        this.techListener=null;
        this.roomInfo = new ArrayList<>();
        this.userName=null;
        this.currentRoomSize=0;
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
    public static int getCurrentRoomSize(){return currentRoomSize;}

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
     *
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
    /**
     * To send objects to server and check if send successfully.
     * Android blocks direct send and receive.
     * new thread works parallel to the main thread (UI thread) thus objects should not be directly stored in Client.
     *
     * @param toSendO  object to send
     * @param listener listen send result
     */
    public synchronized static void send(Object toSendO, onResultListener listener) {
        new Thread(() -> {
            try {
                playerClient.sendObject(toSendO);
                listener.onSuccess();
            } catch (Exception e) {
                Log.e(TAG, LOG_FUNC_FAIL + e.toString());
                listener.onFailure(e.toString());
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
                listener.onFailure(e.toString());
            }
        }).start();
    }

    /**
     * called when you want to send an object then receive and update certain field
     */
    protected synchronized static void sendReceiveHelper(Object toSendO, onReceiveListener listener, String type) {
        try {
            sendReceiveAndUpdate(toSendO, listener, type);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            listener.onFailure("send log message error" + e.toString());
        }
    }




    /**
     * called when you want to send message then receive world
     */
    protected synchronized static void createGameHelper(Object toSendO, onReceiveListener listenerString, onReceiveListener listenerWorld) {
        try {
            sendReceiveGameStart(toSendO, listenerString, listenerWorld);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            listenerString.onFailure("send log message error" + e.toString());
            listenerWorld.onFailure("send log message error" + e.toString());
        }

    }


    /**
     * called when you want to send an object then receive and update certain field
     */
    public static void sendReceiveAndUpdate(Object toSendO, onReceiveListener listener, String type) {
        new Thread(() -> {
            Log.e(TAG, "sendReceiveAndUpdate called");
            try {
                playerClient.sendObject(toSendO);
                Object receivedO = playerClient.recvObject();
                if (type == WORLD) {
                    theWorld = (World) receivedO;
                } else if (type == ROOMS) {
                    roomInfo = (ArrayList<RoomInfo>) receivedO;
                }
//                else if (type == NAME) {
//                    userName = (String) receivedO;
//                }
                listener.onSuccess(receivedO);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                listener.onFailure(e.toString());
            }
        }).start();
    }

    /**
     * called when you want to send an object then receive
     */
    public static void sendAndReceive(Object toSendO, onReceiveListener listener) {
        new Thread(() -> {
            try {
                playerClient.sendObject(toSendO);
                Object receivedO = playerClient.recvObject();
                listener.onSuccess(receivedO);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                listener.onFailure(e.toString());
            }
        }).start();
    }


    /*******function used for sign in and log in activity******/

    protected static void sendAccountInfo(String actName,
                                          String name,
                                          String pwd, onReceiveListener listener) {
        LogMessage m = new LogMessage(actName, name, pwd);
        sendReceiveHelper(m, listener, NAME);
    }

    /**
     * This send SignIn info
     *
     * @param name is username
     * @param pwd  is the password
     */
    public static void sendLogIn(String name, String pwd, onReceiveListener listener) {
        userName=name;
        sendAccountInfo(LOG_SIGNIN, name, pwd, listener);
    }

    /**
     * This send SignUP info
     *
     * @param name is username
     * @param pwd  is the password
     */
    public static void sendSignUp(String name, String pwd, onReceiveListener listener) {
        sendAccountInfo(LOG_SIGNUP, name, pwd, listener);
    }

    /*******function used for room activity******/

    /**
     * Used to update the list of game rooms
     */
    public static void refreshGameInfo(onReceiveListener listener) {
        GameMessage m = new GameMessage(GAME_REFRESH, -1, -1);
        sendReceiveHelper(m, listener, ROOMS);
    }

    /**
     * Used to send room selection to host and receive enter success or not and game board
     */
    public static void sendReceiveGameStart(Object toSendO, onReceiveListener listenerString, onReceiveListener listenerWorld) {
        new Thread(() -> {
            Log.i(TAG, LOG_FUNC_RUN + "new thread on sendReceiveGameStart");
            Object receivedString = null;
            try {
                playerClient.sendObject(toSendO);
                receivedString = playerClient.recvObject();
                listenerString.onSuccess(receivedString);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                listenerString.onFailure(e.toString());
            }
            if (receivedString == null) {
                try {
                    Log.i(TAG, LOG_FUNC_RUN + "receiveString null, create game success");
                    Object receivedWorld = playerClient.recvObject();
                    if (receivedWorld instanceof World) {
                        Log.i(TAG, LOG_FUNC_RUN + "World received");
                        theWorld = (World) receivedWorld;
                        listenerWorld.onSuccess(receivedWorld);
                    } else {
                        Log.i(TAG, LOG_FUNC_RUN + "not World received");
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    listenerWorld.onFailure(e.toString());
                }
            } else {
                listenerWorld.onSuccess(null);
            }
        }).start();
    }


    /**
     * Used to send join a game in the list of rooms
     */
    public static void JoinGame(int gameID, onReceiveListener listenerString, onJoinRoomListener listenerWorld) {
        GameMessage m = new GameMessage(GAME_JOIN, gameID, -1);
        for(RoomInfo in:roomInfo){
            if(in.getRoomID() == gameID){
                currentRoomSize=in.getMaxNumPlayers();
            }
        }
        try {
            new Thread(() -> {
                Log.i(TAG, LOG_FUNC_RUN + "new thread on JoinRoom");
                Object receivedString = null;
                try {
                    playerClient.sendObject(m);
                    receivedString = playerClient.recvObject();
                    listenerString.onSuccess(receivedString);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    listenerString.onFailure(e.toString());
                }
                if (receivedString == null) {
                    try {
                        Object receivedWorld = playerClient.recvObject();
                        if (receivedWorld instanceof World) {
                            Log.i(TAG, LOG_FUNC_RUN + "World received");
                            theWorld = (World) receivedWorld;
                            String report = theWorld.getReport();
                            if(report == "") { //new game
                                listenerWorld.onJoinNew();
                            }else{
                                listenerWorld.onBack();
                            }
                        } else {
                            Log.i(TAG, LOG_FUNC_RUN + "not World received");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }).start();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Used to send create a new game room
     */
    public static void createGame(int playerNum, onReceiveListener listenerString, onReceiveListener listenerWorld) {
        GameMessage m = new GameMessage(GAME_CREATE, -1, playerNum);
        currentRoomSize = playerNum;
        createGameHelper(m, listenerString, listenerWorld);
    }

    /*******function used for placement activity******/

    /**
     * Used to send list of place order and wait new world
     */
    public static void doPlacement(List<PlaceOrder> placements, onReceiveListener listener) {
        sendReceiveHelper(placements, listener, WORLD);
        // return world. world: getMyTerr
    }

    /*******function used for game activity******/


    /**
     * Used to construct a move order
     */
    public static MoveOrder buildMoveOrder(String src, String des, int num, String job) {
        HashMap<String, Integer> dict = new HashMap<>();
        dict.put(job, num);
        Troop target = new Troop(dict, new TextPlayer(userName));
        Log.i(TAG, LOG_FUNC_RUN +"MOVEORDER: num" + num);
        Log.i(TAG, LOG_FUNC_RUN +"MOVEORDER: job" + job);
        return new MoveOrder(src, des, target, MOVE_ACTION);
    }

    /**
     * Used to send a move order
     */
    public static String doOneMove(MoveOrder order, onResultListener listener) {
        try {
            MoveOrder tmp=new MoveOrder(order.getSrcName(),order.getDesName(),order.getActTroop().clone(),MOVE_ACTION);
            theWorld.moveTroop(order, userName);
            Log.e(TAG, theWorld.findTerritory(order.getSrcName()).getInfo());
            Log.e(TAG, theWorld.findTerritory(order.getDesName()).getInfo());

            send(tmp, listener);
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
            AttackOrder tmp=new AttackOrder(order.getSrcName(),order.getDesName(),order.getActTroop().clone(),ATTACK_ACTION);
            theWorld.attackATerritory(order, userName);
            send(tmp, listener);
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
    public static String doSoliderUpgrade(UpgradeTroopOrder order, onResultListener listener) {
        try {
            UpgradeTroopOrder tmp=new UpgradeTroopOrder(order.getSrcName(),order.getLevelBefore(),order.getLevelAfter(),UPTROOP_ACTION);
            theWorld.upgradeTroop(order, userName);
            send(tmp, listener);
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }



    /**
     * Used to send an tech level upgrade order
     */
    public static String doOneUpgrade(onResultListener listener) {
        techOrder=new UpgradeTechOrder(1);
        techListener=listener;
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
        if(techOrder!=null){
           try {
            theWorld.upgradePlayerTechLevelBy1(userName);
            send(techOrder, techListener);
            } catch (Exception e) {
               Log.e(TAG, e.getMessage());
            }
        }
        sendReceiveHelper(order, listener, WORLD);
        techOrder=null;
        techListener=null;
    }

    public static boolean checkLost() {
        return theWorld.checkLost(userName);
    }


    public static void stayInGame(onReceiveListener listener) {
        String message = null;
        sendReceiveHelper(message, listener, WORLD);
    }

    public static void exitGame(onResultListener listener) {
        send(EXIT_GAME_MESSAGE, listener);
    }

    public static List<Territory> getEnemyTerritory(){
        return theWorld.getTerritoriesNotOfPlayer(userName);
    }


}
