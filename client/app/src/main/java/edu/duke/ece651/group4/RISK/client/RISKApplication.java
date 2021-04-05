package edu.duke.ece651.group4.RISK.client;

import android.app.Application;
import android.util.Log;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.listener.onResultListener;
import edu.duke.ece651.group4.RISK.shared.*;
import edu.duke.ece651.group4.RISK.shared.message.GameMessage;
import edu.duke.ece651.group4.RISK.shared.message.LogMessage;

import java.io.IOException;
import java.util.ArrayList;
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
        this.roomInfo = new ArrayList<>();
        this.userName=null;
        Log.i(TAG, LOG_CREATE_SUCCESS);
    }

    public static String getUserName() {
        return userName;
    }
    public static ArrayList<RoomInfo> getRoomInfo() {
        return roomInfo;
    }
    public static World getWorld() {
        return theWorld;
    }
    public static int getMaxLevel() {
        return UNIT_NAMES.size() - 1;
    }
    public static List<String> getLevelNames() {
        return UNIT_NAMES;
    }
    /**
     * @return list of all my territory
     */
    public static List<Territory> getMyTerritory() {
        return theWorld.getTerritoriesOfPlayer(new TextPlayer(userName));
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

    protected synchronized static void sendReceiveHelper(Object toSendO, onReceiveListener listener, String type) {
        try {
            sendReceiveAndUpdate(toSendO, listener, type);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            listener.onFailure("send log message error" + e.toString());
        }
    }

    protected synchronized static void createGameHelper(Object toSendO, onReceiveListener listenerString, onReceiveListener listenerWorld) {
        try {
            sendReceiveGameStart(toSendO, listenerString, listenerWorld);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            listenerString.onFailure("send log message error" + e.toString());
            listenerWorld.onFailure("send log message error" + e.toString());
        }

    }

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

    public static void refreshGameInfo(onReceiveListener listener) {
        GameMessage m = new GameMessage(GAME_REFRESH, -1, -1);
        sendReceiveHelper(m, listener, ROOMS);
    }

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


    public static void JoinGame(int gameID, onReceiveListener listenerString, onReceiveListener listenerWorld) {
        GameMessage m = new GameMessage(GAME_JOIN, gameID, -1);
        createGameHelper(m, listenerString, listenerWorld);
//        sendReceiveHelper(gameID,listener,WORLD);
    }

    public static void createGame(int playerNum, onReceiveListener listenerString, onReceiveListener listenerWorld) {
        GameMessage m = new GameMessage(GAME_CREATE, -1, playerNum);
        createGameHelper(m, listenerString, listenerWorld);
    }

    // TODO:
    public static String doOneMove(MoveOrder order) {
        return null; // move validate
    }
/*
    public static String doOneMove(MoveOrder order, onResultListener listener) {
        try {

            theWorld.moveTroop(order, userName);

//            theWorld.moveTroop(order);

            send(order, listener);
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }
 */

    // TODO:
    public static String doOneAttack(AttackOrder order, onResultListener listener) {
        try {

            theWorld.attackATerritory(order, userName);

//            theWorld.attackATerritory(order);

            send(order, listener);
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }

    public static String doOneUpgrade(UpgradeTroopOrder order, onResultListener listener) {
        try {
            theWorld.upgradeTroop(order, userName);
            send(order, listener);
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }

    public static void doDone(Order order, onReceiveListener listener) {
        sendReceiveHelper(order, listener, WORLD);
    }

//    public static String doTechUpgrade(UpgradeTroopOrder order,onResultListener listener){
//        try {
//
//
//            send(order,listener);
//        }catch(Exception e){
//            return e.getMessage();
//        }
//        return null;
//    }

    public static void doPlacement(List<PlaceOrder> placements, onReceiveListener listener) {
        sendReceiveHelper(placements, listener, WORLD);
        // return world. world: getMyTerr
    }


//    public static MoveOrder buildMoveOrder(String src,String des,int num, String job ){
//        HashMap<String,Integer> dict=new HashMap<>();
//        dict.put(job,num);
//        Troop target=new Troop(dict,new TextPlayer(userName));
//        return new MoveOrder(src,des,target,MOVE_ACTION);
//    }
//
//    public static AttackOrder buildAttackOrder(String src,String des,int num, String job ){
//        HashMap<String,Integer> dict=new HashMap<>();
//        dict.put(job,num);
//        Troop target=new Troop(dict,new TextPlayer(userName));
//        return new AttackOrder (src,des,target,ATTACK_ACTION);
//    }


}