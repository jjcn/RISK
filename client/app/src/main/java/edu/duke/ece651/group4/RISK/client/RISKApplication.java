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
    private static String TAG = RISKApplication.class.getSimpleName();
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
        }
        ).start();
        this.theWorld = null;
        this.totalPopulation = 15;
        this.rnd = new Random();
        this.roomInfo=new ArrayList<>();
        Log.i(TAG, LOG_CREATE_SUCCESS);
    }


    protected synchronized static void sendReceiveHelper(Object toSendO, onReceiveListener listener, String type){
        try {
            sendReceiveAndUpdate(toSendO, new onReceiveListener() {
                @Override
                public void onSuccess(Object o) {
                    listener.onSuccess(o);
                }

                @Override
                public void onFailure(String errMsg) {
                    listener.onFailure(errMsg);
                }
            },type);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            listener.onFailure("send log message error" + e.toString());
        }
    }

    protected synchronized static void createGameHelper(Object toSendO, onReceiveListener listenerString,onReceiveListener listenerWorld){
        try {
            sendReceiveGameStart(toSendO,new onReceiveListener() {
                        @Override
                        public void onSuccess(Object o) {
                            listenerString.onSuccess(o);
                        }

                        @Override
                        public void onFailure(String errMsg) {
                            listenerString.onFailure(errMsg);
                        }
                    },new onReceiveListener() {
                        @Override
                        public void onSuccess(Object o) {
                            listenerWorld.onSuccess(o);
                        }

                        @Override
                        public void onFailure(String errMsg) {
                            listenerWorld.onFailure(errMsg);
                        }
                    }
                    );
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            listenerString.onFailure("send log message error" + e.toString());
            listenerWorld.onFailure("send log message error" + e.toString());
        }

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

    public static void sendReceiveAndUpdate(Object toSendO, onReceiveListener listener,String type) {
        new Thread(() -> {
            Log.e(TAG,"sendReceiveAndUpdate called");
            try {
                if(toSendO instanceof GameMessage){
                    GameMessage g = (GameMessage) toSendO;
                    Log.e(TAG,LOG_FUNC_RUN+"send o:"+g.getAction());
                }
                playerClient.sendObject(toSendO);

                Log.e(TAG,LOG_FUNC_RUN+"room receive start");
                Object receivedO = playerClient.recvObject();
                if(receivedO != null) {
                    Log.e(TAG, LOG_FUNC_RUN + "object receive success" + receivedO.getClass().getSimpleName());
                }else{
                    Log.e(TAG, LOG_FUNC_RUN + "object receive success: null");
                }

                if(type==WORLD){
                    theWorld=(World) receivedO;
                }else if(type==ROOMS){
                    roomInfo=(ArrayList<RoomInfo>) receivedO;
                }else if(type==NAME){
                    userName=(String) receivedO;
                }
                listener.onSuccess(receivedO);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                listener.onFailure(e.toString());
            }
        }).start();
    }


    public static void sendReceiveGameStart(Object toSendO, onReceiveListener listenerString,onReceiveListener listenerWorld) {
        new Thread(() -> {
            Object receivedString=null;
            try {
                playerClient.sendObject(toSendO);
                receivedString = playerClient.recvObject();
                listenerString.onSuccess(receivedString);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                listenerString.onFailure(e.toString());
            }
            if(receivedString==null) {

                try {
                    Object receivedWorld = playerClient.recvObject();
                    if (receivedWorld != null) {
                        listenerWorld.onSuccess(receivedWorld);
                        Log.i(TAG,LOG_FUNC_RUN);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    listenerWorld.onFailure(e.toString());
                }
            }else{
                listenerWorld.onSuccess(null);
            }
        }).start();
    }

    protected static void sendAccountInfo(String actName,
                                          String name,
                                          String pwd, onReceiveListener listener) {
        LogMessage m = new LogMessage(actName, name, pwd);
        sendReceiveHelper(m,listener,NAME);
//        try {
//            sendAndReceive(m, new onReceiveListener() {
//                @Override
//                public void onSuccess(Object o) {
//                    listener.onSuccess(o);
//                }
//
//                @Override
//                public void onFailure(String errMsg) {
//                    listener.onFailure(errMsg);
//                }
//            });
//        } catch (Exception e) {
//            Log.e(TAG, e.toString());
//            listener.onFailure("send log message error" + e.toString());
//        }
    }


    public static void refreshGameInfo(onReceiveListener listener){
        GameMessage m = new GameMessage(GAME_REFRESH,-1,-1);
        sendReceiveHelper(m,listener,ROOMS);
    }

    /**
     * This send SignIn info
     * @param name is username
     * @param pwd is the password
     * @return null if succeed, a error message if false
     * */
    public static void sendLogIn(String name, String pwd, onReceiveListener listener) {
        sendAccountInfo(LOG_SIGNIN, name, pwd, listener);
    }

    /**
     * This send SignUP info
     * @param name is username
     * @param pwd is the password
     * @return null if succeed, a error message if false
     * */
    public static void sendSignUp(String name, String pwd, onReceiveListener listener) {
        sendAccountInfo(LOG_SIGNUP, name, pwd, listener);
    }

    public static ArrayList<RoomInfo> getRoomInfo(){
        return roomInfo;
    }

    public static World getWorld(){
        return theWorld;
    }

    public static void JoinGame(int gameID,onReceiveListener listenerString,onReceiveListener listenerWorld){
        GameMessage m=new GameMessage(GAME_JOIN,gameID, -1);
        createGameHelper(m,listenerString,listenerWorld);
//        sendReceiveHelper(gameID,listener,WORLD);
    }

    public static void createGame(int playerNum,onReceiveListener listenerString,onReceiveListener listenerWorld){
        GameMessage m=new GameMessage(GAME_CREATE,-1, playerNum);
        createGameHelper(m,listenerString,listenerWorld);
    }

    public static int maxLevel(){
        return UNIT_NAMES.size()-1;
    }

    public static List<String> leveNames(){
        return UNIT_NAMES;
    }


    /**
     *
     * @return list of all my territory
     */
    public static List<Territory> myTerritory(){
        return theWorld.getTerritoriesOfPlayer(new TextPlayer(userName));
    }


//    public static List<Territory> getMyTerritory(){
//        return theWorld.
//    }

    public static String doOneMove(BasicOrder order,onResultListener listener){
        try {

            theWorld.moveTroop(order,userName);

//            theWorld.moveTroop(order);

            send(order,listener);
        }catch(Exception e){
            return e.getMessage();
        }
        return null;
    }

    public static String doOneAttack(BasicOrder order,onResultListener listener){
        try {

            theWorld.attackATerritory(order,userName);

//            theWorld.attackATerritory(order);

            send(order,listener);
        }catch(Exception e){
            return e.getMessage();
        }
        return null;
    }

    public static String doOneUpgrade(UpgradeTroopOrder order,onResultListener listener){
        try {
            theWorld.upgradeTroop(order,userName);

            send(order,listener);
        }catch(Exception e){
            return e.getMessage();
        }
        return null;
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

    public static void doPlacement(List<PlaceOrder> placements,onReceiveListener listener){

          sendReceiveHelper(placements,listener,WORLD);
    }

}
