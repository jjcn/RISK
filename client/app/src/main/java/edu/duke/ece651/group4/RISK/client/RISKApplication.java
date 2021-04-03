package edu.duke.ece651.group4.RISK.client;

import android.app.Application;
import android.util.Log;
import edu.duke.ece651.group4.RISK.client.activity.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.activity.onResultListener;
import edu.duke.ece651.group4.RISK.shared.*;
import edu.duke.ece651.group4.RISK.shared.message.LogMessage;

import java.io.IOException;
import java.util.Random;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.shared.Constant.*;

public class RISKApplication extends Application {
    private static String TAG = RISKApplication.class.getSimpleName();
    private static Client playerClient;
    private World theWorld;
    private int totalPopulation;
    private Random rnd;
    static String response;

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
        Log.i(TAG, LOG_CREATE_SUCCESS);
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
        try {
            sendAndReceive(m, new onReceiveListener() {
                @Override
                public void onSuccess(Object o) {
                    listener.onSuccess(o);
                }

                @Override
                public void onFailure(String errMsg) {
                    listener.onFailure(errMsg);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            listener.onFailure("send log message error" + e.toString());
        }
    }

    /*
     * This send SignIn info
     * @param name is username
     * @param pwd is the password
     * @return null if succeed, a error message if false
     * */
    public static void sendLogIn(String name, String pwd, onReceiveListener listener) {
        sendAccountInfo(LOG_SIGNIN, name, pwd, listener);
    }

    /*
     * This send SignUP info
     * @param name is username
     * @param pwd is the password
     * @return null if succeed, a error message if false
     * */
    public static void sendSignUp(String name, String pwd, onReceiveListener listener) {
        sendAccountInfo(LOG_SIGNUP, name, pwd, listener);
    }

}
